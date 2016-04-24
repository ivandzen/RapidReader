package rapidreader.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;


public class SectionPlayer extends AbstractTextPlayer implements ActionListener, AbstractParagraphViewer.ParagraphViewerListener {
	
	private final int 	default_per_word_delay = 6;
	private final int 	punctuation_delay = 4;
	private final int 	paragraph_delay = 12;
	private final int 	short_word_delay = -2;
	private final int 	long_word_delay = 2;
	private final int	giant_word_delay = 4;
	private final int	short_word_length = 4;
	private final int	long_word_length = 9;
	private final int	giant_word_length = 12;
	
	private final int	advertismentPeriod = 50;
	private final int	maxAdvertismentLength = 120;
	private final int	maxAdvertismentWordCount = 15;
	
	private enum WorkingMode {
		Reading,
		Advertisment
	}
	
	private WorkingMode				_mode = WorkingMode.Reading;
	private	int						_advertismentPeriodCounter = advertismentPeriod;
	private String[]				_advertisment = null;
	private int						_currentADPosition = 0;
	
	private BookSection				_section;
	private SectionNode				_currentNode;
	private int						_currentPosition;
	private int						_readingSpeed = 200;
	private Timer					_timer;
	private int						_timerUnit = 60;
	private int						_delayCounter = default_per_word_delay;
	private boolean					_fictionMode = true;
	private ArrayList<TextPlayerListener>	_listeners;
	AbstractTextField 						_textField = null;
	AbstractParagraphViewer			_paragraphViewer = null;
	
	public SectionPlayer() {
		_timer = new Timer(_timerUnit, this);
		_listeners = new ArrayList<TextPlayerListener>();
		setReadingSpeed(200);
		setAdvertisment("LobodaTELL");
	}
	
	@Override
	public void						play() {
		if(!isSectionValid()) {
			playerStopedEvent();
			return;
		}
		if(_timer.isRunning()) return;
		_timer.setDelay(_timerUnit);
		_timer.start();
		playerStartedEvent();
	}
	
	@Override
	public void						pause() {
		if(!_timer.isRunning()) return;
		_timer.stop();
		playerStopedEvent();
	}
	
	@Override
	public boolean					isRunning() { return _timer.isRunning(); }
	
	@Override
	public void						rewind() {
		if(!isSectionValid())
			return;
		pause();
		if(!setCurrentPosition(_currentPosition - 1))
			return;
		if(_currentNode.getType() == SectionNode.SectionNodeType.Text || _currentNode.getType() == SectionNode.SectionNodeType.Title) {
			TextNode currentTextNode = (TextNode)_currentNode;
			String currentWord = currentTextNode.getWord(_currentPosition - currentTextNode.getShift());
			showTextEvent(currentWord,
					currentTextNode.getType() == SectionNode.SectionNodeType.Title || currentTextNode.isBold(), 
					currentTextNode.isEmphasis());
		}
	}
	
	@Override
	public void						forward() {
		if(!isSectionValid())
			return;
		pause();
		if(!setCurrentPosition(_currentPosition + 1))
			return;
		if(_currentNode.getType() == SectionNode.SectionNodeType.Text || _currentNode.getType() == SectionNode.SectionNodeType.Title) {
			TextNode currentTextNode = (TextNode)_currentNode;
			String currentWord = currentTextNode.getWord(_currentPosition - currentTextNode.getShift());
			showTextEvent(currentWord,
					currentTextNode.getType() == SectionNode.SectionNodeType.Title || currentTextNode.isBold(), 
					currentTextNode.isEmphasis());
		}
	}
	
	@Override
	public void						setReadingSpeed(int value) {
		if(value < 1) 
			return;
		_readingSpeed = value;
		_timerUnit = (int)(60000.0 / (value * default_per_word_delay));
		_timer.setDelay(_timerUnit);
	}
	
	@Override
	public int						getReadingSpeed() { return _readingSpeed; }
	
	@Override
	public boolean					setSection(BookSection section) {
		pause();
		_section = section;
		
		if(_section != null) {
			if(_section.getFirstSectionNode() == null)
				return setSection((BookSection)_section.getChildAt(0));
			_currentNode = _section.getFirstSectionNode();
			while(_currentNode.getType() == SectionNode.SectionNodeType.Pause)
				_currentNode = _currentNode.getNext();
			
			if(_paragraphViewer != null)
				_paragraphViewer.setCurrentNode((TextNode)_currentNode);
		}
		else {
			_currentNode = null;
			return false;
		}
		setCurrentPosition(0);
		currentSectionChangedEvent();
		return true;
	}
	
	@Override
	public int						getSectionLength() {
		if(_section == null)
			return 0;
		return _section.getSectionLength();
	}
	
	@Override
	public boolean					setCurrentPosition(int pos) {
		if(_section == null ||
				pos < 0) 
			return false;
		if(pos >= _section.getSectionLength()) {
			pause();
			sectionFinishedEvent();
			return false;
		}
		
		SectionNode oldNode = _currentNode;
		if(pos - _currentPosition < 0)
			while(_currentNode != null && (pos < _currentNode.getShift() || pos >= (_currentNode.getShift() + _currentNode.length())))
				_currentNode = _currentNode.getPrevious();
		else if(pos - _currentPosition > 0)
			while(_currentNode != null && (pos < _currentNode.getShift() || pos >= (_currentNode.getShift() + _currentNode.length())))
				_currentNode = _currentNode.getNext();
		if(_currentNode == null)
			_currentNode = oldNode;
		
		if(_paragraphViewer != null)
			_paragraphViewer.setCurrentNode((TextNode)_currentNode);
		
		_currentPosition = pos;
		positionChangedEvent();
		return true;
	}
	
	@Override
	public int						getCurrentPosition() { return _currentPosition; }
	
	private void					playerStartedEvent() {
		for(int i = 0; i < _listeners.size(); i++)
			_listeners.get(i).playerStarted();
	}
	
	private void					playerStopedEvent() {
		for(int i = 0; i < _listeners.size(); i++)
			_listeners.get(i).playerStoped();
	}
	
	private void					sectionFinishedEvent() {
		for(int i = 0; i < _listeners.size(); i++)
			_listeners.get(i).sectionFinished();
	}
	
	private void					positionChangedEvent() {
		for(int i = 0; i < _listeners.size(); i++)
			_listeners.get(i).positionChanged(_currentPosition);
		if(_paragraphViewer != null) 
			_paragraphViewer.setActiveWord(_currentPosition - _currentNode.getShift());
	}
	
	private void					currentSectionChangedEvent() {
		showTextEvent("", false, false);
		for(int i = 0; i < _listeners.size(); i++)
			_listeners.get(i).currentSectionChanged(_section);
	}
	
	private void					showTextEvent(String text, boolean bold, boolean italic) {
		if(_textField == null)
			return;
		_textField.showText(text, bold, italic);
	}
	
	private void					showAdvertismentEvent(String text) {
		if(_textField == null)
			return;
		_textField.showAdvertisment(text);
	}
	
	public boolean setAdvertisment(String text) {
		_advertisment = null;
		if(text == null || text.length() > maxAdvertismentLength)
			return false;
		String[] words = text.split(" ");
		if(words.length > maxAdvertismentWordCount)
			return false;
		_advertisment = words;
		_currentADPosition = 0;
		return true;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		_delayCounter--;
		if(_delayCounter == 0) {
			if(_mode == WorkingMode.Reading) {
				_advertismentPeriodCounter--;
				if(_advertismentPeriodCounter <= 0) {
					_mode = WorkingMode.Advertisment;
					_advertismentPeriodCounter = advertismentPeriod;
				}
			}
			timerTick();
		}
	}
	
	private void					timerTick() {
		if(_mode == WorkingMode.Reading) {
			if(!isSectionValid()) {
				pause();
				sectionFinishedEvent();
				return;
			}
		
			if(_currentNode.getType() == SectionNode.SectionNodeType.Pause) {
				showTextEvent("    ", false, false);
				setDelayCounter(paragraph_delay);
				nextNode();
				return;
			}
		
			if(_currentNode.getType() == SectionNode.SectionNodeType.Image) {
				nextNode();
				return;
			}
		
			positionChangedEvent();
			showCurrentWord();
			_currentPosition++;
			if(_currentPosition >= _section.getSectionLength()) {
				pause();
				sectionFinishedEvent();
				return;
			}
		
			if(_currentPosition - _currentNode.getShift() == _currentNode.length())
				nextNode();
		}
		else if(_mode == WorkingMode.Advertisment) {
			if(_advertisment == null || _currentADPosition == _advertisment.length) {
				_mode = WorkingMode.Reading;
				_currentADPosition = 0;
				setDelayCounter(1);
				return;
			}
			String currentWord = _advertisment[_currentADPosition++];
			showAdvertismentEvent(currentWord);
			setDelayCounter(1);
		}
	}
	
	private int	computeWordDelay(String word) {
		int result = default_per_word_delay;
		
		if(word.length() <= short_word_length)
			result += short_word_delay;
		else if(word.length() >= giant_word_length)
			result += giant_word_delay;
		else if(word.length() >= long_word_length)
			result += long_word_delay;
		
		if(_fictionMode && word.length() > 0) {
			char lastSymbol = word.charAt(word.length() - 1);
			if(lastSymbol == '.' || 
				lastSymbol == ',' ||
				lastSymbol == ';' ||
				lastSymbol == ':' ||
				lastSymbol == '?')
				result += punctuation_delay;
		}
		return result;
	}
	
	private void					showCurrentWord() {
		TextNode currentTextNode = (TextNode)_currentNode;
		String currentWord = currentTextNode.getWord(_currentPosition - currentTextNode.getShift());
		showTextEvent(currentWord,
				currentTextNode.getType() == SectionNode.SectionNodeType.Title || currentTextNode.isBold(), 
				currentTextNode.isEmphasis());
		setDelayCounter(computeWordDelay(currentWord));
	}
	
	private void					nextNode() {
		if(_currentNode.getNext() == null) {
			pause();
			sectionFinishedEvent();
			return;
		}
		_currentNode = _currentNode.getNext();
		if(_paragraphViewer == null)
			return;
		if(_currentNode.getType() == SectionNode.SectionNodeType.Title ||
				_currentNode.getType() == SectionNode.SectionNodeType.Text)
			_paragraphViewer.setCurrentNode((TextNode)_currentNode);
	}
	
	private void					setDelayCounter(int value) {
		if(value <= 0) 
			return;
		_delayCounter = value;
	}
	
	private boolean					isSectionValid() { return _section != null && _currentNode != null; }
	
	@Override
	public void						addPlayerListener(TextPlayerListener listener) {
		if(listener == null || _listeners.contains(listener))
			return;
		_listeners.add(listener);
	}
	
	@Override
	public void						setTextField(AbstractTextField textField) { _textField = textField; }
	
	@Override
	public void						setParagraphViewer(AbstractParagraphViewer paragraphViewer) { 
		if(paragraphViewer == null)
			return;
		_paragraphViewer = paragraphViewer;
		_paragraphViewer.addParagraphViewerListener(this);
	}
	
	@Override
	public boolean					nextSection() {
		if(_section == null || _section.getNextSection() == null)
			return false;
		return setSection(_section.getNextSection());
	}
	
	@Override
	public boolean					previousSection() {
		if(_section == null || _section.getPreviuosSection() == null)
			return false;
		return setSection(_section.getPreviuosSection());
	}
	
	@Override
	public SectionNode				getCurrentNode() { return _currentNode; }

	@Override
	public void wordSelected(int paragraphIndex) {
		if(_currentNode == null)
			return;
		setCurrentPosition(_currentNode.getShift() + paragraphIndex);
	}

	@Override
	public String getSectionTitle() {
		if(_section == null)
			return null;
		return _section.getTitle();
	}

	@Override
	public int getSectionNumber() {
		if(_section == null)
			return -1;
		return _section.getSectionNumber();
	}
}
