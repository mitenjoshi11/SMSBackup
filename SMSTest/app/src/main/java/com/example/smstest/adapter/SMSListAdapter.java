package com.example.smstest.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smstest.R;
import com.example.smstest.model.SMSEntity;
import com.example.smstest.util.Utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


public class SMSListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private SMSListAdapterListener listener;
    private LinkedHashMap<String, ArrayList<SMSEntity>> data = new LinkedHashMap<>();
    private LinkedHashMap<String, ArrayList<SMSEntity>> hashmaplist;
    private ArrayList<String> mKeys = new ArrayList<>();
    private ArrayList<String> mHashKeys = new ArrayList<>();
    private Context mContext;


    public interface SMSListAdapterListener {
        void onItemClicked(String address);
    }


    public SMSListAdapter(Context mContext, LinkedHashMap<String, ArrayList<SMSEntity>> items, SMSListAdapterListener listener) {
        this.mContext = mContext;
        this.listener = listener;
        this.data = items;
        mKeys.addAll(items.keySet());
        this.mHashKeys.addAll(mKeys);
        this.hashmaplist = new LinkedHashMap<>();
        this.hashmaplist.putAll(items);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sms_list, parent, false);
        ViewHolder vh = new ViewHolder(v, new ViewHolder.ViewHolderInterface() {
            @Override
            public void onItemClicked(int position) {
                listener.onItemClicked(mKeys.get(position));
            }
        });
        return vh;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String key = mKeys.get(position);
        SMSEntity value = data.get(key).get(0);

        ((ViewHolder) holder).message.setText(value.getMsg());
        value.setAddress(Utility.getContactName(mContext, value.getAddress()));
        ((ViewHolder) holder).address.setText(value.getAddress() + " - " + "" + data.get(key).size());
        ((ViewHolder) holder).time.setText(Utility.getTimeSaction(value.getTime()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView address;
        private TextView time;
        private TextView message;
        private ViewHolderInterface listener;

        public interface ViewHolderInterface {
            void onItemClicked(int position);
        }

        public ViewHolder(View v, ViewHolderInterface listener) {
            super(v);
            this.listener = listener;
            address = (TextView) v.findViewById(R.id.txtName);
            message = (TextView) v.findViewById(R.id.txtContent);
            time = (TextView) v.findViewById(R.id.txtDate);
            v.setOnClickListener(itemClickListener);
        }

        View.OnClickListener itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listener.onItemClicked(getAdapterPosition());
                    }
                }, 200);
            }
        };
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        data.clear();
        mKeys.clear();
        if (charText.length() == 0) {
            mKeys.addAll(mHashKeys);
            data.putAll(hashmaplist);
        } else {
            for (Map.Entry<String, ArrayList<SMSEntity>> entry : hashmaplist.entrySet()) {
                boolean isPresent = false;
                ArrayList<SMSEntity> smsData = entry.getValue();

                for (SMSEntity entity : smsData) {
                    if (entity.getMsg().toLowerCase(Locale.getDefault()).contains(charText)) {
                        isPresent = true;
                        break;
                    }
                }
                if (isPresent) {
                    mKeys.add(entry.getKey());
                    data.put(entry.getKey(), entry.getValue());
                }
            }
        }
        notifyDataSetChanged();
    }

}