package rapidreader.core;

public class Bookmark {
	private static final String	startMarker = "BOOKMARK: ";
	private static final String	pathMarker = "FILE: ";
	private static final String	sectionMarker = "SECTION: ";
	private static final String	posMarker = "POS: ";
	
	private String	_bookmarkName;
	private String	_fileName;
	private int		_sectionNumber;
	private int		_sectionPosition;
	
	private static String	simplify(String string) {
		int startIndex = 0;
		int stopIndex = string.length() - 1;
		while(startIndex < string.length() && 
				(string.charAt(startIndex) == ' ' || string.charAt(startIndex) == '\n'))
			startIndex++;
		
		while(stopIndex > -1 && 
				(string.charAt(startIndex) == ' ' || string.charAt(startIndex) == '\n'))
			stopIndex--;
		
		return string.substring(startIndex, stopIndex);
	}
	
	public String	getName() { return _bookmarkName; }
	public String	getFilename() { return _fileName; }
	public int		getSectionNumber() { return _sectionNumber; }
	public int		getPosition() { return _sectionPosition; }
	
	public Bookmark(String name, String filename, int sectionNumber, int position) {
		_bookmarkName = name;
		_fileName = filename;
		_sectionNumber = sectionNumber;
		_sectionPosition = position;
	}
	
	public String	serialize() {
		StringBuilder builder = new StringBuilder();
		builder.append(startMarker);
		builder.append(_bookmarkName + " ");
		builder.append(pathMarker);
		builder.append(_fileName + " ");
		builder.append(sectionMarker);
		builder.append(_sectionNumber + " ");
		builder.append(posMarker);
		builder.append(_sectionPosition + " ");
		builder.append('\n');
		return builder.toString();
	}
	
	public static Bookmark	deserialize(String string) {
		int startMarkerIndex = string.indexOf(startMarker);
		int pathMarkerIndex = string.indexOf(pathMarker);
		int sectionMarkerIndex = string.indexOf(sectionMarker);
		int posMarkerIndex = string.indexOf(posMarker);
		if(startMarkerIndex < 0 || 
				pathMarkerIndex < 0 ||
				sectionMarkerIndex < 0 ||
				posMarkerIndex < 0)
			return null;
		
		startMarkerIndex += startMarker.length();
		String bookmarkName = simplify(string.substring(startMarkerIndex, pathMarkerIndex));
		pathMarkerIndex += pathMarker.length();
		String fileName = simplify(string.substring(pathMarkerIndex, sectionMarkerIndex));
		sectionMarkerIndex += sectionMarker.length();
		String sectionNumber = simplify(string.substring(sectionMarkerIndex, posMarkerIndex));
		posMarkerIndex += posMarker.length();
		String position = simplify(string.substring(posMarkerIndex));
		return new Bookmark(bookmarkName, fileName, Integer.parseInt(sectionNumber), Integer.parseInt(position));
	}
}
