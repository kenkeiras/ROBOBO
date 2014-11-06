APRIL_TAG_DIR := apriltag-0.92/

APRIL_TAG_SRC := \
	$(APRIL_TAG_DIR)/apriltag.c   \
	$(APRIL_TAG_DIR)/g2d.c        \
	$(APRIL_TAG_DIR)/graymodel.c  \
	$(APRIL_TAG_DIR)/homography.c \
	$(APRIL_TAG_DIR)/image_f32.c  \
	$(APRIL_TAG_DIR)/image_u32.c  \
	$(APRIL_TAG_DIR)/image_u8.c   \
	$(APRIL_TAG_DIR)/matd.c       \
	$(APRIL_TAG_DIR)/segment2.c   \
	$(APRIL_TAG_DIR)/tag36h10.c   \
	$(APRIL_TAG_DIR)/tag36h11.c   \
	$(APRIL_TAG_DIR)/tagtest.c    \
	$(APRIL_TAG_DIR)/unionfind.c  \
	$(APRIL_TAG_DIR)/workerpool.c \
	$(APRIL_TAG_DIR)/zarray.c     \
	$(APRIL_TAG_DIR)/zhash.c

APRIL_TAG_INTERFACE := $(APRIL_TAG_DIR)/es_udc_fic_android_robot_control_camera_AprilTagNdkInterface.c

include $(CLEAR_VARS)

LOCAL_LDLIBS    := -llog
LOCAL_CFLAGS    += -std=c99
LOCAL_MODULE    := apriltag
LOCAL_SRC_FILES := $(APRIL_TAG_SRC) $(APRIL_TAG_INTERFACE)

include $(BUILD_SHARED_LIBRARY)
