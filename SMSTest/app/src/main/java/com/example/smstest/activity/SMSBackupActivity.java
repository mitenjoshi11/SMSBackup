package com.example.smstest.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.example.smstest.R;
import com.example.smstest.manager.SMSManager;
import com.example.smstest.model.SMSEntity;
import com.example.smstest.util.Utility;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by mitenjos on 2/17/2017.
 */
public class SMSBackupActivity extends BaseGoogleDriveActivity {
    private static final String TAG = "SMSBackupActivity";
    private ProgressDialog pd;

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        // create new contents resource
        Drive.DriveApi.newDriveContents(getGoogleApiClient())
                .setResultCallback(driveContentsCallback);
    }


    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Utility.showMessage(SMSBackupActivity.this,getString(R.string.file_content_error));
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();

                    pd = new ProgressDialog(SMSBackupActivity.this,R.style.ProgressDialogStyle);
                    pd.setMessage(getString(R.string.loading));
                    pd.show();

                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {

                            // write content to DriveContents
                            OutputStream outputStream = driveContents.getOutputStream();
                            Writer writer = new OutputStreamWriter(outputStream);

                            StringBuilder stringBuilder = new StringBuilder();

                            for (Map.Entry<String, ArrayList<SMSEntity>> entry : SMSManager.getInstance().getSortedSMSList().entrySet()) {
                                stringBuilder.append("\n \n" + entry.getKey() + "\n ");
                                stringBuilder.append("\n" + "********************************" + "\n");
                                ArrayList<SMSEntity> smsData = entry.getValue();

                                for (SMSEntity entity : smsData) {
                                    stringBuilder.append("\n" + Utility.convertTimeDate(Long.parseLong(entity.getTime())) + "\n");
                                    stringBuilder.append(entity.getMsg() + "\n");
                                }
                            }
                            try {
                                writer.write(stringBuilder.toString());
                                writer.close();
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle("BackupSMS")
                                    .setMimeType("text/plain")
                                    .setStarred(true).build();

                            // create a file on root folder
                            Drive.DriveApi.getRootFolder(getGoogleApiClient())
                                    .createFile(getGoogleApiClient(), changeSet, driveContents)
                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Utility.showMessage(SMSBackupActivity.this,getString(R.string.error_backup_file));
                        finish();
                    }
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                    Utility.showMessage(SMSBackupActivity.this,getString(R.string.backup_successfull));
                    finish();
                }
            };


}