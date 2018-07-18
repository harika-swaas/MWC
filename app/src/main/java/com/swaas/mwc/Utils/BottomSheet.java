package com.swaas.mwc.Utils;

/**
 * Created by barath on 7/17/2018.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.ListView;

import com.swaas.mwc.Adapters.ItemAdapter;
import com.swaas.mwc.DMS.MyFoldersDMSActivity;
import com.swaas.mwc.Fragments.Item;
import com.swaas.mwc.R;

import java.util.ArrayList;

public class BottomSheet extends BottomSheetDialog {

    private Context context;

    public BottomSheet(MyFoldersDMSActivity context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        setContentView(view);

        /*ArrayList items = new ArrayList();
        items.add(new Item(R.mipmap.ic_sort_up, "Sort by name", R.mipmap.ic_sort_selected));
        items.add(new Item(R.mipmap.ic_empty, "Sort by newest", R.mipmap.ic_empty));
        items.add(new Item(R.mipmap.ic_empty, "Sort by size", R.mipmap.ic_empty));
        items.add(new Item(R.mipmap.ic_empty, "Sort by type", R.mipmap.ic_empty));

        ItemAdapter adapter = new ItemAdapter(this.context, items);

        //ListView for the items
        ListView listView = (ListView) view.findViewById(R.id.listview);
        listView.setAdapter(adapter);*/
    }
}
