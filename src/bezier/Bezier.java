package bezier;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Bezier extends JComponent{
	
	private static final int BASE_DOTS_COUNT = 4;
	private BezierCanvas canvas_;
	Vector<BezierPoint> bezier_point_vec_;
	
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
		StartButton start_button = new StartButton();
		start_button.setVisible(true);
		JPanel button_panel = new JPanel();
		button_panel.setVisible(true);
		button_panel.setLayout(new GridLayout(1, 1));
		button_panel.add(start_button);
		
		canvas_ = new BezierCanvas();
		setLayout(new BorderLayout());
		add(canvas_, BorderLayout.CENTER);
		add(button_panel, BorderLayout.PAGE_END);
		setVisible(true);
		
		bezier_point_vec_ = new Vector<BezierPoint>();
	}
	
	void addBezierPoint(BezierPoint p) {
		if(bezier_point_vec_.size() < BASE_DOTS_COUNT) {
			bezier_point_vec_.add(p);
			BufferedImage buf = new BufferedImage(400, 400, BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D buf_g = buf.createGraphics();
			buf_g.setBackground(getBackground());
			buf_g.clearRect(0, 0, 400, 400);

			for(BezierPoint bp : bezier_point_vec_) {
				bp.drawPoint(buf_g);
			}
			Graphics g = getGraphics();
			g.drawImage(buf,  0,  0,  this);

		}
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
					Bezier.this.addBezierPoint(new BezierPoint(e.getPoint()));
				}
			});
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		}
	}
	
	class StartButton extends JButton{		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public StartButton() {
			super("Start");
			setForeground(Color.GRAY);
		}		
	}
	
	class BezierPoint extends Point{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public int radius_ = 10;
		
		public BezierPoint(Point p) {
			super(p);
			radius_ = 10;
		}
		
		public void drawPoint(Graphics2D g) {
			g.setColor(Color.BLACK);
			int radius = radius_;
			int diameter = radius_ * 2;
			g.fillOval((int)getX() - radius, (int)getY() - radius, diameter, diameter);
		}
	}
}
