package com.example.smstest.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.smstest.R;
import com.example.smstest.adapter.SMSDetailListAdapter;
import com.example.smstest.manager.SMSManager;

/**
 * Created by mitenjos on 2/16/2017.
 */
public class SMSDetailActivity extends AppCompatActivity {

    private String mSMSAddress;
    private RecyclerView mRecyclerView;
    private SMSDetailListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mSMSAddress = getIntent().getStringExtra(MainActivity.SMS_ADDRESS_KEY);

        getSupportActionBar().setTitle(mSMSAddress);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new SMSDetailListAdapter(SMSManager.getInstance().getMessageFromAddress(mSMSAddress));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
