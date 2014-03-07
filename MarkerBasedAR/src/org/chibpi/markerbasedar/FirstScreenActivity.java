package org.chibpi.markerbasedar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
 
public class FirstScreenActivity extends Activity {
    private static final String TAG = "MarkerBaseAR:FirstScreenActivity";
	// Initializing variables
    EditText inputName;
    EditText inputEmail;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen1);
 
        Button btnCalibrate = (Button) findViewById(R.id.buttonCalibrate);
        Button btnDoAR = (Button) findViewById(R.id.buttonDoAR);
        SharedPreferences sharedPref = this.getSharedPreferences("CameraCalibration",Context.MODE_PRIVATE);
        if (sharedPref.getFloat("0", -1) == -1) {
            Log.i(TAG, "No previous calibration results found");
            btnDoAR.setEnabled(false);
        }
        else {
        	btnDoAR.setEnabled(true);
        }

        //Listening to button event
        btnCalibrate.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View arg0) {
                //Starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), CameraCalibrationActivity.class);
 
                startActivity(nextScreen);
 
            }
        });
        btnDoAR.setOnClickListener(new View.OnClickListener() {
        	 
            public void onClick(View arg0) {
                //Starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), MbarActivity.class);
 
                startActivity(nextScreen);
 
            }
        });
    }
    public void onResume() {
    	super.onResume();
        Button btnDoAR = (Button) findViewById(R.id.buttonDoAR);
        SharedPreferences sharedPref = this.getSharedPreferences("CameraCalibration",Context.MODE_PRIVATE);
        if (sharedPref.getFloat("0", -1) == -1) {
            Log.i(TAG, "No previous calibration results found");
            btnDoAR.setEnabled(false);
        }
        else {
        	btnDoAR.setEnabled(true);
        }
    }
}