package com.example.smstest.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smstest.R;
import com.example.smstest.model.SMSEntity;
import com.example.smstest.util.Utility;

import java.util.ArrayList;


public class SMSDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<SMSEntity> data = new ArrayList<>();

    public SMSDetailListAdapter(ArrayList<SMSEntity> data) {
        this.data = data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_all_sms_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SMSEntity value = data.get(position);

        ((ViewHolder) holder).txtSMS.setText(value.getMsg());
        ((ViewHolder) holder).txtTime.setText(Utility.convertTimeDate(Long.parseLong(value.getTime())));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtSMS;
        private TextView txtTime;

        public ViewHolder(View v) {
            super(v);
            txtSMS = (TextView) v.findViewById(R.id.txtSMS);
            txtTime = (TextView) v.findViewById(R.id.txtTime);

        }
    }
}