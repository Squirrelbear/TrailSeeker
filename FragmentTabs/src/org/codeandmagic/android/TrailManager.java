package org.codeandmagic.android;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

public class TrailManager {
	public static TrailManager managerInstance;
	
	List<TrailData> mymaps;
	List<TrailData> downloaded;
	List<TrailData> online;
	
	List<String> myMapsStringList;
	List<String> downloadedStringList;
	List<String> onlineStringList;

	SrcGrabber downloader;
	DatabaseHandler localDatabase;
	
	public TrailManager(Context context)
	{
		managerInstance = this;
		
		mymaps = new ArrayList<TrailData>();
		downloaded = new ArrayList<TrailData>();
		online = new ArrayList<TrailData>();
		
		localDatabase = new DatabaseHandler(context);
		//resetDatabase();
		mymaps = localDatabase.getTrailData(0, true);
		downloaded = localDatabase.getTrailData(1, true);
		
		myMapsStringList = getStringListFromTrailData(mymaps);
		downloadedStringList = getStringListFromTrailData(downloaded);
		onlineStringList = new ArrayList<String>();
		
		downloader = new SrcGrabber();
	}
	
	public TrailData getSummary(int id, int type)
	{
		switch(type)
		{
		case 0:
			if(id < mymaps.size())
				return mymaps.get(id);
			break;
		case 1: 
			if(id < downloaded.size())
				return downloaded.get(id);
			break;
		case 2:
			if(id < online.size())
				return online.get(id);
			break;
		}
		return null;
	}
	
	public TrailData getFullData(int id, int type) throws Exception
	{
		int uid = 0;
		if(type == 0)
		{
			if(id >= mymaps.size())
				new Exception("Please wait, element is still processing.");
				
			uid = Integer.parseInt(mymaps.get(id).getUID());
		}
		else
		{
			if(id >= downloaded.size())
				new Exception("Please wait, element is still processing.");
			
			uid = Integer.parseInt(downloaded.get(id).getUID());
		}
		
		Log.d("TrailManager", "Retrieving data for: " + id + " UID: " + uid);
		return localDatabase.getTrailData(uid, type);
	}
	
	public boolean isDownloaded(int onlineID)
	{
		String uid = online.get(onlineID).getUID();
		
		Log.d("TrailManager", "Searching for onlineID: " + onlineID + " UID: " + uid);
		for(TrailData d : downloaded)
		{
			if(d.getUID().equals(uid))
				return true;
		}
		return false;
	}
	
	public int getMatchingDownloadID(int onlineID)
	{
		String uid = online.get(onlineID).getUID();
		
		for(int i = 0; i < downloaded.size(); i++)
		{
			if(downloaded.get(i).getUID().equals(uid))
			{
				Log.d("TrailManager", "Matching ID found for: " + onlineID + " Found: " + i);
				return i;
			}
		}
		return -1;
	}
	
	public void addTrailData(int type, TrailData data)
	{
		if(type != 2)
			localDatabase.addData(type, data);
		
		if(type == 0)
		{
			mymaps.add(new TrailData(data.convertToXMLString(false)));
			myMapsStringList = getStringListFromTrailData(mymaps);
			Log.d("TrailManager", data.convertToXMLString(true));
		}
		else if(type == 1)
		{
			downloaded.add(new TrailData(data.convertToXMLString(false)));
			downloadedStringList = getStringListFromTrailData(downloaded);
			Log.d("TrailManager", "Added element to downloaded. UID:" + downloaded.get(downloaded.size()-1).getUID());
		}
		else
		{
			online.add(new TrailData(data.convertToXMLString(false)));
			onlineStringList = getStringListFromTrailData(online);
		}
	}
	
	public void deleteTrailData(int type, TrailData data)
	{
		localDatabase.deleteData(Integer.parseInt(data.getUID()), type);
		if(type == 0)
		{
			mymaps = localDatabase.getTrailData(0, true);
			myMapsStringList = getStringListFromTrailData(mymaps);
		}
		if(type == 1)
		{
			downloaded = localDatabase.getTrailData(1, true);
			downloadedStringList = getStringListFromTrailData(downloaded);
		}
	}
	
	public void deleteTrailData(int type, int id)
	{
		localDatabase.deleteData(id, type);
		if(type == 0)
		{
			mymaps = localDatabase.getTrailData(0, true);
			myMapsStringList = getStringListFromTrailData(mymaps);
		}
		if(type == 1)
		{
			downloaded = localDatabase.getTrailData(1, true);
			downloadedStringList = getStringListFromTrailData(downloaded);
		}
	}
	
	// takes id from index list
	// returns success
	public boolean downloadTrailData(int id)
	{
		String uid = online.get(id).getUID();
		
		String data;
		try {
			data = downloader.grabSource("http://peter.site11.com/mobileapp/gettraildata.php?id=" + uid);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
		if(data.equals("ERROR"))
		{
			return false;
		}
		TrailData addObj = new TrailData(data);
		addTrailData(1, addObj);
		return true;
	}
	
	public boolean downloadOnlineList()
	{
		String data;
		try {
			data = downloader.grabSource("http://peter.site11.com/mobileapp/gettraillist.php");
			Log.d("TrailManager", "Data: " + data);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.d("TrailManager", "Client Protocol Exception while downloading file.");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("TrailManager", "IO Exception while downloading file.");
			return false;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			Log.d("TrailManager", "URI Exception while downloading file.");
			return false;
		}
		if(data.equals("ERROR"))
		{
			Log.d("TrailManager", "Server reported Error while downloading file.");
			return false;
		}
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			Log.d("TrailManager", "Downloaded file list parse failed.");
			return false;
		}
		Document doc;
		try {
			doc = dBuilder.parse(new InputSource(new StringReader(data)));
		} catch (SAXException e) {
			e.printStackTrace();
			Log.d("TrailManager", "SAXException while reading downloaded file.");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("TrailManager", "IOException while reading downloaded file.");
			return false;
		} 
		doc.getDocumentElement().normalize();
		
		online.clear();
		NodeList summaryList = doc.getElementsByTagName("summary");
		for (int i = 0; i < summaryList.getLength(); i++) 
		{
			Node summaryNode = summaryList.item(i);
			Element summaryElement = (Element) summaryNode;
			online.add(new TrailData(summaryElement));
		}
		onlineStringList = getStringListFromTrailData(online);
		return true;
	}
	
	public boolean uploadTrailData(int id) throws Exception
	{
		TrailData uploadData = getFullData(id,0);
		Log.d("TrailManager", "Found nodes: " + uploadData.getNodes().size());
		String[] params = new String[2];
		params[0] = uploadData.convertToXMLString(false);
		params[1] = uploadData.convertToXMLString(true);
		
		String data;
		try {
			
			data = downloader.grabSource("http://peter.site11.com/mobileapp/uploadtraildata.php", params);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
		if(data.equals("ERROR"))
		{
			return false;
		}
		
		// TODO note that the uid could be extracted from the data response
		return true;
	}
	
	public List<String> getStringArray(int type)
	{
		switch (type)
		{
		case 0:
			return myMapsStringList;
		case 1:
			return downloadedStringList;
		case 2:
			return onlineStringList;
		}
		return null;
	}
	
	public void resetDatabase()
	{	
		localDatabase.clearDatabase();
	}
	
	private List<String> getStringListFromTrailData(List<TrailData> data)
	{
		List<String> result = new ArrayList<String>();
		for(int i = 0; i < data.size(); i++)
		{
			result.add(data.get(i).getTitle());
		}
		
		return result;
	}
}
