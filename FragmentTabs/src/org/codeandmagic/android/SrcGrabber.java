package org.codeandmagic.android;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//http://argillander.wordpress.com/2011/11/23/get-web-page-source-code-in-android/

public class SrcGrabber
{
    private HttpGet mRequest;
    private HttpClient mClient;
    private BufferedReader mReader;

    private DocumentBuilder mBuilder;

    private StringBuffer mBuffer;
    private String mNewLine;

    private String[] removeContent;


    public SrcGrabber()
    {
        mRequest = new HttpGet();
        mClient = new DefaultHttpClient();
        mReader = null;

        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            mBuilder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }

        mBuffer = new StringBuffer(2000);
        mNewLine = System.getProperty("line.separator");
        
        removeContent = new String[] { "<!-- Hosting24 Analytics Code -->",
        "<script type=\"text/javascript\" src=\"http://stats.hosting24.com/count.php\"></script>",
        "<!-- End Of Analytics Code -->" };
    }

    public String grabSource(String url) throws ClientProtocolException, IOException, URISyntaxException
    {
        mBuffer.setLength(0);
        
        //int count = 0;
        try
        {
            mRequest.setURI(new URI(url));
            HttpResponse response = mClient.execute(mRequest);

            mReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            while ((line = mReader.readLine()) != null)
            {
            	//count++;
                mBuffer.append(line);
                mBuffer.append(mNewLine);
            }
        }
        finally
        {
            closeReader();
        }

        return removeContent(mBuffer.toString());
    }
    
    public String grabSource(String url, String[] params) throws ClientProtocolException, IOException, URISyntaxException
    {
        mBuffer.setLength(0);
        
        //int count = 0;
        try
        {
            HttpPost httppost = new HttpPost(url);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("summary", params[0]));
            nameValuePairs.add(new BasicNameValuePair("fulldata", params[1]));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = mClient.execute(httppost);

            mReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            while ((line = mReader.readLine()) != null)
            {
            	//count++;
                mBuffer.append(line);
                mBuffer.append(mNewLine);
            }
        }
        finally
        {
            closeReader();
        }

        return removeContent(mBuffer.toString());
    }
    
    public String removeContent(String source)
    {
    	String result = source;
    	for(String rm : removeContent)
    	{
    		result = result.replace(rm, "");
    	}
    	return result;
    }

    public String parseTags(String code, String[] tags) throws IOException, SAXException
    {
        mBuffer.setLength(0);

        Document doc = mBuilder.parse(new ByteArrayInputStream(code.getBytes()));
        Element el = doc.getDocumentElement();

        for (String tag : tags)
            parseTags(el.getElementsByTagName(tag));

        return mBuffer.toString();
    }

    protected void parseTag(Node node)
    {
        if (node.getNodeType() == Node.TEXT_NODE)
        {
            mBuffer.append(node.getNodeValue());
            mBuffer.append(mNewLine);
        }
    }

    private void closeReader()
    {
        if (mReader == null)
            return;

        try
        {
            mReader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void parseTags(NodeList nodes)
    {
        int length = nodes.getLength();

        for (int i = 0; i < length; ++i)
            parseTag(nodes.item(i).getFirstChild());
    }
}
