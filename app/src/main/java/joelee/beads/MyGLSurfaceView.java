package joelee.beads;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by joelee on 2015/6/8.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;
    private Menu mMenu;
    private int count = 0;

    public MyGLSurfaceView(Context context) {
        super(context);
        //mMenu = menu;

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        for (int i=0;i<ANIMATION_STEP_LIST_SIZE;i++){
            ANIMATION_STEP_LIST[i] = (float)Math.abs(Math.log((double)(i+1)/1000)/50);
        }
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    private int ANIMATION_STEP_LIST_SIZE = 100;
    private float ANIMATION_STEP_LIST[] = new float[ANIMATION_STEP_LIST_SIZE];
    private Lock lock = new ReentrantLock();
    //private final Handler handler = new Handler() ;
    private void performACount(){
        count++;
        MenuItem item = mMenu.findItem(R.id.Count_label);
        item.setTitle(String.format("%d",count));

    }

    private void magnetAnimation(int i,boolean moveUP) {
        float nowStep = ANIMATION_STEP_LIST[i];
        if(!moveUP) { //moveDown
            nowStep = -nowStep;
        }
        lock.lock();
        try {
            mRenderer.setMovementAuto(nowStep);
            requestRender();
        }
        catch (Exception ex){
            Log.e("exception","!!!");
        }
        finally {
            lock.unlock();
        }

    }


    public void setmMenu(Menu menu){
        mMenu = menu;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = -(y - mPreviousY);
                Log.v("dy", String.format("%f",dy));
                lock.lock();
                try {
                    if( Math.abs(dy) > 1) {
                        mRenderer.setMovementByTouch( dy/800);
                    }

                    requestRender();
                }
                catch(Exception ex)
                {
                    Log.e("Exception","!!!!!!");
                }
                finally {
                    lock.unlock();
                }


                break;


            case MotionEvent.ACTION_UP:

                if(mRenderer.checkCountOrReturn()){
                    int i=0;
                    performACount();
                    while (mRenderer.needMoreMagnetAnimation()){
                        magnetAnimation(i,false);
                        i++;
                        if(i>99){i = 99;}
                    }
                }

                else {
                    int i=0;
                    while (mRenderer.needMoreMagnetAnimation()){
                        magnetAnimation(i,true);
                        i++;
                        if(i>99){i = 99;}
                    }
                }



                break;
        }
        mPreviousY = y;

        return true;
    }

    public void restCounter(){
        count = 0;
        MenuItem item = mMenu.findItem(R.id.Count_label);
        item.setTitle(String.format("%d",count));
    }


}
