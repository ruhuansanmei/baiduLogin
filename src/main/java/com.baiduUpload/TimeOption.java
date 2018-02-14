package com.baiduUpload;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeOption {
    public static String getTime(String s) {
        SimpleDateFormat sdf;
        Date date = new Date();
        switch (s) {
            case "Refer" :
                sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss SSS");
                return sdf.format(date);
            case "printLog":
                sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                return sdf.format(date);
            case "day":
                sdf = new SimpleDateFormat("d");
                return sdf.format(date);
            case  "ymd":
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(date);
            case "hour":
                sdf = new SimpleDateFormat("h");
                return sdf.format(date);
            default:
                return date.getTime() + "";
        }
    }
}
