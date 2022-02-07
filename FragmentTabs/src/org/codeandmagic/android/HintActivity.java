package org.codeandmagic.android;

import org.codeandmagic.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class HintActivity extends Activity {
	private static final String TAG = "hintactivity";
		
	@Override
    public void onCreate(Bundle savedInstanceState) {         

       super.onCreate(savedInstanceState);   
       
       Bundle extras = getIntent().getExtras();
       String nodeID = extras.getString("nodeID");
       String description = extras.getString("description");
       boolean stateComplete = extras.getBoolean("statecomplete");
       
       this.requestWindowFeature(Window.FEATURE_NO_TITLE);
       setContentView(R.layout.hintactivity);
       
       TextView titleLabel = (TextView)findViewById(R.id.lblPoint);
       titleLabel.setText(nodeID);
       TextView authorLabel = (TextView)findViewById(R.id.lblProgress);
       if(stateComplete)
       {
    	   authorLabel.setText("Trail is complete!");
       }
  		TextView descriptionLabel = (TextView)findViewById(R.id.lblDescription);
  		descriptionLabel.setText(description);
  		
  		Log.d(TAG, "Activity Created");
	}
	
	public void btnReturnPressed(View view) {
		finish();
	}

}
