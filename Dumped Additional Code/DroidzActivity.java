package org.codeandmagic.android;

import org.codeandmagic.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class DroidzActivity extends Activity implements LocationListener {
    /** Called when the activity is first created. */
	
	private static final String TAG = DroidzActivity.class.getSimpleName();

	private MainGamePanel panel;
	private int id;
	private int type;
	LocationManager locationManager;
	private String provider;
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // force window to stay enabled
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	     Criteria criteria = new Criteria();
	     provider = locationManager.getBestProvider(criteria, true);
        
        Bundle extras = getIntent().getExtras();
       	id = extras.getInt("id");
        type = extras.getInt("type");
       	
		Display display = getWindowManager().getDefaultDisplay();
		// Following two lines require API level 13 or higher
		//Point size = new Point();
		//display.getSize(size);
        // set our MainGamePanel as the View
		panel = new MainGamePanel(this, display.getWidth(), display.getHeight(), this);
        setContentView(panel);
        Log.d(TAG, "View added");
    }

	@Override
	protected void onDestroy() {
		Log.d(TAG, "Destroying...");
		panel.setRunning(false);
		super.onDestroy();
	}
    
   @Override
    protected void onResume()
    {
       super.onResume();
        panel.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
    }
   
   @Override
   protected void onPause()
   {
	   super.onPause();
	   panel.onPause();
		locationManager.removeUpdates(this);
   }

    @Override
    protected void onStop()
    {
        super.onStop();
		Log.d(TAG, "Stopping...");
		panel.setRunning(false);
    }
    
    @Override 
    public void onBackPressed()
    {
    	close();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	menu.add("Exit");
    	menu.add("Cancel");
		return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	     if(item.getTitle().toString().equals("Exit"))
	     {
	    	 close();
	    	 return true;
	     }
	     return super.onOptionsItemSelected(item);
	}
    
    public TrailData getTrailData()
    {
    	return TrailManager.managerInstance.getFullData(id, type);
    }
    
    public void close()
    {
    	new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.exitPointViewTitle)
        .setMessage(R.string.exitPointViewMessage)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	DroidzActivity.this.finish();
            }

        })
        .setNegativeButton(R.string.no,  null)
        .show();
    	
    	
    }
    
    @Override
    public void onLocationChanged(Location location) {
		    panel.handleLocationChanged(location);
	  }
	 
	 @Override
	  public void onStatusChanged(String provider, int status, Bundle extras) {
	  }
	
	  @Override
	  public void onProviderEnabled(String provider) {
	  }
	
	  @Override
	  public void onProviderDisabled(String provider) {
	  }
}