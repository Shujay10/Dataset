package com.example.dataset;

import androidx.annotation.NonNull;

/*
    Satisfied 0 - requested
              1 - done
              2 - not possible
 */

public class Model {

    String id;
    String tool;
    String stage;
    String mode;
    String comments;
    String userName;
    int satisfied;


    public Model() {
    }

    @NonNull
    @Override
    public String toString() {
        return tool+" : "+stage+" : "+mode+" : "+comments+" : "+userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getSatisfied() {
        return satisfied;
    }

    public void setSatisfied(int satisfied) {
        this.satisfied = satisfied;
    }
}
