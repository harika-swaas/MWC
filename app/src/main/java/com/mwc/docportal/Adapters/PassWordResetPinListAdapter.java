package com.mwc.docportal.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mwc.docportal.API.Model.ListPinDevices;
import com.mwc.docportal.Fragments.PinVerificationFragment;
import com.mwc.docportal.Preference.PreferenceUtils;
import com.mwc.docportal.R;

import java.util.List;

public class PassWordResetPinListAdapter extends RecyclerView.Adapter<PassWordResetPinListAdapter.PinDeviceViewHolder> {
    private RadioButton lastCheckedRB = null;
    final Context context;
    List<ListPinDevices> mListPinDevices;
    AdapterInterface listener;

    public PassWordResetPinListAdapter(Activity context, List<ListPinDevices> mListPinDevices, AdapterInterface listener) {
        this.context = context;
        this.mListPinDevices = mListPinDevices;
        this.listener = listener;

    }

    @Override
    public PinDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.forgot_password_pin_items, parent, false);
        // set the view's size, margins, paddings and layout parameters
        PinDeviceViewHolder vh = new PinDeviceViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(final PinDeviceViewHolder holder, final int position) {
        holder.name.setText(mListPinDevices.get(position).getDetail());

        if (mListPinDevices.get(position).getDevice_type().equalsIgnoreCase(context.getResources().getString(R.string.Email))) {
            holder.icon.setImageResource(R.mipmap.ic_mail);
        }
        else  if (mListPinDevices.get(position).getDevice_type().equalsIgnoreCase(context.getResources().getString(R.string.Mobile))) {
            holder.icon.setImageResource(R.mipmap.ic_mobile);
        }
        else
        {
            holder.icon.setImageResource(R.mipmap.ic_mobile);
        }

        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RadioButton radioButton = (RadioButton) buttonView.findViewById(R.id.user_pin_device_radio_type);
                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                }
                //store the clicked radiobutton
                lastCheckedRB = radioButton;
                if(isChecked){

                    if(listener != null)
                    {
                        listener.onClick(mListPinDevices.get(position).getDevice_type(), mListPinDevices.get(position).getUser_pin_device_id());
                    }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListPinDevices.size();
    }

    public class PinDeviceViewHolder extends RecyclerView.ViewHolder {
        TextView name;// init the item view's
        ImageView icon;
        RadioButton radioButton;

        public PinDeviceViewHolder(View itemView) {

            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.user_pin_device_type);
            icon = (ImageView) itemView.findViewById(R.id.user_pin_device_icon);
            radioButton = (RadioButton) itemView.findViewById(R.id.user_pin_device_radio_type);
        }
    }

    public interface AdapterInterface
    {
        void onClick(String value, String userDeviceId);
    }


}
