package rapidreader.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Timer;

import javax.swing.JPanel;

import rapidreader.core.Logger;

public class ADBlock extends JPanel implements ActionListener {

	private final int			adTypePicture = 1;
	private final int			adTypeText = 2;
	private static final long 	serialVersionUID = 1L;
	private String				url = "http://default.website.com";
	private Timer				timer = new Timer(100, this);
	private int					adType = adTypeText;
	private String				adText = "Здесь могла бы быть ваша реклама";
	private int					adTextShift = 0;
	
	public void	setUrl(String urlStr) { url = urlStr; }
	
	public void	setText(String text) { adText = text; }
	
	public ADBlock() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void	mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (IOException e1) {
					Logger.log(e1.getMessage());
				} catch (URISyntaxException e1) {
					Logger.log(e1.getMessage());
				}
			}
		});
		setPreferredSize(new Dimension(40, 40));
		timer.start();
	}

	@Override
	public void	paint(Graphics g) {
		Dimension size = getSize();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, size.width, size.height);
		if(adType == adTypeText) {
			g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 30));
			g.setColor(Color.BLUE);
			g.drawString(adText, adTextShift, 36);
		}
		else if(adType == adTypePicture) {
			
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		adTextShift -= 3;
		if(adTextShift < -1000)
			adTextShift = getSize().width;
		updateUI();
	}
}
