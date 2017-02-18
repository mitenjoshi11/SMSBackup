package com.example.smstest.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.smstest.R;
import com.example.smstest.adapter.SMSListAdapter;
import com.example.smstest.manager.SMSManager;
import com.example.smstest.model.SMSEntity;
import com.example.smstest.util.Utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements SMSListAdapter.SMSListAdapterListener, Observer {

    public static final String SMS_ADDRESS_KEY = "SMS_ADDRESS";

    String[] permissions = new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS};

    private static final int PERMISSIONS = 111;
    private RecyclerView mRecyclerView;
    private EditText editText;
    private SMSListAdapter mAdapter;
    private ArrayList<SMSEntity> mSMSList = new ArrayList<>();
    private SMSManager mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mInstance = SMSManager.getInstance();
        editText = (EditText) findViewById(R.id.edtSearch);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SendSMSActivity.class));
            }
        });

        if (checkPermissions()) {
            //  permissions  granted.
            new GetSMS().execute();

        }
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editText.getText().toString().toLowerCase(Locale.getDefault());
                mAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });
        SMSManager.getInstance().addObserver(this);
    }

    private void setAdapter() {
        mInstance.setSortedSMSList(sortMessages(mInstance.getSMSList()));
        mAdapter = new SMSListAdapter(MainActivity.this, mInstance.getSortedSMSList(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_backup) {
            if (Utility.isNetworkAvailable(MainActivity.this)) {
                startActivity(new Intent(MainActivity.this, SMSBackupActivity.class));
            } else {
                Utility.showMessage(MainActivity.this, getString(R.string.no_internet_msg));
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(String address) {
        Intent intent = new Intent(MainActivity.this, SMSDetailActivity.class);
        intent.putExtra(SMS_ADDRESS_KEY, address);
        startActivity(intent);
    }

    @Override
    public void update(Observable observable, Object o) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter = null;
                setAdapter();
            }
        });
    }

    private class GetSMS extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this, R.style.ProgressDialogStyle);
            pd.setMessage(getString(R.string.loading));
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mSMSList = getAllSms();
            mInstance.setSMSList(mSMSList);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
                setAdapter();
            }
        }
    }

    private ArrayList<SMSEntity> getAllSms() {
        ArrayList<SMSEntity> lstSms = new ArrayList<SMSEntity>();
        SMSEntity objSms = null;
        Uri message = Uri.parse("content://sms/inbox");
        ContentResolver cr = getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        c.moveToFirst();
        while (c.moveToNext()) {

            objSms = new SMSEntity();
            objSms.setAddress(c.getString(c
                    .getColumnIndexOrThrow("address")));
            objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
            objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));

            lstSms.add(objSms);
        }
        c.close();


        return lstSms;
    }

    private LinkedHashMap<String, ArrayList<SMSEntity>> sortMessages(ArrayList<SMSEntity> data) {
        LinkedHashMap<String, ArrayList<SMSEntity>> map = new LinkedHashMap<String, ArrayList<SMSEntity>>();

        for (SMSEntity address : data) {
            String saction = address.getAddress();
            if (!map.containsKey(saction)) {
                map.put(saction, new ArrayList<SMSEntity>());
            }
            map.get(saction).add(address);
        }
        return map;
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSIONS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                    new GetSMS().execute();

                } else {
                    // no permissions granted.
                }
                return;
            }
        }
    }
}
