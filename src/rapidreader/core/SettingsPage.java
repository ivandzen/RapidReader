package rapidreader.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SettingsPage {
	private String	urlStr = null;
	private Document document = null;
	
	abstract public class PageProperty {
		private String	name;
		
		public PageProperty(String propName) {
			name = propName;
		}
		
		public String	getName() { return name; }
		abstract public Object	getValue();
	}
	
	public class BooleanProperty extends PageProperty{
		private Boolean	value;
		public BooleanProperty(String name, boolean propValue) {
			super(name);
			value = new Boolean(propValue);
		}
		
		@Override
		public Boolean	getValue() { return value; }
	}
	
	public class IntegerProperty extends PageProperty{
		private Integer	value;
		public IntegerProperty(String name, int propValue) {
			super(name);
			value = new Integer(propValue);
		}
		
		@Override
		public Integer	getValue() { return value; }
	}
	
	public class FloatProperty extends PageProperty{
		private Float	value;
		public FloatProperty(String name, float propValue) {
			super(name);
			value = new Float(propValue);
		}
		
		@Override
		public Float	getValue() { return value; }
	}
	
	public class StringProperty extends PageProperty{
		private String	value;
		public StringProperty(String name, String propValue) {
			super(name);
			value = propValue;
		}
		
		@Override
		public String	getValue() { return value; }
	}
	
	public class ComplexProperty extends PageProperty {
		private ArrayList<PageProperty>	properties;
		public ComplexProperty(String name, ArrayList<PageProperty> internalProperties) {
			super(name);
			properties = internalProperties;
		}
		
		@Override
		public ArrayList<PageProperty>	getValue() { return properties; }
		
		public PageProperty	getProperty(String name) {
			for(int i = 0; i < properties.size(); i++) {
				PageProperty current = properties.get(i);
				if(current.getName() == name)
					return current;
			}
			return null;
		}
	}
	
	public void	update() throws IOException, SAXException, ParserConfigurationException {
		URL url = new URL(urlStr);
		URLConnection connection = url.openConnection();

		connection.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0;    H010818)" );
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String strLine = "";
		String content = "";
		while ((strLine = in.readLine()) != null){
		   content += strLine;
		}
		if(content == "")
			return;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource source = new InputSource(new StringReader(content));
		document = builder.parse(source);
	}
	
	public SettingsPage(String siteUrl) {
		if(siteUrl == null)
			return;
		urlStr = siteUrl;
		try {
			update();
		} catch (IOException e) {
			Logger.log(e.getMessage());
		} catch (SAXException e) {
			Logger.log(e.getMessage());
		} catch (ParserConfigurationException e) {
			Logger.log(e.getMessage());
		}
	}
	
	private PageProperty	parseProperty(Element element) {
		
		if(element.getTagName().compareToIgnoreCase("prop") != 0)
			return null;
		
		String name = element.getAttribute("id");
		String classAttribute = element.getAttribute("class");
		if(classAttribute == null)
			return null;
		
		String value = element.getTextContent();
		
		if(classAttribute.compareToIgnoreCase("boolean") == 0){
			value = value.replaceAll("\\s+", "");
			if(value.compareToIgnoreCase("true") == 0)
				return new BooleanProperty(name, true);
			else if(value.compareToIgnoreCase("false") == 0)
				return new BooleanProperty(name, false);
			return null;
		}
		else if(classAttribute.compareToIgnoreCase("integer") == 0) {
			value = value.replaceAll("\\s+", "");
			return new IntegerProperty(name, Integer.parseInt(value));
		}
		else if(classAttribute.compareToIgnoreCase("float") == 0) {
			value = value.replaceAll("\\s+", "");
			return new FloatProperty(name, Float.parseFloat(value));
		}
		else if(classAttribute.compareToIgnoreCase("string") == 0)
			return new StringProperty(name, value);
		else if(classAttribute.compareToIgnoreCase("complex") == 0) {
			Node child = element.getFirstChild();
			ArrayList<PageProperty> internalProperties = new ArrayList<PageProperty>();
			while(child != null) {
				if(child.getNodeType() == Node.ELEMENT_NODE) {
					PageProperty temp = parseProperty((Element)child);
					if(temp != null)
						internalProperties.add(temp);
				}
				child = child.getNextSibling();
			}
			if(internalProperties.isEmpty())
				return null;
			return new ComplexProperty(name, internalProperties);
		}
		return null;
	}
	
	public PageProperty	getProperty(String name) {
		if(document == null)
			return null;
		Element rootElement = document.getDocumentElement();
		Node node = rootElement.getFirstChild();
		while(node != null) {
			if(node.getNodeType() == Node.ELEMENT_NODE && ((Element)node).getAttribute("id").compareToIgnoreCase(name) == 0)
					break;
			node = node.getNextSibling();
		}
		
		if(node == null)
			return null;
		
		Element element = (Element)node;
		
		return parseProperty(element);
	}
}
