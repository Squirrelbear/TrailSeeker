package org.codeandmagic.android;

import java.util.ArrayList;
import java.util.List;

import org.codeandmagic.android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;

public class CreateTrailActivity extends Activity implements LocationListener {
	private static final String TAG = "createtrail";
	static final int ADD_NODE_REQUEST = 1; 
	static final int EDIT_NODE_REQUEST = 2; 
	private List<TrailNode> trailNodes;
	private String[] nodeTextList;
	LocationManager locationManager;
	private String provider;
	private double lastLat, lastLon;
	private boolean gpsFirstPoint;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {         

       super.onCreate(savedInstanceState); 
       
       locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
       Criteria criteria = new Criteria();
       provider = locationManager.getBestProvider(criteria, false);
       
       gpsFirstPoint = false;
       Location location = locationManager.getLastKnownLocation(provider);
       if(location != null)
       {
    	   gpsFirstPoint = true;
    	   lastLat = location.getLatitude();
    	   lastLon = location.getLongitude();
       }
       
       this.requestWindowFeature(Window.FEATURE_NO_TITLE);
       setContentView(R.layout.createactivity);
       
       nodeTextList = new String[0];
       trailNodes = new ArrayList<TrailNode>();
       Log.d(TAG, "Loaded: " + TAG);
   }
	
	/* Request updates at startup */
	  @Override
	  protected void onResume() {
	    super.onResume();
	    locationManager.requestLocationUpdates(provider, 400, 1, this);
	  }
	  
	  /* Remove the locationlistener updates when Activity is paused */
	  @Override
	  protected void onPause() {
	    super.onPause();
	    locationManager.removeUpdates(this);
	  }

	  @Override
	  public void onLocationChanged(Location location) {
	    lastLat = (float) (location.getLatitude());
	    lastLon = (float) (location.getLongitude());
	  }
	
	public void btnAddPressed(View view) {
		if(!gpsFirstPoint)
		{
			Toast.makeText(this,"Error! GPS unavailable.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Create TrailNode
		TrailNode node = new TrailNode(trailNodes.size(), lastLat, lastLon, "");
		
		// perform edit on node with flag create
		// flag create means if canceled the remove action is performed
		editNode(ADD_NODE_REQUEST, node);
	}
	
	public void btnRemovePressed(View view) {
		if(nodeTextList.length == 0)
		{
			Toast.makeText(this,"Error! Please add a point first.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		 new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.removeTitle)
        .setMessage(R.string.removeMessage)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	// get the id of the element to be removed
        		Spinner spinner = (Spinner)findViewById(R.id.pointListSpinner);
        		int rmIndex = (int) spinner.getSelectedItemId();
        		
        		// call remove on the list element
        		removeNode(rmIndex);
            }

        })
        .setNegativeButton(R.string.no, null)
        .show();
	}
	
	public void btnEditPressed(View view) {
		if(nodeTextList.length == 0)
		{
			Toast.makeText(this,"Error! Please add a point first.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// determine id
		Spinner spinner = (Spinner)findViewById(R.id.pointListSpinner);
		int editIndex = (int) spinner.getSelectedItemId();
		
		// get TrailNode data
		TrailNode node = trailNodes.get(editIndex);
		
		// perform edit with flag create disabled
		editNode(EDIT_NODE_REQUEST, node);
	}
	
	public void btnFinishPressed(View view) {
		if(!validTrail())
			return;
		
		new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.finishCreateTitle)
        .setMessage(R.string.finishCreateMessage)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            	// Create the Trail and save it
            	if(validTrail())
            	{
            		createTrail();
            		
            		//Stop the activity
                	CreateTrailActivity.this.finish(); 
            	}
            }

        })
        .setNegativeButton(R.string.no, null)
        .show();

	}
	
	// http://stackoverflow.com/questions/2257963/how-to-show-a-dialog-to-confirm-that-the-user-wishes-to-exit-an-android-activity
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    //Handle the back button
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	        //Ask the user if they want to quit
	        new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle(R.string.quitCreateTitle)
	        .setMessage(R.string.quitCreateMessage)
	        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {

	                //Stop the activity
	            	CreateTrailActivity.this.finish();    
	            }

	        })
	        .setNegativeButton(R.string.no, null)
	        .show();

	        return true;
	    }
	    else {
	        return super.onKeyDown(keyCode, event);
	    }

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    if (requestCode == ADD_NODE_REQUEST) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	            TrailNode node = new TrailNode();
	            node.setID(data.getIntExtra("id", 0));
	            node.setGeoPoint(data.getDoubleExtra("lat", 0), data.getDoubleExtra("lon", 0));
	            node.setDescription(data.getStringExtra("description"));
	            trailNodes.add(node);
	            String nodeListEntry = "Point " + (node.getID()+1) + " (" + String.format("%.3f, %.3f", node.getLat(), node.getLon()) + ")";
	            addNodeTextEntry(nodeListEntry);
	        }
	    }
	    else if(requestCode == EDIT_NODE_REQUEST)
	    {
	    	// Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	        	TrailNode node = trailNodes.get(data.getIntExtra("id", 0));
	        	node.setGeoPoint(data.getDoubleExtra("lat", 0), data.getDoubleExtra("lon", 0));
	            node.setDescription(data.getStringExtra("description"));
	            String nodeListEntry = "Point " + (node.getID()+1) + " (" + String.format("%.3f, %.3f", node.getLat(), node.getLon()) + ")";
	            nodeTextList[node.getID()] = nodeListEntry;
	            updatePointListSpinner();
	        }
	    }
	}
	
	private void editNode(int REQUEST_CODE, TrailNode node)
	{
		Intent editIntent = new Intent(this, EditPointActivity.class);
		editIntent.putExtra("id", node.getID());
    	editIntent.putExtra("lat", node.getLat());
    	editIntent.putExtra("lon",  node.getLon());;
    	editIntent.putExtra("description",  node.getDescription());
    	startActivityForResult(editIntent, REQUEST_CODE);
	}
	
	private void removeNode(int rmIndex)
	{
		trailNodes.remove(rmIndex);
		for(int i = rmIndex; i < trailNodes.size(); i++)
		{
			trailNodes.get(i).setID(trailNodes.get(i).getID()-1);
		}
		updateAllNodeTextList();
		
		updatePointListSpinner();
	}
	
	private void updateAllNodeTextList()
	{
		nodeTextList = new String[trailNodes.size()];
		for(int i = 0; i < trailNodes.size(); i++)
		{
			TrailNode node = trailNodes.get(i);
			nodeTextList[i] = "Point " + (node.getID()+1) + " (" + String.format("%.3f, %.3f", node.getLat(), node.getLon()) + ")";
		}
	}
	
	private void addNodeTextEntry(String addText)
	{
		String[] resultList = new String[nodeTextList.length+1];
		for(int i = 0; i < nodeTextList.length; i++)
		{
			resultList[i] = nodeTextList[i];
		}
		resultList[resultList.length-1] = addText;
		nodeTextList = resultList;
		
		updatePointListSpinner();
	}
	
	private void updatePointListSpinner()
	{
		Spinner spinner = (Spinner)findViewById(R.id.pointListSpinner);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, nodeTextList);
		spinner.setAdapter(spinnerArrayAdapter);
		spinner.setSelection(nodeTextList.length-1);
	}
	
	private boolean validTrail()
	{
		EditText titleInput = (EditText)findViewById(R.id.titleInput);
		String title = titleInput.getText().toString().trim();
		if(title.length() == 0)
		{
			Toast.makeText(this,"Please enter a title.", Toast.LENGTH_SHORT).show();
			return false;
		}

		EditText authorInput = (EditText)findViewById(R.id.authorInput);
		String author = authorInput.getText().toString().trim();
		if(author.length() == 0)
		{
			Toast.makeText(this,"Please enter an author.", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		EditText descriptionInput = (EditText)findViewById(R.id.descriptionInput);
		String description = descriptionInput.getText().toString().trim();
		if(description.length() == 0)
		{
			Toast.makeText(this,"Please enter a description.", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(trailNodes.size() <= 1)
		{
			Toast.makeText(this,"You need at least two waypoints!", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	private void createTrail()
	{	
		TrailData newData = new TrailData();
		
		EditText titleInput = (EditText)findViewById(R.id.titleInput);
		newData.setTitle(titleInput.getText().toString().trim());

		EditText authorInput = (EditText)findViewById(R.id.authorInput);
		newData.setAuthor(authorInput.getText().toString().trim());
		
		EditText descriptionInput = (EditText)findViewById(R.id.descriptionInput);
		newData.setDescription(descriptionInput.getText().toString().trim());
		
		Spinner detectRangeSpinner = (Spinner)findViewById(R.id.pointRangeSpinner);
		String detectRangeSel = (String)detectRangeSpinner.getSelectedItem();
		String detectRange = detectRangeSel.split(" ")[0];
		newData.setDetectRange(Float.parseFloat(detectRange));
		
		TrailNode firstNode = trailNodes.get(0);
		newData.setStartLat(firstNode.getLat());
		newData.setStartLon(firstNode.getLon());
		newData.addNodeRange(trailNodes);
		
		TrailManager.managerInstance.addTrailData(0, newData);
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
