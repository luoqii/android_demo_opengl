package com.song.bangbang.example.demoopengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.song.bangbang.example.demoopengl.CubeActivity.MyGlSurfaceView.MyRender;

public class CubeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);

		GLSurfaceView glSurfaceView = new MyGlSurfaceView(this);
		glSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR
				| GLSurfaceView.DEBUG_LOG_GL_CALLS);
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

	public static class MyGlSurfaceView extends GLSurfaceView {

		private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
		private final float TRACKBALL_SCALE_FACTOR = 36.0f;
		private MyRender mRenderer;
		private float mPreviousX;
		private float mPreviousY;

		public MyGlSurfaceView(Context context) {
			super(context);
		}

		@Override
		public void setRenderer(Renderer renderer) {
			// TODO Auto-generated method stub
			super.setRenderer(renderer);
			mRenderer = (MyRender) renderer;
			setRenderMode(RENDERMODE_WHEN_DIRTY);
		}

		@Override
		public boolean onTrackballEvent(MotionEvent e) {
			mRenderer.mAngleX += e.getX() * TRACKBALL_SCALE_FACTOR;
			mRenderer.mAngleY += e.getY() * TRACKBALL_SCALE_FACTOR;
			requestRender();
			return true;
		}

		@Override
		public boolean onTouchEvent(MotionEvent e) {
			float x = e.getX();
			float y = e.getY();
			switch (e.getAction()) {
			case MotionEvent.ACTION_MOVE:
				float dx = x - mPreviousX;
				float dy = y - mPreviousY;
				mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
				mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
				requestRender();
			}
			mPreviousX = x;
			mPreviousY = y;
			return true;
		}

		public static class MyRender implements GLSurfaceView.Renderer {

			private Cube mModel;

			public float mAngleX;
			public float mAngleY;

			public MyRender(Context context) {

				mModel = new Cube(context);
			}

			@Override
			public void onSurfaceCreated(GL10 gl, EGLConfig config) {

				gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
				
				gl.glEnable(GL10.GL_DEPTH_TEST);

				gl.glEnable(GL10.GL_TEXTURE_2D);
				// int[] textures = new int[1];
				// gl.glGenTextures(1, textures, 0);
				// gl.glActiveTexture(GL10.GL_TEXTURE0);
				// gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
				// min & mag
				gl.glTexParameterf(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
				// wraps
				// gl.glTexParameterx(GL10.GL_TEXTURE_2D,
				// GL10.GL_TEXTURE_WRAP_S,
				// GL10.GL_REPEAT);
				// gl.glTexParameterx(GL10.GL_TEXTURE_2D,
				// GL10.GL_TEXTURE_WRAP_T,
				// GL10.GL_REPEAT);
				// // env
				// gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				// GL10.GL_REPLACE);
			}

			@Override
			public void onSurfaceChanged(GL10 gl, int width, int height) {
				gl.glViewport(0, 0, width, height);

				// make adjustments for screen ratio
				// XXX bysong why we need this ???
				float ratio = (float) width / height;
				gl.glMatrixMode(GL10.GL_PROJECTION); // set matrix to projection
														// mode
				gl.glLoadIdentity(); // reset the matrix to its default state
//				 gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7); // apply the
				// projection matrix
			}

			@Override
			public void onDrawFrame(GL10 gl) {
				gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

				// Set GL_MODELVIEW transformation mode
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadIdentity(); // reset the matrix to its default state

				// XXX bysong why we need this ???
				// When using GL_MODELVIEW, you must set the view point
//				 GLU.gluLookAt(gl, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
				
				gl.glRotatef(mAngleX, 0, 1, 0);
				gl.glRotatef(mAngleY, 1, 0, 0);

				mModel.draw(gl);
			}
		}

		public static class Cube {
			public static final int PLANE_BACK = 1;
			public static final int PLANE_FRONT = 2;
			public static final int PLANE_LEFT = 3;
			public static final int PLANE_RIGHT = 4;
			public static final int PLANE_TOP = 5;
			public static final int PLANE_BOTTOM = 6;

			public static final float ZERO = 0.f;
			public static final float ONE = 1f;
			public static final float W = 2f;
			public static final int BYTE_PER_FLOAT = 4;
			public static final int BYTE_PER_SHORT = 2;

			public static final int COORD_PER_VERTEX = 4;
			public static final float[] VERTEX = new float[] {
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
					ONE, ONE, ONE, W, };
			public static final int TEX_PER_VERTEX = 3;
			public static final float[] TEX = new float[] {
					// back plane
					// left top
					0, 1, 0,
					// left bottom
					0, 0, 0,
					// right bottom
					1, 0, 0,
					// right top
					1, 1, 0,
					// front plane
					// left top
					0, 1, 1,
					// left bottom
					0, 0, 1,
					// right bottom
					1, 0, 1, 
					// right top
					1, 1, 1, };
			// ccw
			public static final short[] INDEX = new short[] {
					// back plane
					0, 2, 1, 0, 3, 2,
					// front plane
					4, 5, 6, 4, 6, 7,
					// left plane
					0, 1, 5, 0, 5, 4,
					// right plane
					7, 6, 2, 7, 2, 3,
					// top plane
					0, 4, 7, 0, 7, 4,
					// bottom plane
					5, 1, 2, 5, 2, 6, };

			private FloatBuffer mVbb;
			private FloatBuffer mCbb;
			private FloatBuffer mTbb;
			private ShortBuffer mIbb;

			private Context mContext;

			public Cube(Context context) {
				mContext = context;

				ByteBuffer bb = ByteBuffer.allocateDirect(VERTEX.length
						* BYTE_PER_FLOAT);
				bb.order(ByteOrder.nativeOrder());

				mVbb = bb.asFloatBuffer();
				mVbb.put(VERTEX);
				mVbb.position(0);

				bb = ByteBuffer.allocateDirect(VERTEX.length * BYTE_PER_FLOAT);
				bb.order(ByteOrder.nativeOrder());

				mCbb = bb.asFloatBuffer();
				mCbb.put(VERTEX);
				mCbb.position(0);
				
				bb = ByteBuffer.allocateDirect(TEX.length * BYTE_PER_FLOAT);
				bb.order(ByteOrder.nativeOrder());

				mTbb = bb.asFloatBuffer();
				mTbb.put(TEX);
				mTbb.position(0);

				bb = ByteBuffer.allocateDirect(INDEX.length * BYTE_PER_SHORT);
				bb.order(ByteOrder.nativeOrder());
				mIbb = bb.asShortBuffer();
				mIbb.put(INDEX);
				mIbb.position(0);
			}

			public void draw(GL10 gl) {
				gl.glShadeModel(GL10.GL_SMOOTH);
				gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glVertexPointer(COORD_PER_VERTEX, GL10.GL_FLOAT, 0, mVbb);

				gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
				gl.glColorPointer(COORD_PER_VERTEX, GL10.GL_FLOAT, 0, mCbb);

				gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				gl.glTexCoordPointer(TEX_PER_VERTEX, GL10.GL_FLOAT, 0, mTbb);

				{
					// mIbb.position(0);
					// gl.glDrawElements(GL10.GL_TRIANGLES, INDEX.length,
					// GL10.GL_UNSIGNED_SHORT, mIbb);
				}

				{
					// back plane
					bintTexture(PLANE_BACK);
					mIbb.position(0);
					gl.glDrawElements(GL10.GL_TRIANGLES, 6,
							GL10.GL_UNSIGNED_SHORT, mIbb);
					// front plane
					bintTexture(PLANE_FRONT);
					mIbb.position(6);
					gl.glDrawElements(GL10.GL_TRIANGLES, 6,
							GL10.GL_UNSIGNED_SHORT, mIbb);
					// left plane
					bintTexture(PLANE_LEFT);
					mIbb.position(12);
					gl.glDrawElements(GL10.GL_TRIANGLES, 6,
							GL10.GL_UNSIGNED_SHORT, mIbb);
					// right plane
					bintTexture(PLANE_RIGHT);
					mIbb.position(18);
					gl.glDrawElements(GL10.GL_TRIANGLES, 6,
							GL10.GL_UNSIGNED_SHORT, mIbb);
					// top plane
					bintTexture(PLANE_TOP);
					mIbb.position(24);
					gl.glDrawElements(GL10.GL_TRIANGLES, 6,
							GL10.GL_UNSIGNED_SHORT, mIbb);
					// botttom plane
					bintTexture(PLANE_BOTTOM);
					mIbb.position(30);
					gl.glDrawElements(GL10.GL_TRIANGLES, 6,
							GL10.GL_UNSIGNED_SHORT, mIbb);
				}
			}

			private void bintTexture(int which) {
				int res = R.raw.robot;
				if (PLANE_BACK == which) {
					res = R.raw.robot_back;
				}
				if (PLANE_FRONT == which) {
					res = R.raw.robot_front;
				}
				if (PLANE_LEFT == which) {
					res = R.raw.robot_left;
				}
				if (PLANE_RIGHT == which) {
					res = R.raw.robot_right;
				}
				if (PLANE_BOTTOM == which) {
					res = R.raw.robot_bottom;
				}
				if (PLANE_TOP == which) {
					res = R.raw.robot_top;
				}
				InputStream is = mContext.getResources().openRawResource(res);
				Bitmap bitmap;
				try {
					bitmap = BitmapFactory.decodeStream(is);
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						// Ignore.
					}
				}
				// bitmap =
				// ((BitmapDrawable)mContext.getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();
				// bitmap = getBitmap(which);

				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
				bitmap.recycle();
			}

			Bitmap getBitmap(int which) {
				Config config = Config.ARGB_8888;
				Bitmap b = Bitmap.createBitmap(128, 128, config);
				// b.eraseColor(0);
				Canvas c = new Canvas(b);
				Paint paint = new TextPaint();
				paint.setColor(Color.BLACK);
				paint.setTextSize(30);
				paint.setAntiAlias(true);
				paint.setARGB(0xff, 0x00, 0x00, 0x00);
				c.drawColor(Color.WHITE);
				c.drawText(which + " ", 0, 0, paint);

				return b;
			}
		}
	}
}
