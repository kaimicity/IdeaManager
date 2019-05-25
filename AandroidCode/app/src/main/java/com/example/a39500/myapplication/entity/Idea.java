package com.example.a39500.myapplication.entity;

import com.example.a39500.myapplication.R;

import java.util.Date;

public class Idea {
    private int iid;
    private String ideaIndex;
    private String topic;
    private String content;
    private Date modDate;
    private int userId;
    private boolean btns;
    int backColor;
    int position;

    public Idea(int iid, String ideaIndex, String topic, String content, Date md, int uid) {
        this.iid = iid;
        this.ideaIndex = ideaIndex;
        this.topic = topic;
        this.content = content;
        this.modDate = md;
        this.userId = uid;
        this.btns = false;
        this.backColor = R.color.signUp;
    }

    public int getIid() {
        return iid;
    }

    public void setIid(int iid) {
        this.iid = iid;
    }

    public String getIdeaIndex() {
        return ideaIndex;
    }

    public void setIdeaIndex(String ideaIndex) {
        this.ideaIndex = ideaIndex;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isBtns() {
        return btns;
    }

    public void setBtns(boolean btns) {
        this.btns = btns;
    }

    public int getBackColor() {
        return backColor;
    }

    public void waitKill() {
        this.backColor = R.color.wait_kill;
    }

    public void release() {
        this.backColor = R.color.signUp;
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void checkLink(){
        this.backColor = R.color.check;
    }

    public  void linked(){
        this.backColor = R.color.linked;
    }
}
