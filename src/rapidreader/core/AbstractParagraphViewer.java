package rapidreader.core;


public interface AbstractParagraphViewer {
	public void		setCurrentNode(TextNode node);
	public void		setActiveWord(int index);
	public void		setFontHeight(int fontHeight);
	public void		addParagraphViewerListener(ParagraphViewerListener listener);
	
	public interface ParagraphViewerListener {
		public void	wordSelected(int paragraphIndex);
	}
}
