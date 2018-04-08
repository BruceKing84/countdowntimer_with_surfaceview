package com.example.ganshenml.testapp4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by ganshenml on 2016/4/12.
 */
public class TestView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder surfaceHolder;
    CountThread countThread;
    private int endAngle = 0;//计算的角度(秒数)


    public TestView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        countThread = new CountThread(surfaceHolder);
    }

    public TestView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        countThread = new CountThread(surfaceHolder);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!countThread.isAlive()) {//如果线程不存在，则启动线程——>当应用挂起的时候Thread是存在的，如果不做这个判断，会报“Thread already started ”错误
            countThread.start();//SurfaceView创建时开启线程
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (countThread.isAlive()) {
//            countThread.isStop = true;
            countThread.interrupt();
        }
    }

    class CountThread extends Thread {
        SurfaceHolder surfaceHolder;
        boolean isStop;
        Paint paint, paintText;

        //线程构造方法中做一些初始化的工作
        public CountThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
            isStop = false;
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStrokeWidth(20);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLUE);

            paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintText.setTextSize(128);
            paintText.setColor(Color.GREEN);
            paintText.setTextAlign(Paint.Align.CENTER);
        }

        @Override
        public void run() {
            Canvas canvas = null;
            int pivotX = getResources().getDisplayMetrics().widthPixels / 2;
            RectF rectF = new RectF(pivotX - 300, pivotX - 300, pivotX + 300, pivotX + 300);
            while (!isStop) {
                try {
                    canvas = surfaceHolder.lockCanvas();
                    canvas.drawColor(Color.WHITE);//设置画布背景为白色
//                    canvas.drawRoundRect(300, 300, 600, 600, 150, 150, paint);//直接使用该行代码来画圆是行不通的，因为这个方法要求版本21，我的手机运行android版本是19
                    canvas.drawArc(rectF, -90, endAngle++, false, paint);//-90在这里不等于270，所以要想从最上方开始画弧，就得用-90
                    canvas.drawText(countTime(endAngle), pivotX, pivotX, paintText);//显示计算的时间
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {//需要对canvas进行非空判断
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        //根据秒数计算时间格式
        private String countTime(int endAngle) {
            String timeStr = "";
            int minuteInt = endAngle / 60;//分钟数值
            int secondInt = endAngle % 60;//秒数数值
            if (minuteInt > 10) {
                if (secondInt < 10) {
                    timeStr = String.valueOf(minuteInt) + ":0" + String.valueOf(secondInt);
                } else {
                    timeStr = String.valueOf(minuteInt) + ":" + String.valueOf(secondInt);
                }
            } else if (minuteInt >= 0 && minuteInt < 10) {
                if (secondInt < 10) {
                    timeStr = "0" + String.valueOf(minuteInt) + ":0" + String.valueOf(secondInt);
                } else {
                    timeStr = "0" + String.valueOf(minuteInt) + ":" + String.valueOf(secondInt);
                }
            }
            return timeStr;
        }
    }

    public void setEndAngle(int endAngle) {
        this.endAngle = endAngle;
    }
}
