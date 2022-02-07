package org.codeandmagic.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * This is the main surface that handles the ontouch events and draws
 * the image to the screen.
 */
public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private static final String TAG = MainGamePanel.class.getSimpleName();
	
	private MainThread thread;
	private int screenWidth, screenHeight;
	private DroidzActivity activity;
	private TrailView view;
	private boolean mGameIsRunning;
	
	public MainGamePanel(Context context, int screenWidth, int screenHeight, DroidzActivity activity) {
		super(context);
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
		// reference the controlling activity
		this.activity = activity;
		
		// create the game loop thread
		thread = new MainThread(getHolder(), this);
		mGameIsRunning = false;
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		view = new TrailView(this.screenWidth, this.screenHeight, activity.getTrailData(), this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// at this point the surface is created and
		// we can safely start the game loop
		start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		thread.setRunning(false);
		thread.onResume();
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// try again shutting down the thread
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
		
		view.handleTouchEvent(event.getX(), event.getY());
		
		return super.onTouchEvent(event);
	}

	public void update(long elapsedTime)
	{
		view.update(elapsedTime);
	}
	
	public void render(Canvas canvas)
	{
		// clear the backbuffer
		canvas.drawColor(Color.WHITE);
		
		view.draw(canvas);
		
		 //Log.d(TAG, "Render done");
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		render(canvas);
	}
	
	public void close()
	{
		activity.close();
	}
	
	public Activity getActivity()
	{
		return activity;
	}
	
	public void setRunning(boolean running)
	{
		//thread.setRunning(running);
		mGameIsRunning = running;
		
		if(!running)
			thread.onPause();
		
	}	
	
	public void start() {
	    if (!mGameIsRunning) {
			thread.setRunning(true);
	        thread.start();
	        mGameIsRunning = true;
	    } else {
	        thread.onResume();
	    }
	}

	public void onPause()
	{
		thread.onPause();
	}

	public void onResume() {
		thread.onResume();
	}
	
	  public void handleLocationChanged(Location location) {
		    view.handleLocationChanged(location);
	  }
}
