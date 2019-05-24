package com.mwc.docportal.Scanning.enhance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mwc.docportal.MainActivity;
import com.mwc.docportal.R;

import com.mwc.docportal.Scanning.camera.Scanning_List_Activity;
import com.mwc.docportal.Scanning.model.DocumentManager;
import com.mwc.docportal.Scanning.model.Page;
import com.thegrizzlylabs.geniusscan.sdk.core.ImageType;
import com.thegrizzlylabs.geniusscan.sdk.core.RotationAngle;
import com.thegrizzlylabs.geniusscan.sdk.core.Scan;


public class ImageProcessingActivity extends Activity {

   @SuppressWarnings("unused")
   private final static String TAG = ImageProcessingActivity.class.getSimpleName();

   public final static String EXTRA_PAGE = "page";

   private ImageView imageView;
   private ImageView distortionCorrectionButton;
   private ProgressDialog progressDialog;
   private Page page;
   Context context = this;
   ImageView savePageImageview, changeEnhancementImageview, rotateImageView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      page = getIntent().getParcelableExtra(EXTRA_PAGE);
      page.setImageType(ImageType.NONE);

      setContentView(R.layout.image_processing_activity);
      imageView = findViewById(R.id.image_view);
      savePageImageview = (ImageView) findViewById(R.id.savePageImageview);
      changeEnhancementImageview = (ImageView) findViewById(R.id.changeEnhancementImageview);
      rotateImageView = (ImageView) findViewById(R.id.rotateImageView);
      distortionCorrectionButton = findViewById(R.id.distortion_correction_button);


      onClickListeners();
   }

   private void onClickListeners()
   {
      savePageImageview.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            DocumentManager.getInstance(context).addPage(page);
            Intent intent = new Intent(context, Scanning_List_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
         }
      });

      changeEnhancementImageview.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.enhancement_dialog_title)
                    .setItems(new CharSequence[]{
                            getString(R.string.image_type_none),
                            getString(R.string.image_type_color),
                            getString(R.string.image_type_whiteboard),
                            getString(R.string.image_type_black_white)
                    },new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                          ImageType imageType = new ImageType[] {
                                  ImageType.NONE,
                                  ImageType.COLOR,
                                  ImageType.WHITEBOARD,
                                  ImageType.BLACK_WHITE
                          }[which];
                          page.setImageType(imageType);
                          progressDialog.show();
                          enhance();
                       }
                    }).show();
         }
      });

      distortionCorrectionButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            page.setDistortionCorrectionEnabled(!page.isDistortionCorrectionEnabled());
            updateDistortionCorrectionButton();
            progressDialog.show();
            enhance();
         }
      });

      rotateImageView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            rotate(RotationAngle.ROTATION_90_CCW);
         }
      });
   }

   @Override
   protected void onResume() {
      super.onResume();
      updateDistortionCorrectionButton();
      displayScan(page.getOriginalImage());

      progressDialog = new ProgressDialog(context);
      progressDialog.setMessage("Processing...");
   //   progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
      progressDialog.show();
      enhance();
   }

   private void updateDistortionCorrectionButton() {
      distortionCorrectionButton.setImageResource(page.isDistortionCorrectionEnabled() ? R.mipmap.ic_grid_on_white_24dp : R.mipmap.ic_grid_off_white_24dp);
   }

   private void endEnhance() {
      displayEnhancedScan();
      progressDialog.dismiss();
   }

   private void endRotate() {
      displayEnhancedScan();
      progressDialog.dismiss();
   }

   public void changeEnhancement(View view) {
      new AlertDialog.Builder(this)
              .setTitle(R.string.enhancement_dialog_title)
              .setItems(new CharSequence[]{
                      getString(R.string.image_type_none),
                      getString(R.string.image_type_color),
                      getString(R.string.image_type_whiteboard),
                      getString(R.string.image_type_black_white)
              },new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
             ImageType imageType = new ImageType[] {
                     ImageType.NONE,
                     ImageType.COLOR,
                     ImageType.WHITEBOARD,
                     ImageType.BLACK_WHITE
             }[which];
             page.setImageType(imageType);
             progressDialog.show();
             enhance();
         }
      }).show();
   }

   public void toggleDistortionCorrection(View view) {
      page.setDistortionCorrectionEnabled(!page.isDistortionCorrectionEnabled());
      updateDistortionCorrectionButton();
      progressDialog.show();
      enhance();
   }

   private void rotate(RotationAngle angle) {
      progressDialog.show();
      new RotateAsyncTask(this, angle) {
         @Override
         protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            endRotate();
         }
      }.execute(page);
   }

   private void enhance() {
      new EnhanceAsyncTask(this) {
         @Override
         protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            endEnhance();
         }
      }.execute(page);
   }

   private void displayEnhancedScan() {
      displayScan(page.getEnhancedImage());
   }

   private void displayScan(Scan scan) {
      Options opts = new Options();
      opts.inSampleSize = 2;
      Bitmap bitmap = BitmapFactory.decodeFile(scan.getAbsolutePath(this), opts);
      imageView.setImageBitmap(bitmap);
      imageView.invalidate();
   }

   public void rotateLeft(View view) {
      rotate(RotationAngle.ROTATION_90_CCW);
   }

   public void rotateRight(View view) {
      rotate(RotationAngle.ROTATION_90_CW);
   }

   public void savePage(View view) {
      DocumentManager.getInstance(this).addPage(page);
      Intent intent = new Intent(this, Scanning_List_Activity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
      finish();
   }

}
