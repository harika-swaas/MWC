package com.vincent.filepicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vincent.filepicker.ColorCodeModel;
import com.vincent.filepicker.GeneralFunctions;
import com.vincent.filepicker.R;
import com.vincent.filepicker.ToastUtil;
import com.vincent.filepicker.Util;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.util.ArrayList;

/**
 * Created by Vincent Woo
 * Date: 2016/10/26
 * Time: 10:23
 */

public class NormalFilePickAdapter extends BaseAdapter<NormalFile, NormalFilePickAdapter.NormalFilePickViewHolder> {
 //   private int mMaxNumber;
    private int mCurrentNumber = 0;
    private Context context;

    public NormalFilePickAdapter(Context ctx) {
        this(ctx, new ArrayList<NormalFile>());
    }

    public NormalFilePickAdapter(Context ctx, ArrayList<NormalFile> list) {
        super(ctx, list);
       // mMaxNumber = max;
        context = ctx;
    }

    @Override
    public NormalFilePickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.vw_layout_item_normal_file_pick, parent, false);
        return new NormalFilePickViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final NormalFilePickViewHolder holder, final int position) {
        final NormalFile file = mList.get(position);

        holder.mTvTitle.setText(Util.extractFileNameWithSuffix(file.getPath()));
        holder.mTvTitle.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        if (holder.mTvTitle.getMeasuredWidth() >
                Util.getScreenWidth(mContext) - Util.dip2px(mContext, 10 + 32 + 10 + 48 + 10 * 2)) {
            holder.mTvTitle.setLines(2);
        } else {
            holder.mTvTitle.setLines(1);
        }

        if (file.isSelected()) {
            holder.mCbx.setSelected(true);
        } else {
            holder.mCbx.setSelected(false);
        }

        String extension = "";
        if(file.getPath() != null && !file.getPath().isEmpty())
        {
            extension = file.getPath().substring(file.getPath().lastIndexOf(".")+1);

        }

        ColorCodeModel colorCodeModel = GeneralFunctions.getColorCodesforFileType(extension);
        if(colorCodeModel != null)
        {

            holder.thumbnailIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getPrimaryColor()));
            holder.thumbnailCornerIcon.setColorFilter(context.getResources().getColor(colorCodeModel.getSecondaryColor()));
            holder.thumbnailText.setText(colorCodeModel.getFileType());
        }



       /* if (file.getPath().endsWith("xls") || file.getPath().endsWith("xlsx")) {
            holder.mIvIcon.setImageResource(R.drawable.vw_ic_excel);
        } else if (file.getPath().endsWith("doc") || file.getPath().endsWith("docx")){
            holder.mIvIcon.setImageResource(R.drawable.vw_ic_word);
        } else if (file.getPath().endsWith("ppt") || file.getPath().endsWith("pptx")){
            holder.mIvIcon.setImageResource(R.drawable.vw_ic_ppt);
        } else if (file.getPath().endsWith("pdf")){
            holder.mIvIcon.setImageResource(R.drawable.vw_ic_pdf);
        } else if (file.getPath().endsWith("txt")){
            holder.mIvIcon.setImageResource(R.drawable.vw_ic_txt);
        } else {
            holder.mIvIcon.setImageResource(R.drawable.vw_ic_file);
        }
*/
        holder.mCbx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isSelected() && isUpToMax()) {
                    ToastUtil.getInstance(mContext).showToast(R.string.vw_up_to_max);
                    return;
                }

                if (holder.mCbx.isSelected()) {
                    holder.mCbx.setSelected(false);
                    mCurrentNumber--;
                } else {
                    holder.mCbx.setSelected(true);
                    mCurrentNumber++;
                }

                mList.get(holder.getAdapterPosition()).setSelected(holder.mCbx.isSelected());

                if (mListener != null) {
                    mListener.OnSelectStateChanged(holder.mCbx.isSelected(), mList.get(holder.getAdapterPosition()));
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Uri uri = Uri.parse("file://" + file.getPath());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "audio/mp3");
                if (Util.detectIntent(mContext, intent)) {
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "No Application exists for audio!", Toast.LENGTH_SHORT).show();
                }*/

                if (!v.isSelected() && isUpToMax()) {
                    ToastUtil.getInstance(mContext).showToast(R.string.vw_up_to_max);
                    return;
                }

                if (holder.mCbx.isSelected()) {
                    holder.mCbx.setSelected(false);
                    mCurrentNumber--;
                } else {
                    holder.mCbx.setSelected(true);
                    mCurrentNumber++;
                }

                mList.get(holder.getAdapterPosition()).setSelected(holder.mCbx.isSelected());

                if (mListener != null) {
                    mListener.OnSelectStateChanged(holder.mCbx.isSelected(), mList.get(holder.getAdapterPosition()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class NormalFilePickViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailIcon, thumbnailCornerIcon;
        private TextView mTvTitle;
        private ImageView mCbx;
        public TextView thumbnailText;

        public NormalFilePickViewHolder(View itemView) {
            super(itemView);
            thumbnailIcon = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            thumbnailCornerIcon = (ImageView) itemView.findViewById(R.id.thumbnail_corner_image);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_file_title);
            thumbnailText = (TextView) itemView.findViewById(R.id.thumbnail_text);
            mCbx = (ImageView) itemView.findViewById(R.id.cbx);
        }
    }

    private boolean isUpToMax() {
      //  return mCurrentNumber >= mMaxNumber;
        return false;
    }

}
