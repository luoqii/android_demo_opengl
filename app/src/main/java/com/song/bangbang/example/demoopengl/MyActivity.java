package com.song.bangbang.example.demoopengl;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR|GLSurfaceView.DEBUG_LOG_GL_CALLS);
        glSurfaceView.setRenderer(new MyRender());
        ((ViewGroup) findViewById(R.id.container)).addView(glSurfaceView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MyRender implements GLSurfaceView.Renderer {

        private final Triangle mTriangle;

        public MyRender() {
            mTriangle = new Triangle();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);

            // make adjustments for screen ratio
            // XXX bysong
            float ratio = (float) width / height;
            gl.glMatrixMode(GL10.GL_PROJECTION);        // set matrix to projection mode
            gl.glLoadIdentity();                        // reset the matrix to its default state
            gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);  // apply the projection matrix
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);

            // Set GL_MODELVIEW transformation mode
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();   // reset the matrix to its default state

            // XXX bysong
            // When using GL_MODELVIEW, you must set the view point
            GLU.gluLookAt(gl, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            mTriangle.draw(gl);
        }
    }

    public static class Triangle {
        // number of coordinates per vertex in this array
        static final int BYTE_PER_FLOAT = 4;
        static final int COORDS_PER_VERTEX = 3;
        static float TRIANGLE_COORDINATES[] = {
                // in counterclockwise order:
                0.0f,  0.622008459f, 0.0f,// top
                -0.5f, -0.311004243f, 0.0f,// bottom left
                0.5f, -0.311004243f, 0.0f // bottom right
        };

        static float COLOR[] = { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f };

        private FloatBuffer mVertexBuffer;

        public Triangle() {
            ByteBuffer vbb = ByteBuffer.allocateDirect(TRIANGLE_COORDINATES.length * COORDS_PER_VERTEX * BYTE_PER_FLOAT);
            vbb.order(ByteOrder.nativeOrder());
            mVertexBuffer = vbb.asFloatBuffer();
            mVertexBuffer.put(TRIANGLE_COORDINATES);
            mVertexBuffer.position(0);
        }

        public void draw(GL10 gl) {
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            gl.glColor4f(COLOR[0], COLOR[1], COLOR[2], COLOR[3]);
            gl.glVertexPointer(COORDS_PER_VERTEX, GL10.GL_FLOAT, 0, mVertexBuffer);

            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);

            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }
    }

}
