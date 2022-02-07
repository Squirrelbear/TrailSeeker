package org.codeandmagic.android;

import android.annotation.TargetApi;
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@TargetApi(8)
public class TrailData {
	private String title;
	private String author;
	private String description;
	private String creationDate;
	private double detectRange;
	private int numberpoints;
	private double startLat;
	private double startLon;
	private String uid;
	
	private List<TrailNode> nodeList;
	
	public TrailData()
	{
		GregorianCalendar date = new GregorianCalendar();
		this.creationDate = date.get(Calendar.DAY_OF_MONTH) + "/"
				   + date.get(Calendar.MONTH) + "/"
				   + date.get(Calendar.YEAR);
	
		this.title = "";
		this.author = "";
		this.description = "";
		this.detectRange = 0;
		this.numberpoints = 0;
		this.startLat = 0;
		this.startLon = 0;
		this.uid = "-1";
		
		nodeList = new ArrayList<TrailNode>();
	}
	
	public TrailData(String title, String author, String description, double detectRange, double startLat, double startLon)
	{
		GregorianCalendar date = new GregorianCalendar();
		this.creationDate = date.get(Calendar.DAY_OF_MONTH) + "/"
				   + date.get(Calendar.MONTH) + "/"
			  	   + date.get(Calendar.YEAR);
	
		this.title = title;
		this.author = author;
		this.description = description;
		this.detectRange = detectRange;
		this.numberpoints = 0;
		this.startLat = startLat;
		this.startLon = startLon;
		this.uid = "-1";
				
		nodeList = new ArrayList<TrailNode>();
	}
	
	public TrailData(String trailXMLData)
	{
		this.uid = "-1";
		loadFromXMLString(trailXMLData);
	}
	
	public TrailData(Element summaryElement)
	{
		this.uid = "-1";
		loadSummaryFromElement(summaryElement);
		nodeList = new ArrayList<TrailNode>();
	}

	public String convertToXMLString(boolean includeNodes)
	{
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder;
		try {
			docBuilder = dbfac.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			return "";
		}
	    Document doc = docBuilder.newDocument();
		
		Element root;
		if(includeNodes)
		{
			root = (Element) doc.createElement("traildata");
			doc.appendChild(root);

			root.appendChild(convertSummaryToXMLElement(doc));

			for (int i = 0; i < nodeList.size(); i++) 
			{
				root.appendChild(nodeList.get(i).getXMLElement(doc));
			}
		}
		else
		{
			root = convertSummaryToXMLElement(doc);
			doc.appendChild(root);
		}
			
		//set up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = null;
		try {
			trans = transfac.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return "";
		}
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		//create string from xml tree
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc);
		try {
			trans.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
			return "";
		}
		String xmlString = sw.toString();
		
		return xmlString;
	}
	
	public void loadFromXMLString(String trailXMLData)
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}
		Document doc;
		try {
			doc = dBuilder.parse(new InputSource(new StringReader(trailXMLData)));
		} catch (SAXException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} 
		doc.getDocumentElement().normalize();
		
		NodeList summaryList = doc.getElementsByTagName("summary");
		Node summaryNode = summaryList.item(0);
		Element summaryElement = (Element) summaryNode;
		loadSummaryFromElement(summaryElement);
		
		nodeList = new ArrayList<TrailNode>();
		NodeList nList = doc.getElementsByTagName("node");
		for (int i = 0; i < nList.getLength(); i++) 
		{
			Node nNode = nList.item(i);
			Element nElement = (Element) nNode;
			nodeList.add(new TrailNode(nElement));
			Log.d("TrailData", "Loaded Node: " + nodeList.get(i).getDescription());
		}
	}
	
	public List<TrailNode> getNodes()
	{
		return nodeList;
	}
	
	public TrailNode getNodeByID(int id)
	{
		for(TrailNode n : nodeList)
		{
			if(n.getID() == id)
			{
				return n;
			}
		}
		return null;
	}
	
	public void addNode(TrailNode newNode)
	{
		numberpoints++;
		nodeList.add(newNode);
	}
	
	public void addNodeRange(List<TrailNode> nodeRange)
	{
		nodeList.addAll(nodeRange);
		numberpoints = nodeList.size();
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getAuthor()
	{
		return author;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getCreationDate()
	{
		return creationDate;
	}
	
	public double getDetectRange()
	{
		return detectRange;
	}
	
	public int getNumberpoints()
	{
		return numberpoints;
	}
	
	public double getStartLat()
	{
		return startLat;
	}
	
	public double getStartLon()
	{
		return startLon;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setCreationDate(String creationDate)
	{
		this.creationDate = creationDate;
	}

	public void setDetectRange(double detectRange)
	{
		this.detectRange = detectRange;
	}

	public void setNumberpoints(int numberpoints)
	{
		this.numberpoints = numberpoints;
	}

	public void setStartLat(double startLat)
	{
		this.startLat = startLat;
	}

	public void setStartLon(double startLon)
	{
		this.startLon = startLon;
	}
	
	public String getUID()
	{
		return uid;
	}
	
	public void setUID(String uid)
	{
		this.uid = uid;
	}
	
	private Element convertSummaryToXMLElement(Document targetDocument)
	{
		Element summaryElement = targetDocument.createElement("summary");
		if(!uid.equals("-1"))
			summaryElement.setAttribute("uid", uid);

		Element titleElement = targetDocument.createElement("title");
		Text titleText = targetDocument.createTextNode(title);
		summaryElement.appendChild(titleElement);
		titleElement.appendChild(titleText);

		Element authorElement = targetDocument.createElement("author");
		Text authorText = targetDocument.createTextNode(author);
		summaryElement.appendChild(authorElement);
		authorElement.appendChild(authorText);

		Element descriptionElement = targetDocument.createElement("description");
		Text descriptionText = targetDocument.createTextNode(description);
		summaryElement.appendChild(descriptionElement);
		descriptionElement.appendChild(descriptionText);
		
		Element creationDateElement = targetDocument.createElement("creationDate");
		Text creationDateText = targetDocument.createTextNode(creationDate);
		summaryElement.appendChild(creationDateElement);
		creationDateElement.appendChild(creationDateText);
		
		Element detectRangeElement = targetDocument.createElement("detectRange");
		Text detectRangeText = targetDocument.createTextNode(detectRange+"");
		summaryElement.appendChild(detectRangeElement);
		detectRangeElement.appendChild(detectRangeText);

		Element numberpointElement = targetDocument.createElement("numberpoints");
		Text numberpointText = targetDocument.createTextNode(numberpoints+"");
		summaryElement.appendChild(numberpointElement);
		numberpointElement.appendChild(numberpointText);	
				
		Element startLatElement = targetDocument.createElement("startLat");
		Text startLatText = targetDocument.createTextNode(startLat+"");
		summaryElement.appendChild(startLatElement);
		startLatElement.appendChild(startLatText);
		
		Element startLonElement = targetDocument.createElement("startLon");
		Text startLonText = targetDocument.createTextNode(startLon+"");
		summaryElement.appendChild(startLonElement);
		startLonElement.appendChild(startLonText);
		
		return summaryElement;
	}
	
	private void loadSummaryFromElement(Element summaryElement)
	{
		// only load the attribute if it has been sent by server
		// this means the object is a downloaded traildata object
		Log.d("TrailManager", "Loading summary from element");
		if(summaryElement.hasAttribute("uid"))
		{
			Log.d("TrailManager", "Setting UID: " + summaryElement.getAttribute("uid"));
			this.uid = summaryElement.getAttribute("uid");
		}
	
		this.title = getTagValue("title", summaryElement);
		this.author = getTagValue("author", summaryElement);
		this.description = getTagValue("description", summaryElement);
		this.creationDate = getTagValue("creationDate", summaryElement);
		this.detectRange = Double.parseDouble(getTagValue("detectRange", summaryElement));
		this.numberpoints = Integer.parseInt(getTagValue("numberpoints", summaryElement));
		this.startLat = Double.parseDouble(getTagValue("startLat", summaryElement));
		this.startLon = Double.parseDouble(getTagValue("startLon", summaryElement));
	}
	
	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue();
  	}
	
}
