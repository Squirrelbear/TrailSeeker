package org.codeandmagic.android;

import org.codeandmagic.android.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class Main extends FragmentActivity {
	private static final String TAG = "main";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        TrailManager.managerInstance = new TrailManager(getBaseContext());
        Log.d(TAG, "Loaded: " + TAG);
    }
    
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
     menu.add("Create New Trail");
     menu.add("Cancel");
     return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    if(item.getTitle().toString().equals("Create New Trail")) 
	    {
	    	 Intent createIntent = new Intent(this, CreateTrailActivity.class);
	    	 startActivity(createIntent);
	    	 return true;
	     }
		return super.onOptionsItemSelected(item);
	}
}