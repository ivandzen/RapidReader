package rapidreader.core;

public class TextNode extends SectionNode {
	
	public TextNode(String text, SectionNode previous, boolean title, boolean emphasis, String reference) {
		super(title ? SectionNodeType.Title : SectionNodeType.Text , previous);
		if(text != null)
			_text = text.split(" ");
		_emphasis = emphasis;
		_reference = reference;
	}

	@Override
	public int length() {
		if(_text == null)
			return 0;
		return _text.length;
	}
	
	public String		getWord(int index) { 
		if(index < 0 || index >= _text.length)
			return "";
		return _text[index]; 
	}
	
	public boolean		isBold() { return _bold; }
	public boolean		isEmphasis() { return _emphasis; }
	public String		getReference() { return _reference; }

	private String[]	_text;
	private boolean		_bold = false;
	private boolean		_emphasis = false;
	private String		_reference = null;
}
