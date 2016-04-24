package rapidreader.gui;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import rapidreader.core.Bookmark;
import rapidreader.core.BookmarkManager;


public class BookmarksMenu extends JMenu implements BookmarkManager.BookmarkManagerListener{

	private static final long 		serialVersionUID = 1L;
	private JMenuItem				addBookmarkItem;
	private JMenuItem				executeBookmarkListItem;
	private JMenu					bookmarksSubmenu = new JMenu("Last bookmarks");
	private ArrayList<JMenuItem>	bookmarkItems = new ArrayList<JMenuItem>();
	private ArrayList<BookmarksMenuListener>	listeners = new ArrayList<BookmarksMenuListener>();
	
	public void		addBookmarksMenuListener(BookmarksMenuListener listener) {
		if(listener == null || listeners.contains(listener))
			return;
		listeners.add(listener);
	}
	
	private void	executeAddBookmarkDialogEvent() {
		for(int i = 0; i < listeners.size(); i++)
			listeners.get(i).executeAddBookmarkDialog();
	}
	
	private void	executeBookmarkListDialogEvent() {
		for(int i = 0; i < listeners.size(); i++)
			listeners.get(i).executeBookmarkListDialog();
	}
	
	private void	setCurrentBookmarkEvent(Bookmark bookmark) {
		for(int i = 0; i < listeners.size(); i++)
			listeners.get(i).setCurrentBookmark(bookmark);
	}
	
	public interface BookmarksMenuListener {
		public void	executeAddBookmarkDialog();
		public void	executeBookmarkListDialog();
		public void	setCurrentBookmark(Bookmark bookmark);
	}
	
	private class AddBookmarkAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		
		public AddBookmarkAction() {
			super("Add bookmark");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			executeAddBookmarkDialogEvent();
		}
		
	}
	
	private class ExecuteBookmarkListAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		
		public ExecuteBookmarkListAction() {
			super("Edit bookmarks");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			executeBookmarkListDialogEvent();
		}
		
	}
	
	private class BookmarkItemAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		private Bookmark	bookmark;
		
		public BookmarkItemAction(Bookmark mark) {
			super(mark.getName());
			bookmark = mark;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			setCurrentBookmarkEvent(bookmark);
		}
		
	}
	
	private void	updateLastBookmarksMenu() {
		bookmarksSubmenu.removeAll();
		for(int i = 0; i < bookmarkItems.size(); i++)
			bookmarksSubmenu.add(bookmarkItems.get(i));
	}
	
	public BookmarksMenu() {
		super("Bookmarks");
		addBookmarkItem = new JMenuItem(new AddBookmarkAction());
		executeBookmarkListItem = new JMenuItem(new ExecuteBookmarkListAction());
		bookmarksSubmenu = new JMenu("Last bookmarks");
		updateLastBookmarksMenu();
		
		add(addBookmarkItem);
		add(executeBookmarkListItem);
		addSeparator();
		add(bookmarksSubmenu);
	}

	@Override
	public void bookmarkAdded(Bookmark bookmark) {
		bookmarkItems.add(new JMenuItem(new BookmarkItemAction(bookmark)));
		updateLastBookmarksMenu();
	}

	@Override
	public void bookmarkDeleted(String name) {
		for(int i = 0; i < bookmarkItems.size(); i++)
			if(bookmarkItems.get(i).getText() == name) {
				bookmarkItems.remove(i);
				updateLastBookmarksMenu();
				return;
			}
	}

}
