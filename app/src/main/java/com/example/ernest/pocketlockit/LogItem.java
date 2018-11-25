package com.example.ernest.pocketlockit;

public class LogItem {

    private int id;
    private String time;
    private String tag;

    public LogItem(int id, String time, String tag){
        this.id = id;
        this.time = time;
        this.tag = tag;
    }

    public String getTime(){
        return this.time;
    }

    public String getTag(){
        return this.tag;
    }
    public int getId(){
        return this.id;
    }
}
