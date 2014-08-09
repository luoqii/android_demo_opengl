package com.song.bangbang.example.demoopengl;

import android.app.Activity;
import android.content.Context;
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
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class CubeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR|GLSurfaceView.DEBUG_LOG_GL_CALLS);
        glSurfaceView.setRenderer(new MyRender(this));
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

        private Cube mModel;

        public MyRender(Context context) {

            mModel = new Cube(context);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);

            // make adjustments for screen ratio
            // XXX bysong why we need this ???
            float ratio = (float) width / height;
            gl.glMatrixMode(GL10.GL_PROJECTION);        // set matrix to projection mode
            gl.glLoadIdentity();                        // reset the matrix to its default state
//            gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);  // apply the projection matrix
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);

            // Set GL_MODELVIEW transformation mode
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();   // reset the matrix to its default state

            // XXX bysong why we need this ???
            // When using GL_MODELVIEW, you must set the view point
//            GLU.gluLookAt(gl, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

            mModel.draw(gl);
        }
    }

    public static class Cube {
    	public static final float ZERO = 0.f;
    	public static final float ONE = 1.f;
    	public static final float W = 1.f;
        public  static final int BYTE_PER_FLOAT = 4;
        public  static final int BYTE_PER_SHORT = 2;
       
       public static final int COORD_PER_VERTEX = 4;
    	public static final float[] VERTEX = new float[]{
    		// back plane
    		// left top
    		ZERO, ONE, ZERO, W,
    		// left bottom
    		ZERO, ZERO, ZERO, W,
    		// right bottom
    		ONE, ZERO, ZERO, W,
    		// right top
    		ONE, ONE, ZERO, W,
    		// front plane
    		// left top
    		ZERO, ONE, ONE, W,
    		// left bottom
    		ZERO, ZERO, ONE, W,
    		// right bottom
    		ONE, ZERO, ONE, W,
    		// right top
    		ONE, ONE, ONE, W,
    	};
    	// ccw
    	public static final short[] INDEX = new short[] {
    		// back plane
    		0, 2, 1,
    		0, 3, 2,
    		// front plane
    		4, 5, 6,
    		4, 6, 7,
    		// left plane
    		0, 1, 5,
    		0, 5, 4,
    		// right plane
    		7, 6, 2,
    		7, 2, 3,
    		// top plane
    		0, 4, 7,
    		0, 7, 4,
    		// bottom plane
    		5, 1, 2,
    		5, 2, 6,
    	};
    	
    	private FloatBuffer mVbb;
    	private FloatBuffer mCbb;
    	private ShortBuffer mIbb;
    	
    	private Context mContext;

		public Cube(Context context){
    		mContext = context;
    		
    		ByteBuffer bb = ByteBuffer.allocateDirect(VERTEX.length * BYTE_PER_FLOAT);
    		bb.order(ByteOrder.nativeOrder());
    		
    		mVbb = bb.asFloatBuffer();
    		mVbb.put(VERTEX);
    		mVbb.position(0);
    		
    		bb = ByteBuffer.allocateDirect(VERTEX.length * BYTE_PER_FLOAT);
    		bb.order(ByteOrder.nativeOrder());
    		
    		mCbb = bb.asFloatBuffer();
    		mCbb.put(VERTEX);
    		mCbb.position(0);
    		
    		bb = ByteBuffer.allocateDirect(INDEX.length * BYTE_PER_SHORT);
    		bb.order(ByteOrder.nativeOrder());
    		mIbb = bb.asShortBuffer();
    		mIbb.put(INDEX);
    		mIbb.position(0);
    	}
		
		public void draw(GL10 gl){
			gl.glShadeModel(GL10.GL_SMOOTH);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			
			gl.glVertexPointer(COORD_PER_VERTEX, GL10.GL_FLOAT, 0, mVbb);
			gl.glColorPointer(COORD_PER_VERTEX, GL10.GL_FLOAT, 0, mCbb);
			
//			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);
			gl.glDrawElements(GL10.GL_TRIANGLES, INDEX.length, GL10.GL_UNSIGNED_SHORT, mIbb);
		}
    }

}
