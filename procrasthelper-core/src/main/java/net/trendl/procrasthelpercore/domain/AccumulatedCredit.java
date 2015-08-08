package net.trendl.procrasthelpercore.domain;

import java.io.Serializable;

/**
 * Created by tomas.rendl on 30.7.2015.
 */
public class AccumulatedCredit implements Serializable {
    private String id;
    private String userId;
    private double value;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
