package org.codeandmagic.android;

import org.codeandmagic.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class TabsFragment extends Fragment implements OnTabChangeListener {

	private static final String TAG = "FragmentTabs";
	public static final String TAB_MYMAPS = "mymaps";
	public static final String TAB_DOWNLOADED = "downloaded";
	public static final String TAB_ONLINE = "findnew";

	private View mRoot;
	private TabHost mTabHost;
	private int mCurrentTab;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.tabs_fragment, null);
		mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
		setupTabs();
		return mRoot;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);

		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTab(mCurrentTab);
		// manually start loading stuff in the first tab
		updateTab(TAB_DOWNLOADED, R.id.tab_2);
		updateTab(TAB_MYMAPS, R.id.tab_1);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		switch(mCurrentTab)
		{
		case 0:
			updateTab(TAB_MYMAPS, R.id.tab_1);
			break;
		case 1:
			updateTab(TAB_DOWNLOADED, R.id.tab_2);
			break;
		case 2:
			updateTab(TAB_ONLINE, R.id.tab_3);
			break;
		}
	}

	private void setupTabs() {
		mTabHost.setup(); // important!
		mTabHost.addTab(newTab(TAB_MYMAPS, R.string.tab_mymaps, R.id.tab_1));
		mTabHost.addTab(newTab(TAB_DOWNLOADED, R.string.tab_downloaded, R.id.tab_2));
		mTabHost.addTab(newTab(TAB_ONLINE, R.string.tab_online, R.id.tab_3));
	}

	private TabSpec newTab(String tag, int labelId, int tabContentId) {
		Log.d(TAG, "buildTab(): tag=" + tag);

		View indicator = LayoutInflater.from(getActivity()).inflate(
				R.layout.tab,
				(ViewGroup) mRoot.findViewById(android.R.id.tabs), false);
		((TextView) indicator.findViewById(R.id.text)).setText(labelId);

		TabSpec tabSpec = mTabHost.newTabSpec(tag);
		tabSpec.setIndicator(indicator);
		tabSpec.setContent(tabContentId);
		return tabSpec;
	}

	@Override
	public void onTabChanged(String tabId) {
		//Log.d(TAG, "onTabChanged(): tabId=" + tabId);
		if (TAB_MYMAPS.equals(tabId)) {
			updateTab(tabId, R.id.tab_1);
			mCurrentTab = 0;
			return;
		}
		if (TAB_DOWNLOADED.equals(tabId)) {
			updateTab(tabId, R.id.tab_2);
			mCurrentTab = 1;
			return;
		}
		if (TAB_ONLINE.equals(tabId)) {
			updateTab(tabId, R.id.tab_3);
			mCurrentTab = 2;
			return;
		}
	}

	private void updateTab(String tabId, int placeholder) {
		FragmentManager fm = getFragmentManager();
		if (fm.findFragmentByTag(tabId) == null) {
			fm.beginTransaction()
					.replace(placeholder, new MyListFragment(tabId), tabId)
					.commit();
		}
		else
		{
			((MyListFragment) fm.findFragmentByTag(tabId)).handleResume();
		}
	}

}
