package rapidreader.gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JPanel;

import rapidreader.core.AbstractParagraphViewer;
import rapidreader.core.TextNode;


public class TextNodeViewer extends JPanel implements ComponentListener, AbstractParagraphViewer, MouseListener{

	private static final long serialVersionUID = 1L;
	private int			_charStep;
	private int			_fontHeight;
	private TextNode	_currentNode = null;
	private int			_lineInterval = 2;
	private int			_borderSize = 8;
	private int			_rightBorderSize = 19;
	private int			_activeWordIndex = -1;
	private int			_lastActiveWordndex = -1;
	private boolean		_showScrollbar = false;
	private ArrayList<TextWord>	_words = new ArrayList<TextWord>();
	private ArrayList<ParagraphViewerListener>	_listeners = new ArrayList<ParagraphViewerListener>();

	class TextWord {
		
		private Point	_position;
		private int		_paragraphIndex;
		
		public TextWord(int paragraphIndex, Point position) {
			_position = position;
			_paragraphIndex = paragraphIndex;
			setFontHeight(16);
		}
		
		public void	paint(Graphics g) {
			if(_paragraphIndex == _activeWordIndex)
				g.setColor(Color.RED);
			else
				g.setColor(Color.BLACK);
			String word = _currentNode.getWord(_paragraphIndex);
			for(int i = 0; i < word.length(); i++) {
				char[] c_arr = { word.charAt(i) };
				String s = String.copyValueOf(c_arr);
				g.drawString(s, _position.x + i * _charStep, _position.y);
			}
			//g.drawString(_currentNode.getWord(_paragraphIndex), _position.x, _position.y);
		}
		
		public boolean		containsPoint(Point p) {
			Dimension size = getSize();
			if((p.x > _position.x) &&
					p.x < _position.x + size.width &&
					p.y > _position.y - size.height &&
					p.y < _position.y)
				return true;
			return false;
		}
		
		public void			setPosition(Point pos) { _position = pos; }
		public Point		getPosition() { return _position; }
		public Dimension	getSize() { return new Dimension(_charStep * _currentNode.getWord(_paragraphIndex).length(), _fontHeight); }
	}
	
	@Override
	public void	setCurrentNode(TextNode node) {
		_words.clear();
		_currentNode = node;
		_activeWordIndex = -1;
		_lastActiveWordndex = -1;
		if(_currentNode != null) {
			for(int i = 0; i < _currentNode.length(); i++)
				_words.add(new TextWord(i, new Point(0, 0)));
			updateLines();
		}
		updateUI();
	}
	
	@Override
	public void	setActiveWord(int index) {
		if(_currentNode == null || _words.size() <= index)
			return;
		_lastActiveWordndex = _activeWordIndex;
		_activeWordIndex = index;
		Graphics g = getGraphics();
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, _fontHeight));
		_words.get(_activeWordIndex).paint(g);
		if(_lastActiveWordndex >= 0)
			_words.get(_lastActiveWordndex).paint(g);
	}
	
	public TextNodeViewer() {
		addComponentListener(this);
		addMouseListener(this);
		setPreferredSize(new Dimension(1000, 200));
	}
	
	@Override
	public void		setFontHeight(int fontHeight) {
		_fontHeight = fontHeight;
		_charStep = (int)(0.6 * _fontHeight);
		_lineInterval = (int)(0.2 * _fontHeight);
		updateLines();
		updateUI();
	}
	
	public void	paint(Graphics g) {
		g.setColor(new Color(255, 255, 240));
		g.fillRect(0, 0, getSize().width, getSize().height);
		g.setColor(Color.BLACK);
		g.drawRect(1, 0, getSize().width - 3, getSize().height - 1);
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, _fontHeight));
		for(int i = 0; i < _words.size(); i++)
			_words.get(i).paint(g);
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		updateLines();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		updateLines();
	}
	
	private void	updateLines() {
		if(_currentNode == null || _words.isEmpty() || !isVisible())
			return;
		_showScrollbar = false;
		Dimension size = getSize();
		int currentWordIndex = 0;
		int current_y_pos = _borderSize + _fontHeight;
		while(currentWordIndex < _words.size()) {
			int current_x_pos = _borderSize;
			while(current_x_pos <= size.width - _rightBorderSize) {
				TextWord currentWord = _words.get(currentWordIndex);
				if(current_x_pos + currentWord.getSize().width + _charStep > size.width - _rightBorderSize)
					break;
				
				currentWordIndex++;
				currentWord.setPosition(new Point(current_x_pos, current_y_pos));
				current_x_pos += currentWord.getSize().width + _charStep;
				if(currentWordIndex == _words.size())
					break;
			}
			current_y_pos += _lineInterval + _fontHeight;
			if(current_y_pos >= size.height - _borderSize)
				_showScrollbar = true;
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		Point point = arg0.getPoint();
		for(int i = 0; i < _words.size(); i++)
			if(_words.get(i).containsPoint(point)) {
				setActiveWord(i);
				sendWordSelectedEvent(i);
			}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void	sendWordSelectedEvent(int wordIndex) {
		for(int i = 0; i < _listeners.size(); i++)
			_listeners.get(i).wordSelected(wordIndex);
	}

	@Override
	public void addParagraphViewerListener(ParagraphViewerListener listener) {
		if(listener == null || _listeners.contains(listener))
			return;
		_listeners.add(listener);
	}
}
