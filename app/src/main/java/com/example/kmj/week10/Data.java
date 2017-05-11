package com.example.kmj.week10;

/**
 * Created by KMJ on 2017-05-04.
 */

public class Data {
    public String name;
    public String url;

    public String getUrl() {
        return url;
    }

    public Data (String name, String url){
        this.name=name;
        this.url=url;
    }

    public String toString() {
        return "<"+name+">"+url;
    }
}
