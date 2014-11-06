#include <android/log.h>
#include <stdlib.h>

#include <jni.h>
#include <assert.h>

#include "apriltag-0.92/apriltag.h"
#include "apriltag-0.92/tag36h11.h"
#include "apriltag-0.92/zarray.h"

#include "es_udc_fic_android_robot_control_camera_AprilTagNdkInterface.h"

/**
 * Convert an April tag detections into it's string form.
 *
 * @param env JNI environment.
 * @param tag Tag detection to convert.
 *
 * @return The detection formated
 *          “<code>, <id>, <hamming distance>, <rotation>, <observed perimeter>”
 */
jstring convert_detection_to_jstr(JNIEnv *env, april_tag_detection_t *tag){
    // 4 (hex) chars for each property + 2 for each ', ', + a \0
    // 4 * 5 + 2 * 4 + 1 chars
    const int max_string_size = 4 * 5 + 2 * 4 + 1;
    char *str = malloc(sizeof(char) * max_string_size);

    // Just returning a NULL on error will cause problems in NULL-ended arrays
    assert(str != NULL);

    // Code, rotation and observed perimeter are not (yet) passed by apriltags-c
    int len = snprintf(str, max_string_size, "%04x, %04x, %04x, %04x, %04x",
                       0xffff,      // Code
                       tag->id, // ID,
                       tag->hamming, // Hamming distance
                       0xffff, // Rotation
                       0xffff  // Observed perimeter
        );

    __android_log_print(ANDROID_LOG_DEBUG, "UDC_NDK", "%s\n",
                        str);

    jstring js = (*env)->NewStringUTF(env, str);

    free(str);

    return js;
}


/**
 * Convert a zarray of April tag detections into the string array which will
 * be returned to java.
 *
 * @param env JNI environment.
 * @param zdetections Array of detections to convert.
 *
 * @return The same detections jobjectArray of strings, containing
 *          “<code>, <id>, <hamming distance>, <rotation>, <observed perimeter>”
 */
jobjectArray convert_detections_to_str_array(JNIEnv *env, zarray_t *zdetections){
    int detection_count = zarray_size(zdetections);
    __android_log_print(ANDROID_LOG_DEBUG, "UDC_NDK", "%i detections [%p]\n",
                        detection_count, zdetections);


    // Prepare the detection array
    jclass str_class = (*env)->FindClass(env, "java/lang/String");
    jobjectArray detections = (*env)->NewObjectArray(env, detection_count,
                                                     str_class, NULL);
    if (detections == NULL){
        __android_log_print(ANDROID_LOG_ERROR, "UDC_NDK",
                            "Couldn't malloc detections. Out of memory?\n");
        return NULL;
    }

    // Format each detection
    for (int i = 0; i < detection_count; i++){
        april_tag_detection_t *tag = NULL;
        zarray_get(zdetections, i, &tag);

        if (tag == NULL){
            __android_log_print(ANDROID_LOG_ERROR, "UDC_NDK",
                                "Couldn't retrieve detection number %i\n",
                                i + 1);

            continue;
        }

        __android_log_print(ANDROID_LOG_DEBUG,
                            "UDC_NDK", "%i: id: %4d hamming: %d, goodness: %f",
                            i, tag->id, tag->hamming, tag->goodness);

        (*env)->SetObjectArrayElement(env, detections, i, convert_detection_to_jstr(env, tag));
        april_tag_detection_destroy(tag);
    }

    return detections;
}


/**
 * Take an image as an array of grascale bytes, width and height and perform an
 * april tag detection over it.
 *
 * @param env JNI environment.
 * @param obj Called java object.
 * @param packed_image Grascale image as an array of bytes.
 * @param width Image width in pixels.
 * @param height Image height in pixels.
 *
 * @returns Detections as a jobjectArray of strings, containing
 *          “<code>, <id>, <hamming distance>, <rotation>, <observed perimeter>”
 */
JNIEXPORT jobjectArray JNICALL Java_es_udc_fic_android_robot_1control_camera_AprilTagNdkInterface_process
(JNIEnv *env, jobject obj, jbyteArray packed_image, jint width, jint height){

    // Build the necessary structures
    /// @TODO make tag family configurable
    april_tag_family_t *tf = tag36h11_create();
    if (tf == NULL){
        return NULL;
    }

    april_tag_detector_t *td = april_tag_detector_create(tf);
    if (td == NULL){
        tag36h11_destroy(tf);
        return NULL;
    }


    // Grascale byte images may be injected directly into a image_u8_t struct
    jboolean is_copy;
    jbyte* image = (*env)->GetByteArrayElements(env, packed_image, &is_copy);
    __android_log_print(ANDROID_LOG_DEBUG, "UDC_NDK", "Processing image...");

    image_u8_t *im = image_u8_create(width, height);
    // image_u8_create alloc's a buffer, but we can use directly the one passed
    // through JNI, don't forget to free() the one allocated!
    free(im->buf);
    im->buf = image;

    // Process the image
    zarray_t *detections_z = april_tag_detector_detect(td, im);
    __android_log_print(ANDROID_LOG_DEBUG, "UDC_NDK", "Done");

    // Cleanup the structures as soon as we can
    april_tag_detector_destroy(td);
    tag36h11_destroy(tf);

    // Remember im->buf was the one passed through JNI, don't free it yet
    im->buf = NULL;
    image_u8_destroy(im);


    if (detections_z == NULL){
        return NULL;
    }

    // Convert the detections to a supported format
    char **detections = convert_detections_to_str_array(env, detections_z);

    // and we're done
    zarray_destroy(detections_z);

    return detections;
}
