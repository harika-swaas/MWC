package com.mwc.docportal.Scanning.processing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.mwc.docportal.R;
import com.mwc.docportal.Scanning.enhance.ImageProcessingActivity;
import com.mwc.docportal.Scanning.model.Page;
import com.thegrizzlylabs.geniusscan.sdk.ui.BorderDetectionImageView;
import com.thegrizzlylabs.geniusscan.sdk.ui.MagnifierBorderDetectionListener;
import com.thegrizzlylabs.geniusscan.sdk.ui.MagnifierView;

public class BorderDetectionActivity extends Activity {

   @SuppressWarnings("unused")
   private static final String TAG = BorderDetectionActivity.class.getSimpleName();

   private Page page;

   private ProgressDialog progressDialog;
   private BorderDetectionImageView imageView;
   private MagnifierView magnifierView;

   public final static String EXTRA_PAGE = "page";
   Context context = this;

   ImageView quardrange_imageView, selectImageView;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.border_detection_activity);

      page = getIntent().getParcelableExtra(EXTRA_PAGE);

      imageView = findViewById(R.id.image_view);
      imageView.setColor(R.color.app_color_primary_dark);

      magnifierView = findViewById(R.id.magnifier_view);
      imageView.setListener(new MagnifierBorderDetectionListener(magnifierView));

      quardrange_imageView = findViewById(R.id.quardrange_imageView);
      selectImageView = findViewById(R.id.selectImageView);

      quardrange_imageView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

            setQuadrangleToFullImage();
         }
      });
      selectImageView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = new Intent(context, ImageProcessingActivity.class);
            intent.putExtra(ImageProcessingActivity.EXTRA_PAGE, page);
            startActivity(intent);
         }
      });

   }

   @Override
   protected void onResume() {
      super.onResume();
      String filename = page.getOriginalImage().getAbsolutePath(this);
      BitmapFactory.Options opts = new BitmapFactory.Options();
      opts.inSampleSize = 2;

      Bitmap bitmap = BitmapFactory.decodeFile(filename, opts);
      imageView.setImageBitmap(bitmap);
      magnifierView.setBitmap(bitmap);

      progressDialog = new ProgressDialog(context);
      progressDialog.setMessage("Processing...");
    //  progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
      progressDialog.show();
      new AnalyzeAsyncTask(this) {
         @Override
         protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            endAnalyze();
         }
      }.execute(page);
   }

   protected void endAnalyze() {
      progressDialog.dismiss();
      addQuadrangleToView();
   }

   void addQuadrangleToView() {
      imageView.setQuad(page.getQuadrangle());
      imageView.invalidate();
   }

   public void setQuadrangleToFullImage() {
      page.getQuadrangle().setToFullImage();
      imageView.invalidate();
   }



   public void select(View view) {
      Intent intent = new Intent(this, ImageProcessingActivity.class);
      intent.putExtra(ImageProcessingActivity.EXTRA_PAGE, page);
      startActivity(intent);
   }

}
