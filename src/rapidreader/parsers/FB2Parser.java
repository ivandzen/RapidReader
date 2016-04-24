package rapidreader.parsers;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import rapidreader.core.BookSection;
import rapidreader.core.Logger;
import rapidreader.core.PauseNode;
import rapidreader.core.ReaderBook;
import rapidreader.core.SectionNode;
import rapidreader.core.TextFileParser;
import rapidreader.core.TextNode;

public class FB2Parser implements TextFileParser {
	
	private DocumentBuilder		_documentBuilder;
	private String				_genre;
	private ArrayList<String>	_authors;
	private String				_title;
	private int					_sectioncounter = 0;
	
	public FB2Parser() {
		try {
			_authors = new ArrayList<String>();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			_documentBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			Logger.log(e.getMessage());
			_documentBuilder = null;
		}
	}

	@Override
	public String fileExtension() { return "fb2"; }

	@Override
	public ReaderBook parseFile(File file) {
		if(_documentBuilder == null || file == null)
			return null;
		try {
			Document doc = _documentBuilder.parse(file);
			Element rootElement = doc.getDocumentElement();
			_sectioncounter = 0;
			return parseBook(file.getAbsolutePath(), rootElement);
		} catch (SAXException e) {
			Logger.log(e.getMessage());
			return null;
		} catch (IOException e) {
			Logger.log(e.getMessage());
			return null;
		}
	}
	
	private ReaderBook	parseBook(String filename, Element element)
	{
		if(element == null)
		{
			System.out.println("parseBook: null reference");
			return null;
		}
		if(element.getNodeName() != "FictionBook")
		{
			System.out.println("Wrong FB2 document format");
			return null;
		}
		
		Element bookDescription = getFirstChildElement(element);
		if(!parseBookDescription(bookDescription)) {
			System.out.println("Book description may be corrupted");
			return null;
		}
		
		Element bookBody = getNextSiblingElement(bookDescription);
		BookSection data = parseBookSection(bookBody);
		if(data == null)
		{
			System.out.println("Book body may be corrupted");
			return null;
		}
		return new ReaderBook(filename, _authors, _genre, _title, data);
	}
	
	private boolean				parseBookDescription(Element desc) {
		if(desc.getNodeName() != "description")
			return false;
		Element titleInfo = getFirstChildElement(desc);
		if(titleInfo == null || titleInfo.getNodeName() != "title-info")
		
		_genre = "";
		_authors.clear();
		_title = "";
		//TODO
		return true;
	}
	
	private BookSection			parseBookSection(Element element){
		if(element == null || 
				(element.getNodeName() != "section" && element.getNodeName() != "body"))
			return null;
		SectionNode firstNode = null;
		SectionNode currentNode = null;
		String id = element.getAttribute("id");
		ArrayList<BookSection> subSections = new ArrayList<BookSection>();
		Element current = getFirstChildElement(element);
		while(current != null) {
			String currentElementName = current.getNodeName();
			if(currentElementName == "title")
				currentNode = parseTitle(current, currentNode, false);
			else if(currentElementName == "epigraph")
				currentNode = parseEpigraph(current, currentNode, false, false);
			else if(currentElementName == "p")
				currentNode = parseParagraph(current, currentNode, false, false);
			else if(currentElementName == "poem")
				currentNode = parsePoem(current, currentNode, false, false);
			else if(currentElementName == "section") {
				BookSection newSection = parseBookSection(current);
				if(newSection != null)
					subSections.add(newSection);
			}
			if(firstNode == null) {
				firstNode = currentNode;
				while(firstNode != null && firstNode.getPrevious() != null)
					firstNode = firstNode.getPrevious();
			}
			current = getNextSiblingElement(current);
		}
		
		return new BookSection(firstNode, id, _sectioncounter++, subSections);
	}
	
	private SectionNode		parseTitle(Element element, SectionNode firstNode, boolean emphasis) {
		SectionNode result = firstNode;
		Element current = getFirstChildElement(element);
		while(current != null) {
			if(current.getNodeName() == "p")
				result = parseParagraph(current, result, true, emphasis);
			current = getNextSiblingElement(current);
		}
		return new PauseNode(3, result);
	}
	
	private SectionNode 	parseEpigraph(Element element, SectionNode firstNode, boolean title, boolean emphasis) {
		SectionNode result = firstNode;
		Element current = getFirstChildElement(element);
		while(current != null) {
			if(current.getNodeName() == "p")
				result = parseParagraph(current, result, title, emphasis);
			else if(current.getNodeName() == "poem")
				result = parsePoem(current, result, title, emphasis);
			current = getNextSiblingElement(current);
		}
		return result;
	}
	
	private SectionNode			parsePoem(Element element, SectionNode firstNode, boolean title, boolean emphasis) {
		SectionNode result = firstNode;
		Element current = getFirstChildElement(element);
		while(current != null) {
			if(current.getNodeName() == "title")
				result = parseTitle(current, result, emphasis);
			else if(current.getNodeName() == "epigraph")
				result = parseEpigraph(current, result, title, emphasis);
			else if(current.getNodeName() == "stanza")
				result = parseStanza(current, result);
			current = getNextSiblingElement(current);
		}
		return new PauseNode(3, result);
	}
	
	private SectionNode		parseParagraph(Element element, SectionNode firstNode, boolean title, boolean emphasis) {
		SectionNode result = parseElementNodes(element, firstNode, title, emphasis, null);
		return new PauseNode(3, result);
	}
	
	private SectionNode			parseStanza(Element element, SectionNode firstNode) {
		return firstNode;
	}
	
	private SectionNode			parseEmphasis(Element element, SectionNode firstNode, String reference, boolean title) {
		return parseElementNodes(element, firstNode, title, true, reference);
	}
	
	private SectionNode			parseReference(Element element, SectionNode firstNode, boolean title, boolean emphasis) {
		String reference = element.getAttribute("xlink:href");
		return parseElementNodes(element, firstNode, title, emphasis, reference);
	}
	
	private SectionNode			parseElementNodes(Element element, SectionNode firstNode, boolean title, boolean emphasis, String reference) {
		SectionNode result = firstNode;
		Node current = element.getFirstChild();
		while(current != null) {
			if(current.getNodeType() == Node.TEXT_NODE){
				Text txt = (Text)current;
				result = new TextNode(txt.getNodeValue(), result, title, false, reference);
			}
			else if(current.getNodeType() == Node.ELEMENT_NODE && 
					current.getNodeName() == "emphasis") {
				result = parseEmphasis((Element)current, result, reference, title);
			}
			else if(current.getNodeType() == Node.ELEMENT_NODE && 
					current.getNodeName() == "a")
				result = parseReference((Element)current, result, title, emphasis);
			current = current.getNextSibling();
		}
		return result;
	}
	
	private Element				getFirstChildElement(Element element)
	{
		if(element == null)
			return null;
		Node current = element.getFirstChild();
		while(current != null && current.getNodeType() != Node.ELEMENT_NODE)
			current = current.getNextSibling();
		return (Element)current;
	}
	
	private Element				getNextSiblingElement(Element element)
	{
		if(element == null)
			return null;
		Node next = element.getNextSibling();
		while(next != null && next.getNodeType() != Node.ELEMENT_NODE)
			next = next.getNextSibling();
		return (Element)next;
	}
}
