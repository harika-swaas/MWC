package com.mwc.docportal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mwc.docportal.API.Model.UserProfileUpdateModel.UserProfileCountryModel;
import com.mwc.docportal.R;

import java.util.List;

public class CountryCustomAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflter;
    List<UserProfileCountryModel.Data> countriesList;

    public CountryCustomAdapter(Context applicationContext, List<UserProfileCountryModel.Data> countriesListData) {
        this.context = applicationContext;
        this.countriesList = countriesListData;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return countriesList.size();
    }

    @Override
    public Object getItem(int i) {
        return countriesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.country_list_item, null);
        TextView names = (TextView) view.findViewById(R.id.Country_textView);
        names.setText(countriesList.get(i).getName());
        return view;
    }
}