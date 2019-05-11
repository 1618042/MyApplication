package com.example.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
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
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss/SS", Locale.getDefault());

    OpenHelper helper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xTextView = (TextView)findViewById(R.id.xValue);
        yTextView = (TextView)findViewById(R.id.yValue);
        zTextView = (TextView)findViewById(R.id.zValue);
        timeView = (TextView)findViewById(R.id.timeview);

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
                    }
                });
            }
        },0,1);//0秒後に1秒感覚で実行

    }
    @Override
    public void onSensorChanged(SensorEvent event){
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

            //System.out.println(event.sensor.getType());

            //csv出力
            try{
                FileWriter fw = new FileWriter(getFilesDir()+filename,true);//true追記、false上書き
                PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

                pw.print(time);
                pw.print(",");
                pw.print(event.values[0]);
                pw.print(",");
                pw.print(event.values[1]);
                pw.print(",");
                pw.print(event.values[2]);
                pw.println();

                pw.close();
                //System.out.println("出力が完了しました。");
                //System.out.println("PATH："+getFilesDir());
                //System.out.println(time);
            }catch (IOException e){
                //例外時処理
                e.printStackTrace();
                //System.out.print("例外時処理です。");

            }
        }
        //DB

        if (helper == null){
            helper = new OpenHelper(getApplicationContext());
            Log.d("debug","OpenHelper");
        }
        if (db == null){
            helper.getWritableDatabase();
            Log.d("debug","helper.getWritableDatabase");
        }
        insertData(/*db,*/event);
        Log.d("debug","insertData");
        readData();
        Log.d("debug","readData");

    }
    private LineDataSet createSet(String label, int color){
        LineDataSet set = new LineDataSet(null, label);
        set.setLineWidth(2.5f);
        set.setColor(color);
        set.setDrawCircles(false);
        set.setDrawValues(false);

        return set;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    protected void onResume(){
        super.onResume();
        manager.registerListener(this,sensor,62500);

    }

    @Override
    protected void onPause(){
        super.onPause();
        manager.unregisterListener(this);
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
                mChart.setData(new LineData());
                break;*/
        }
    }

    private void insertData(/*SQLiteDatabase db,*/ SensorEvent event){
        helper = new OpenHelper(this);
        db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(" time", time);
        String value = event.values[0]+","+event.values[1]+","+event.values[2];
        values.put(" accelerometer", value);
        System.out.println("value: "+ value);
        db.insert("test1db", null, values);

    }
    private void readData(){
        if (helper == null){
            helper = new OpenHelper(getApplicationContext());
            Log.d("a","help");
        }
        if(db == null){
            db = helper.getReadableDatabase();
            Log.d("a","db");
        }
        Cursor cursor = db.query(
                "test1db",
                new String[]{ "time", "accelerometer" },
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
}
