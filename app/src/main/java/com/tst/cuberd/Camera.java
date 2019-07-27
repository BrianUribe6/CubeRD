package com.tst.cuberd;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class Camera extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    Mat mRgba, imgGrey, hierarchy;
    List<MatOfPoint> contours;
    JavaCameraView cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    Button captureButton;
    private static final int CAM_REQ = 441;
    private static final String TAG = "CameraCapture";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
//        getSupportActionBar().hide();
        cameraBridgeViewBase = findViewById(R.id.camera_view);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        captureButton = findViewById(R.id.capture_btn);

        //Requesting user permission to use Camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA
            }, CAM_REQ);
        }

        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                if(status == BaseLoaderCallback.SUCCESS){
                    cameraBridgeViewBase.enableView();
                }else{
                    super.onManagerConnected(status);
                }
            }
        };
    }
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(width, height, CvType.CV_8UC4);
        imgGrey = new Mat(width, height, CvType.CV_8UC1);

    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        imgGrey.release();
        hierarchy.release();
}
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

//        Mat cannyOutput = new Mat();
////        //FIXME Threshold1 and Threshold2 values must be tweaked
////        Imgproc.Canny(imgGrey, cannyOutput, 85, 255);
////
////        contours = new ArrayList<>();
////        hierarchy = new Mat();
////        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
////
////        Scalar color = new Scalar(255,0, 0);
//////        Imgproc.drawContours(mRgba, contours, -1, color, 5); // this would do the same as the bottom code
////        // this implementation could help to draw specific contours
////        for (int i = 0; i < contours.size(); i++) {
////            Imgproc.drawContours(mRgba, contours, i, color, 5);
////        }
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test(mRgba);

            }
        });

        return mRgba;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            Toast.makeText(this, "OpenCV was unable to open", Toast.LENGTH_SHORT).show();
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, baseLoaderCallback);
        }
        else{
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null){
            cameraBridgeViewBase.disableView();
        }
    }
    private Mat test(Mat frame){
        Log.d(TAG, "test: Taking Picture");
        Imgproc.cvtColor(frame, imgGrey, CvType.CV_8UC1);
        contours = new ArrayList<>();
        hierarchy = new Mat();

        Mat cannyOutput = new Mat();
        Imgproc.Canny(imgGrey, cannyOutput, 85, 255);
        Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Scalar color = new Scalar(255,0, 0);
//        Imgproc.drawContours(mRgba, contours, -1, color, 5); // this would do the same as the bottom code
        // this implementation could help to draw specific contours
        for (int i = 0; i < contours.size(); i++) {
            Imgproc.drawContours(frame, contours, i, color, 5);
        }
        Log.d(TAG, "test: returning frame");
        return cannyOutput;
    }
}
