package rapidreader.core;

import java.util.ArrayList;



public class BookReaderCore {
	private AbstractTextPlayer 					_player = null;
	private BookManager							_bookManager = new BookManager();
	private ReaderBook							_currentBook = null;
	private ArrayList<BookReaderCoreListener>	_listeners = new ArrayList<BookReaderCoreListener>();
	
	public interface BookReaderCoreListener {
		public void	bookChanged(ReaderBook book);
	}
	
	private void	bookChangedEvent(ReaderBook book) {
		for(int i = 0; i < _listeners.size(); i++)
			_listeners.get(i).bookChanged(book);
	}
	
	public void	addBookReaderCoreListener(BookReaderCoreListener listener) {
		if(listener == null || _listeners.contains(listener))
			return;
		_listeners.add(listener);
	}
	
	public BookReaderCore(AbstractTextPlayer player) {
		_player = player;
	}
	
	public AbstractTextPlayer	player() { return _player; }
	
	public void			closeCurrentBook() {
		_currentBook = null;
		_player.setSection(null);
		bookChangedEvent(_currentBook);
	}
	
	public boolean		setBookmark(Bookmark bookmark) {
		if(bookmark == null)
			return false;
		if(!loadBook(bookmark.getFilename()))
			return false;
		BookSection section = _currentBook.getSection(bookmark.getSectionNumber());
		if(section == null)
			return false;
		if(_player != null) {
			_player.setSection(section);
			_player.setCurrentPosition(bookmark.getPosition());
		}
		return true;
	}
	
	public boolean		loadBook(String filename) {
		ReaderBook book = _bookManager.getBook(filename);
		if(book == null)
			return false;
		_currentBook = book;
		bookChangedEvent(_currentBook);
		if(_player != null)
			_player.setSection(_currentBook.getData());
		return true;
	}
	
	public Bookmark		createBookmarkAtCurrentPosition(String bookmarkName) {
		if(_player == null)
			return null;
		if(_currentBook == null)
			return null;
		String fileName = _currentBook.getFilename();
		if(_player.getCurrentNode() == null)
			return null;
		int sectionNumber = _player.getSectionNumber();
		if(sectionNumber < 0)
			return null;
		int currentPosition = _player.getCurrentPosition();
		return new Bookmark(bookmarkName, fileName, sectionNumber, currentPosition);
	}
	
	public boolean		isReadingActive() { return _currentBook != null && _player.getCurrentPosition() >= 0; }
	
	public	boolean		addTextFileParser(TextFileParser parser) { return _bookManager.addTextFileParser(parser); }
	
	public int			getBooksCount() { return _bookManager.getBooksCount(); }
	public ReaderBook	getBook(int index) { return _bookManager.getBook(index); }
}
