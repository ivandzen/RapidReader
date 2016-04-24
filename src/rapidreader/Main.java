package rapidreader;
import rapidreader.gui.ReaderMainFrame;


public class Main {
	public static void main(String[] args)
	{
		//Bookmark.fromString("BOOKMARK: bookmark1 FILE: /s/df/s/d/fsdf sdfsdfs/sdfs.fb2 SECTION: somesome POS: 12314");
		System.setProperty("file.encoding", "UTF-8");
		System.setProperty("http.proxyHost", "127.0.0.1");
		System.setProperty("http.proxyPort", "3128");
		ReaderMainFrame mainFrame = new ReaderMainFrame();
		mainFrame.setVisible(true);
	}
}