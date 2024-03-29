LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#OPENCV_CAMERA_MODULES:=off
#OPENCV_INSTALL_MODULES:=off
#OPENCV_LIB_TYPE:=SHARED
include ../../OpenCV-2.4.6-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES  := MarkerBasedAR_jni.cpp CameraCalibration.cpp  Marker.cpp TinyLA.cpp GeometryTypes.cpp   MarkerDetector.cpp

LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl 

LOCAL_MODULE     := marker_based_ar

include $(BUILD_SHARED_LIBRARY)
