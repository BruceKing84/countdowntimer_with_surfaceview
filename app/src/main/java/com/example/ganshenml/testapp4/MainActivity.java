package com.example.ganshenml.testapp4;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    TestView testView;
    Intent myIntent;
    CountTimeNumService.MyBinder myBinder;
    ServiceConnection serviceConnection;
    int countTimeNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testView = (TestView) findViewById(R.id.testView);

        //通过Binder的方式来获得CountTimeNumService的对象(myBinder)
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myBinder = (CountTimeNumService.MyBinder) service;
                Log.e("countTimeNum",String.valueOf(countTimeNum) );
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e("Service","异常停止");
            }
        };

        //指定service在后台继续计算时间
        myIntent = new Intent(MainActivity.this,CountTimeNumService.class);
        myIntent.putExtra("countTimeNum", countTimeNum);
        bindService(myIntent, serviceConnection, BIND_AUTO_CREATE);

    }

    //按钮点击停止计时
    public void stop(View view) {
        testView.countThread.isStop = true;
    }


    //通过onRestart方法获取后台Service计算的时间值，并传递给自定义的SurfaceView（这里是TestView）
    @Override
    protected void onRestart() {
        super.onRestart();
        countTimeNum = myBinder.getService().getCountTimeNum();
        Log.e("onRestart中countTimeNum", String.valueOf(countTimeNum));
        testView.setEndAngle(countTimeNum);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        testView.countThread.isStop = true;
        if(testView.countThread != null) {
            if (testView.countThread.isAlive()) {//关闭线程
                testView.countThread.interrupt();
            }
        }

        //停止Service
        if(serviceConnection != null) {
            myBinder.getService().setStopFlag(true);
            unbindService(serviceConnection);
            stopService(myIntent);
        }
    }
}
