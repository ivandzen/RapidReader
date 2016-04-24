package rapidreader.core;


abstract public class AbstractTextPlayer {
	abstract public void		play();
	abstract public void		pause();
	abstract public boolean		isRunning();
	abstract public void		rewind();
	abstract public void		forward();
	abstract public void		setReadingSpeed(int value); 
	abstract public int			getReadingSpeed();
	abstract public boolean		setSection(BookSection section);
	abstract public int			getSectionLength();
	abstract public boolean		setCurrentPosition(int pos);
	abstract public int			getCurrentPosition();
	abstract public void		addPlayerListener(TextPlayerListener listener);
	abstract public void		setTextField(AbstractTextField textField);
	abstract public void		setParagraphViewer(AbstractParagraphViewer paragraphViewer);
	abstract public boolean		nextSection();
	abstract public boolean		previousSection();
	abstract public SectionNode	getCurrentNode();
	abstract public String		getSectionTitle();
	abstract public int			getSectionNumber();
	
	public interface TextPlayerListener {
		public void	playerStarted();
		public void	playerStoped();
		public void	sectionFinished();
		public void	positionChanged(int position);
		public void	currentSectionChanged(BookSection section);
	}
}
