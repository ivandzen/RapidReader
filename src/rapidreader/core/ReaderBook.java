package rapidreader.core;
import java.util.ArrayList;


public class ReaderBook {
	public ReaderBook(String filename, ArrayList<String> authors, String genre, String title, BookSection data) {
		_filename = filename;
		_authors = authors;
		_genre = genre;
		_title = title;
		_data = data;
	}
	
	public String				getFilename() { return _filename; }
	public int					getAuthorsCount() { return _authors.size(); }
	public String				getAuthor(int index) { return _authors.get(index); }
	public String				getGenre() { return _genre; }
	public String				getTitle() { return _title; }
	public BookSection			getData() { return _data; }
	public BookSection			getSection(int sectionNumber) {
		if(_data != null)
			return _data.getSection(sectionNumber);
		return null;
	}
	
	private String				_filename;
	private	ArrayList<String>	_authors;
	private	String				_genre;
	private String				_title;
	private BookSection			_data;
}
