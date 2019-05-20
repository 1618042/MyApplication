package com.example.myapplication;

import android.hardware.SensorEvent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class AsyncHttp extends AsyncTask<String, Integer, Boolean> {
    String time1;
    SensorEvent event1;
    Location location1;
    HttpURLConnection urlConnection = null;
    Boolean flg = false;
    Double time2;
    Double x_event;
    Double y_event;
    Double z_event;
    Double latitude;
    Double longitude;

    public AsyncHttp(String time1, SensorEvent event1, Location location1){
        Log.d("debug","AsyncHttp");
        this.time1 = time1;
        this.event1 = event1;
        this.location1 = location1;
    }

    public void timeset(){
        Log.d("debug","timeset");
        //time2 = Double.parseDouble(time1);
        Log.d("debug","timeset2");

    }
    public void eventset(){
        String xevent = String.valueOf(event1.values[0]);
        String yevent = String.valueOf(event1.values[1]);
        String zevent = String.valueOf(event1.values[2]);
        x_event = Double.parseDouble(xevent);
        y_event = Double.parseDouble(yevent);
        z_event = Double.parseDouble(zevent);
    }

    public void locationset(){
        String s_latitude = String.valueOf(location1.getLatitude());
        String s_longitude = String.valueOf(location1.getLongitude());
        latitude = Double.parseDouble(s_latitude);
        longitude = Double.parseDouble(s_longitude);
    }

    @Override
    protected Boolean doInBackground(String... params){
        Log.d("debug","background");
        String urlinput = "http://mznjerk.mizunolab.info/phpMyAdmin/";
        URL url = null;
        Log.d("debug","62");
        timeset();
        Log.d("debug","64");
        eventset();
        locationset();
        try {

            String sendData = "input_username="+ URLEncoder.encode("mznjerk","utf-8")+"&input_password="+URLEncoder.encode("kansoukikashiteyo","utf-8");
            url = new URL(urlinput);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.write(sendData);
            out.flush();
            out.close();
            urlConnection.getInputStream();
            flg = true;
            Log.d("debug","URL");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flg;
    }
}
