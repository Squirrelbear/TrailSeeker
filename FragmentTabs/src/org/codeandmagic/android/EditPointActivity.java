package org.codeandmagic.android;

import java.util.List;

import org.codeandmagic.android.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

public class EditPointActivity extends MapActivity {
	private static final String TAG = "editendpoint";
	
	private int id;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {         

		super.onCreate(savedInstanceState);    
       	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
       	setContentView(R.layout.editpointactivity);

       	Bundle extras = getIntent().getExtras();
       	id = extras.getInt("id");
       	
       	EditText latTextInput = (EditText)findViewById(R.id.latInput);
       	latTextInput.setText(Double.toString(extras.getDouble("lat")));
   		EditText lonTextInput = (EditText)findViewById(R.id.lonInput);
   		lonTextInput.setText(Double.toString(extras.getDouble("lon")));
   		EditText descriptionTextInput = (EditText)findViewById(R.id.descriptionInputEditPoint);
   		descriptionTextInput.setText(extras.getString("description"));
   		
   		MapView mapView = (MapView) findViewById(R.id.mapview);
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.nodepin);
		CustomItemizedOverlay itemizedoverlay = new CustomItemizedOverlay(drawable);
		
		 int lat = (int) (extras.getDouble("lat") * 1E6);
		 int lon = (int) (extras.getDouble("lon") * 1E6);
	    GeoPoint point = new GeoPoint(lat, lon);
		FrameLayout markerLayout =
                (FrameLayout) getLayoutInflater().inflate(R.layout.marker, null);
	    NumberedOverlay overlayitem = new NumberedOverlay(id+1, point, markerLayout, this);
	    itemizedoverlay.addOverlay(overlayitem);
	    mapOverlays.add(itemizedoverlay);
	    
	    mapView.getController().setCenter(point);
	    mapView.getController().setZoom(16);
   		
   		Log.d(TAG, "Loaded: " + TAG);
   }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    //Handle the back button
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	        //Ask the user if they want to quit
	        new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle(R.string.savePointTitle)
	        .setMessage(R.string.savePointMessage)
	        .setPositiveButton(R.string.btnSavePoint, new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	if(validData())
	            		returnResult();
	            }

	        })
	        .setNegativeButton(R.string.btnCancel,  new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	setResult(Activity.RESULT_CANCELED, null);
	            	finish();
	            }

	        })
	        .show();

	        return true;
	    }
	    else {
	        return super.onKeyDown(keyCode, event);
	    }

	}
	
	public void btnSavePointPressed(View view) {
		if(validData())
			returnResult();
	}
	
	public void btnCancelPressed(View view) {
		setResult(Activity.RESULT_CANCELED, null);
    	finish();
	}
	

	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	private void returnResult()
	{
		Intent resultIntent = new Intent();
    	resultIntent.putExtra("id", id);
    	EditText latTextInput = (EditText)findViewById(R.id.latInput);
    	resultIntent.putExtra("lat", Double.parseDouble(latTextInput.getText().toString()));
    	EditText lonTextInput = (EditText)findViewById(R.id.lonInput);
    	resultIntent.putExtra("lon", Double.parseDouble(lonTextInput.getText().toString()));
    	EditText descriptionTextInput = (EditText)findViewById(R.id.descriptionInputEditPoint);
    	resultIntent.putExtra("description", descriptionTextInput.getText().toString().trim());    		
    	setResult(Activity.RESULT_OK, resultIntent);
    	finish();
	}
	
	private boolean validData()
	{
		EditText latTextInput = (EditText)findViewById(R.id.latInput);
    	if(latTextInput.getText().toString().length() == 0)
    	{
    		Toast.makeText(this,"Please enter a Latitude.", Toast.LENGTH_SHORT).show();
			return false;
    	}
    	EditText lonTextInput = (EditText)findViewById(R.id.lonInput);
    	if(lonTextInput.getText().toString().length() == 0)
    	{
    		Toast.makeText(this,"Please enter a Longitude.", Toast.LENGTH_SHORT).show();
			return false;
    	}
    	EditText descriptionTextInput = (EditText)findViewById(R.id.descriptionInputEditPoint);
    	if(descriptionTextInput.getText().toString().length() == 0)
    	{
    		Toast.makeText(this,"Please enter a description.", Toast.LENGTH_SHORT).show();
			return false;
    	}
    	
    	return true;
	}	
	
}
