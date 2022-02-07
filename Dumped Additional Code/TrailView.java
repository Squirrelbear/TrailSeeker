package org.codeandmagic.android;

import java.util.List;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;

public class TrailView {
	private enum ViewState { GetToStart, GetToNext, Complete };
	
	private int screenWidth, screenHeight;
	private MainGamePanel panel;
	private PointViewer pointViewer;
	private double lat, lon;
	private Location nextLocation;
	private ViewState viewState;
	private double range;
	private List<TrailNode> nodes;
	private int curNode;
	private double distanceToPoint;
	
	public TrailView(int screenWidth, int screenHeight, TrailData data, MainGamePanel panel)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.panel = panel;
		pointViewer = new PointViewer(this.screenWidth, this.screenHeight);
		lat = lon = 0;
		viewState = ViewState.GetToStart;
		range = data.getDetectRange();
		nodes = data.getNodes();
		nextLocation = new Location("Point 1");
		nextLocation.setLatitude(nodes.get(0).getLat());
		nextLocation.setLongitude(nodes.get(0).getLon());
		curNode = 0;
	}
	
	public void update(long elapsedTime)
	{
		
	}
	
	public void draw(Canvas canvas)
	{
		canvas.drawColor(Color.LTGRAY);
		if(viewState != ViewState.GetToStart)
			pointViewer.drawPointList(canvas);
		else
		{
			Paint paintText = new Paint(); 
			paintText.setColor(Color.BLACK); 
			paintText.setTextSize(50); 
			canvas.drawText("Get to the start point to begin!", screenWidth/2-350, screenHeight/2-25, paintText);
			canvas.drawText(String.format("You are %.1fm away!", distanceToPoint), screenWidth/2-250, screenHeight/2+40, paintText);
			canvas.drawText(String.format("Target: (%.3f,%.3f)", nextLocation.getLatitude(), nextLocation.getLongitude()),screenWidth/2-300, screenHeight/2+80,  paintText);
			canvas.drawText(String.format("Current: (%.3f,%.3f)", lat, lon),screenWidth/2-300, screenHeight/2+120,  paintText);
		}
	}
	
	public void handleTouchEvent(float x, float y)
	{
		if(viewState != ViewState.GetToStart)
		{
			int closestPoint = pointViewer.findClosestPoint(x, y, 100);
			
			if(closestPoint != -1)
			{
				openNodeHint(closestPoint);
			}
		}
	}
	
	public void handleLocationChanged(Location location) {
		lat = location.getLatitude();
		lon = location.getLongitude();
		distanceToPoint = location.distanceTo(nextLocation);
		if(viewState != ViewState.Complete && distanceToPoint <= range)
		{
			triggerNextLocation();
		}
	}
	
	private void triggerNextLocation()
	{
		if(viewState == ViewState.GetToStart)
			viewState = ViewState.GetToNext;

		if(curNode >= nodes.size() - 1)
			viewState = ViewState.Complete;
		
		if(curNode > 0)
		{
			// add the next node to the map
			Location prevLocation = new Location("Point " + curNode);
			prevLocation.setLatitude(nodes.get(curNode).getLat());
			prevLocation.setLongitude(nodes.get(curNode).getLon());
			float distance = prevLocation.distanceTo(nextLocation);
			float rotation = prevLocation.bearingTo(nextLocation);
			pointViewer.addPoint(distance, rotation);
		
		}
		
		// open the hint related to the current node that was just reached
		openNodeHint(curNode++);
	}
	
	private void openNodeHint(int nodeID)
	{
		String description = nodes.get(nodeID).getDescription();
		Intent hintIntent = new Intent(panel.getActivity(), HintActivity.class);
		hintIntent.putExtra("node", "Point " + nodeID);
		hintIntent.putExtra("description", description);
		hintIntent.putExtra("statecomplete", (viewState == ViewState.Complete));
		panel.getActivity().startActivity(hintIntent);
	}
}
