package net.trendl.procrasthelpercore.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by Tomas.Rendl on 22.7.2015.
 */
public class AppliedReward implements Serializable {

    private String id;
    private Reward reward;
    private DateTime appliedDate;
    private boolean pending;

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public DateTime getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(DateTime appliedDate) {
        this.appliedDate = appliedDate;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
