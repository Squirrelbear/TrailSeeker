package org.codeandmagic.android;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class CustomItemizedOverlay extends ItemizedOverlay<OverlayItem>{
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private PointViewer pointViewer;
	
	public CustomItemizedOverlay(Drawable defaultMarker) {
		  super(boundCenterBottom(defaultMarker));
		}
	
	public CustomItemizedOverlay(Drawable defaultMarker, PointViewer pointViewer) {
		  super(boundCenterBottom(defaultMarker));
		 this.pointViewer = pointViewer;
		}
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	@Override
	public int size() {
	  return mOverlays.size();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}
	
	@Override
    protected boolean onTap(int i) {
		if(pointViewer != null)
			pointViewer.openNodeHint(i);
      return(true);
    }
}
