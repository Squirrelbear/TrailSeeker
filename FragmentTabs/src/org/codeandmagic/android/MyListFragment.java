package org.codeandmagic.android;

import java.util.ArrayList;
import java.util.List;

import org.codeandmagic.android.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("ValidFragment")
public class MyListFragment extends ListFragment implements
		LoaderCallbacks<Void> {

	private static final String TAG = "FragmentTabs";

	private String mTag;
	private MyAdapter mAdapter;
	private ArrayList<String> mItems;
	private LayoutInflater mInflater;
	//private int mTotal;
	//private int mPosition;
	private int tagType;

	/*private static final String[] WORDS = { "Lorem", "ipsum", "dolor", "sit",
			"amet", "consectetur", "adipiscing", "elit", "Fusce", "pharetra",
			"luctus", "sodales" };
	private static final String[] NUMBERS = { "I", "II", "III", "IV", "V",
			"VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV" };

	private static final int SLEEP = 1;*/

	private final int myMapColor = R.color.color_mymap;
	private final int downloadedColor = R.color.color_downloaded;
	private final int onlineColor = R.color.color_online;

	public MyListFragment() {
	}

	public MyListFragment(String tag) {
		mTag = tag;
		
		if(TabsFragment.TAB_MYMAPS.equals(mTag))
		{
			tagType = 0;
		}
		else if(TabsFragment.TAB_DOWNLOADED.equals(mTag))
		{
			tagType = 1;
		}
		else
		{
			tagType = 2;
		}

		/*mTotal = TabsFragment.TAB_MYMAPS.equals(mTag) ? WORDS.length
				: NUMBERS.length;*/
		
		Log.d(TAG, "Constructor: tag=" + tag);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// this is really important in order to save the state across screen
		// configuration changes for example
		setRetainInstance(true);

		mInflater = LayoutInflater.from(getActivity());

		// you only need to instantiate these the first time your fragment is
		// created; then, the method above will do the rest
		if (mAdapter == null) {
			mItems = new ArrayList<String>();
			mAdapter = new MyAdapter(getActivity(), mItems);
		}
		getListView().setAdapter(mAdapter); 
		
		//registerForContextMenu(getListView()); 
		getListView().setOnItemClickListener( new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		    	//Toast.makeText(getActivity(), "ID: " + arg2, Toast.LENGTH_LONG).show();
		    	openSummary(arg2);
		    }});
		
		if(tagType == 2)
		{
			DownloadListTask downloadTask = new DownloadListTask();
			downloadTask.execute(0);
		}
		
		// initiate the loader to do the background work
		//getLoaderManager().initLoader(0, null, this);
	}
	
	public void handleResume()
	{
		Log.d(TAG, "Handling Resume: tag=" + tagType);
		updateList();
	}
	
	public void updateList()
	{
		mItems.clear();
		mItems.addAll(TrailManager.managerInstance.getStringArray(tagType));
		//Toast.makeText(getActivity(),"List Loaded " + TrailManager.managerInstance.getStringArray(tagType).size(), Toast.LENGTH_LONG).show();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public Loader<Void> onCreateLoader(int id, Bundle args) {
		AsyncTaskLoader<Void> loader = new AsyncTaskLoader<Void>(getActivity()) {

			@Override
			public Void loadInBackground() {
				/*try {
					// simulate some time consuming operation going on in the
					// background
					Thread.sleep(SLEEP);
				} catch (InterruptedException e) {
				}*/
				return null;
			}
		};
		// somehow the AsyncTaskLoader doesn't want to start its job without
		// calling this method
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Void> loader, Void result) {

		// add the new item and let the adapter know in order to refresh the
		// views
		/*mItems.add(TabsFragment.TAB_MYMAPS.equals(mTag) ? WORDS[mPosition]
				: NUMBERS[mPosition]);
		mAdapter.notifyDataSetChanged();

		// advance in your list with one step
		mPosition++;
		if (mPosition < mTotal - 1) {
			getLoaderManager().restartLoader(0, null, this);
			Log.d(TAG, "onLoadFinished(): loading next...");
		} else {
			registerForContextMenu(getListView()); 
			getListView().setOnItemClickListener( new OnItemClickListener() {
			    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			    	openSummary(arg2);
			    	//Toast.makeText(getActivity(), "Arg 2: " + arg2, Toast.LENGTH_SHORT).show();  
			    }});
			Log.d(TAG, "onLoadFinished(): done loading!");
		}*/
	}

	@Override
	public void onLoaderReset(Loader<Void> loader) {
	}
	
	/*@Override  
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
    super.onCreateContextMenu(menu, v, menuInfo);  
        menu.setHeaderTitle("Context Menu");  
        menu.add(0, v.getId(), 0, "Open");  
        //menu.add(0, v.getId(), 0, "Delete");  
        menu.add(0, v.getId(), 0, "Cancel"); 
    }  
  
    @Override  
    public boolean onContextItemSelected(MenuItem item) {  
        if(item.getTitle()=="Open"){
        	Toast.makeText(getActivity(), "ID: " + item.getItemId(), Toast.LENGTH_LONG).show();
        	//openSummary(item.getItemId());
        	}  
        //else if(item.getTitle()=="Delete"){deleteTrail(item.getItemId());}  
        else {return false;}  
    return true;  
    }  */
    
    public void openSummary(int id){  
    	//Toast.makeText(getActivity(), "function 1 called", Toast.LENGTH_SHORT).show();  
    	Intent summaryIntent = new Intent(getActivity(), SummaryActivity.class);
    	summaryIntent.putExtra("id", id);
    	summaryIntent.putExtra("type", tagType);
    	startActivity(summaryIntent);
    }  
    public void deleteTrail(int id){  
        //Toast.makeText(getActivity(), "function 2 called", Toast.LENGTH_SHORT).show(); 
    	TrailManager.managerInstance.deleteTrailData(tagType, TrailManager.managerInstance.getSummary(id, tagType));
    	updateList();
    }
    
	private class MyAdapter extends ArrayAdapter<String> {

		public MyAdapter(Context context, List<String> objects) {
			super(context, R.layout.list_item, R.id.text, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			Wrapper wrapper;

			if (view == null) {
				view = mInflater.inflate(R.layout.list_item, null);
				wrapper = new Wrapper(view);
				view.setTag(wrapper);
			} else {
				wrapper = (Wrapper) view.getTag();
			}

			wrapper.getTextView().setText(getItem(position));
			if(mTag == TabsFragment.TAB_MYMAPS)
			{
				wrapper.getBar().setBackgroundColor(getResources().getColor(myMapColor));
			}
			else if(mTag == TabsFragment.TAB_DOWNLOADED)
			{
				wrapper.getBar().setBackgroundColor(getResources().getColor(downloadedColor));
			}
			else if(mTag == TabsFragment.TAB_ONLINE)
			{
				wrapper.getBar().setBackgroundColor(getResources().getColor(onlineColor));
			}
			
			return view;
		}

	}

	// use an wrapper (or view holder) object to limit calling the
	// findViewById() method, which parses the entire structure of your
	// XML in search for the ID of your view
	private class Wrapper {
		private final View mRoot;
		private TextView mText;
		private View mBar;

		public Wrapper(View root) {
			mRoot = root;
		}

		public TextView getTextView() {
			if (mText == null) {
				mText = (TextView) mRoot.findViewById(R.id.text);
			}
			return mText;
		}

		public View getBar() {
			if (mBar == null) {
				mBar = mRoot.findViewById(R.id.bar);
			}
			return mBar;
		}
	}
	
	private class DownloadListTask extends AsyncTask<Integer, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//displayProgressText("Uploading...");
			Toast.makeText(getActivity(),"Downloading List...", Toast.LENGTH_LONG).show();
		}
		
		@Override
		protected Boolean doInBackground(Integer ... params) {		
			return TrailManager.managerInstance.downloadOnlineList();
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
		    super.onPostExecute(result);
		    //displayProgressText("Upload Complete!");
		    if(result)
		    {
			    updateList();
			    Toast.makeText(getActivity(),"List Downloaded", Toast.LENGTH_LONG).show();
		    }
		    else
		    {
		    	Toast.makeText(getActivity(),"Failed Retrieving List", Toast.LENGTH_LONG).show();
		    }
		}
	}
}
