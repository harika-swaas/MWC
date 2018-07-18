package com.swaas.mwc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.swaas.mwc.Fragments.Item;
import com.swaas.mwc.R;

import java.util.List;

/**
 * Created by barath on 7/17/2018.
 */

public class ItemAdapter extends BaseAdapter {

    protected List bsItems;
    protected LayoutInflater inflater;

    static class ViewHolder {
        RelativeLayout lstClick;
        ImageView image;
        TextView text;
        ImageView image2;
    }

    public ItemAdapter(Context context, List bsItems) {
        this.bsItems = bsItems;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return bsItems.size();
    }

    @Override
    public Object getItem(int position) {
        return bsItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        Item item = (Item) bsItems.get(position);
        return item.getImage2();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        Item item = (Item) bsItems.get(position);

        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.bottomitems, parent, false);
            convertView.setTag(holder);

            holder.image = (ImageView) convertView.findViewById(R.id.icon1);
            holder.image2 = (ImageView) convertView.findViewById(R.id.icon2);
            holder.text = (TextView) convertView.findViewById(R.id.sortname);
            holder.lstClick = (RelativeLayout) convertView.findViewById(R.id.list_click);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image.setImageResource(item.getImage());
        holder.text.setText(item.getText());
        holder.image2.setImageResource(item.getImage());



        return convertView;
    }
}