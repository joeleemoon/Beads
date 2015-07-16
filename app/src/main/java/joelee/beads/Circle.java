package joelee.beads;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by joelee on 2015/6/8.
 */
public class Circle {

    private  int mProgram, mPositionHandle, mColorHandle, mMVPMatrixHandle ;
    private FloatBuffer mVertexBuffer;
    private static float uvs[];
    private FloatBuffer uvBuffer;

    private float vertices[] = new float[364 * 3];
    private float Y;
    private float newY;
    private int order;
    private float fixed_pos;
    private Context mContext;
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    //float color[] = { 0.55f, 0.32f, 0.06f, 1.0f };

    private static final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";


    private static final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    /*private static final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "for(int l =0 ; l < 10 ;l++){"+
                    "  gl_FragColor = vec4(0.55,0.32,0.06+l/10.0,0.5);" +
                            "}"+
                    "}";
                    */


    private static final String vs_Image =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 a_texCoord;" +
                    "varying vec2 v_texCoord;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  v_texCoord = a_texCoord;" +
                    "}";
    private static final String fs_Image =
            "precision mediump float;" +
                    "varying vec2 v_texCoord;" +
                    "uniform sampler2D s_texture;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D( s_texture, v_texCoord );" +
                    "}";


/*
    Circle(Context c){
        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = 0;
        Y = 0f;
        newY = 0f;
        fixed_pos = 0f;
        mContext = c;
        //SetupImage();



        for(int i =1; i <364; i++){
            vertices[(i * 3)] = (float) (0.5 * Math.cos((3.14/180) * (float)i ));
            vertices[(i * 3)+ 1] = (float) (0.5 * Math.sin((3.14/180) * (float)i ));
            vertices[(i * 3)+ 2] = 0;
        }


        Log.v("Thread", "" + vertices[0] + "," + vertices[1] + "," + vertices[2]);
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexByteBuffer.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
        //int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);


    }

    */
    /*
    public static int loadShader(int type, String shaderCode){

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
    */
Circle(Context c) {
    vertices[0] = 0;
    vertices[1] = 0;
    vertices[2] = 0;
    Y = 0f;
    newY = 0f;
    fixed_pos = 0f;
    mContext = c;
    SetupImage();


    for (int i = 1; i < 364; i++) {
        vertices[(i * 3)] = (float) (0.5 * Math.cos((3.14 / 180) * (float) i));
        vertices[(i * 3) + 1] = (float) (0.5 * Math.sin((3.14 / 180) * (float) i));
        vertices[(i * 3) + 2] = 0;
    }


    Log.v("Thread", "" + vertices[0] + "," + vertices[1] + "," + vertices[2]);
    ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
    vertexByteBuffer.order(ByteOrder.nativeOrder());
    mVertexBuffer = vertexByteBuffer.asFloatBuffer();
    mVertexBuffer.put(vertices);
    mVertexBuffer.position(0);
    //int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    /*
    int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
    int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

    mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
    GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
    GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
    GLES20.glLinkProgram(mProgram);
    */


    int vertexShader = riGraphicTools.loadShader(GLES20.GL_VERTEX_SHADER, riGraphicTools.vs_Image);
    int fragmentShader = riGraphicTools.loadShader(GLES20.GL_FRAGMENT_SHADER, riGraphicTools.fs_Image);

    riGraphicTools.sp_Image = GLES20.glCreateProgram();             // create empty OpenGL ES Program
    GLES20.glAttachShader(riGraphicTools.sp_Image, vertexShader);   // add the vertex shader to program
    GLES20.glAttachShader(riGraphicTools.sp_Image, fragmentShader); // add the fragment shader to program
    GLES20.glLinkProgram(riGraphicTools.sp_Image);                  // creates OpenGL ES program executables

}



    public void SetupImage()
    {
        // Create our UV coordinates.
        uvs = new float[] {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

        // Generate Textures, if more needed, alter these numbers.
        int[] texturenames = new int[1];
        GLES20.glGenTextures(1, texturenames, 0);

        // Retrieve our image from resources.
        int id = mContext.getResources().getIdentifier("mipmap/ic_launcher_1", null,
                mContext.getPackageName());

        //Log.v("ID",String.format("%d",id));

        // Temporary create a bitmap
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);

        // Bind texture to texturename
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        // Set wrapping mode
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        // We are done using the bitmap so we should recycle it.
        bmp.recycle();

    }







    public void draw (float[] mvpMatrix){
        /*

        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,12
                ,mVertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 364);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        */


        GLES20.glUseProgram(riGraphicTools.sp_Image);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, 3,
                GLES20.GL_FLOAT, false,0
                ,mVertexBuffer);  //12,0

        // get handle to fragment shader's vColor member
        /*
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        */


        int mTexCoordLoc = GLES20.glGetAttribLocation(riGraphicTools.sp_Image, "a_texCoord" );
        Log.v("mTexCoordLoc", String.format("%d", mTexCoordLoc));

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray ( mTexCoordLoc );

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT,
                false,
                0, uvBuffer);




        mMVPMatrixHandle = GLES20.glGetUniformLocation(riGraphicTools.sp_Image, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        int mSamplerLoc = GLES20.glGetUniformLocation (riGraphicTools.sp_Image, "s_texture" );

        // Set the sampler texture unit to 0, where we have saved the texture.
        GLES20.glUniform1i ( mSamplerLoc, 0);
        Log.v("mSampler", String.format("%d", mSamplerLoc));


        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 364);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);

        GLES20.glDisableVertexAttribArray(mTexCoordLoc);










    }

    public void setY(float y){Y = y;}
    public float getY(){return Y;}

    public void setOrder(int i){order = i;}
    public int getOrder(){return order;}

    public void setNewY(float y){newY = y;}
    public float getNewY(){return newY;}
    public void applyNewY(){
        Y = newY ;
    }

}

