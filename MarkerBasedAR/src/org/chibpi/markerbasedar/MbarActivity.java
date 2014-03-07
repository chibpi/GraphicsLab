package org.chibpi.markerbasedar;



import java.util.Arrays;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;


import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;


import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;

public class MbarActivity extends Activity implements CvCameraViewListener2 {

    private static final String    TAG                 = "Mbar::Activity";

    private Mat                    mRgba;

    private	MarkerDetector         mNativeDetector;



    private CameraBridgeViewBase   mOpenCvCameraView;
    private GLClearRenderer        mRenderer;
    
    private int ctr=0;
    private float fx =472.8612f;
    private float fy= 472.8612f;
    private float cx= 239.5f;
    private float cy= 159.5f;
    private float dist[]={0.26940343f,-2.5384436f,.0f,.0f,6.5350184f};
    private double ddist[]=new double[5];
    private Mat cameraMatrix;
    private Mat distortionCoefficients;
   
    

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {

		@Override
        
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    cameraMatrix=new Mat();
                    distortionCoefficients=new Mat();
                    Mat.eye(3, 3, CvType.CV_64FC1).copyTo(cameraMatrix);
                    Mat.zeros(5, 1, CvType.CV_64FC1).copyTo(distortionCoefficients);
                    if (CalibrationResult.tryLoad((Activity)this.mAppContext, cameraMatrix, distortionCoefficients)){
            	        double cameraMatrixValues[]=new double[cameraMatrix.cols()*cameraMatrix.rows()];
            	        cameraMatrix.get(0,0,cameraMatrixValues);
            	        fx=(float) cameraMatrixValues[0];
            	        fy=(float) cameraMatrixValues[4];
            	        cx=(float) cameraMatrixValues[2];
            	        cy=(float) cameraMatrixValues[5];
            	        distortionCoefficients.get(0,0,ddist);
            	        for (int i=0; i< ddist.length; i++) {
            	        	dist[i]=(float) ddist[i];
            	        }
                    }
                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("marker_based_ar");

                    mNativeDetector = new MarkerDetector(fx,fy,cx,cy,dist);
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MbarActivity() {
  

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.marker_based_ar_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.mbar_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
        // Now let's create an OpenGL surface.
        GLSurfaceView glView = (GLSurfaceView) findViewById(R.id.glview);;
        // To see the camera preview, the OpenGL surface has to be created translucently.
        // See link above.
        glView.setEGLConfigChooser( 8, 8, 8, 8, 16, 0 );
        glView.getHolder().setFormat( PixelFormat.TRANSLUCENT );
        // The renderer will be implemented in a separate class, GLView, which I'll show next.
        mRenderer=new GLClearRenderer();
        glView.setRenderer( mRenderer );
        // Now set this as the main view.
        //addContentView( glView );
        
        glView.setZOrderOnTop(true);
     
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
    	Log.i(TAG, "called onResume");
        super.onResume();


        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();
        
        //Test read frames from file
        Log.i(TAG,"Build.PRODUCT: "+Build.PRODUCT);
        if ("sdk".equals( Build.PRODUCT )) {
        	mRgba = Highgui.imread(Environment.getExternalStorageDirectory().getPath()+"/marker-"+((ctr)%277)+".jpg");
        	ctr+=10;
        }
        mNativeDetector.process(mRgba);
        Log.i(TAG,"mRenderer: "+ mRenderer.toString()+
        		" transformations: "+Arrays.toString(mNativeDetector.getTransformations()));
        float projectionMatrix[]=buildProjectionMatrix(inputFrame.rgba().width(),inputFrame.rgba().height());
        mRenderer.setProjectionMatrix(projectionMatrix);
        mRenderer.setTransformations(mNativeDetector.getTransformations());
        return mRgba;
    }

    private float[] buildProjectionMatrix(int screen_width, int screen_height) {
    	  float projectionMatrix[]=new float[16];
    	  float near = 0.01f;  // Near clipping distance
    	  float far = 100f;  // Far clipping distance

    	  // Camera parameters

    	  projectionMatrix[0] =  - 2.0f * fx / screen_width;
    	  projectionMatrix[1] = 0.0f;
    	  projectionMatrix[2] = 0.0f;
    	  projectionMatrix[3] = 0.0f;

    	  projectionMatrix[4] = 0.0f;
    	  projectionMatrix[5] = 2.0f * fy / screen_height;
    	  projectionMatrix[6] = 0.0f;
    	  projectionMatrix[7] = 0.0f;

    	  projectionMatrix[8] = 2.0f * cx / screen_width - 1.0f;
    	  projectionMatrix[9] = 2.0f * cy / screen_height - 1.0f;
    	  projectionMatrix[10] = -( far+near ) / ( far - near );
    	  projectionMatrix[11] = -1.0f;

    	  projectionMatrix[12] = 0.0f;
    	  projectionMatrix[13] = 0.0f;
    	  projectionMatrix[14] = -2.0f * far * near / ( far - near );
    	  projectionMatrix[15] = 0.0f;

    	
		return projectionMatrix;
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.mbar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        switch (item.getItemId()) {

        
		    case R.id.back_to_menu:
		    	Intent nextScreen = new Intent(getApplicationContext(), FirstScreenActivity.class);
		    	 
		        startActivity(nextScreen);
		    	finish();
		    	return true;
        }
        return true;
    }



 
}
