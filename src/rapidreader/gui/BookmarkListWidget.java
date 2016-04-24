package rapidreader.gui;
import javax.swing.DefaultListModel;
import javax.swing.JList;

import rapidreader.core.Bookmark;
import rapidreader.core.BookmarkManager;

public class BookmarkListWidget extends JList implements BookmarkManager.BookmarkManagerListener{

	private static final long serialVersionUID = 1L;
	private BookmarkManager		manager;
	private DefaultListModel	model;
	
	public BookmarkListWidget(BookmarkManager bookmarkMan) {
		manager = bookmarkMan;
		model = (DefaultListModel)getModel();
		manager.addBookmarkManagerListener(this);
	}

	@Override
	public void bookmarkAdded(Bookmark bookmark) {
		model.add(model.getSize(), bookmark);
	}

	@Override
	public void bookmarkDeleted(String name) {
		for(int i = 0; i < model.getSize(); i++) {
			Bookmark current = (Bookmark)model.get(i);
			if(current.getName() == name) {
				model.remove(i);
				return;
			}
		}
	}
	
	
}
