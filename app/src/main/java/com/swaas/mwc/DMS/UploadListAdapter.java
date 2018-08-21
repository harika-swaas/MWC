package com.swaas.mwc.DMS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;

import java.util.ArrayList;

import static android.view.View.VISIBLE;

/**
 * Created by barath on 8/9/2018.
 */

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {


    Context context;
    ArrayList<String> uploadList;
    private boolean activateDone;
    private boolean activateLoad;
    int pos;
    AlertDialog mCustomAlertDialog;
    public UploadListAdapter(UploadListActivity context, ArrayList<String> uploadList) {
        this.context=context;
        this.uploadList=uploadList;
    }

    public void ActivateDone(boolean activate,int position) {
        this.activateDone = activate;
        this.pos = position;
        notifyItemChanged(position);
    }

    public void ActivateLoad(boolean activate,int position) {
        this.activateLoad = activate;
        this.pos = position;
        notifyItemChanged(position);
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
            String filename = uploadList.get(position).substring(uploadList.get(position).lastIndexOf("/") + 1);
            String extension = uploadList.get(position).substring(uploadList.get(position).lastIndexOf(".")+1);
            holder.name.setText(filename);

            holder.thumbnailView.setVisibility(View.VISIBLE);
            if (extension.equalsIgnoreCase("pdf")) {
                //holder.imageView.setImageResource(R.mipmap.ic_pdf);
                holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pdf_color));
                holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pdf_corner_color));
                holder.thumbnailText.setText(extension);
            } else if (extension.equalsIgnoreCase("xlsx") ||
                    extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsm")
                    || extension.equalsIgnoreCase("csv")) {
                holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xls_color));
                holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xls_corner_color));
                holder.thumbnailText.setText(extension);
            } else if (extension.equalsIgnoreCase("doc") ||
                    extension.equalsIgnoreCase("docx") || extension.equalsIgnoreCase("docm")
                    || extension.equalsIgnoreCase("gdoc") || extension.equalsIgnoreCase("keynote")) {
                holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_doc_color));
                holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_doc_corner_color));
                holder.thumbnailText.setText(extension);
            } else if (extension.equalsIgnoreCase("ppt") ||
                    extension.equalsIgnoreCase("pptx") || extension.equalsIgnoreCase("pps")
                    || extension.equalsIgnoreCase("ai")) {
                holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_ppt_color));
                holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_ppt_corner_color));
                holder.thumbnailText.setText(extension);
            } else if (extension.equalsIgnoreCase("xml") ||
                    extension.equalsIgnoreCase("log") || extension.equalsIgnoreCase("zip")
                    || extension.equalsIgnoreCase("rar") || extension.equalsIgnoreCase("zipx")
                    || extension.equalsIgnoreCase("mht")) {
                holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_color));
                holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_corner_color));
                holder.thumbnailText.setText(extension);
            } else if (extension.equalsIgnoreCase("xml") ||
                    extension.equalsIgnoreCase("log") || extension.equalsIgnoreCase("rtf") ||
                    extension.equalsIgnoreCase("txt") || extension.equalsIgnoreCase("epub")) {
                holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_color));
                holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_xml_corner_color));
                holder.thumbnailText.setText(extension);
            } else if (extension.equalsIgnoreCase("msg") || extension.equalsIgnoreCase("dot") || extension.equalsIgnoreCase("odt")
                    || extension.equalsIgnoreCase("ott")) {
                holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_msg_color));
                holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_msg_corner_color));
                holder.thumbnailText.setText(extension);
            } else if (extension.equalsIgnoreCase("pages")) {
                holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pages_color));
                holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pages_corner_color));
                holder.thumbnailText.setText(extension);
            } else if (extension.equalsIgnoreCase("pub") || extension.equalsIgnoreCase("ods")) {
                holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pub_color));
                holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_pub_corner_color));
                holder.thumbnailText.setText(extension);
            } else if (extension.equalsIgnoreCase("gif") || extension.equalsIgnoreCase("jpeg")
                    || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("bmp")
                    || extension.equalsIgnoreCase("tif") || extension.equalsIgnoreCase("tiff") || extension.equalsIgnoreCase("eps")
                    || extension.equalsIgnoreCase("svg") || (extension.equalsIgnoreCase("odp")
                    || extension.equalsIgnoreCase("otp"))) {
                holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_gif_color));
                holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_gif_corner_color));
                holder.thumbnailText.setText(extension);
            } else if (extension.equalsIgnoreCase("avi")
                    || extension.equalsIgnoreCase("flv") || extension.equalsIgnoreCase("mpeg") ||
                    extension.equalsIgnoreCase("mpg") || extension.equalsIgnoreCase("swf") || extension.equalsIgnoreCase("wmv")) {
                holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_avi_color));
                holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_avi_corner_color));
                holder.thumbnailText.setText(extension);
            } else if (extension.equalsIgnoreCase("mp3")
                    || extension.equalsIgnoreCase("wav") || extension.equalsIgnoreCase("wma")) {
                holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_mp3_color));
                holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_mp3_corner_color));
                holder.thumbnailText.setText(extension);
            } else {
                holder.thumbnailIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_default_color));
                holder.thumbnailCornerIcon.setColorFilter(ContextCompat.getColor(context, R.color.thumbnail_default_corner_color));
                holder.thumbnailText.setText(extension);
            }
            for(int i= 0;i<uploadList.size();i++) {
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
            }
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    {
                        // Toast.makeText(UploadListActivity.this, String.valueOf(size1) + "files were not uploaded", Toast.LENGTH_SHORT).show();

                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.custom_dialog, null);
                        builder.setView(view);
                        builder.setCancelable(false);

                        final Button BtnAllow = (Button) view.findViewById(R.id.allow_button);
                        BtnAllow.setText("ok");
                        final Button BtnCancel = (Button) view.findViewById(R.id.cancel_button);
                        TextView textView =(TextView) view.findViewById(R.id.txt_message);
                        textView.setVisibility(View.GONE);
                        TextView text = (TextView) view.findViewById(R.id.message);
                        text.setText("Are you sure you want to delete "+ uploadList.get(position).substring(uploadList.get(position).lastIndexOf("/") + 1));
                        mCustomAlertDialog = builder.create();
                        mCustomAlertDialog.show();
                        BtnAllow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mCustomAlertDialog.dismiss();
                                uploadList= PreferenceUtils.getupload(context,"key");
                                uploadList.remove(position);
                                PreferenceUtils.setupload(context,uploadList,"key");
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

                }
            });

        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;// init the item view's
        RelativeLayout thumbnailView;
        ProgressBar progress;
        ImageView done;
        ImageView thumbnailIcon;
        ImageView thumbnailCornerIcon;
        TextView thumbnailText;
        ImageView delete;

        public ViewHolder(final View itemView) {

            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.folder_name1);
            thumbnailView = (RelativeLayout)itemView.findViewById(R.id.thumbnail_layout);
             progress=(ProgressBar) itemView.findViewById(R.id.progressBar12);
            done=(ImageView)itemView.findViewById(R.id.done);
            progress.setVisibility(View.INVISIBLE);
            done.setVisibility(View.INVISIBLE);
            thumbnailIcon = (ImageView)itemView.findViewById(R.id.thumbnail_image);
            thumbnailCornerIcon=(ImageView)itemView.findViewById(R.id.thumbnail_corner_image);
            thumbnailText=(TextView) itemView.findViewById(R.id.thumbnail_text);
            delete=(ImageView) itemView.findViewById(R.id.delete);
        }

    }
    @Override
    public int getItemCount() {
        return uploadList.size();
    }
}
