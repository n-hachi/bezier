package bezier;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Bezier extends JComponent{
	
	private BezierCanvas canvas_;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame frame = new JFrame("bezier");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Bezier bezier = new Bezier();
		frame.add(bezier);
		frame.pack();
		frame.setVisible(true);
	}
	
	public Bezier() {
		canvas_ = new BezierCanvas();
		setLayout(new BorderLayout());
		add(canvas_, BorderLayout.CENTER);
		setVisible(true);
	}
	
	class BezierCanvas extends JPanel{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public BezierCanvas() {
			setPreferredSize(new Dimension(400, 400));
			setVisible(true);
			addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					drawPoint(e.getPoint());
				}
			});
		}
		
		public void drawPoint(Point p) {
			Graphics2D g2 = (Graphics2D)getGraphics();
			g2.setColor(Color.BLACK);
			g2.fillOval((int)p.getX(), (int)p.getY(), 5, 5);
		}
	}
}
