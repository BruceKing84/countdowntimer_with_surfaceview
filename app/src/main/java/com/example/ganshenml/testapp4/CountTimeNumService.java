package com.example.ganshenml.testapp4;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by ganshenml on 2016/4/13.
 * 后台Service在后台继续计算传过来的时间值
 */
public class CountTimeNumService extends Service {

    private int countTimeNum = 0;
    private CountTimeNumThread countTimeNumThread;
    private IBinder myBinder = new MyBinder();
    private boolean stopFlag = false;

    class MyBinder extends Binder{
        CountTimeNumService getService(){
            return CountTimeNumService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        countTimeNum = intent.getIntExtra("countTimeNum",-1);
        Log.e("onBind中countTimeNum",String.valueOf(countTimeNum));
        countTimeNumThread = new CountTimeNumThread();
        countTimeNumThread.start();
        return myBinder;
    }



    //新开线程开始计算时间
    class CountTimeNumThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (!stopFlag) {
                try {
                    countTimeNum++;
                    Log.e("run中countTimeNum", String.valueOf(countTimeNum));
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(countTimeNumThread != null) {
            if (countTimeNumThread.isAlive()) {
                countTimeNumThread.interrupt();//销毁线程
                Log.e("onDestroy","执行了销毁");
            }
        }
    }

    public int getCountTimeNum() {
        Log.e("getCountTimeNum中值",String.valueOf(countTimeNum));
        return countTimeNum;
    }

    public void setStopFlag(boolean stopFlag) {
        this.stopFlag = stopFlag;
    }
}
