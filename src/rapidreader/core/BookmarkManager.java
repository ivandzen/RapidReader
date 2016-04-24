package rapidreader.core;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map.Entry;



public class BookmarkManager {
	private Hashtable<String, Bookmark> _bookmarks = new Hashtable<String, Bookmark>();
	private ArrayList<BookmarkManagerListener>	_listeners = new ArrayList<BookmarkManagerListener>();
	
	public interface	BookmarkManagerListener {
		public void		bookmarkAdded(Bookmark bookmark);
		public void		bookmarkDeleted(String name);
	}
	
	public void		addBookmarkManagerListener(BookmarkManagerListener listener) {
		if(listener == null)
			return;
		if(_listeners.contains(listener))
			return;
		_listeners.add(listener);
	}
	
	private void	bookmarkAddedEvent(Bookmark bookmark) {
		for(int i = 0; i < _listeners.size(); i++)
			_listeners.get(i).bookmarkAdded(bookmark);
	}
	
	private void	bookmarkDeletedEvent(String name) {
		for(int i = 0; i < _listeners.size(); i++)
			_listeners.get(i).bookmarkDeleted(name);
	}
	
	public int	readBookmarksFromFile(String filename) 
									throws FileNotFoundException,
											IOException {
		if(filename == null)
			return 0;
		File file = new File(filename);
		if(!file.exists())
			return 0;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String string = reader.readLine();
		int result = 0;
		while(string != null) {
			if(addBookmark(Bookmark.deserialize(string)))
				result++;
			string = reader.readLine();
		}
		reader.close();
		return result;
	}
	
	public boolean	saveBookmarksToFile(String filename) 
									throws FileNotFoundException,
											IOException {
		File file = new File(filename);
		if(!file.exists() && !file.createNewFile())
			return false;
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for(Entry<String, Bookmark> entry : _bookmarks.entrySet()) {
			writer.write(entry.getValue().serialize());
		}
		writer.close();
		return true;
	}
	
	public int		getBookmarkCount() { return _bookmarks.size(); }
	
	public Enumeration<String>	getBookmarkNames() { return _bookmarks.keys(); }
	
	public Bookmark	getBookmark(String name) { return _bookmarks.get(name); }
	
	public void		removeBookmark(String name) {
		if(!_bookmarks.contains(name))
			return;
		_bookmarks.remove(name);
		bookmarkDeletedEvent(name);
	}
	
	public boolean	addBookmark(Bookmark bookmark) {
		if(bookmark == null)
			return false;
		if(!_bookmarks.containsKey(bookmark.getName()))
			bookmarkAddedEvent(bookmark);
		_bookmarks.put(bookmark.getName(), bookmark);
		return true;
	}
}
