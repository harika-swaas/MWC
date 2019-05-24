package com.mwc.docportal.Scanning.camera;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.mwc.docportal.API.Model.UploadModel;
import com.mwc.docportal.BuildConfig;
import com.mwc.docportal.DMS.NavigationMyFolderActivity;
import com.mwc.docportal.DMS.UploadListActivity;
import com.mwc.docportal.GridAutofitLayoutManager;
import com.mwc.docportal.MainActivity;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;
import com.mwc.docportal.Scanning.enhance.PdfGenerationTask;
import com.mwc.docportal.Scanning.model.DocumentManager;
import com.mwc.docportal.Scanning.model.Page;
import com.mwc.docportal.Utils.DateHelper;
import com.thegrizzlylabs.geniusscan.sdk.core.GeniusScanLibrary;
import com.thegrizzlylabs.geniusscan.sdk.core.LicenseException;
import com.thegrizzlylabs.geniusscan.sdk.pdf.PDFGeneratorError;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Scanning_List_Activity extends AppCompatActivity implements OnStartDragListener{


    FloatingActionMenu addingPages;
    RecyclerView image_recycler_view;
    Context context = this;
    ImageViewAdapter imageViewAdapter;
    ImageView fullImageView;
    RelativeLayout imageList_layout;
    String currentImagePath;

    ItemTouchHelper mItemTouchHelper;
    TextView empty_list_data;
    LinearLayout save_pdf_layout;
    List<Page> finalPDFList;
    TextView clear_txt, save_txt;
    Toolbar toolbar;
    boolean isFromMainPage;
    android.app.AlertDialog mAlertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning_list);

        finalPDFList = new ArrayList<>();
        initSDK();
        initializeViews();
        setUpToolbar();
        onClickListeners();

        if(getIntent() != null)
        {
            isFromMainPage = getIntent().getBooleanExtra("IsFromMainPage", false);
        }

        if(isFromMainPage)
        {
            startActivity(new Intent(Scanning_List_Activity.this, ScanActivity.class));
        }

    }

    private void onClickListeners()
    {
        addingPages.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Scanning_List_Activity.this, ScanActivity.class));
            }
        });

        save_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   showFileNameEditAlertDialog();
                generatePDFAndUpload();
            }
        });

        clear_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentManager.getInstance(Scanning_List_Activity.this).getPages().clear();
                imageViewAdapter.notifyDataSetChanged();
                invalidateOptionsMenu();
                save_pdf_layout.setVisibility(View.GONE);
                empty_list_data.setVisibility(View.VISIBLE);
                image_recycler_view.setVisibility(View.GONE);

            }
        });


    }

    private void showFileNameEditAlertDialog()
    {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.editname_alert, null);
        builder.setView(view);
        builder.setCancelable(false);

        Button cancel = (Button) view.findViewById(R.id.cancel_button);
        Button allow = (Button) view.findViewById(R.id.ok_button);
        final EditText filename = (EditText) view.findViewById(R.id.edit_filename);
        String imagePath = DateHelper.getCurrentTime();
        filename.setText("Scan "+imagePath);
      //  Selection.setSelection(filename.getText(), filename.getText().length());
        filename.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        /*filename.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().contains(".pdf")){
                    filename.setText(".pdf");
                    Selection.setSelection(filename.getText(), filename.getText().length());
                }
            }
        });
*/

        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editFileName = filename.getText().toString().trim();
                if(editFileName != null && !editFileName.isEmpty())
                {
                    generatePDFAndUpload();
                }
                else
                {
                    Toast.makeText(context, getString(R.string.filename_txt), Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });

        mAlertDialog = builder.create();
        mAlertDialog.show();


    }

    private void initializeViews()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        image_recycler_view = (RecyclerView)findViewById(R.id.image_recycler_view);
        image_recycler_view.setNestedScrollingEnabled(false);
        addingPages = (FloatingActionMenu) findViewById(R.id.menu_camera_item);
        fullImageView = (ImageView) findViewById(R.id.fullImageView);
        imageList_layout = (RelativeLayout) findViewById(R.id.imageList_layout);
        empty_list_data = (TextView) findViewById(R.id.empty_list_data);
        save_pdf_layout = (LinearLayout) findViewById(R.id.save_pdf_layout);
        clear_txt = (TextView) findViewById(R.id.clear_txt);
        save_txt = (TextView) findViewById(R.id.save_txt);
    }

    private void setUpToolbar()
    {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.mipmap.ic_back));
        getSupportActionBar().setTitle("Document Scanning");
    }
    private void initSDK() {
        // This code shows how to initialize the SDK with a license key.
        // Without a license key, the SDK runs for 60 seconds and then the app needs to be restarted.

        try {
            // Replace this key by your key
            GeniusScanLibrary.init(this, "533c5005525702030257005839525a0e4a0e45071a525d5a1259461256546f0157025550080c040452");
        } catch(LicenseException e) {
            new AlertDialog.Builder(this)
                    .setMessage("This version is not valid anymore. Please update to the latest version.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    private void generatePDFAndUpload()
    {
        //  List<Page> pages = DocumentManager.getInstance(this).getPages();
         String imagePath = DateHelper.getCurrentTime();

        final File outputFile = new File(getExternalCacheDir(), "Scan "+imagePath+".pdf");
     //   final File outputFile = new File(getExternalCacheDir(), editFileName+".pdf");
        new PdfGenerationTask(this, finalPDFList, outputFile.getAbsolutePath(), false, new PdfGenerationTask.OnPdfGeneratedListener() {
            @Override
            public void onPdfGenerated(PDFGeneratorError result) {
                if (result != PDFGeneratorError.SUCCESS) {
                    Toast.makeText(context, "Error generating PDF: " + result, Toast.LENGTH_LONG).show();
                    return;
                }
                List<UploadModel> list_upload;
                if(PreferenceUtils.getImageUploadList(context,"key") != null && PreferenceUtils.getImageUploadList(context,"key").size() > 0)
                {
                    list_upload = PreferenceUtils.getImageUploadList(context,"key");
                }
                else
                {
                    list_upload = new ArrayList<>();
                }

                UploadModel uploadModel = new UploadModel();
                uploadModel.setFilePath(outputFile.getAbsolutePath());
                uploadModel.setObjectId(PreferenceUtils.getObjectId(context));
                list_upload.add(uploadModel);
                PreferenceUtils.setImageUploadList(context,list_upload,"key");

                Intent intent = new Intent (context, UploadListActivity.class);
                startActivity(intent);

                DocumentManager.getInstance(context).getPages().clear();
            }
        }).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        int pageCount = DocumentManager.getInstance(this).getPages().size();
        if(pageCount > 0)
        {
            empty_list_data.setVisibility(View.GONE);
            image_recycler_view.setVisibility(View.VISIBLE);
            save_pdf_layout.setVisibility(View.VISIBLE);
            List<Page> pages = DocumentManager.getInstance(this).getPages();
            setAdapter(pages);
        }
        else
        {
            save_pdf_layout.setVisibility(View.GONE);
            empty_list_data.setVisibility(View.VISIBLE);
            image_recycler_view.setVisibility(View.GONE);
        }

    }


    private void setAdapter(List<Page> pages)
    {
        finalPDFList = pages;
        ImageViewAdapter.OnItemClickListener listenerr = new ImageViewAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(String imagePath)
            {
                currentImagePath = imagePath;
                getSupportActionBar().setTitle("");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                fullImageView.setVisibility(View.VISIBLE);
                imageList_layout.setVisibility(View.GONE);
                File imgFile = new  File(imagePath);

                if(imgFile.exists()){

                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    fullImageView.setImageBitmap(myBitmap);
                }
                invalidateOptionsMenu();
            }
        };
        int mNoOfColumns = GridAutofitLayoutManager.calculateNoOfColumns(getApplicationContext());
      //  int mNoOfColumns = 3;
        image_recycler_view.setLayoutManager(new GridLayoutManager(context, mNoOfColumns));
        imageViewAdapter = new ImageViewAdapter(pages, context, listenerr, this);

        ItemTouchHelper.Callback callback =
                new EditItemTouchHelperCallback(imageViewAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(image_recycler_view);
        image_recycler_view.setAdapter(imageViewAdapter);
    }

    @Override
    public void onBackPressed() {
        if(getSupportActionBar().getTitle().equals(""))
        {
            fullImageView.setVisibility(View.GONE);
            imageList_layout.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Document Scanning");
        }
        else
        {
            DocumentManager.getInstance(context).getPages().clear();
            finish();
        }
        invalidateOptionsMenu();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        if(getSupportActionBar().getTitle().equals(""))
        {
            deleteItem.setVisible(true);
        }
        else
        {
            deleteItem.setVisible(false);
        }

        int positionOfMenuItem = 0;
        deleteItem = menu.getItem(positionOfMenuItem);
        SpannableString spannableString = new SpannableString("Delete");
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableString.length(), 0);
        deleteItem.setTitle(spannableString);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_delete:
                deleteParticularItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteParticularItem()
    {
        List<Page> pages = DocumentManager.getInstance(this).getPages();
        if(pages != null && pages.size() > 0)
        {
            for(Page pageItem : pages)
            {
                if(pageItem.getEnhancedImage().getAbsolutePath(context).equalsIgnoreCase(currentImagePath))
                {
                    DocumentManager.getInstance(this).deletePage(pageItem);
                    break;
                }
            }
            fullImageView.setVisibility(View.GONE);
            imageList_layout.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Document Scanning");
            imageViewAdapter.notifyDataSetChanged();
            invalidateOptionsMenu();
        }
        List<Page> currentListItems = DocumentManager.getInstance(this).getPages();
        if(currentListItems.size() == 0)
        {
            save_pdf_layout.setVisibility(View.GONE);
            empty_list_data.setVisibility(View.VISIBLE);
            image_recycler_view.setVisibility(View.GONE);
        }

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

}
