package org.codeandmagic.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class NumberedOverlay extends OverlayItem {
	private int number = 1;
	//private Drawable marker;
	private FrameLayout markerLayout;

	public NumberedOverlay(int number, GeoPoint point, FrameLayout markerLayout, Activity activity) {
		super(point, "", "");
		this.markerLayout = markerLayout;
		setNumber(number);
		
		this.mMarker = generateMarker(activity);
		this.mMarker.setBounds(-mMarker.getIntrinsicWidth()/2, -mMarker.getIntrinsicHeight(), mMarker.getIntrinsicWidth() /2, 0);
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	@Override
	public Drawable getMarker(int stateBitset) {
		return mMarker;
	}

	public Drawable generateMarker(Activity activity) {

		Bitmap viewCapture = null;
		Drawable drawOverlay = null;

		// make sure our marker layout isn't null
		if (markerLayout != null) {

			// set the text string into the view before we turn it into an image
			TextView textView = (TextView) markerLayout.findViewById(R.id.text);
			textView.setText(getNumber() + "");

			// calling setBackgroundResource seems to overwrite our padding in
			// the layout;
			// the following is a hack i found in this google bug report; fixes
			// gravity issue as well
			// http://code.google.com/p/android/issues/detail?id=17885
			int paddingTop = textView.getPaddingTop();
			int paddingLeft;
			if(getNumber() < 10)
				paddingLeft = textView.getPaddingLeft() + 10;
			else 
				paddingLeft = textView.getPaddingLeft();
			int paddingRight = textView.getPaddingRight();
			int paddingBottom = textView.getPaddingBottom();

			textView.setBackgroundResource(R.drawable.nodepin);

			// part of the hack above to reset the padding specified in the
			// marker layout
			textView.setPadding(paddingLeft, paddingTop, paddingRight,
					paddingBottom);

			// we need to enable the drawing cache
			markerLayout.setDrawingCacheEnabled(true);

			// this is the important code
			// Without it the view will have a dimension of 0,0 and the bitmap
			// will be null
			markerLayout.measure(
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(),
					markerLayout.getMeasuredHeight());

			// we need to build our drawing cache
			markerLayout.buildDrawingCache(true);

			// not null? then we are ready to capture our bitmap image
			if (markerLayout.getDrawingCache() != null) {
				viewCapture = Bitmap.createBitmap(markerLayout
						.getDrawingCache());

				// if the view capture is not null we should turn off the
				// drawing cache
				// and then create our marker drawable with the view capture
				if (viewCapture != null) {
					markerLayout.setDrawingCacheEnabled(false);
					drawOverlay = new BitmapDrawable(activity.getResources(), viewCapture);
					/*Bitmap saveThis = ((BitmapDrawable)drawOverlay).getBitmap();
					Log.d("CustomMapMarkers", "Created marker.");						

					try {
						 String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
						 String fileName = extStorageDirectory + "/markertest1.png";
					        FileOutputStream out = new FileOutputStream(fileName);
					        saveThis.compress(Bitmap.CompressFormat.PNG, 90, out);
					        Log.d("CustomMapMarkers", fileName);
					 } catch (Exception e) {
					        e.printStackTrace();
					 }*/


					return drawOverlay;
				}
			} else {
				Log.d("CustomMapMarkers",
						"Item * generateMarker *** getDrawingCache is null");
			}
		}
		Log.d("CustomMapMarkers", "Item * generateMarker *** returning null");
		return null;
	}

	/*
	 * @Override public boolean draw(Canvas canvas, MapView mapView, boolean
	 * shadow, long when) { super.draw(canvas, mapView, shadow);
	 * 
	 * //---translate the GeoPoint to screen pixels--- Point screenPts = new
	 * Point(); mapView.getProjection().toPixels(p, screenPts);
	 * 
	 * //---add the marker--- Bitmap bmp = BitmapFactory.decodeResource(
	 * activity.getResources(), R.drawable.nodepin); canvas.drawBitmap(bmp,
	 * screenPts.x, screenPts.y-50, null); Paint paintText = new Paint();
	 * paintText.setColor(Color.BLACK); paintText.setTextSize(50);
	 * canvas.drawText(number+"", screenPts.x + 10, screenPts.y - 40,
	 * paintText); return true; }
	 */
}
