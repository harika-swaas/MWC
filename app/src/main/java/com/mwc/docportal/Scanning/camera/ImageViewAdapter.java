package com.mwc.docportal.Scanning.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mwc.docportal.R;
import com.mwc.docportal.Scanning.model.Page;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ViewHolder> implements ItemTouchHelperAdapter
{

        OnItemClickListener listenerr;
        private Context context;
        public List<Page> pagesListData;
         private OnStartDragListener mDragStartListener;

        public ImageViewAdapter(List<Page> pageList, Context context, OnItemClickListener listener, OnStartDragListener dragListner) {
            this.context = context;
            this.pagesListData = pageList;
            this.listenerr = listener;
            this.mDragStartListener = dragListner;

        }




public class ViewHolder extends RecyclerView.ViewHolder {


    public ImageView imageView;
    public View layout;
    ViewHolder vh;

    public ViewHolder(View itemView) {
        super(itemView);
        layout = itemView;
        imageView = (ImageView) itemView.findViewById(R.id.imageItem);


    }
}

    /*@Override
    public int getItemViewType(int position) {
        return position;
    }*/



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.image_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public int getItemCount() {
        return pagesListData.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Page currentPageItem = pagesListData.get(position);
        String imagePath = currentPageItem.getEnhancedImage().getAbsolutePath(context);
        File imgFile = new  File(imagePath);

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
           holder.imageView.setImageBitmap(myBitmap);

        }



        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(context, Image_FullView_Activity.class);
                intent.putExtra("ImagePath", currentPageItem.getEnhancedImage().getAbsolutePath(context));
                context.startActivity(intent);*/

                if(listenerr != null)
                {
                    listenerr.onItemClick(currentPageItem.getEnhancedImage().getAbsolutePath(context));
                }

            }
        });

        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               // if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
            //    }
                return false;
            }
        });

        /*holder.layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
              //      mDragStartListener.onStartDrag(holder);

                return false;
            }
        });*/

    }

    public interface OnItemClickListener {
        void onItemClick(String item);
    }




    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //Log.v("", "Log position" + fromPosition + " " + toPosition);
        if (fromPosition < pagesListData.size() && toPosition < pagesListData.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(pagesListData, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(pagesListData, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
        return true;
    }



}

