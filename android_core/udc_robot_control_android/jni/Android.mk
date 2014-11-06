APRIL_TAG_DIR     := jni/apriltag-0.92

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
include $(APRIL_TAG_DIR)/AprilTag.mk

OPENCV_CAMERA_MODULES  := on
OPENCV_LIB_TYPE        := static
OPENCV_INSTALL_MODULES := on
include jni/opencv/platforms/build_android_arm/OpenCV.mk
