package com.example.myapplication;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    SensorManager manager;
    Sensor sensor;
    TextView xTextView;
    TextView yTextView;
    TextView zTextView;

    LineChart mChart;
    String [] names = new String[]{"x-value", "y-value", "z-value"};
    int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xTextView = (TextView)findViewById(R.id.xValue);
        yTextView = (TextView)findViewById(R.id.yValue);
        zTextView = (TextView)findViewById(R.id.zValue);

        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mChart = (LineChart) findViewById(R.id.lineChart);
        //mChart.setDescription(" ");
        mChart.setData(new LineData());
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
            mChart.setVisibleXRangeMaximum(50);
            mChart.moveViewToX(data.getEntryCount());
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

}

