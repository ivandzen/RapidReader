package rapidreader.core;
import java.io.File;
import java.util.Hashtable;


public class BookManager {
	private Hashtable<String, TextFileParser>	_parsers = new Hashtable<String, TextFileParser>();
	private Hashtable<String, ReaderBook>		_books = new Hashtable<String, ReaderBook>();
	
	public	ReaderBook			openBook(File file) {
		if(_books.contains(file.getAbsolutePath()))
			return _books.get(file.getAbsolutePath());
		String[] fileNameSplitted = file.getName().split("\\.(?=[^\\.]+$)");
		
		if(fileNameSplitted.length == 2){
			if(!_parsers.containsKey(fileNameSplitted[fileNameSplitted.length - 1])) {
				System.out.println("No such parser");
				return null;
			}				
			TextFileParser parser = _parsers.get(fileNameSplitted[fileNameSplitted.length - 1]);
			ReaderBook book = parser.parseFile(file);
			if(book != null)
				_books.put(file.getAbsolutePath(), book);
			return book;
		}
		return null;
	}
	
	public boolean		addTextFileParser(TextFileParser parser) {
		if(parser == null)
			return false;
		_parsers.put(parser.fileExtension(), parser);
		return true;
	}
	
	public int			getBooksCount() { return _books.size(); }
	public ReaderBook	getBook(int index) { return _books.get(index); }
	public ReaderBook	getBook(String filename) { return openBook(new File(filename)); }
}
