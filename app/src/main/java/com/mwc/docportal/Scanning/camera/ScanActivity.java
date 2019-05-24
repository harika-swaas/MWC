package com.mwc.docportal.Scanning.camera;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;

import com.github.clans.fab.FloatingActionMenu;
import com.mwc.docportal.R;
import com.mwc.docportal.Scanning.model.Page;
import com.mwc.docportal.Scanning.processing.BorderDetectionActivity;
import com.thegrizzlylabs.geniusscan.sdk.camera.CameraManager;
import com.thegrizzlylabs.geniusscan.sdk.camera.FocusIndicator;
import com.thegrizzlylabs.geniusscan.sdk.camera.ScanFragment;
import com.thegrizzlylabs.geniusscan.sdk.camera.realtime.BorderDetector;
import com.thegrizzlylabs.geniusscan.sdk.core.GeniusScanLibrary;
import com.thegrizzlylabs.geniusscan.sdk.core.LicenseException;
import com.thegrizzlylabs.geniusscan.sdk.core.QuadrangleAnalyzeResult;
import com.thegrizzlylabs.geniusscan.sdk.core.RotationAngle;
import com.thegrizzlylabs.geniusscan.sdk.core.ScanContainer;


public class ScanActivity extends FragmentActivity implements ScanFragment.CameraCallbackProvider {
   private static final String TAG = ScanActivity.class.getSimpleName();
   private static final int PERMISSION_REQUEST_CODE = 1;

   private ScanFragment scanFragment;
   private Page page;

   private boolean cameraPermissionGranted = false;
   Context context = this;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // Go full screen
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setContentView(R.layout.scanning_activity);

      final FloatingActionMenu captureButton = findViewById(R.id.captureButton);

      captureButton.setOnMenuButtonClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            takePicture();
         }
      });
      /*captureButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            takePicture();
         }
      });*/

      FocusIndicator focusIndicator = findViewById(R.id.focus_indicator);

      scanFragment = (ScanFragment) getSupportFragmentManager().findFragmentById(R.id.scan_fragment);
      scanFragment.setRealTimeDetectionOverlayColor(R.color.app_color_primary_dark);
      scanFragment.setPreviewAspectFill(false);
      scanFragment.setRealTimeDetectionEnabled(true);
      scanFragment.setFocusIndicator(focusIndicator);
      scanFragment.setAutoTriggerAnimationEnabled(true);
      scanFragment.setBorderDetectorListener(new BorderDetector.BorderDetectorListener() {
         @Override
         public void onTriggerRequested() {
            takePicture();
         }

         @Override
         public void onBorderDetectionResult(QuadrangleAnalyzeResult result) {

         }
      });

      page = new Page();

      cameraPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
      if (!cameraPermissionGranted) {
         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
      }
   }

   @Override
   protected void onResume() {
      super.onResume();

      if (cameraPermissionGranted) {
         scanFragment.initializeCamera();
      }
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      cameraPermissionGranted = requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
      // Camera will be initialized in onResume
   }

   private void takePicture() {
      scanFragment.takePicture(page);
   }

   @Override
   public CameraManager.Callback getCameraManagerCallback() {
      return new CameraManager.Callback() {
         @Override
         public void onCameraReady(CameraManager cameraManager, Camera camera) {}

         @Override
         public void onCameraFailure(CameraManager cameraManager) {}

         @Override
         public void onShutterTriggered(CameraManager cameraManager) {}

         @Override
         public void onPictureTaken(CameraManager cameraManager, int cameraOrientation, ScanContainer scanContainer) {
            new RotateTask(scanContainer, RotationAngle.fromDegrees(cameraOrientation)).execute();
         }
      };
   }

   class RotateTask extends AsyncTask<Void, Void, Void> {

      private RotationAngle rotationAngle;
      private ScanContainer scanContainer;
      private ProgressDialog progressDialog;
      private Exception exception = null;

      RotateTask(ScanContainer scanContainer, RotationAngle rotationAngle) {
         this.rotationAngle = rotationAngle;
         this.scanContainer = scanContainer;
      }

      @Override
      protected void onPreExecute() {
         super.onPreExecute();
         progressDialog = new ProgressDialog(context);
         progressDialog.setMessage("Processing...");
      //   progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
         progressDialog.show();
      }

      @Override
      protected Void doInBackground(Void... params) {
         String path = scanContainer.getOriginalImage().getAbsolutePath(ScanActivity.this);
         if (rotationAngle != RotationAngle.ROTATION_0) {
            try {
               GeniusScanLibrary.rotateImage(path, path, rotationAngle);
            } catch (Exception e) {
               exception = e;
            }
         }

         return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
         super.onPostExecute(aVoid);
         progressDialog.dismiss();

         if (exception instanceof LicenseException) {
            new AlertDialog.Builder(ScanActivity.this)
                    .setMessage(exception.getMessage())
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
         } else if (exception != null) {
            throw new RuntimeException(exception);
         } else {
            Intent intent = new Intent(ScanActivity.this, BorderDetectionActivity.class);
            intent.putExtra(BorderDetectionActivity.EXTRA_PAGE, (Page) scanContainer);
            startActivity(intent);
         }
      }
   }

}