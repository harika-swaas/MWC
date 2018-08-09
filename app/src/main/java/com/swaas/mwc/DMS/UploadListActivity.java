package com.swaas.mwc.DMS;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;

import com.swaas.mwc.R;

/**
 * Created by barath on 8/8/2018.
 */

public class UploadListActivity extends Activity{

    ImageView back;
    ImageView add;
    ImageView upload;
    ListView upload_list;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_list);
        back = (ImageView)findViewById(R.id.back_image_view);
        add = (ImageView)findViewById(R.id.add);
        upload = (ImageView)findViewById(R.id.uploadlist);
        upload_list = (ListView) findViewById(R.id.list_upload);
    }
}
