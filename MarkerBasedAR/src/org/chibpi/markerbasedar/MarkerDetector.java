package org.chibpi.markerbasedar;

import org.opencv.core.Mat;

public class MarkerDetector
{
    public MarkerDetector(float fx, float fy, float cx, float cy, float[] dist) {
        mNativeObj = nativeCreateObject(fx, fy, cx, cy, dist);
    }

    public float[] getTransformations() {
    	return nativeGetTransformations(mNativeObj);
    }

    public void process(Mat image) {
        nativeProcess(mNativeObj, image.getNativeObjAddr());
    }

    public void release() {
        nativeDestroyObject(mNativeObj);
        mNativeObj = 0;
    }

    private long mNativeObj = 0;

    private static native long nativeCreateObject(float fx, float fy, float cx, float cy, float[] dist);
    private static native void nativeDestroyObject(long thiz);
    private static native float[] nativeGetTransformations(long thiz);
    private static native void nativeProcess(long thiz, long inputImage);
}
