package net.trendl.procrasthelpercore.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by Tomas.Rendl on 22.7.2015.
 */
public class Task implements Serializable {
    private String id;
    private String name;
    private double difficulty;
    private boolean completed;
    private DateTime insertDate;

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

    public double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public DateTime getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(DateTime insertDate) {
        this.insertDate = insertDate;
    }
}
