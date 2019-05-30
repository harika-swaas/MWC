package com.mwc.docportal.DMS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mwc.docportal.API.Model.ColorCodeModel;
import com.mwc.docportal.API.Model.UploadModel;
import com.mwc.docportal.Common.CommonFunctions;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;

/**
 * Created by barath on 8/9/2018.
 */

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {


    Context context;
    List<UploadModel> uploadList;
    int pos;
    AlertDialog mCustomAlertDialog;
    public UploadListAdapter(UploadListActivity context, List<UploadModel> uploadList) {
        this.context = context;
        this.uploadList = uploadList;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitemupload, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v); // pass the view to View Holder
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if(uploadList!=null && uploadList.size()>0) {
            /*uploadList = PreferenceUtils.getupload(context,"key");*/
          //  String filename = uploadList.get(position).getFilePath().substring(uploadList.get(position).getFilePath().lastIndexOf("/") + 1);
            String fileName =  uploadList.get(position).getFileName();
            String extension = uploadList.get(position).getFilePath().substring(uploadList.get(position).getFilePath().lastIndexOf(".")+1);

            if (fileName.indexOf(".") > 0)
                fileName = fileName.substring(0, fileName.lastIndexOf("."));

            holder.edit_filename.setText(fileName);

            holder.extension_name.setText("."+extension);
            holder.thumbnailView.setVisibility(View.VISIBLE);

            ColorCodeModel colorCodeModel = CommonFunctions.getColorCodesforFileType(extension);
            if(colorCodeModel != null)
            {
                holder.thumbnailIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getPrimaryColor()));
                holder.thumbnailCornerIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getSecondaryColor()));
                holder.thumbnailText.setText(colorCodeModel.getFileType());
            }

            holder.done.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.upload_success_color)));
            holder.failureIcon.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.magenta)));
            holder.edit_filename.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            holder.edit_filename.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                   /* String existingFilename = uploadList.get(position).getFilePath().substring(uploadList.get(position).getFilePath().lastIndexOf("/") + 1);
                    String newFileName = uploadList.get(position).getFilePath().replace(existingFilename, holder.edit_filename.getText().toString().trim()+
                            holder.extension_name.getText().toString());
                    uploadList.get(position).setFilePath(newFileName);*/

                   if(holder.edit_filename.getText().toString().trim().equals(""))
                   {
                       uploadList.get(position).setFileName("");
                       DrawableCompat.setTint(holder.edit_filename.getBackground(), ContextCompat.getColor(context, R.color.dark_red));
                   }
                   else
                   {
                       uploadList.get(position).setFileName(holder.edit_filename.getText().toString().trim()+
                               holder.extension_name.getText().toString());
                       DrawableCompat.setTint(holder.edit_filename.getBackground(), ContextCompat.getColor(context, R.color.sky_blue));
                   }


                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });




           /* for(int i= 0;i<uploadList.size();i++) {
                    if(i==pos) {
                        if (activateDone) {
                            holder.done.setVisibility(View.VISIBLE);
                            holder.progress.setVisibility(View.INVISIBLE);
                            holder.delete.setVisibility(View.INVISIBLE);

                        } else {
                            holder.done.setVisibility(View.INVISIBLE);

                        }
                        if (activateLoad) {
                            holder.progress.setVisibility(VISIBLE);
                            holder.delete.setVisibility(View.INVISIBLE);

                        } else {
                            holder.progress.setVisibility(View.INVISIBLE);

                        }
                    }
            }*/

            if(uploadList.get(position).isSuccess() == true)
            {
                holder.done.setVisibility(View.VISIBLE);
                holder.progress.setVisibility(View.INVISIBLE);
                holder.delete.setVisibility(View.INVISIBLE);
                holder.failureIcon.setVisibility(View.INVISIBLE);
            }
            else if(uploadList.get(position).isInProgress() == true)
            {
                holder.done.setVisibility(View.INVISIBLE);
                holder.progress.setVisibility(View.VISIBLE);
                holder.delete.setVisibility(View.INVISIBLE);
                holder.failureIcon.setVisibility(View.INVISIBLE);
            }
            else if(uploadList.get(position).isYetToStart() == true)
            {
                holder.done.setVisibility(View.INVISIBLE);
                holder.progress.setVisibility(View.INVISIBLE);
                holder.delete.setVisibility(View.INVISIBLE);
                holder.failureIcon.setVisibility(View.INVISIBLE);
            }
            else if(uploadList.get(position).isFailure() == true)
            {
                holder.done.setVisibility(View.INVISIBLE);
                holder.progress.setVisibility(View.INVISIBLE);
                holder.delete.setVisibility(View.INVISIBLE);
                holder.failureIcon.setVisibility(View.VISIBLE);

            }
            else
            {
                holder.done.setVisibility(View.INVISIBLE);
                holder.progress.setVisibility(View.INVISIBLE);
                holder.failureIcon.setVisibility(View.INVISIBLE);
                holder.delete.setVisibility(View.VISIBLE);
            }




            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        // Toast.makeText(UploadListActivity.this, String.valueOf(size1) + "files were not uploaded", Toast.LENGTH_SHORT).show();

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.custom_dialog, null);
                        builder.setView(view);
                        builder.setCancelable(false);

                        final Button BtnAllow = (Button) view.findViewById(R.id.allow_button);
                        BtnAllow.setText("Remove");
                        final Button BtnCancel = (Button) view.findViewById(R.id.cancel_button);
                        TextView textView =(TextView) view.findViewById(R.id.txt_message);
                        textView.setVisibility(View.GONE);
                        TextView text = (TextView) view.findViewById(R.id.message);
                      //  text.setText("Are you sure you want to delete "+ uploadList.get(position).substring(uploadList.get(position).lastIndexOf("/") + 1));
                        text.setText("Do you want to remove?");
                        mCustomAlertDialog = builder.create();
                        mCustomAlertDialog.show();
                        BtnAllow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mCustomAlertDialog.dismiss();
                                uploadList= PreferenceUtils.getImageUploadList(context,"key");
                                uploadList.remove(position);
                                PreferenceUtils.setImageUploadList(context,uploadList,"key");
                                Intent intent = new Intent(context,UploadListActivity.class);
                                context.startActivity(intent);
                            }
                        });

                        BtnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mCustomAlertDialog.dismiss();
                            }
                        });
                }
            });



        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView extension_name;// init the item view's
        RelativeLayout thumbnailView;
        ProgressBar progress;
        ImageView done, failureIcon;;
        ImageView thumbnailIcon;
        ImageView thumbnailCornerIcon;
        TextView thumbnailText;
        ImageView delete;
        EditText edit_filename;

        public ViewHolder(final View itemView) {

            super(itemView);
            // get the reference of item view's
            extension_name = (TextView) itemView.findViewById(R.id.extension_name);
            thumbnailView = (RelativeLayout)itemView.findViewById(R.id.thumbnail_layout);
             progress=(ProgressBar) itemView.findViewById(R.id.progressBar12);
            done=(ImageView)itemView.findViewById(R.id.done);
            progress.setVisibility(View.INVISIBLE);
            done.setVisibility(View.INVISIBLE);
            thumbnailIcon = (ImageView)itemView.findViewById(R.id.thumbnail_image);
            thumbnailCornerIcon=(ImageView)itemView.findViewById(R.id.thumbnail_corner_image);
            thumbnailText=(TextView) itemView.findViewById(R.id.thumbnail_text);
            delete=(ImageView) itemView.findViewById(R.id.delete);
            failureIcon = (ImageView)itemView.findViewById(R.id.failure);
            failureIcon.setVisibility(View.INVISIBLE);
            edit_filename = (EditText) itemView.findViewById(R.id.edit_filename);
        }

    }
    @Override
    public int getItemCount() {
        return uploadList.size();
    }
}
