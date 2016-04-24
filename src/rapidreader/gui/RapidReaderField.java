package rapidreader.gui;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.Timer;

import rapidreader.core.AbstractTextField;

public class RapidReaderField extends JTextField implements AbstractTextField {

	private static final long serialVersionUID = 1L;
	private String	_currentWord;
	private boolean _bold;
	private boolean _italic;
	private boolean _advertisment;
	private int		_charStep;
	private boolean	_enableAD = false;
	
	public void	setEnableAdvertisment(boolean value) { _enableAD = value; }
	
	public RapidReaderField() {
		super();
		setMinimumSize(new Dimension(70, 50));
		setCursor(Cursor.getDefaultCursor());
	}
	
	@Override
	public void showText(String text, boolean bold, boolean italic) {
		_currentWord = text;
		_bold = bold;
		_italic = italic;
		_advertisment = false;
		repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		Dimension size = getSize();
		
		if(_advertisment)
			g.setColor(new Color(240, 230, 220));
		else
			g.setColor(Color.WHITE);
		
		g.fillRect(0, 0, size.width, size.height);
		g.setColor(Color.BLACK);
		int fontHeight = size.height - 26;
		_charStep = (int)(0.6 * fontHeight);
		g.fillRect(0, 0, size.width, 3);
		g.fillRect(0, size.height - 3, size.width, 3);
		g.fillRect((int)(size.width / 2.0) - (int)(_charStep / 2) - 1, 0, 3, 12);
		g.fillRect((int)(size.width / 2.0) - (int)(_charStep / 2) - 1, size.height - 12, 3, 12);
		
		if(_currentWord == null || _currentWord.length() == 0)
			return;
		
		int fontType = Font.PLAIN;//_advertisment ? Font.ITALIC : Font.PLAIN;
		
		if(_bold)
			fontType = Font.BOLD;
		
		if(_italic)
			fontType |= Font.ITALIC;
		
		g.setFont(new Font(Font.MONOSPACED, fontType, fontHeight));
		
		int wordLength = getWordLength();
		int wordLengthHalf = wordLength > 1 ? (int)(wordLength / 2.0) : 1;
		int x_shift = (int)(size.width / 2.0) - wordLengthHalf * _charStep;
		
		int string_y_pos = size.height - 12 - (int)(fontHeight * 0.15);
		
		for(int i = 0; i < _currentWord.length(); i++)
			paintChar(g, _currentWord.charAt(i), x_shift + i * _charStep, string_y_pos, i == wordLengthHalf - 1);
	}
	
	private int		getWordLength() {
		int i;
		for(i = (_currentWord.length() - 1); i >= 0; i--)
			if(_currentWord.charAt(i) != '.' &&
					_currentWord.charAt(i) != ',' &&
					_currentWord.charAt(i) != ':' &&
					_currentWord.charAt(i) != ';' &&
					_currentWord.charAt(i) != '-' &&
					_currentWord.charAt(i) != '!' &&
					_currentWord.charAt(i) != '?' &&
					_currentWord.charAt(i) != '(' &&
					_currentWord.charAt(i) != ')' &&
					_currentWord.charAt(i) != '/' &&
					_currentWord.charAt(i) != '\\' &&
					_currentWord.charAt(i) != ' ' &&
					_currentWord.charAt(i) != '\t' &&
					_currentWord.charAt(i) != '\n' )
					break;
		return i + 1;
	}
	
	private void	paintChar(Graphics g, char c, int x_pos, int y_pos, boolean central) {
		if(central)
			g.setColor(Color.RED);
		else 
			g.setColor(Color.BLACK);

		char[] c_arr = { c };
		String s = String.copyValueOf(c_arr);
		g.drawString(s, x_pos, y_pos);
	}

	@Override
	public void showAdvertisment(String text) {
		if(!_enableAD)
			return;
		_currentWord = text;
		_bold = false;
		_italic = false;
		_advertisment = true;
		repaint();
	}
}
