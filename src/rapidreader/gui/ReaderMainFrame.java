package rapidreader.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


import rapidreader.core.AbstractTextField;
import rapidreader.core.BookReaderCore;
import rapidreader.core.BookSection;
import rapidreader.core.Bookmark;
import rapidreader.core.BookmarkManager;
import rapidreader.core.Logger;
import rapidreader.core.NavigatorListener;
import rapidreader.core.ReaderBook;
import rapidreader.core.SectionPlayer;
import rapidreader.core.AbstractTextPlayer;
import rapidreader.core.SettingsPage;
import rapidreader.core.SettingsPage.BooleanProperty;
import rapidreader.core.SettingsPage.StringProperty;
import rapidreader.parsers.FB2Parser;

public class ReaderMainFrame extends JFrame implements ActionListener, 
														TreeSelectionListener, 
														AbstractTextPlayer.TextPlayerListener, 
														ChangeListener, 
														//NavigatorListener, 
														KeyListener,
														BookReaderCore.BookReaderCoreListener, 
														BookmarksMenu.BookmarksMenuListener{

	private static final long serialVersionUID = 1L;
	//========================ACTIONS===========================================
	private OpenFileAction						_openFileAction;
	private BookTreeVisibleAction				_bookTreeVisibleAction;
	private ParagraphPreviewAction				_paragraphPreviewAction;
	private StartStopReadingAction 				_startStopReadingAction;
	private FromBeginingAction					_fromBeginingAction;
	private NextChapterAction					_nextChapterAction;
	private ReadFromClipboardAction				_readFromClipboardAction;
	//========================BASE CLASSES======================================
	private BookReaderCore						_core;
	private BookmarkManager						_bookmarkManager = new BookmarkManager();
	//========================GUI COMPONENTS====================================
	//private TextNavigator						_textNavigator;
	private TextNodeViewer						_textNodeViewer = new TextNodeViewer();
	private JButton								_startStopButton;
	private JButton								_fromBeginingButton;
	private JButton								_nextChapterButton;
	private JButton								_readFromClipboardButton;
	private JToggleButton						_bookTreeVisible;
	private JToggleButton						_paragraphPreviewButton;
	private JComboBox							_speedCombo;
	private JSlider								_progressBar = new JSlider();
	private JLabel								_progressLabel;
	private JFileChooser						_fileDialog;
	private DefaultMutableTreeNode				_rootNode;
	private JFrame								_bookTreeFrame = new JFrame();
	private JTree								_bookTree;
	private JPanel 								_mainPanel = new JPanel();
	private ADBlock								_adBlock = new ADBlock();
	private AddBookmarkFrame					_addBookmarkFrame = new AddBookmarkFrame();
	//=======================ICONS==============================================
	private ImageIcon							_contentsIcon;
	private ImageIcon							_paragraphPreviewIcon;
	private ImageIcon							_playIcon;
	private ImageIcon							_pauseIcon;
	private ImageIcon							_fromStartIcon;
	private ImageIcon							_nextChapterIcon;
	
	private String								_applicationPath;
	
	private class AddBookmarkFrame extends JFrame {

		private static final long serialVersionUID = 1L;
		private JComboBox		bookmarkNameBox = new JComboBox();
		private JButton			okButton = new JButton(new OkButtonClicked());
		private JButton			cancelButton = new JButton(new CancelButtonClicked());
		
		private void	hideFrame() { 
			bookmarkNameBox.removeAllItems();
			setVisible(false); 
		}
		private void	fillBookmarkNameBoxItems() {
			Enumeration<String> bookmarkNames = _bookmarkManager.getBookmarkNames();
			while(bookmarkNames.hasMoreElements())
				bookmarkNameBox.addItem(bookmarkNames.nextElement());
		}
		
		public void		showFrame() {
			fillBookmarkNameBoxItems();
			setVisible(true);
		}
		
		private class OkButtonClicked extends AbstractAction {
			private static final long serialVersionUID = 1L;
			
			public OkButtonClicked() {
				super("OK");
			}

			@Override
			public void actionPerformed(ActionEvent arg0) {
				_bookmarkManager.addBookmark(_core.createBookmarkAtCurrentPosition((String)bookmarkNameBox.getSelectedItem()));
				hideFrame();
			}
		}
		
		private class CancelButtonClicked extends AbstractAction {
			private static final long serialVersionUID = 1L;
			
			public CancelButtonClicked() {
				super("Cancel");
			}

			@Override
			public void actionPerformed(ActionEvent arg0) {
				hideFrame();
			}
		}

		public AddBookmarkFrame() {
			super("Add Bookmark");
			setSize(200, 80);
			setAlwaysOnTop(true);
			setLocationRelativeTo(null);
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			
			JPanel bottomPanel = new JPanel();
			bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
			bottomPanel.add(cancelButton);
			bottomPanel.add(okButton);
			
			bookmarkNameBox.setEditable(true);
			mainPanel.add(bookmarkNameBox);
			mainPanel.add(bottomPanel);
			
			add(mainPanel);
			setVisible(false);
		}
	}
	
	class OpenFileAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		public OpenFileAction(){
			super("Open file");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int ret = _fileDialog.showDialog(null, "Open file");
			if(ret == JFileChooser.APPROVE_OPTION)
				_core.loadBook(_fileDialog.getSelectedFile().getAbsolutePath());
		}
	}
	
	class BookTreeVisibleAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public BookTreeVisibleAction() {
			super("Book tree");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(_bookTreeVisible.isSelected())
				_bookTreeFrame.setVisible(true);
			else
				_bookTreeFrame.setVisible(false);
		}
		
	}
	
	class ParagraphPreviewAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ParagraphPreviewAction() {
			super("Paragraph preview");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(_paragraphPreviewButton.isSelected()) {
				setSize(getSize().width, getSize().height + _textNodeViewer.getPreferredSize().height);
				_textNodeViewer.setVisible(true);
			}
			else {
				_textNodeViewer.setVisible(false);
				setSize(getSize().width, getSize().height - _textNodeViewer.getSize().height);
			}
		}
		
	}
	
	class StartStopReadingAction extends AbstractAction{

		private static final long serialVersionUID = 1L;

		public StartStopReadingAction() {
			super("Start/Stop");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(_core.player().isRunning())
				_core.player().pause();
			else 
				_core.player().play();
		}
	}
	
	class FromBeginingAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public FromBeginingAction() {
			super("", _fromStartIcon);
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(_core.player().getCurrentPosition() == 0)
				_core.player().previousSection();
			else
				_core.player().setCurrentPosition(0);
			_core.player().play();
		}
		
	}
	
	class NextChapterAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public NextChapterAction() {
			super("", _nextChapterIcon);
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(_core.player().nextSection())
				_core.player().play();
		}
	}
	
	class ReadFromClipboardAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public ReadFromClipboardAction() {
			super("Clipboard");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			_core.player().setSection(BookSection.createFromClipboard());
			_core.player().play();
		}
	}
	
	public ReaderMainFrame() {
		_applicationPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Rapier - The Rapid Reader");
        setSize(580,80);
        setMinimumSize(new Dimension(200, 200));
        setLocationRelativeTo(null);
		initActions();
		initWidgets();
		initMenuBar();
		addKeyListener(this);
		try {
			_bookmarkManager.readBookmarksFromFile(_applicationPath + "bookmarks.bmf");
		} catch (FileNotFoundException e) {
			Logger.log(e.getMessage());
		} catch (IOException e) {
			Logger.log(e.getMessage());
		}
		
		addWindowListener(new WindowAdapter() {
							public void windowClosing(WindowEvent e) {
								try {
									_bookmarkManager.saveBookmarksToFile(_applicationPath + "bookmarks.bmf");
								} catch (FileNotFoundException e1) {
									Logger.log(e1.getMessage());
								} catch (IOException e1) {
									Logger.log(e1.getMessage());
								}
							}
							});
	}
	
	private void	setBottomAdBlockVisible(boolean isVisible) {
		_adBlock.setVisible(isVisible);
	}
	
	private void	initActions() {
		try {
			_contentsIcon = new ImageIcon(ImageIO.read(getClass().getResource("/rapidreader/resources/Contents.png")));
			_paragraphPreviewIcon = new ImageIcon(ImageIO.read(getClass().getResource("/rapidreader/resources/ParagraphPreview.png")));
			_playIcon = new ImageIcon(ImageIO.read(getClass().getResource("/rapidreader/resources/Play.png")));
			_pauseIcon = new ImageIcon(ImageIO.read(getClass().getResource("/rapidreader/resources/Pause.png")));
			_fromStartIcon = new ImageIcon(ImageIO.read(getClass().getResource("/rapidreader/resources/FromStart.png")));
			_nextChapterIcon = new ImageIcon(ImageIO.read(getClass().getResource("/rapidreader/resources/NextChapter.png")));
		} catch (IOException e) {
			Logger.log(e.getMessage());
		}
		
		_bookTreeVisibleAction = new BookTreeVisibleAction();
		_paragraphPreviewAction = new ParagraphPreviewAction();
		_openFileAction = new OpenFileAction();
		_startStopReadingAction = new StartStopReadingAction();
		_fromBeginingAction = new FromBeginingAction();
		_nextChapterAction	= new NextChapterAction();
		_readFromClipboardAction = new ReadFromClipboardAction();

	}
	
	private void	initWidgets() {	
		
		_mainPanel.setLayout(new BoxLayout(_mainPanel, BoxLayout.Y_AXIS));
		_fileDialog = new JFileChooser();
		
		SectionPlayer player = new SectionPlayer();
		player.addPlayerListener(this);
		_core = new BookReaderCore(player);
		_core.addTextFileParser(new FB2Parser());
		_core.addBookReaderCoreListener(this);
		
		_progressBar.setMinimum(0);
		_progressBar.setMaximum(0);
		_progressBar.addChangeListener(this);
		
		JPanel controlPanel = new JPanel();
		controlPanel.addKeyListener(this);
		_readFromClipboardButton = new JButton(_readFromClipboardAction);
		controlPanel.add(_readFromClipboardButton);
		
		_bookTreeVisible = new JToggleButton(_bookTreeVisibleAction);
		_bookTreeVisible.setText(" ");
		_bookTreeVisible.setIcon(_contentsIcon);
		_bookTreeVisible.setSelected(false);
		_bookTreeVisible.addKeyListener(this);
		controlPanel.add(_bookTreeVisible);
		
		_paragraphPreviewButton = new JToggleButton(_paragraphPreviewAction);
		_paragraphPreviewButton.setText("");
		_paragraphPreviewButton.setIcon(_paragraphPreviewIcon);
		_paragraphPreviewAction.actionPerformed(null);
		_paragraphPreviewButton.setSelected(false);
		controlPanel.add(_paragraphPreviewButton);
		
		_fromBeginingButton = new JButton(_fromBeginingAction);
		controlPanel.add(_fromBeginingButton);
		
		_startStopButton = new JButton(_startStopReadingAction);
		_startStopButton.setText("");
		_startStopButton.setIcon(_playIcon);
		_startStopButton.addKeyListener(this);
		controlPanel.add(_startStopButton);
		
		_nextChapterButton = new JButton(_nextChapterAction);
		controlPanel.add(_nextChapterButton);

		String[] comboItems = {
				"150 wpm",
				"200 wpm",
				"250 wpm",
				"300 wpm", 
				"350 wpm",
				"400 wpm",
				"500 wpm", 
				"600 wpm", 
				"700 wpm", 
				"800 wpm", 
				"900 wpm", 
				"1000 wpm" 
				};
		_speedCombo = new JComboBox(comboItems);
		_speedCombo.setSelectedIndex(1);
		_speedCombo.addActionListener(this);
		controlPanel.add(_speedCombo);
		
		_progressLabel = new JLabel("0/0");
		controlPanel.add(_progressLabel);
		
		_rootNode = new DefaultMutableTreeNode();
		_bookTree = new JTree(_rootNode);
		_bookTree.setExpandsSelectedPaths(true);
		_bookTree.setShowsRootHandles(true);
		_bookTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		_bookTree.setEditable(false);
		_bookTree.addTreeSelectionListener(this);
		_bookTree.setRootVisible(true);
		JScrollPane bookTreePane = new JScrollPane(_bookTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		RapidReaderField textField = new RapidReaderField();
		textField.addKeyListener(this);
		player.setTextField(textField);
		
		player.setParagraphViewer(_textNodeViewer);
		
		_mainPanel.add(textField);
		_mainPanel.add(_progressBar);
		_mainPanel.add(controlPanel);
		_mainPanel.add(_textNodeViewer);
		_mainPanel.add(_adBlock);
		add(_mainPanel, BorderLayout.CENTER);
		
		_bookTreeFrame.add(bookTreePane, BorderLayout.CENTER);
		_bookTreeFrame.setSize(300, 400);
		_bookTreeFrame.setTitle("Rapire - Table of contents");
		_bookTreeFrame.setVisible(false);
		
		setBackground(Color.WHITE);
		setAlwaysOnTop(true);
		setBottomAdBlockVisible(true);
		
		SettingsPage settings = new SettingsPage("http://rapidreader.esy.es/settings.xml");
		BooleanProperty prop = (BooleanProperty)settings.getProperty("bottom_ad_visible");
		if(prop != null) 
			_adBlock.setVisible(prop.getValue());
		
		StringProperty prop2 = (StringProperty)settings.getProperty("bottom_ad_text");
		if(prop2 != null)
			_adBlock.setText(prop2.getValue());
		
		StringProperty prop3 = (StringProperty)settings.getProperty("bottom_ad_href");
		if(prop3 != null)
			_adBlock.setUrl(prop3.getValue());
		
		BooleanProperty prop4 = (BooleanProperty)settings.getProperty("reader_field_ad_enabled");
		if(prop4 != null)
			textField.setEnableAdvertisment(prop4.getValue());
	}
	
	private void initMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		
		JMenuItem openMenuItem = new JMenuItem("Open");
		openMenuItem.setAction(_openFileAction);
		fileMenu.add(openMenuItem);
		
		JMenuItem fromBeginingItem = new JMenuItem("From begining");
		fromBeginingItem.setAction(_fromBeginingAction);
		
		BookmarksMenu bookmarksMenu = new BookmarksMenu();
		_bookmarkManager.addBookmarkManagerListener(bookmarksMenu);
		bookmarksMenu.addBookmarksMenuListener(this);
		
		menuBar.add(fileMenu);
		menuBar.add(bookmarksMenu);
		setJMenuBar(menuBar);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		switch(_speedCombo.getSelectedIndex())
		{
		case 0: _core.player().setReadingSpeed(150); break;
		case 1: _core.player().setReadingSpeed(200); break;
		case 2: _core.player().setReadingSpeed(250); break;
		case 3: _core.player().setReadingSpeed(300); break;
		case 4: _core.player().setReadingSpeed(350); break;
		case 5: _core.player().setReadingSpeed(400); break;
		case 6: _core.player().setReadingSpeed(500); break;
		case 7: _core.player().setReadingSpeed(600); break;
		case 8: _core.player().setReadingSpeed(700); break;
		case 9: _core.player().setReadingSpeed(800); break;
		case 10: _core.player().setReadingSpeed(900); break;
		case 11: _core.player().setReadingSpeed(1000); break;
		}
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		if(_rootNode == (DefaultMutableTreeNode)_bookTree.getLastSelectedPathComponent())
			return;
		BookSection selectedSection = (BookSection)_bookTree.getLastSelectedPathComponent();
		if(selectedSection == null)
			return;
		_core.player().setSection(selectedSection);
	}
	
	@Override
	public void currentSectionChanged(BookSection section) {
		if(section == null)
			return;
		_progressBar.setMaximum(_core.player().getSectionLength());
		ArrayList<Object> path = new ArrayList<Object>();
		BookSection current = section;
		while(current != null) {
			path.add(current);
			current = current.getParentSection();
		}
		path.add(_rootNode);
		Collections.reverse(path);
		_bookTree.setSelectionPath(new TreePath(path.toArray()));
	}

	@Override
	public void playerStarted() { 
		_startStopButton.setIcon(_pauseIcon);
	}

	@Override
	public void playerStoped() { 
		_startStopButton.setIcon(_playIcon);
	}

	@Override
	public void positionChanged(int position) {
		_progressBar.setValue(_core.player().getCurrentPosition());
		StringBuilder sb = new StringBuilder();
		sb.append(_core.player().getCurrentPosition());
		sb.append("/");
		sb.append(_core.player().getSectionLength());
		_progressLabel.setText(sb.toString());
	}

	@Override
	public void sectionFinished() { 
		_nextChapterButton.setVisible(true); 
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if(_progressBar.getValue() != _core.player().getCurrentPosition()) {
			_core.player().pause();
			_core.player().setCurrentPosition(_progressBar.getValue());
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_LEFT: {
			_core.player().rewind();
			break;
		}
		case KeyEvent.VK_RIGHT: {
			_core.player().forward();
			break;
		}
		case KeyEvent.VK_SPACE: {
			_startStopReadingAction.actionPerformed(null);
			break;
		}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bookChanged(ReaderBook book) {
		_rootNode.removeAllChildren();
		if(book != null) {
			_rootNode.add(book.getData());
			if(book.getTitle() != null && book.getTitle().length() > 0)
				setTitle(book.getTitle() + " - Rapire - The Rapid Reader");
			_bookTree.updateUI();
		}
	}

	@Override
	public void executeAddBookmarkDialog() {
		if(_core.isReadingActive())
			_addBookmarkFrame.showFrame();
	}

	@Override
	public void executeBookmarkListDialog() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCurrentBookmark(Bookmark bookmark) {
		if(bookmark == null)
			return;
		_core.setBookmark(bookmark);
	}
	
}