package rapidreader.core;

import java.io.File;

public interface TextFileParser {
	public String		fileExtension();
	public ReaderBook	parseFile(File file);
}
