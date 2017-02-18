package com.example.smstest.manager;

import android.util.Log;

import com.example.smstest.model.SMSEntity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Observable;

/**
 * Created by mitenjos on 2/16/2017.
 */
public class SMSManager extends Observable {

    private static SMSManager instance = new SMSManager();

    private ArrayList<SMSEntity> mSMSList = new ArrayList<>();

    private LinkedHashMap<String, ArrayList<SMSEntity>> mSortedSMSList = new LinkedHashMap<>();

    /**
     * This method will return singleton instance of current class.
     *
     * @return SMSManager
     */
    public static SMSManager getInstance() {
        if (instance == null) {
            Log.d("SMSManager", "getInstance : New Instance creation");
            instance = new SMSManager();
        }
        return instance;
    }

    public ArrayList<SMSEntity> getSMSList() {
        return mSMSList;
    }

    public void setSMSList(ArrayList<SMSEntity> mSMSList) {
        this.mSMSList = mSMSList;
    }

    public LinkedHashMap<String, ArrayList<SMSEntity>> getSortedSMSList() {
        return mSortedSMSList;
    }

    public void setSortedSMSList(LinkedHashMap<String, ArrayList<SMSEntity>> mSortedSMSList) {
        this.mSortedSMSList = mSortedSMSList;
    }

    public ArrayList<SMSEntity> getMessageFromAddress(String address) {
        return mSortedSMSList.get(address);
    }

    public void updateValue(SMSEntity data) {
        synchronized (this) {
            mSMSList.add(0, data);
            mSortedSMSList.clear();
            setChanged();
            notifyObservers(data);
        }
    }
}
