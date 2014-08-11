package com.song.bangbang.example.demoopengl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.song.bangbang.example.demoopengl.CubeActivity.MyGlSurfaceView.MyRender;

import java.io.IOException;
import java.io.InputStream;
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

	static class MyGlSurfaceView extends GLSurfaceView {

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

				View p = (View) getParent();
				p = (View) p.getParent();
				((TextView) (p.findViewById(R.id.textView)))
						.setText("mAngleX: " + mRenderer.mAngleX
								+ "\nmAngleY: " + mRenderer.mAngleY);
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
				mModel = new TextCure(context);
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

				mModel.onSurfaceCreated(gl, config);
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

				mModel.onSurfaceChanged(gl, width, height);
			}

			@Override
			public void onDrawFrame(GL10 gl) {
				gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

				// Set GL_MODELVIEW transformation mode
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadIdentity(); // reset the matrix to its default state

				// XXX bysong why we need this ???
				// When using GL_MODELVIEW, you must set the view point
//				 GLU.gluLookAt(gl, 0, 0, 5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

				gl.glRotatef(mAngleX, 0, 1, 0);
				gl.glRotatef(mAngleY, 1, 0, 0);

				mModel.onDrawFrame(gl);
			}
		}

		public static class Cube {
            public static final int PLANE_FRONT = 0;
			public static final int PLANE_BACK = 1;
			public static final int PLANE_LEFT = 2;
			public static final int PLANE_RIGHT = 3;
			public static final int PLANE_TOP = 4;
			public static final int PLANE_BOTTOM = 5;
			public static final int PLANE_COUNT = 6;

			public static final float ZERO = 0.0f;
			public static final float ONE = 0.99f;
			public static final float W = 1f;
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
					0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0,
					// front plane
					0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1,
					// back plane
					0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 0,
					// front plane
					0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, };
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
					0, 4, 7, 0, 7, 3,
					// bottom plane
					5, 1, 2, 5, 2, 6, };

			private FloatBuffer mVbb;
			private FloatBuffer mCbb;
			private FloatBuffer mTbb;
			private ShortBuffer mIbb;

			private Context mContext;

            public static void bindTexture(Context context, int which) {
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
                InputStream is = context.getResources().openRawResource(res);
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

                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
                bitmap.recycle();
            }

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

			public void onSurfaceCreated(GL10 gl, EGLConfig config) {
				// TODO Auto-generated method stub

			}

			public void onSurfaceChanged(GL10 gl, int width, int height) {
				// TODO Auto-generated method stub

			}

			public void onDrawFrame(GL10 gl) {
				gl.glShadeModel(GL10.GL_FLAT);
				gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glVertexPointer(COORD_PER_VERTEX, GL10.GL_FLOAT, 0, mVbb);

				gl.glEnable(GL10.GL_CULL_FACE);
				gl.glCullFace(GL10.GL_BACK);

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
					bindTexture(PLANE_BACK);
					mIbb.position(0);
					gl.glDrawElements(GL10.GL_TRIANGLES, 6,
							GL10.GL_UNSIGNED_SHORT, mIbb);
					// front plane
					bindTexture(PLANE_FRONT);
					mIbb.position(6);
					gl.glDrawElements(GL10.GL_TRIANGLES, 6,
							GL10.GL_UNSIGNED_SHORT, mIbb);
					// left plane
					bindTexture(PLANE_LEFT);
					mIbb.position(12);
					gl.glDrawElements(GL10.GL_TRIANGLES, 6,
							GL10.GL_UNSIGNED_SHORT, mIbb);
					// right plane
					bindTexture(PLANE_RIGHT);
					mIbb.position(18);
					gl.glDrawElements(GL10.GL_TRIANGLES, 6,
							GL10.GL_UNSIGNED_SHORT, mIbb);
					// top plane
					bindTexture(PLANE_TOP);
					mIbb.position(24);
					gl.glDrawElements(GL10.GL_TRIANGLES, 6,
							GL10.GL_UNSIGNED_SHORT, mIbb);
					// botttom plane
					bindTexture(PLANE_BOTTOM);
					mIbb.position(30);
					gl.glDrawElements(GL10.GL_TRIANGLES, 6,
							GL10.GL_UNSIGNED_SHORT, mIbb);
				}
			}

            public void bindTexture(int which) {
                bindTexture(mContext, which);
            }

			Bitmap getBitmap(int whichPlane) {
				Config config = Config.ARGB_8888;
				int w = 128;
				int h = 128;
				Bitmap b = Bitmap.createBitmap(w, h, config);
				// b.eraseColor(0);
				Canvas c = new Canvas(b);
				Paint p = new TextPaint();
				p.setColor(Color.RED);
				p.setTextSize(20);
				p.setAntiAlias(true);
				String familyName = "Times New Roman";
				Typeface font = Typeface.create(familyName, Typeface.NORMAL);

				p.setColor(Color.BLACK);
				p.setTypeface(font);
				p.setTextAlign(Align.CENTER);
				c.drawColor(Color.TRANSPARENT);
				 c.drawColor(Color.WHITE);

				c.drawText(toPlaneStr(whichPlane), w/2, h/2, p);

				return b;
			}
			
			String toPlaneStr(int whichPlane){
				String str = "";
				if (PLANE_BACK == whichPlane) {
					str = "BACK";
				}
				if (PLANE_FRONT == whichPlane) {
					str = "FRONT";
				}
				if (PLANE_LEFT == whichPlane) {
					str = "LEFT";
				}
				if (PLANE_RIGHT == whichPlane) {
					str = "RIGHT";
				}
				if (PLANE_BOTTOM == whichPlane) {
					str = "BOTTOM";
				}
				if (PLANE_TOP == whichPlane) {
					str = "TOP";
				}
				
				return str;
			}
		}

		public static class TextCure extends Cube {
			public static final int TEXUT_UNIT_COUNT = PLANE_COUNT;

			public static final float[] VERTEX = new float[] {
					// front
					ZERO, ONE, ONE, W,
					ZERO, ZERO, ONE, W, 
					ONE, ZERO, ONE, W,
					ONE, ONE, ONE, W,
					// back
					ZERO, ONE, ZERO, W, 
					ZERO, ZERO, ZERO, W, 
					ONE, ZERO, ZERO,	W, 
					ONE, ONE, ZERO, W,
					// left
					ZERO, ONE, ZERO, W, 
					ZERO, ZERO, ZERO, W, 
					ZERO, ZERO, ONE, W, 
					ZERO, ONE, ONE, W, 
                 // right
					ONE, ONE, ZERO, W, 
					ONE, ZERO, ZERO, W, 
					ONE, ZERO, ONE, W, 
					ONE, ONE, ONE, W, 
					// top
					ZERO, ONE, ZERO, W,
					ZERO, ONE, ONE, W,
					ONE, ONE, ONE, W,
					ONE, ONE, ZERO, W,
					// bottom
					ZERO, ZERO, ZERO, W,
					ZERO, ZERO, ONE, W,
					ONE, ZERO, ONE, W,
					ONE, ZERO, ZERO, W,
			};

			public static final float[] TEX = new float[] {
					// front
                    ONE, ZERO,ONE, ONE, ZERO, ONE, ZERO, ZERO,
                    // back
                    ZERO, ZERO, ZERO, ONE, ONE, ONE, ONE, ZERO,
					// left
                    ONE, ZERO,ONE, ONE, ZERO, ONE, ZERO, ZERO,
					// right
                    ZERO, ZERO, ZERO, ONE, ONE, ONE, ONE, ZERO,
					// top
                    ONE, ZERO,ONE, ONE, ZERO, ONE, ZERO, ZERO,
					// bottom
                    ZERO, ZERO, ZERO, ONE, ONE, ONE, ONE, ZERO, };
			public static final byte[] INDEX_FRONT = new byte[] {
					// front
					0, 3, 2, 1,
                    // back
					0, 0, 0, 0,
					// left
					0, 0, 0, 0,
					// right
					0, 0, 0, 0,
					// top
					0, 0, 0, 0,
					// bottom
					0, 0, 0, 0, };

			public static final byte[] INDEX_BACK = new byte[] {
					// front
					0, 0, 0, 0,
					// back
					4, 5, 7, 6,
					// left
					0, 0, 0, 0,
					// right
					0, 0, 0, 0,
					// top
					0, 0, 0, 0,
					// bottom
					0, 0, 0, 0, };

			public static final byte[] INDEX_LEFT = new byte[] {
					// front
					0, 0, 0, 0,
					// back
					0, 0, 0, 0,
					// left
					8, 11, 10,9,
                    // left
					0, 0, 0, 0,
					// top
					0, 0, 0, 0,
					// bottom
					0, 0, 0, 0,};
			public static final byte[] INDEX_RIGHT = new byte[] {
				// front
				0, 0, 0, 0,
				// back
				0, 0, 0, 0,
				// left
				0, 0, 0, 0,
				// left
				12, 13, 15, 14,
				// top
				0, 0, 0, 0,
				// bottom
				0, 0, 0, 0,};

			public static final byte[] INDEX_TOP = new byte[] {
				// front
				0, 0, 0, 0,
				// back
				0, 0, 0, 0,
				// left
				0, 0, 0, 0,
				// left
				0, 0, 0, 0,
				// top
				16, 19, 18,17,
                    // bottom
				0, 0, 0, 0,};
			public static final byte[] INDEX_BOTTOM = new byte[] {
				// front
				0, 0, 0, 0,
				// back
				0, 0, 0, 0,
				// left
				0, 0, 0, 0,
				// right
				0, 0, 0, 0,
				// top
				0, 0, 0, 0,
				// bottom
				20, 21, 23, 22,};

			private FloatBuffer mVertext;
			private FloatBuffer mText;

			private ByteBuffer mIndexFront;
			private ByteBuffer mIndexBack;
			private ByteBuffer mIndexLeft;

			private int[] mTextures;

			private ByteBuffer mIndexRight;

			private ByteBuffer mIndexTop;

			private ByteBuffer mIndexBottom;

			private FloatBuffer mColor;

			public TextCure(Context context) {
				super(context);
				
				ByteBuffer bb = ByteBuffer.allocateDirect(VERTEX.length * 4);
				bb.order(ByteOrder.nativeOrder());
				mVertext = bb.asFloatBuffer();
				mVertext.put(VERTEX);
				mVertext.position(0);

				bb = ByteBuffer.allocateDirect(VERTEX.length * 4);
				bb.order(ByteOrder.nativeOrder());
				mColor = bb.asFloatBuffer();
				mColor.put(VERTEX);
				mColor.position(0);

				bb = ByteBuffer.allocateDirect(TEX.length * 4);
				bb.order(ByteOrder.nativeOrder());				
				mText = bb.asFloatBuffer();
				mText.put(TEX);
				mText.position(0);

				bb = ByteBuffer.allocateDirect(INDEX_FRONT.length * 1);
				bb.order(ByteOrder.nativeOrder());		
				bb.put(INDEX_FRONT);
				mIndexFront = bb;
				mIndexFront.position(0);
				
				bb = ByteBuffer.allocateDirect(INDEX_BACK.length * 1);
				bb.order(ByteOrder.nativeOrder());				
				bb.put(INDEX_BACK);
				mIndexBack = bb;
				mIndexBack.position(0);

				bb = ByteBuffer.allocateDirect(INDEX_LEFT.length * 1);
				bb.order(ByteOrder.nativeOrder());				
				bb.put(INDEX_LEFT);
				mIndexLeft = bb;
				mIndexLeft.position(0);
				
				bb = ByteBuffer.allocateDirect(INDEX_RIGHT.length * 1);
				bb.order(ByteOrder.nativeOrder());				
				bb.put(INDEX_RIGHT);
				mIndexRight = bb;
				mIndexRight.position(0);
				
				bb = ByteBuffer.allocateDirect(INDEX_TOP.length * 1);
				bb.order(ByteOrder.nativeOrder());				
				bb.put(INDEX_TOP);
				mIndexTop = bb;
				mIndexTop.position(0);
				
				bb = ByteBuffer.allocateDirect(INDEX_BOTTOM.length * 1);
				bb.order(ByteOrder.nativeOrder());				
				bb.put(INDEX_BOTTOM);
				mIndexBottom = bb;
				mIndexBottom.position(0);
			}

			@Override
			public void onSurfaceCreated(GL10 gl, EGLConfig config) {
				// TODO Auto-generated method stub
				super.onSurfaceCreated(gl, config);

				IntBuffer textures = IntBuffer.allocate(TEXUT_UNIT_COUNT);
				gl.glGenTextures(TEXUT_UNIT_COUNT, textures);
				mTextures = textures.array();

				int textUnit = PLANE_FRONT;
                gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[textUnit]);
                bindTexture(textUnit);
                gl.glTexParameterx(GL10.GL_TEXTURE_2D,
                        GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
                gl.glTexParameterx(GL10.GL_TEXTURE_2D,
                        GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);

                textUnit = PLANE_BACK;
				gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[textUnit]);
				bindTexture(textUnit);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);

				textUnit = PLANE_LEFT;
				gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[textUnit]);
				bindTexture(textUnit);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);

				textUnit = PLANE_RIGHT;
				gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[textUnit]);
				bindTexture(textUnit);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);

				textUnit = PLANE_TOP;
				gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[textUnit]);
				bindTexture(textUnit);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D,
						GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);

				textUnit = PLANE_BOTTOM;
				gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[textUnit]);
				bindTexture(textUnit);
			}

			@Override
			public void onDrawFrame(GL10 gl) {
				// super.onDrawFrame(gl);
				gl.glShadeModel(GL10.GL_SMOOTH);

				gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glVertexPointer(COORD_PER_VERTEX, GL10.GL_FLOAT, 0, mVertext);

//				gl.glEnable(GL10.GL_CULL_FACE);
//				gl.glCullFace(GL10.GL_BACK);

				gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
				gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColor);

				gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mText);

				gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[PLANE_FRONT]);
				gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 4,
						GL10.GL_UNSIGNED_BYTE, mIndexFront);

				gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[PLANE_BACK]);
				gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 8,
						GL10.GL_UNSIGNED_BYTE, mIndexBack);

				gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[PLANE_LEFT]);
				gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 12,
						GL10.GL_UNSIGNED_BYTE, mIndexLeft);

				gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[PLANE_RIGHT]);
				gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 16,
						GL10.GL_UNSIGNED_BYTE, mIndexRight);

				gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[PLANE_TOP]);
				gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 20,
						GL10.GL_UNSIGNED_BYTE, mIndexTop);

//				gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextures[PLANE_BOTTOM]);
//				gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 24,
//						GL10.GL_UNSIGNED_BYTE, mIndexBottom);
				
			}
		}
	}


}
