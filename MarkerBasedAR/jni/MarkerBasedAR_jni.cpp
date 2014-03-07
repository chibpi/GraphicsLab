#include <MarkerBasedAR_jni.h>
#include <opencv2/core/core.hpp>
#include "MarkerDetector.hpp"

#include <string>
#include <vector>

#include <android/log.h>

#define LOG_TAG "MarkerBasedAR/MarkerBasedAR"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;


JNIEXPORT jlong JNICALL Java_org_chibpi_markerbasedar_MarkerDetector_nativeCreateObject
(JNIEnv * jenv, jclass, jfloat fx, jfloat fy, jfloat cx, jfloat cy, jfloatArray dist)
{
    LOGD("Java_org_chibpi_markerbasedar_MarkerDetector_nativeCreateObject enter");
    jlong result = 0;

    try
    {

    	float* distNativeValues = (float *)jenv->GetFloatArrayElements(dist, 0);
    	MarkerDetector *md=new MarkerDetector(CameraCalibration(fx,fy,cx,cy,distNativeValues));
    	result = (jlong)md;

    }
    catch(cv::Exception& e)
    {
        LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeCreateObject caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code {highgui::VideoCapture_n_1VideoCapture__()}");
        return 0;
    }

    LOGD("Java_org_chibpi_markerbasedar_MarkerDetector_nativeCreateObject exit");
    return result;
}

JNIEXPORT void JNICALL Java_org_chibpi_markerbasedar_MarkerDetector_nativeDestroyObject
(JNIEnv * jenv, jclass, jlong thiz)
{
    LOGD("Java_org_chibpi_markerbasedar_MarkerDetector_nativeDestroyObject enter");
    try
    {
        if(thiz != 0)
        {

            delete (MarkerDetector*)thiz;
        }
    }
    catch(cv::Exception& e)
    {
        LOGD("nativeestroyObject caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeDestroyObject caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code {highgui::VideoCapture_n_1VideoCapture__()}");
    }
    LOGD("JJava_org_chibpi_markerbasedar_MarkerDetector_nativeDestroyObject exit");
}




JNIEXPORT void JNICALL Java_org_chibpi_markerbasedar_MarkerDetector_nativeProcess
(JNIEnv * jenv, jclass, jlong thiz, jlong image)
{
    LOGD("Java_org_chibpi_markerbasedar_MarkerDetector_nativeProcess enter");
    try
    {

       ((MarkerDetector*)thiz)->processFrame(*((Mat*)image));

    }
    catch(cv::Exception& e)
    {
        LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeDetect caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code {highgui::VideoCapture_n_1VideoCapture__()}");
    }
    LOGD("Java_org_chibpi_markerbasedar_MarkerDetector_nativeProcess exit");
}

JNIEXPORT jfloatArray JNICALL Java_org_chibpi_markerbasedar_MarkerDetector_nativeGetTransformations
(JNIEnv * jenv, jclass, jlong thiz) {
    LOGD("Java_org_chibpi_markerbasedar_MarkerDetector_nativeGetTransformations enter");
    jfloatArray result=NULL;
    try
    {

    	std::vector<Transformation> transformations=((MarkerDetector*)thiz)->getTransformations();
    	result=jenv->NewFloatArray((int)transformations.size()*16);
    	if (result == NULL) {
    	     return NULL; /* out of memory error thrown */
    	}
    	for (unsigned i=0; i < transformations.size(); i++) {
    		Transformation tr=transformations[i];

    		Matrix44 tm=tr.getMat44();

           	jenv->SetFloatArrayRegion(result,16*i,16,(float *)tm.mat);
    	}
    }
    catch(cv::Exception& e)
    {
        LOGD("nativeGetTransformations caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeGetTransformations caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code {highgui::VideoCapture_n_1VideoCapture__()}");
        return NULL;
    }
    LOGD("Java_org_chibpi_markerbasedar_MarkerDetector_nativeGetTransformations exit");
    return result;
}

