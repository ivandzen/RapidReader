package rapidreader.core;
abstract public class SectionNode {
	public enum SectionNodeType {
		Pause,
		Text,
		Title,
		Image
	}
	
	public SectionNode(SectionNodeType type) {
		_previous = null;
		_next = null;
		_type = type;
	}
	
	public SectionNode(SectionNodeType type, SectionNode previous) {
		if(previous != null) {
			previous._next = this;
			_shift = previous.getShift() + previous.length();
		}
		_previous = previous;
		_next = null;
		_type = type;
	}
	
	public SectionNodeType	getType() { return _type; }
	public SectionNode		getPrevious() { return _previous; }
	public SectionNode		getNext() { return _next; }
	abstract public int		length();
	public int				getShift() { return _shift; }
	
	private int				_shift = 0;
	private SectionNodeType	_type;
	private SectionNode		_previous;
	private SectionNode		_next;
}
