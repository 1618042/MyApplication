package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener, LocationListener {
    SensorManager manager;
    Sensor sensor;
    TextView xTextView;
    TextView yTextView;
    TextView zTextView;
    TextView timeView;

    LineChart mChart;
    String [] names = new String[]{"x-value", "y-value", "z-value"};
    int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE};

    //boolean line = true;

    String filename = "test.csv";

    String time;

    Handler handler;
    Timer timer;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SS", Locale.getDefault());

    OpenHelper helper;
    SQLiteDatabase db;

    TextView textView1;
    TextView textView2;
    LocationManager locationManager;
    SensorEvent event1;
    Location location1;

    int i=0;
    int j=0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonset();
        time();
        createdb();
    }
    public void createdb(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println(i +j);
                if (i==1 || j==1){
                    sqliteset(event1,location1);
                }
            }
        };
    }

    public void buttonset(){
        xTextView = (TextView)findViewById(R.id.xValue);
        yTextView = (TextView)findViewById(R.id.yValue);
        zTextView = (TextView)findViewById(R.id.zValue);
        timeView = (TextView)findViewById(R.id.timeview);
        textView1 = (TextView)findViewById(R.id.textview1);
        textView2 = (TextView)findViewById(R.id.textview2);

        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mChart = (LineChart) findViewById(R.id.lineChart);
        mChart.setData(new LineData());

        Button buttonStart = findViewById(R.id.button_start);
        buttonStart.setOnClickListener(this);
        Button buttonStop = findViewById(R.id.button_stop);
        buttonStop.setOnClickListener(this);
        //Button buttonChange = findViewById(R.id.button_change);
        //buttonChange.setOnClickListener(this);

        mapset();
        phpMyAdminset();


    }
    public void phpMyAdminset(){
        //phpMyAdmin
        Button dbbutton = findViewById(R.id.db_button);
        dbbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String time1 = String.valueOf(time);
                //AsyncHttp post = new AsyncHttp(time1, event1, location1);
                //post.execute();
                MySQL mySQL = new MySQL();
            }
        });
    }


    public void mapset(){
        //map
        Button mapsactivity = findViewById(R.id.mapsactivity);
        mapsactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //db.close();
                Intent intent = new Intent(getApplication(), MapsActivity.class);
                intent.putExtra("latitude1", String.valueOf(location1.getLatitude()));
                intent.putExtra("longitude1", String.valueOf(location1.getLongitude()));
                startActivity(intent);
            }
        });
    }

    public void time(){
        handler = new Handler(getMainLooper());
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Calendar calendar = Calendar.getInstance();
                        String nowDate = simpleDateFormat.format(calendar.getTime());
                        timeView.setText(String.valueOf(nowDate));
                        time = nowDate;
                        //csvFile(event1,location1); //csv出力
                    }
                });
            }
        },0,1);//0秒後に1秒感覚で実行
    }
    @Override
    public void onSensorChanged(SensorEvent event){
        event1 = event;
        i=1;
        xTextView.setText(String.valueOf(event.values[0]));
        yTextView.setText(String.valueOf(event.values[1]));
        zTextView.setText(String.valueOf(event.values[2]));

        LineData data = mChart.getLineData();
        if (data != null){
            for (int i = 0; i < 3; i++){
                ILineDataSet set = data.getDataSetByIndex(i);
                if (set == null){
                    set = createSet(names[i], colors[i]);
                    data.addDataSet(set);
                }
                data.addEntry(new Entry(set.getEntryCount(), event.values[i]), i);
                data.notifyDataChanged();
            }
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(50);//表示幅を決定する
            mChart.moveViewToX(data.getEntryCount());

            if (j==1){
                sqliteset(event1,location1); //sqlite
            }
        }
    }
    private LineDataSet createSet(String label, int color){
        LineDataSet set = new LineDataSet(null, label);
        set.setLineWidth(2.5f);
        set.setColor(color);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        return set;
    }

    public void csvFile(SensorEvent event1, Location location1){
        try{
            FileWriter fw = new FileWriter(getFilesDir()+filename,true);//true追記、false上書き
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            pw.print(time);
            pw.print(",");
            pw.print(event1.values[0]);
            pw.print(",");
            pw.print(event1.values[1]);
            pw.print(",");
            pw.print(event1.values[2]);
            pw.print(",");
            if (location1 == null){
                pw.print(location1);
                pw.print(",");
                pw.print(location1);
            }else {
                pw.print(location1.getLatitude());
                pw.print(",");
                pw.print(location1.getLongitude());
            }
            pw.println();
            pw.close();
        }catch (IOException e){
            e.printStackTrace(); //例外時処理
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
    }
    @Override
    protected void onResume(){
        super.onResume();
        manager.registerListener(this,sensor,62500);
        //GPSチェック
        if ( (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (PermissionChecker.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) ){
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
            System.out.println("GPS");
        }
        //Wi-fiチェック
        if ( (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (PermissionChecker.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) ){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
            System.out.println("Wi-fi");
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        manager.unregisterListener(this);
        if (locationManager != null){
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.button_start:
                manager.registerListener(this,sensor, 602500);
                break;

            case R.id.button_stop:
                manager.unregisterListener(this);
                break;

            /*case R.id.button_change:
                if(line){
                    line = false;
                }else{
                    line = true;
                }
                break;*/
        }
    }

    public void sqliteset(SensorEvent event1, Location location1){
        if (helper == null){
            helper = new OpenHelper(getApplicationContext());
        }
        if (db == null){
            helper.getWritableDatabase();
        }
        insertData(event1, location1);
        readData();
    }

    private void insertData( SensorEvent event, Location location){
        helper = new OpenHelper(this);
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(" time", time);
        values.put(" x_axis", String.valueOf(event.values[0]));
        values.put(" y_axis", String.valueOf(event.values[1]));
        values.put(" z_axis", String.valueOf(event.values[2]));
        values.put(" latitude", location.getLatitude());
        values.put(" longitude", location.getLongitude());
        db.insert("test1db", null, values);
    }
    private void readData(){
        if (helper == null){
            helper = new OpenHelper(getApplicationContext());
        }
        if(db == null){
            db = helper.getReadableDatabase();
        }
        Cursor cursor = db.query(
                "test1db",
                new String[]{ "time", "x_axis", "y_axis", "z_axis" , "latitude", "longitude" },
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();

        StringBuilder sbuilder = new StringBuilder();
        for (int i = 0; i < cursor.getCount(); i++){
            sbuilder.append(cursor.getString(0));
            sbuilder.append(": ");
            sbuilder.append(cursor.getInt(1));
            sbuilder.append("\n");
            cursor.moveToNext();
        }
        cursor.close();//忘れずに！
    }

    @Override
    public void onLocationChanged(Location location){
        j=1;
        location1 = location;
        textView1.setText(String.valueOf(location.getLatitude()));
        textView2.setText(String.valueOf(location.getLongitude()));
        if (i == 1){
            sqliteset(event1,location1);
        }
    }

    @Override
    public void onProviderDisabled(String provider){

    }

    @Override
    public void onProviderEnabled(String provider){

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){

    }
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }
}