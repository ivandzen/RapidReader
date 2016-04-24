package rapidreader.core;


public class PauseNode extends SectionNode {
	public PauseNode(int time) {
		super(SectionNodeType.Pause);
		_timeUnits = time;
	}
	
	public PauseNode(int time, SectionNode previous) {
		super(SectionNodeType.Pause, previous);
		_timeUnits = time;
	}
	
	public int	getTimeUnits() { return _timeUnits; }
	
	@Override
	public int length() { return 0; }
	
	private int	_timeUnits = 2;
}
