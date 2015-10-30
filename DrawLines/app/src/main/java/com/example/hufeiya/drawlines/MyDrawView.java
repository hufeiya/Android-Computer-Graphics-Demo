package com.example.hufeiya.drawlines;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hufeiya on 15-10-29.
 */
public class MyDrawView extends SurfaceView implements SurfaceHolder.Callback {
    public static final int UPDATE_VIEW = 1;
    private DrawThread thread;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private List<MyPoint> savedLines = new ArrayList<MyPoint>();
    private MyPoint beginPoint = new MyPoint(0,0);

    public MyDrawView(Context context, AttributeSet set) {
        super(context, set);
        this.surfaceHolder = this.getHolder();
        this.surfaceHolder.addCallback(this);
        paint  = new Paint();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                beginPoint.setXY(x, y);


                break;
            }
            case MotionEvent.ACTION_UP:{
                savedLines.add(new MyPoint(beginPoint));
                savedLines.add(new MyPoint(x, y));
                thread.touchUp = true;

                break;
            }
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.thread = new DrawThread();
        this.thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    private void rebuildOldLines(Canvas canvas){
        if (savedLines.size() % 2 == 1){
            return;
        }
        for(int i = 0;i < savedLines.size();i+=2){
            myDrawline(canvas,savedLines.get(i), savedLines.get(i + 1));

        }
    }

    private void myDrawline(Canvas canvas,MyPoint begin,MyPoint end) {
        if (begin.getX() > end.getX()){//swap the begin end
            MyPoint temp = begin;
            begin = end;
            end = temp;
        }
        if(end.getY() >= begin.getY()){
            if(end.getX()-begin.getX() > end.getY()-begin.getY()){
                myDrawline_0to1(canvas,begin,end);
            }else{
                myDrawline_1toInfinite(canvas,begin,end);
            }
        }else {
            if(begin.getX()-end.getX() < end.getY()-begin.getY()){
                myDrawline_1to0(canvas,begin,end);
            }else{
                myDrawline_Infiniteto_1(canvas,begin,end);
            }
        }
    }

    //When the gradient is [0,1)
    private void myDrawline_0to1(Canvas canvas,MyPoint begin,MyPoint end){

        int x0,y0,x1,y1,x,y;
        x0 = begin.getX();
        y0 = begin.getY();
        x1 = end.getX();
        y1 = end.getY();
        x = x0;
        y = y0;
        int d = 2*(y0-y1)*(x0+1)+(x1-x0)*(2*y0+1)+2*x0*y1-2*x1*y0;
        for(;x <= x1;x++){
            canvas.drawPoint(x,y,paint);
            if (d < 0){
                y += 1;
                d = d + 2*(x1-x0) + 2*(y0-y1);
            }else{
                d = d + 2*(y0-y1);
            }
        }
    }

    //When the gradient is(-1,0]
    private void myDrawline_1to0(Canvas canvas,MyPoint begin,MyPoint end){
        int x0,y0,x1,y1,x,y;
        x0 = begin.getX();
        y0 = -begin.getY();
        x1 = end.getX();
        y1 = -end.getY();
        x = x0;
        y = y0;
        int d = 2*(y0-y1)*(x0+1)+(x1-x0)*(2*y0+1)+2*x0*y1-2*x1*y0;
        for(;x <= x1;x++){
            canvas.drawPoint(x,-y,paint);
            if (d < 0){
                y += 1;
                d = d + 2*(x1-x0) + 2*(y0-y1);
            }else{
                d = d + 2*(y0-y1);
            }
        }
    }
    //When the gradient is (1,+)
    private void myDrawline_1toInfinite(Canvas canvas,MyPoint begin,MyPoint end){

        int x0,y0,x1,y1,x,y;
        x0 = begin.getY();
        y0 = begin.getX();
        x1 = end.getY();
        y1 = end.getX();
        x = x0;
        y = y0;
        int d = 2*(y0-y1)*(x0+1)+(x1-x0)*(2*y0+1)+2*x0*y1-2*x1*y0;
        for(;x <= x1;x++){
            canvas.drawPoint(y,x,paint);
            if (d < 0){
                y += 1;
                d = d + 2*(x1-x0) + 2*(y0-y1);
            }else{
                d = d + 2*(y0-y1);
            }
        }
    }
    //When the gradient is(-,-1]
    private void myDrawline_Infiniteto_1(Canvas canvas,MyPoint begin,MyPoint end) {
        int x0, y0, x1, y1, x, y;
        x0 = -begin.getY();
        y0 = begin.getX();
        x1 = -end.getY();
        y1 = end.getX();
        x = x0;
        y = y0;
        int d = 2 * (y0 - y1) * (x0 + 1) + (x1 - x0) * (2 * y0 + 1) + 2 * x0 * y1 - 2 * x1 * y0;
        for (; x <= x1; x++) {
            canvas.drawPoint(y, -x, paint);
            if (d < 0) {
                y += 1;
                d = d + 2 * (x1 - x0) + 2 * (y0 - y1);
            } else {
                d = d + 2 * (y0 - y1);
            }
        }
    }
    public List<MyPoint> getSavedLines(){
        return this.savedLines;
    }
    class MyPoint{
        private int x;
        private int y;
        public MyPoint(int x,int y){
            this.x = x;
            this.y = y;
        }
        public MyPoint(MyPoint myPoint){
            this.x = myPoint.x;
            this.y = myPoint.y;
        }
        public int getX(){
            return this.x;
        }
        public int getY(){
            return this.y;
        }
        public void setX(int x){
            this.x = x;
        }
        public  void setY(int y){
            this.y = y;
        }
        public  void setXY(int x,int y){
            this.x = x;
            this.y = y;
        }
    }
    class DrawThread extends Thread{
        private boolean touchUp = false;
        @Override
        public void run() {
            while(true){
                if(touchUp)
                {
                    Log.d("DrawThread","start paint ");
                    Canvas canvas = surfaceHolder.lockCanvas();
                    canvas.drawColor(Color.WHITE);
                    rebuildOldLines(canvas);
                    surfaceHolder.unlockCanvasAndPost(canvas);
                    touchUp = false;
                }
            }
        }
    }
}