package org.codeandmagic.android;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class PointViewer
{
    List<Vector2> points;
    List<Vector2> unscalePoints;
    RectF bounds;
    RectF realBounds;
    Vector2 lastPoint;
    float scaleX, scaleY;

    public PointViewer(int width, int height)
    {
        realBounds = new RectF(0, 0, width, height);
        bounds = new RectF(-10, -10, 10, 10);
        scaleX = (bounds.right - bounds.left) * 1.0f / (realBounds.right - realBounds.left);
        scaleY = (bounds.bottom - bounds.top) * 1.0f / (realBounds.bottom - realBounds.top);

        points = new ArrayList<Vector2>();
        points.add(rescalePoint(new Vector2(0, 0)));
        unscalePoints = new ArrayList<Vector2>();
        unscalePoints.add(new Vector2(0, 0));

        lastPoint = new Vector2(0, 0);
    }

    public void addPoint(float distance, float rotation)
    {
        Vector2 newPoint = determinePoint(lastPoint, distance, rotation);

        RectF testRect = new RectF((int)(bounds.left * 1.1f), (int)(bounds.top * 1.1f), 
                            (int)(bounds.left * 0.9f + bounds.width() * 0.8f), 
                            (int)(bounds.top * 0.9f + bounds.height() * 0.8f));


        unscalePoints.add(newPoint);
        if (testRect.contains((int)newPoint.X, (int)newPoint.Y))
        {
            // new point is in the existing area just add the point to the list
            points.add(rescalePoint(newPoint));
        }
        else
        {
            // new point is outside the safe bounds of the circle. 
            // need to perform a scale update to all points with new bounds
            if (newPoint.X < testRect.left)
            {
                // absolute in case the point is actually within the safety barrier
                float distChange = Math.abs(bounds.left - newPoint.X) + 10;
                bounds.left -= (int)distChange;
                bounds.right += (int)distChange;
            }
            else if (newPoint.X > testRect.right)
            {
                float distChange = Math.abs(newPoint.X - bounds.left) + 10;
                bounds.right += (int)distChange;
            }

            if (newPoint.Y < testRect.top)
            {
                // absolute in case the point is actually within the safety barrier
                float distChange = Math.abs(bounds.top - newPoint.Y) + 10;
                bounds.top -= (int)distChange;
                bounds.bottom += (int)distChange;
            }
            else if (newPoint.Y > testRect.bottom)
            {
                float distChange = Math.abs(newPoint.Y - bounds.top) + 10;
                bounds.bottom += (int)distChange;
            }

            scaleX = (bounds.right - bounds.left) * 1.0f / (realBounds.right - realBounds.left);
            scaleY = (bounds.bottom - bounds.top) * 1.0f / (realBounds.bottom - realBounds.top);
            
            points = new ArrayList<Vector2>();
            for (Vector2 uP : unscalePoints)
            {
                points.add(rescalePoint(uP));
            }
        }
    }

    private Vector2 rescalePoint(Vector2 point)
    {
        Vector2 newPoint = new Vector2(point.X, point.Y);
        newPoint.X = realBounds.left + (point.X - bounds.left) / scaleX;
        newPoint.Y = realBounds.top + (point.Y - bounds.top) / scaleY;
        return newPoint;
    }

    // returns new point location relative to old location
    @SuppressLint("FloatMath")
	public Vector2 determinePoint(Vector2 lastPoint, float distance, float rotation)
    {
        Vector2 result = new Vector2(lastPoint.X, lastPoint.Y);
        result.X += distance * (float)Math.cos(Math.toRadians(rotation - 90));
        result.Y += distance * (float)Math.sin(Math.toRadians(rotation - 90));

        return result;
    }

    public void drawPointList(Canvas canvas)
    {
        //DrawRect(canvas, 1, realBounds);

    	Paint paintText = new Paint(); 
		paintText.setColor(Color.BLACK); 
		paintText.setTextSize(50); 
    	
        for (int i = 0; i < points.size(); i++)
        {
        	canvas.drawText((i + 1) + "", points.get(i).X, points.get(i).Y, paintText);
        }
    }
    
    public int findClosestPoint(float x, float y, float maxDistance)
    {
    	Vector2 p = new Vector2(x,y);
    	int minID = -1;
    	float minDistance = 100000;
    	
    	for(int i = 0; i < points.size(); i++)
    	{
    		float d = p.distance(points.get(i));
    		if(d < maxDistance && d < minDistance)
    		{
    			minDistance = d;
    			minID = i;
    		}
    	}
    	
    	return minID;
    }

    /*private void DrawRect(Canvas canvas, float width, RectF rect)
    {
        DrawLine(canvas, width, Color.Black, new Vector2(rect.left, rect.top), new Vector2(rect.right, rect.top));
        DrawLine(canvas, width, Color.Black, new Vector2(rect.right, rect.top), new Vector2(rect.right, rect.bottom));
        DrawLine(canvas, width, Color.Black, new Vector2(rect.right, rect.bottom), new Vector2(rect.left, rect.bottom));
        DrawLine(canvas, width, Color.Black, new Vector2(rect.left, rect.bottom), new Vector2(rect.left, rect.top));
    }

    private void DrawLine(Canvas canvas, float width, Color color, Vector2 point1, Vector2 point2)
    {
        float angle = (float)Math.atan2(point2.Y - point1.Y, point2.X - point1.X);
        float length = Vector2.Distance(point1, point2);

        batch.Draw(blank, point1, null, color,
                   angle, Vector2.Zero, new Vector2(length, width),
                   SpriteEffects.None, 0);
    }*/
    
    @SuppressLint("FloatMath")
	private class Vector2
    {
    	public float X, Y;
    	
    	public Vector2(float X, float Y)
    	{
    		this.X = X;
    		this.Y = Y;
    	}
    	
    	public float distance(Vector2 b)
    	{
    		return distance(this, b);
    	}
    	
		public float distance(Vector2 a, Vector2 b)
    	{
    		return (float) Math.sqrt(Math.pow((a.X - b.X), 2) + Math.pow((a.Y - b.Y), 2));
    	}
    }
}
