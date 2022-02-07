package org.codeandmagic.android;

import java.util.List;

import org.codeandmagic.android.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SummaryActivity extends MapActivity {
	private static final String TAG = "summaryactivity";
	
	private int type, id;
	private TrailData summaryData;
	private boolean downloaded;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {         

       super.onCreate(savedInstanceState);   
       
       Bundle extras = getIntent().getExtras();
       id = extras.getInt("id");
       type = extras.getInt("type");
       downloaded = false;
       
       this.requestWindowFeature(Window.FEATURE_NO_TITLE);
       
       // check if already downloaded:
       if(type == 2 && TrailManager.managerInstance.isDownloaded(id))
       {
    	   type = 1;
    	   id = TrailManager.managerInstance.getMatchingDownloadID(id);
    	   downloaded = true;
       }
       
       if(type == 2)
       {
    	   setContentView(R.layout.summaryonlineactivity);
       }
       else
       {
    	   setContentView(R.layout.summaryactivity);
       }
       
       if(downloaded){
    	   displayProgressText("Already Download...");
       }
       
       summaryData = TrailManager.managerInstance.getSummary(id, type);
       TextView titleLabel = (TextView)findViewById(R.id.lblTitle);
       titleLabel.setText(summaryData.getTitle());
       TextView authorLabel = (TextView)findViewById(R.id.lblAuthor);
  		authorLabel.setText("By: " + summaryData.getAuthor() + " on " + summaryData.getCreationDate());
  		TextView descriptionLabel = (TextView)findViewById(R.id.lblDescription);
  		descriptionLabel.setText(summaryData.getDescription());
  		
  		MapView mapView = (MapView) findViewById(R.id.mapview);
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.nodepin);
		CustomItemizedOverlay itemizedoverlay = new CustomItemizedOverlay(drawable);
		
		int lat = (int) (summaryData.getStartLat() * 1E6);
	    int lon = (int) (summaryData.getStartLon() * 1E6);
	    GeoPoint point = new GeoPoint(lat, lon);
		FrameLayout markerLayout =
                (FrameLayout) getLayoutInflater().inflate(R.layout.marker, null);
		//OverlayItem item = new OverlayItem(point, "Test", "test");
	    NumberedOverlay overlayitem = new NumberedOverlay(1, point, markerLayout, this);
	    itemizedoverlay.addOverlay(overlayitem);
	    mapOverlays.add(itemizedoverlay);
	    
	    mapView.getController().setCenter(point);
	    mapView.getController().setZoom(16);
  		
  		Log.d(TAG, "Loaded: " + TAG);
   }
	
	public void btnBeginPressed(View view) {
		try
		{
			TrailManager.managerInstance.getFullData(id, type);
		}
		catch(Exception e)
		{
			Toast.makeText(SummaryActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
			return;
		}
		//Toast.makeText(this,"Opening Type: " + type + " ID: " + id + " UID: " + TrailManager.managerInstance.getSummary(id, type).getUID(), Toast.LENGTH_SHORT).show();
		Intent beginIntent = new Intent(this, PointViewer.class);
		beginIntent.putExtra("id", id);
		beginIntent.putExtra("type", type);
    	startActivity(beginIntent);
		//Toast.makeText(this,"This would open the begin view", Toast.LENGTH_SHORT).show();
	}
	
	public void btnDownloadPressed(View view) {
		if(!downloaded)
		{
			DownloadTask downTask = new DownloadTask();
			downTask.execute(id);
		}
		else
		{
			btnBeginPressed(null);
		}
	}
	
	// No longer used
	/*public void btnShowStartPressed(View view) {
		Intent showStartPointIntent = new Intent(this, PointViewer.class);
		showStartPointIntent.putExtra("lat", summaryData.getStartLat());
		showStartPointIntent.putExtra("lon", summaryData.getStartLon());
    	startActivity(showStartPointIntent);
		//Toast.makeText(this,"Feature disabled. View report!", Toast.LENGTH_LONG).show();
	}*/
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
		if(type != 2)
	       {
			menu.add("Delete");
			if(type == 0)
				menu.add("Upload");
		     menu.add("Cancel");
		     return true;
	       }
		return false;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	     if(item.getTitle().toString().equals("Delete"))
	     {
	    	 new AlertDialog.Builder(this)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle(R.string.deleteTitle)
		        .setMessage(R.string.deleteMessage)
		        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	TrailManager.managerInstance.deleteTrailData(type, summaryData);
		            	finish();
		            }

		        })
		        .setNegativeButton(R.string.no,  null)
		        .show();
	    	 return true;
	     }
	     else if(item.getTitle().toString().equals("Upload"))
	     {
	    	 new AlertDialog.Builder(this)
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle(R.string.uploadTitle)
		        .setMessage(R.string.uploadMessage)
		        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	Log.d("SummaryActivity", "Uploading: " + id);
		            	UploadTask upTask = new UploadTask();
		            	upTask.execute(id);
		            }

		        })
		        .setNegativeButton(R.string.no,  null)
		        .show();
	    	 return true;
	     }
		return super.onOptionsItemSelected(item);
	}
	
	private void displayProgressText(String text)
	{
		TextView progressLabel = (TextView)findViewById(R.id.lblSumProgress);
		progressLabel.setText(text);
	}
	
	private class UploadTask extends AsyncTask<Integer, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			displayProgressText("Uploading...");
		}
		
		@Override
		protected Boolean doInBackground(Integer ... params) {	
			try
			{
				return TrailManager.managerInstance.uploadTrailData(params[0]);
			}
			catch(Exception e)
			{
				return false;
			}
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
		    super.onPostExecute(result);
		    if(result)
		    	displayProgressText("Upload Complete!");
		    else
		    	displayProgressText("Upload Failed!");
		}
	}
	
	private class DownloadTask extends AsyncTask<Integer, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			displayProgressText("Downloading...");
			Button btnDownload = (Button)findViewById(R.id.btnDownload);
			btnDownload.setEnabled(false);
		}
		
		@Override
		protected Boolean doInBackground(Integer ... params) {		
			return TrailManager.managerInstance.downloadTrailData(params[0]);
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
		    super.onPostExecute(result);

		    Button btnDownload = (Button)findViewById(R.id.btnDownload);
		    if(result)
		    {
		    	btnDownload.setText("Begin");
		    	displayProgressText("Download Complete!");
		    	type = 1;
		    	id = TrailManager.managerInstance.getMatchingDownloadID(id);
		    	downloaded = true;
		    }
		    else
		    {
		    	displayProgressText("Download Failed!");
		    }
			btnDownload.setEnabled(true);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
}
