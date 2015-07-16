package joelee.beads;

/**
 * Created by joelee on 2015/6/8.
 */
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private Square[]  mSquare;
    private Square    focusSquare;
    private int Square_NUM = 10;
    private float BUTTOM_BOUND = -5.5f;
    private int ANIMATION_STEP_SIZE = 100;


    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    private float[] scratch = new float[16];
    private float[] fixed_Y_list = new float[Square_NUM];
    private float[] animationStepList = new float[ANIMATION_STEP_SIZE];
    private Lock lock = new ReentrantLock();


    private float mY=0f;
    private Context mContext;

    private Deque Square_Loop = new LinkedList<>();


    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        //mTriangle = new Triangle();
        //mSquare   = new Square();
        mSquare   = new Square[Square_NUM];
        //mImage = new Image(mContext);
        Square tmpSquare;
        //SetupImage();
        for(int i=0;i<Square_NUM;i++){
            tmpSquare = new Square(mContext);
            tmpSquare.setOrder(i);
            tmpSquare.setY((float) i - 4.5f); //Give it offset
            fixed_Y_list[i] = (float)i-4.5f;
            Square_Loop.addLast(tmpSquare);
            mSquare[i] = tmpSquare;
            if(tmpSquare.getOrder() == Square_NUM/2){
                focusSquare = tmpSquare;
            }
        }

        for(int i =0; i<ANIMATION_STEP_SIZE;i++){
            animationStepList[i] = (float)Math.abs(Math.log((double)(i+1)/1000)/100);
            Log.v("animationStep",String.format("%f",animationStepList[i]));
        }

    }
    public MyGLRenderer(Context c ){
        mContext = c;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        //float[] scratch = new float[16];

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -6, 0f, 0f, 0f, 0.0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw square
        //mSquare.draw(mMVPMatrix);

        // Create a rotation for the triangle

        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);

        //Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);
        //checkLoopSquare();
        Iterator itr = Square_Loop.iterator();
        //Log.v("Focus nowY", String.format("%f", focusSquare.getY()));


        for(int i=0;i<Square_NUM;i++){
            Square nowSquare = mSquare[i];

            lock.lock();
            try {
                nowSquare.setY(nowSquare.getY() + mY);
            }
            finally {
                lock.unlock();
            }

            //mSquare[i].setY(mSquare[i].getNowY() + mY);

            Matrix.setRotateM(mRotationMatrix, 0, 0, 0, 0, 1.0f);
            Matrix.scaleM(mRotationMatrix,0,0.9f,0.9f,0f);
            Matrix.translateM(mRotationMatrix, 0, 0, nowSquare.getY(), 1.0f);

            // Combine the rotation matrix with the projection and camera view
            // Note that the mMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            // Draw
            nowSquare.draw(scratch);

        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }



    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public  void setMovementByTouch(float y){
        if (focusSquare.getY() > fixed_Y_list[focusSquare.getOrder()-1]  && focusSquare.getY() <= fixed_Y_list[focusSquare.getOrder()]) {
            //Don't let square up
            mY = y;
        }
        else {
            mY = 0.0f;
        }
    }

    public  void setMovementAuto(float y){
        if (focusSquare.getY() > fixed_Y_list[focusSquare.getOrder()-1] ) {
            //Don't let square up
            mY = y;
        }
        else {
            mY = 0.0f;
        }
    }


    private void SquareTurnAround(){
        Square nowFirst = (Square)Square_Loop.getFirst();
        Square nowLast  = (Square)Square_Loop.getLast();

        lock.lock();
        try {
            Square_Loop.addLast(nowFirst);
            Square_Loop.removeFirst();
            resetLoopOrder();
        }
        finally {
            lock.unlock();
        }

    }

    private void resetLoopOrder(){
        Iterator itr = Square_Loop.iterator();
        int i=0;
        while (itr.hasNext()){
            Square tmp = (Square)itr.next();
            tmp.setOrder(i);

            i++;
        }
    }

    public boolean checkCountOrReturn(){
        float STEP = 0.3f;
        if(focusSquare.getY() <= fixed_Y_list[focusSquare.getOrder()]-STEP && focusSquare.getY() >= fixed_Y_list[focusSquare.getOrder()-1]){
            //move Square to it's next pos  //Count!!!!!
            //Log.v("lol", String.format("Square %d %f <= %f", focusSquare.getOrder(), focusSquare.getY(), fixed_Y_list[focusSquare.getOrder()]-STEP));
            Square nextFocus = null;
            //Log.v("lol", String.format("%f != %f", focusSquare.getY(), fixed_Y_list[focusSquare.getOrder() - 1]));
            Iterator iterator = Square_Loop.iterator();
            while (iterator.hasNext()){
                Square tmp = (Square)iterator.next();
                lock.lock();
                try {
                    tmp.setNewY(fixed_Y_list[((tmp.getOrder() - 1) + Square_NUM) % Square_NUM]);
                    if(tmp.getOrder() == focusSquare.getOrder()+1){ //Set focus to next upper Square
                        nextFocus = tmp;
                    }
                }
                finally {
                    lock.unlock();
                }

            }

            SquareTurnAround();
            if(nextFocus!=null) { focusSquare = nextFocus; }
            return true;
        }
        else {
            //back to it fixed pos.
            Log.v("lol", String.format("%f != %f", focusSquare.getY(), fixed_Y_list[focusSquare.getOrder() - 1]));
            Log.v("!!","!!");
            Iterator iterator = Square_Loop.iterator();
            while (iterator.hasNext()){
                Square tmp = (Square)iterator.next();
                lock.lock();
                try {
                    tmp.setNewY(fixed_Y_list[tmp.getOrder()]);
                    //Log.v("lol",String.format("%f != %f",focusSquare.getY(),fixed_Y_list[focusSquare.getOrder()-1]));
                }
                finally {
                    lock.unlock();
                }
            }
            return false;
        }

    }

    public boolean needMoreMagnetAnimation(){
        float dif = Math.abs(focusSquare.getY()-focusSquare.getNewY());
        //Log.v("DIF",String.format("%f",dif));
        if(dif > 0.1f && dif < 0.955f){return true; }

        else {
            Iterator iterator = Square_Loop.iterator();
            lock.lock();
            try {
                while (iterator.hasNext()){
                    Square nowSquare = (Square)iterator.next();
                    nowSquare.applyNewY();
                }
            }
            finally {
                lock.unlock();
            }
            return false;
        }
    }


}