package com.vincent.filepicker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vincent.filepicker.Constant;
import com.vincent.filepicker.DividerListItemDecoration;
import com.vincent.filepicker.R;
import com.vincent.filepicker.adapter.FolderListAdapter;
import com.vincent.filepicker.adapter.NormalFilePickAdapter;
import com.vincent.filepicker.adapter.OnSelectStateListener;
import com.vincent.filepicker.filter.FileFilter;
import com.vincent.filepicker.filter.callback.FilterResultCallback;
import com.vincent.filepicker.filter.entity.Directory;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent Woo
 * Date: 2016/10/26
 * Time: 10:14
 */

public class NormalFilePickActivity extends BaseActivity {
    public static final int DEFAULT_MAX_NUMBER = 9;
    public static final String SUFFIX = "Suffix";
  //  private int mMaxNumber;
    private int mCurrentNumber = 0;
    private RecyclerView mRecyclerView;
    private NormalFilePickAdapter mAdapter;
    private ArrayList<NormalFile> mSelectedList = new ArrayList<>();
    private List<Directory<NormalFile>> mAll;
    private ProgressBar mProgressBar;
    private String[] mSuffix;

    private TextView tv_count;
    private TextView tv_folder;
    private LinearLayout ll_folder;
    private RelativeLayout rl_done;
    private RelativeLayout tb_pick;
    LinearLayout emptyView;
    @Override
    void permissionGranted() {
        loadData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vw_activity_file_pick);

    //    mMaxNumber = getIntent().getIntExtra(Constant.MAX_NUMBER, DEFAULT_MAX_NUMBER);
        mSuffix = getIntent().getStringArrayExtra(SUFFIX);
        initView();
    }

    private void initView() {
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_count.setText(mCurrentNumber + " selected");

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_file_pick);
        emptyView  =(LinearLayout) findViewById(R.id.empty_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerListItemDecoration(this,
                LinearLayoutManager.VERTICAL, R.drawable.vw_divider_rv_file));
        mAdapter = new NormalFilePickAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnSelectStateListener(new OnSelectStateListener<NormalFile>() {
            @Override
            public void OnSelectStateChanged(boolean state, NormalFile file) {
                if (state) {
                    mSelectedList.add(file);
                    mCurrentNumber++;
                } else {
                    mSelectedList.remove(file);
                    mCurrentNumber--;
                }
                tv_count.setText(mCurrentNumber + " selected");
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.pb_file_pick);

        rl_done = (RelativeLayout) findViewById(R.id.rl_done);
        rl_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(Constant.RESULT_PICK_FILE, mSelectedList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        tb_pick = (RelativeLayout) findViewById(R.id.tb_pick);
        ll_folder = (LinearLayout) findViewById(R.id.ll_folder);
        if (isNeedFolderList) {
            ll_folder.setVisibility(View.VISIBLE);
            ll_folder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFolderHelper.toggle(tb_pick);
                }
            });
            tv_folder = (TextView) findViewById(R.id.tv_folder);
            tv_folder.setText(getResources().getString(R.string.vw_all));

            mFolderHelper.setFolderListListener(new FolderListAdapter.FolderListListener() {
                @Override
                public void onFolderListClick(Directory directory) {
                    mFolderHelper.toggle(tb_pick);
                    tv_folder.setText(directory.getName());

                    if (TextUtils.isEmpty(directory.getPath())) { //All
                        refreshData(mAll);
                    } else {
                        for (Directory<NormalFile> dir : mAll) {
                            if (dir.getPath().equals(directory.getPath())) {
                                List<Directory<NormalFile>> list = new ArrayList<>();
                                list.add(dir);
                                refreshData(list);
                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    private void loadData() {
        FileFilter.getFiles(this, new FilterResultCallback<NormalFile>() {
            @Override
            public void onResult(List<Directory<NormalFile>> directories) {
                // Refresh folder list
                if (isNeedFolderList) {
                    ArrayList<Directory> list = new ArrayList<>();
                    Directory all = new Directory();
                    all.setName(getResources().getString(R.string.vw_all));
                    list.add(all);
                    list.addAll(directories);
                    mFolderHelper.fillData(list);
                }

                mAll = directories;
                refreshData(directories);
            }
        }, mSuffix);
    }

    private void refreshData(List<Directory<NormalFile>> directories) {
        mProgressBar.setVisibility(View.GONE);
        List<NormalFile> list = new ArrayList<>();
        for (Directory<NormalFile> directory : directories) {

           /* if(directory.getName().equalsIgnoreCase(".Doc_Portal")) {
            }
            else {
                list.addAll(directory.getFiles());
            }*/
            list.addAll(directory.getFiles());
        }


        List<NormalFile> filesList = new ArrayList<>();
        File directoryData = new File(Environment.getExternalStorageDirectory().toString()+"/.Doc_Portal");

        File[] files = directoryData.listFiles();
        Log.d("Files", "Size: "+ files.length);

        List<File> getFilePathsList = new ArrayList<>();
        if(files != null && files.length > 0)
        {
            for(File fileData : files)
            {
                List<File> localFilelist = getListFiles(fileData);
                getFilePathsList.addAll(localFilelist);
            }
        }

        if(getFilePathsList != null && getFilePathsList.size() > 0 && list.size() >0)
        {
            for(NormalFile filedata : list)
            {
                boolean isFound = false;

                for(File originalData : getFilePathsList)
                {
                    if(originalData.getPath() != null && filedata.getPath().equalsIgnoreCase(originalData.getPath()))
                    {
                        isFound = true;
                    }
                }

                if (!isFound) {
                    filesList.add(filedata);
                }
            }
        }
        else
        {
            filesList.addAll(list);
        }


        for (NormalFile file : mSelectedList) {
            int index = filesList.indexOf(file);
            if (index != -1) {
                filesList.get(index).setSelected(true);
            }
        }

        if(filesList != null && filesList.size() > 0)
        {
            emptyView.setVisibility(View.GONE);
            mAdapter.refresh(filesList);
        }
        else
        {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        if(files != null && files.length > 0)
        {
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(getListFiles(file));
                } else {
                    inFiles.add(file);
                }
            }
        }
        return inFiles;
    }

}
