package org.codeandmagic.android;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.w3c.dom.Element;

import com.google.android.maps.GeoPoint;

import android.location.Location;

public class TrailNode {
	private int id;
	private double lat;
	private double lon;
	private String description;
	
	public TrailNode()
	{
		this.id = -1;
		this.lat = 0;
		this.lon = 0;
		this.description = "";
	}
	
	public TrailNode(Element nodeElement)
	{
		loadFromXMLElement(nodeElement);
	}
	
	public TrailNode(int id, double lat, double lon, String description)
	{
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.description = description;
	}
	
	public void loadFromXMLElement(Element nodeElement)
	{
		this.id = Integer.parseInt(getTagValue("id", nodeElement));
		this.lat = Float.parseFloat(getTagValue("lat", nodeElement));
		this.lon = Float.parseFloat(getTagValue("lon", nodeElement));
		this.description = getTagValue("description", nodeElement);
	}
	
	public Element getXMLElement(Document targetDocument)
	{
		Element nodeElement = targetDocument.createElement("node");
		
		Element idElement = targetDocument.createElement("id");
		Text idText = targetDocument.createTextNode(id + "");
		nodeElement.appendChild(idElement);
		idElement.appendChild(idText);
		
		Element latElement = targetDocument.createElement("lat");
		Text latText = targetDocument.createTextNode(lat + "");
		nodeElement.appendChild(latElement);
		latElement.appendChild(latText);

		Element lonElement = targetDocument.createElement("lon");
		Text lonText = targetDocument.createTextNode(lon + "");
		nodeElement.appendChild(lonElement);
		lonElement.appendChild(lonText);

		Element descriptionElement = targetDocument.createElement("description");
		Text descriptionText = targetDocument.createTextNode(description);
		nodeElement.appendChild(descriptionElement);
		descriptionElement.appendChild(descriptionText);
		
		return nodeElement;
	}
	
	public void setID(int id)
	{
		this.id = id;
	}
	
	public void setGeoPoint(double lat, double lon)
	{
		this.lat = lat;
		this.lon = lon;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public int getID()
	{
		return id;
	}
	
	public double getLat()
	{
		return lat;
	}
	
	public double getLon()
	{
		return lon;
	}
	
	public Location getPointAsLocation()
	{
		Location location = new Location("TrailNode");
		location.setLatitude(getLat());
		location.setLongitude(getLon());
		return location;
	}
	
	public GeoPoint getPointAsGeoPoint()
	{
		int lat = (int) (getLat() * 1E6);
	    int lon = (int) (getLon() * 1E6);
	    GeoPoint point = new GeoPoint(lat, lon);
	    return point;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	 
	        Node nValue = (Node) nlList.item(0);
	 
		return nValue.getNodeValue();
  	}
}
