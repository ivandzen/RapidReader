package rapidreader.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import rapidreader.core.NavigatorListener;

public class TextNavigator extends JPanel implements ComponentListener, MouseListener, MouseMotionListener{
	
	private final double radian = 57.295779513;
	
	private enum PressedPart {
		None,
		Central,
		North,
		South,
		East,
		West,
		ScrollRing
	}

	private static final long serialVersionUID = 1L;
	private int navigator_radius;
	private int	central_button_radius;
	private int	x_center;
	private int	y_center;
	private Color	inactiveColor = Color.WHITE;
	private Color	border_color = Color.DARK_GRAY;
	private Color 	active_color = new Color(220, 220, 240);
	private PressedPart	pressed_part = PressedPart.None;
	private ArrayList<NavigatorListener>_navigatorListeners = new ArrayList<NavigatorListener>();
	private double	last_angle;
	
	private void	sendCentralButtonPressed() {
		for(int i = 0; i < _navigatorListeners.size(); i++)
			_navigatorListeners.get(i).centralButtonPressed();
	}
	
	private void	sendNorthButtonPressed() {
		for(int i = 0; i < _navigatorListeners.size(); i++)
			_navigatorListeners.get(i).northButtonPressed();
	}
	
	private void	sendSouthButtonPressed() {
		for(int i = 0; i < _navigatorListeners.size(); i++)
			_navigatorListeners.get(i).southButtonPressed();
	}
	
	private void	sendEastButtonPressed() {
		for(int i = 0; i < _navigatorListeners.size(); i++)
			_navigatorListeners.get(i).eastButtonPressed();
	}
	
	private void	sendWestButtonPressed() {
		for(int i = 0; i < _navigatorListeners.size(); i++)
			_navigatorListeners.get(i).westButtonPressed();
	}
	
	private void	sendRingScrolled(double angle) {
		for(int i = 0; i < _navigatorListeners.size(); i++)
			_navigatorListeners.get(i).ringScrolled(angle);
	}
	
	public TextNavigator() {
		super();
		setPreferredSize(new Dimension(200, 200));
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		componentResized(null);
	}
	
	public void	paint(Graphics g) {
		//g.setColor(inactiveColor);
		//g.fillRect(0, 0, getSize().width, getSize().height);
		drawNavigator(g);
	}
	
	private void	drawNavigator(Graphics g) {
		g.setColor(border_color);
		g.drawOval(x_center - navigator_radius, y_center - navigator_radius, navigator_radius * 2, navigator_radius * 2);
		if(pressed_part == PressedPart.ScrollRing ||
				pressed_part == PressedPart.East ||
				pressed_part == PressedPart.West ||
				pressed_part == PressedPart.North ||
				pressed_part == PressedPart.South) {
			g.setColor(active_color);
		}
		else
			g.setColor(inactiveColor);
		g.fillOval(x_center - navigator_radius + 1, y_center - navigator_radius + 1, navigator_radius * 2 - 2, navigator_radius * 2 - 2);
		drawCentralRing(g);
	}
	
	private void	drawCentralRing(Graphics g) {
		g.setColor(border_color);
		g.drawOval(x_center - central_button_radius, y_center - central_button_radius, central_button_radius * 2, central_button_radius * 2);
		if(pressed_part == PressedPart.Central)
			g.setColor(active_color);
		else
			g.setColor(inactiveColor);
		g.fillOval(x_center - central_button_radius + 1, y_center - central_button_radius + 1, central_button_radius * 2 - 2, central_button_radius * 2 - 2);
	}
	
	private double computeAngle(Point radius) {
		if(radius.x == 0) {
			if(radius.y > 0)
				return 1.570796327 * radian;
			return -1.570796327 * radian;
		}
		double a = Math.atan((double)radius.y / (double)radius.x) * radian;
		
		if(radius.x > 0)
			return a;
		
		if(a < 0) return 180 + a;
		return -180 + a;
	}
	
	private PressedPart	getPressedPart(Point p) {
		Point radius = new Point(p.x - x_center, y_center - p.y);
		if(Math.pow(radius.x, 2.0) + Math.pow(radius.y, 2.0) > Math.pow(navigator_radius, 2))
			return PressedPart.None;
		if(Math.pow(radius.x, 2.0) + Math.pow(radius.y, 2.0) < Math.pow(central_button_radius, 2))
			return PressedPart.Central;
		
		last_angle = computeAngle(radius);

		return PressedPart.ScrollRing;
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		Dimension size = getSize();
		int minimal_size = size.width < size.height ? size.width : size.height;
		navigator_radius = (int)((minimal_size - 20) / 2);
		central_button_radius = (int)(navigator_radius / 2.5);
		x_center = (int)(size.width / 2);
		y_center = (int)(size.height / 2);
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		pressed_part = getPressedPart(arg0.getPoint());
		updateUI();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		pressed_part = PressedPart.None;
		updateUI();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		pressed_part = getPressedPart(arg0.getPoint());
		if(pressed_part == PressedPart.ScrollRing) {
			Point radius = new Point(arg0.getPoint().x - x_center, y_center - arg0.getPoint().y);
			double angle = computeAngle(radius);
			if(angle > 90 && last_angle < -90 || last_angle > 90 && angle < -90)
				sendRingScrolled(360 - angle - last_angle);
			sendRingScrolled(last_angle - angle);
			last_angle = angle;
		}
		updateUI();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}
	
	public boolean	addNavigatorListener(NavigatorListener listener) {
		if(listener == null || _navigatorListeners.contains(listener))
			return false;
		_navigatorListeners.add(listener);
		return true;
	}

}
