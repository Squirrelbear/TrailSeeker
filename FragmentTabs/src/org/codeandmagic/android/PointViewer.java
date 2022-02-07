package org.codeandmagic.android;

import java.util.List;

import org.codeandmagic.android.R;

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
import android.widget.FrameLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;


public class PointViewer extends MapActivity implements LocationListener {
	private static final int REQUIRE_START_REQUEST = 1;
	private double range;
	private List<TrailNode> nodes;
	private int curNode;
	private Location nextLocation;
	private CustomItemizedOverlay itemizedoverlay;
	private boolean isComplete;
	LocationManager locationManager;
	private String provider;
	private GeoPoint center;
	private int spanLat, spanLon;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.map);
	    
	    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	     Criteria criteria = new Criteria();
	     provider = locationManager.getBestProvider(criteria, true);
	    
	    Bundle extras = getIntent().getExtras();
       	int id = extras.getInt("id");
       	TrailData data;
        int type = extras.getInt("type");
        try
        {
        	data = TrailManager.managerInstance.getFullData(id, type);
        }
        catch(Exception e)
        {
        	finish();
        	return;
        }
        nodes = data.getNodes();
        range = data.getDetectRange();
	    
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    mapView.setSatellite(true);
	    
	    Drawable drawable = this.getResources().getDrawable(R.drawable.nodepin);
	    itemizedoverlay = new CustomItemizedOverlay(drawable, this);
	    isComplete = false;

	    curNode = 0;
	    
	    Intent editIntent = new Intent(this, GetToStartActivity.class);
    	editIntent.putExtra("type", type);
    	editIntent.putExtra("id",  id);
    	startActivityForResult(editIntent, REQUIRE_START_REQUEST);
	}
	
	public void onLocationChanged(Location location) {
		if(nextLocation == null) return;
		double distanceToPoint = location.distanceTo(nextLocation);
		if(!isComplete && distanceToPoint <= range)
		{
			addPoint(curNode++);
		}
	}
	
	public void addPoint(int index)
	{
		MapView mapView = (MapView) findViewById(R.id.mapview);
		List<Overlay> mapOverlays = mapView.getOverlays();
		GeoPoint point = nodes.get(index).getPointAsGeoPoint();
		FrameLayout markerLayout =
                (FrameLayout) getLayoutInflater().inflate(R.layout.marker, null);
	    NumberedOverlay overlayitem = new NumberedOverlay(index+1, point, markerLayout, this);
	    itemizedoverlay.addOverlay(overlayitem);
	    mapOverlays.add(itemizedoverlay);
	    
	    zoomAllAndCentre();
	    mapView.getController().zoomToSpan(spanLat, spanLon);
	    mapView.getController().animateTo(center);
	    
	    if(index >= nodes.size() - 1)
	    {
			isComplete = true;
			nextLocation = null;
	    }
		else
		{
	    	nextLocation = nodes.get(index).getPointAsLocation();
		}
	    
	    openNodeHint(index);
	}
	
	public void openNodeHint(int nodeID)
	{
		String description = nodes.get(nodeID).getDescription();
		Intent hintIntent = new Intent(this, HintActivity.class);
		hintIntent.putExtra("node", "Point " + nodeID);
		hintIntent.putExtra("description", description);
		hintIntent.putExtra("statecomplete", isComplete);
		startActivity(hintIntent);
	}
	
	public void zoomAllAndCentre()
	{
		int minLat = Integer.MAX_VALUE;
		int maxLat = Integer.MIN_VALUE;
		int minLon = Integer.MAX_VALUE;
		int maxLon = Integer.MIN_VALUE;

		for (int i = 0; i < curNode; i++) 
		{ 

		      int lat = nodes.get(i).getPointAsGeoPoint().getLatitudeE6();
		      int lon = nodes.get(i).getPointAsGeoPoint().getLongitudeE6();

		      maxLat = Math.max(lat, maxLat);
		      minLat = Math.min(lat, minLat);
		      maxLon = Math.max(lon, maxLon);
		      minLon = Math.min(lon, minLon);
		 }
		
		spanLat = (int) (Math.abs(maxLat - minLat) * 1.5);
		spanLon = (int) (Math.abs(maxLon - minLon) * 1.5);
		center = new GeoPoint( (maxLat + minLat)/2, (maxLon + minLon)/2 );
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    if (requestCode == REQUIRE_START_REQUEST) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	        	addPoint(curNode++);
	        }
	        else
	        {
	        	// the user elected to quit
	        	finish();
	        }
	    }
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
            	PointViewer.this.finish();
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
	protected boolean isRouteDisplayed() {
	    return false;
	}

	@Override
	public void onProviderDisabled(String arg0) {
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}
}
