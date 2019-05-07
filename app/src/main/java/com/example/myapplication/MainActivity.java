package com.example.myapplication;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Date;
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

    boolean line = true;

    String filename = "test.csv";

    String time;

    Handler handler;
    Timer timer;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒", Locale.getDefault());

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
        },0,1000);//0秒後に1秒感覚で実行

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

            //出力
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
        manager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_UI);

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
                manager.registerListener(this,sensor, SensorManager.SENSOR_DELAY_UI);
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

    /*private String date(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒", Locale.getDefault());
        System.out.println("時間");
        return sdf.format(date);
    }*/

}

