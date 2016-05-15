package com.beast.bkara.model.supportmodel;

import com.beast.bkara.model.Record;
import com.beast.bkara.model.User;

/**
 * Created by Darka on 4/28/2016.
 */
public class RatingRecord {

    private Long id;
    private Record record;
    private User user;
    private int rateValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getRateValue() {
        return rateValue;
    }

    public void setRateValue(int rateValue) {
        this.rateValue = rateValue;
    }
}
