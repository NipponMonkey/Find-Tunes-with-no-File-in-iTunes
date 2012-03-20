package nm.itunes.findTunesWithNoFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TunesXML {
	
	private Element root;
	
	public void init() {
		DocumentBuilderFactory docFB = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docB = docFB.newDocumentBuilder();
			Document doc = docB.parse(getXMLFile());
			root = doc.getDocumentElement();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void findTunesWithNoFile() {
		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			XPathExpression xPathExpression = xPath.compile("dict/dict/dict");
			NodeList tunesList = (NodeList) xPathExpression.evaluate(root, XPathConstants.NODESET);
			System.out.println("Searching " + tunesList.getLength() + " tunes in library...");
			long time = System.nanoTime();
			int counter = 0;
			for (int i = 0; i < tunesList.getLength(); i++) {
				Node tune = tunesList.item(i);
				String location = getTuneLocation(tune);
				if (!location.isEmpty()) {
					if (new File(location).isFile() == false) {
						counter++;
						System.out.println("Not Found: " + location + "; " + getTuneDetail(tune, "Name"));
					}
				}
			}
			time = System.nanoTime() - time;
			String secString = new DecimalFormat("#0.00").format(time / 1000000000f) + "secs";
			System.out.println("Found " + counter + " broken links in " + secString);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getTuneLocation(Node tune) {
		NodeList details = tune.getChildNodes();
		int len = details.getLength();
		for (int i = len - 2; i >= 0; i -= 1) {
			if (details.item(i) != null) {
				if (details.item(i).getTextContent().equals("Location") == true) {
					return convertLocationForWindows(details.item(i + 1).getTextContent());
				}
			}
		}
		return "";
	}
	
	private String getTuneDetail(Node tune, String key) {
		NodeList details = tune.getChildNodes();
		int len = details.getLength();
		for (int i = 0; i < len; i++) {
			if (details.item(i) != null) {
				if (details.item(i).getTextContent().equals(key) == true) {
					return details.item(i + 1).getTextContent();
				}
			}
		}
		return "";
	}
	
	private String convertLocationForWindows(String location) {
		Pattern pattern = Pattern.compile("file://localhost/(.+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(location);
		if (matcher.matches()) {
			try {
				String l = matcher.group(1).replace("+", "{KEY_PLUS}");
				return URLDecoder.decode(l, "UTF-8").replace("{KEY_PLUS}", "+");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
	
	private File getXMLFile() {
		return new File("C:/Users/NipponMonkey/Music/iTunes/iTunes Music Library.xml");
	}
}
