package rapidreader.core;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;

public class BookSection extends DefaultMutableTreeNode{
	
	private static final long serialVersionUID = 1L;
	private int						_sectionNumber = 0;
	private BookSection				_parent = null;
	private BookSection				_next = null;
	private BookSection				_prev = null;
	private int						_sectionLength = -1;
	private String					_id = "";
	private String					_title = "";
	private SectionNode				_firstNode = null;
	
	public BookSection(SectionNode firstNode){
		super();
		_firstNode = firstNode;
		readTitle();
	}
	
	public BookSection(SectionNode firstNode, String id, int sectionNumber, ArrayList<BookSection> subSections){
		super();
		_sectionNumber = sectionNumber;
		_firstNode = firstNode;
		_id = id;
		if(subSections != null)
			for(int i = 0; i < subSections.size(); i++) {
				if(i > 0)
					subSections.get(i - 1)._next = subSections.get(i);
				if(i < subSections.size() - 1)
					subSections.get(i + 1)._prev = subSections.get(i);
				subSections.get(i)._parent = this;
				add(subSections.get(i));
			}
		readTitle();
	}
	
	public BookSection				getSection(int number) {
		if(number < 0)
			return null;
		if(number == _sectionNumber)
			return this;
		BookSection result = null;
		for(int i = 0; i < getChildCount(); i++) {
			BookSection current = (BookSection)getChildAt(i);
			result = current.getSection(number);
			if(result != null)
				break;
		}
		return result;
	}
	
	public String					toString() { return _title; }
	public String					getId() { return _id; }
	public String					getTitle() { return _title; }
	public SectionNode				getFirstSectionNode() { return _firstNode; }
	
	public int						getSectionLength() {
		if(_sectionLength < 0) {
			_sectionLength = 0;
			SectionNode current = _firstNode;
			while(current != null)
			{
				_sectionLength += current.length();
				current = current.getNext();
			}
		}
		return _sectionLength;
	}
	
	public static BookSection		createFromClipboard() { 
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		if(contents == null || !contents.isDataFlavorSupported(DataFlavor.stringFlavor)) 
			return null;
		
		try {
			String text = (String)contents.getTransferData(DataFlavor.stringFlavor);
			return new BookSection(new TextNode(text, null, false, false, ""));
		} catch (UnsupportedFlavorException e) {
			Logger.log(e.getMessage());
		} catch (IOException e) {
			Logger.log(e.getMessage());
		}
		return null;
	}
	
	public BookSection	getParentSection() { return _parent; }
	public BookSection	getNextSection() { return _next; }
	public BookSection	getPreviuosSection() { return _prev; }
	public int			getSectionNumber() { return _sectionNumber; }
	
	private void	readTitle() {
		if(_firstNode != null)
		{
			SectionNode current = _firstNode;
			while(current != null && current.getType() != SectionNode.SectionNodeType.Title && current.getType() != SectionNode.SectionNodeType.Text)
				current = current.getNext();
			if(current == null)
				return;
			while(current != null && current.getType() == SectionNode.SectionNodeType.Title)
			{
				TextNode titleNode = (TextNode)current;
				for(int i = 0; i < titleNode.length(); i++)
					_title += titleNode.getWord(i) + " ";
				current = current.getNext();
			}
			if(!_title.isEmpty())
				return;
			
			TextNode textNode = (TextNode)current;
			for(int i = 0; i < 5 && i < textNode.length(); i++)
				_title += textNode.getWord(i) + " ";
		}
	}
}
