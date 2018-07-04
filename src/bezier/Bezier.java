package bezier;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Bezier extends JComponent{
	
	private static final int BASE_DOTS_COUNT = 4;
	private BezierCanvas canvas_;
	Vector<BezierPoint> bezier_point_vec_;
	Vector<BezierPoint> final_point_vec_;
	private int turn_ = 0;
	private Timer timer_;
	
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
		final_point_vec_ = new Vector<BezierPoint>();
	}
	
	void drawBaseFigure() {
		for(BezierPoint bp : bezier_point_vec_) {
			canvas_.drawPoint(bp);
		}
		
		if (bezier_point_vec_.size() >= 2) {
			ListIterator<BezierPoint> iter1 = bezier_point_vec_.listIterator(0);
			ListIterator<BezierPoint> iter2 = bezier_point_vec_.listIterator(1);
			while(iter2.hasNext()) {
				canvas_.drawLine(iter1.next(), iter2.next());
			}	
		}		
	}
	
	void addBezierPoint(BezierPoint p) {
		if(bezier_point_vec_.size() > BASE_DOTS_COUNT) {
			return;
		}

		bezier_point_vec_.add(p);
		drawBaseFigure();
		canvas_.repaint();
	}
	
	void startAnimation() {
		turn_ = 0;
		timer_ = new Timer();
		timer_.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				update();
			}
		}, 0, 50);
	}
	
	void update() {
		drawBaseFigure();
		
		if(turn_ == 0) {
			turn_++;
			return;
		}
		
		if(turn_ >= 100) {
			timer_.cancel();
		}
		
		Vector<BezierPoint> point_vec = bezier_point_vec_;
		while(point_vec.size() > 1) {
			Vector<BezierPoint> temp_vec = new Vector<BezierPoint>();
			ListIterator<BezierPoint> iter1 = point_vec.listIterator(0);
			ListIterator<BezierPoint> iter2 = point_vec.listIterator(1);
			while(iter2.hasNext()) {
				BezierPoint child_bp = genChildPoint(iter1.next(), iter2.next(), turn_, 100);
				canvas_.drawPoint(child_bp);
				temp_vec.addElement(child_bp);
			}
			point_vec = temp_vec;
			if(temp_vec.size() == 1) {
				final_point_vec_.addElement(temp_vec.elementAt(0));
			}
		}
		
		for(BezierPoint bp : final_point_vec_) {
			canvas_.drawPoint(bp);
		}
		
		turn_++;
		
		canvas_.repaint();
	}
			
	class BezierCanvas extends JPanel{
		BufferedImage buf_;
		Graphics2D buf_g_;

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
			setDoubleBuffered(true);
			buf_ = new BufferedImage(400, 400, BufferedImage.TYPE_3BYTE_BGR);
			buf_g_ = buf_.createGraphics();
			buf_g_.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			buf_g_.setBackground(getBackground());
			buf_g_.clearRect(0, 0, 400, 400);
		}
		
		public void drawPoint(BezierPoint bp) {
			buf_g_.setColor(Color.BLACK);
			int diameter = bp.radius_ * 2;
			buf_g_.fillOval(bp.x - bp.radius_, bp.y - bp.radius_, diameter, diameter);
		}
		
		public void drawLine(BezierPoint bp1, BezierPoint bp2) {
			buf_g_.setColor(Color.BLACK);
			buf_g_.drawLine(bp1.x, bp1.y, bp2.x, bp2.y);
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(buf_, 0, 0, this);
			buf_g_.clearRect(0, 0, 400, 400);
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
			
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					Bezier.this.startAnimation();
				}
			});			
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
	
	BezierPoint genChildPoint(BezierPoint bp1, BezierPoint bp2, int turn, int max) {
		int p1 = turn;
		int p2 = max - turn;
		
		int x = (int)((bp1.getX() * p2 + bp2.getX() * p1)/max);
		int y = (int)((bp1.getY() * p2 + bp2.getY() * p1)/max);
		
		return new BezierPoint(new Point(x, y));
	}
}
