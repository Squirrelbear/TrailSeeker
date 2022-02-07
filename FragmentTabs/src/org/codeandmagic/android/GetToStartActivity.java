package org.codeandmagic.android;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class GetToStartActivity extends MapActivity implements LocationListener {
	private double range;
	private Location startLocation;
	LocationManager locationManager;
	private String provider;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.gettostartactivity);
	    
	    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	     Criteria criteria = new Criteria();
	     provider = locationManager.getBestProvider(criteria, true);
	    
	    Bundle extras = getIntent().getExtras();
	    int id = extras.getInt("id");
	    int type = extras.getInt("type");
       
       TrailData summaryData = TrailManager.managerInstance.getSummary(id, type);
       TextView titleLabel = (TextView)findViewById(R.id.lblTitle);
       titleLabel.setText(summaryData.getTitle());
       TextView authorLabel = (TextView)findViewById(R.id.lblAuthor);
  		authorLabel.setText("By: " + summaryData.getAuthor() + " on " + summaryData.getCreationDate());
  		TextView descriptionLabel = (TextView)findViewById(R.id.lblDescription);
  		descriptionLabel.setText(summaryData.getDescription());
  		TextView distancenLabel = (TextView)findViewById(R.id.lblDistance);
  		distancenLabel.setText("You need to get to the start.\nDistance from start: [waiting for GPS]");
  		
  		 range =summaryData.getDetectRange();
         startLocation = new Location("Node");
         startLocation.setLatitude(summaryData.getStartLat());
         startLocation.setLongitude(summaryData.getStartLon());
  		
  		MapView mapView = (MapView) findViewById(R.id.mapview);
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.nodepin);
		CustomItemizedOverlay itemizedoverlay = new CustomItemizedOverlay(drawable);
		
		int lat = (int) (summaryData.getStartLat() * 1E6);
	    int lon = (int) (summaryData.getStartLon() * 1E6);
	    GeoPoint point = new GeoPoint(lat, lon);
		FrameLayout markerLayout =
                (FrameLayout) getLayoutInflater().inflate(R.layout.marker, null);
	    NumberedOverlay overlayitem = new NumberedOverlay(1, point, markerLayout, this);
	    itemizedoverlay.addOverlay(overlayitem);
	    mapOverlays.add(itemizedoverlay);
	    
	    mapView.getController().setCenter(point);
	    mapView.getController().setZoom(16);
	}
	
	public void onLocationChanged(Location location) {
		double distanceToPoint = location.distanceTo(startLocation);
		if(distanceToPoint <= range)
		{
			Intent resultIntent = new Intent();
	    	setResult(Activity.RESULT_OK, resultIntent);
	    	finish();
		}
		else
		{
			TextView distancenLabel = (TextView)findViewById(R.id.lblDistance);
	  		distancenLabel.setText("You need to get to the start.\nDistance from start: " + String.format("%.3f m", distanceToPoint));
		}
	}
	
	public void btnExitPressed(View view)
	{
		close();
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
	
	public void close()
    {
    	new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.exitPointViewTitle)
        .setMessage(R.string.exitPointViewMessage)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	Intent resultIntent = new Intent();
    	    	setResult(Activity.RESULT_CANCELED, resultIntent);
            	GetToStartActivity.this.finish();
            }

        })
        .setNegativeButton(R.string.no,  null)
        .show();
    	
    	
    }
	
	protected void onResume()
    {
       super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
    }
	
	@Override
	   protected void onPause()
	   {
		   super.onPause();
			locationManager.removeUpdates(this);
	   }

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
