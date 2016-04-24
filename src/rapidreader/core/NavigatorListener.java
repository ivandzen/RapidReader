package rapidreader.core;

public interface NavigatorListener {
	public void	centralButtonPressed();
	public void	ringScrolled(double angle);
	public void	northButtonPressed();
	public void	southButtonPressed();
	public void	eastButtonPressed();
	public void	westButtonPressed();
}
