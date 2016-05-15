package com.beast.bkara.model.supportmodel;

import com.beast.bkara.model.Record;

import java.util.ArrayList;

/**
 * Created by VINH on 5/2/2016.
 */
public class ListRecordsHistory {

    public ArrayList<Record> getLstRecordsHistory() {
        return lstRecordsHistory;
    }

    public void setLstRecordsHistory(ArrayList<Record> lstRecordsHistory) {
        this.lstRecordsHistory = lstRecordsHistory;
    }

    private ArrayList<Record> lstRecordsHistory;
}
