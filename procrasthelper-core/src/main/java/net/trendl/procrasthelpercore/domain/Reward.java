package net.trendl.procrasthelpercore.domain;

import java.io.Serializable;

/**
 * Created by Tomas.Rendl on 22.7.2015.
 */
public class Reward implements Serializable {
    private String id;
    private String name;
    private int appliedDifficulty;
    private int minRepeatInterval ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinRepeatInterval() {
        return minRepeatInterval;
    }

    public void setMinRepeatInterval(int minRepeatInterval) {
        this.minRepeatInterval = minRepeatInterval;
    }

    public int getAppliedDifficulty() {
        return appliedDifficulty;
    }

    public void setAppliedDifficulty(int appliedDifficulty) {
        this.appliedDifficulty = appliedDifficulty;
    }
}
