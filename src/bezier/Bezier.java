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
import java.awt.geom.Point2D;
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
	
	private enum State{
		Inadequacy,
		Ready,
		Running,
	}

	private static final int BASE_DOTS_COUNT = 4;
	private static final int RADIUS = 3;
	private static final int FINAL_RADIUS = 5;	
	private static final int HEIGHT = 600;
	private static final int WIDTH = 600;
	private static final int MAX_TURN = 50;
	private static final int PERIOD = 100;
	static Color DISABLE_COLOR = Color.gray;
	static Color ENABLE_COLOR = Color.BLACK;
	
	private StartButton start_button_ = null;
	private ResetButton reset_button_ = null;
	private State state_ = State.Inadequacy;
	private BezierCanvas canvas_;
	Vector<BezierPoint> base_point_vec_;
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

	/**
	 * Constructor
	 */
	public Bezier() {
		start_button_ = new StartButton();
		start_button_.setVisible(true);
		reset_button_ = new ResetButton();
		reset_button_.setVisible(true);
		JPanel button_panel = new JPanel();
		button_panel.setVisible(true);
		button_panel.setLayout(new GridLayout(1, 2));
		button_panel.add(start_button_);
		button_panel.add(reset_button_);
		
		canvas_ = new BezierCanvas(Bezier.WIDTH, Bezier.HEIGHT);
		setLayout(new BorderLayout());
		add(canvas_, BorderLayout.CENTER);
		add(button_panel, BorderLayout.PAGE_END);

		setVisible(true);
		
		base_point_vec_ = new Vector<BezierPoint>();
		final_point_vec_ = new Vector<BezierPoint>();
	}
	
	
	void drawBasePoints() {
		for(BezierPoint bp : base_point_vec_) {
			canvas_.drawPoint(bp);
		}
	}
	
	void drawBaseLines() {
		if (base_point_vec_.size() >= 2) {
			ListIterator<BezierPoint> iter1 = base_point_vec_.listIterator(0);
			ListIterator<BezierPoint> iter2 = base_point_vec_.listIterator(1);
			while(iter2.hasNext()) {
				canvas_.drawLine(iter1.next(), iter2.next(), Color.red);
			}	
		}				
	}
	
	void addBezierPoint(BezierPoint p) {
		if(base_point_vec_.size() >= BASE_DOTS_COUNT) {
			return;
		}

		base_point_vec_.add(p);
		drawBasePoints();
		drawBaseLines();
		
		if(base_point_vec_.size() >= BASE_DOTS_COUNT) {
			changeState(State.Ready);
		}
		
		canvas_.repaint();
	}
	
	void toggleState() {
		// if state is inadequacy, nothing to do
		if(state_ == State.Inadequacy) {
			return;
		}
		
		// if state is Running, start timer
		else if(state_ == State.Running) {
			changeState(State.Ready);
			timer_.cancel();
			return;
		}
		
		// if state_ is Ready(in other words, neither inadequacy anor Running) start timer.
		else {
			if(turn_ >= Bezier.MAX_TURN) {
				turn_ = 0;
				final_point_vec_.clear();
			}
			changeState(State.Running);
			timer_ = new Timer();
			timer_.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					update();
				}
			}, 0, Bezier.PERIOD);
		}
	}
	
	void update() {
		drawBasePoints();
		
		// while update
		Vector<BezierPoint> parent_point_vec = base_point_vec_;
		while(parent_point_vec.size() > 1) {
			Vector<BezierPoint> child_point_vec = new Vector<BezierPoint>();
			
			ListIterator<BezierPoint> iter1 = parent_point_vec.listIterator(0);
			ListIterator<BezierPoint> iter2 = parent_point_vec.listIterator(1);
			while(iter2.hasNext()) {
				BezierPoint bp1 = iter1.next();
				BezierPoint bp2 = iter2.next();
				
				// generate child point and draw dots
				BezierPoint child_bp = genChildPoint(bp1, bp2, turn_, MAX_TURN, Bezier.RADIUS);
								
				// draw line and point
				// To hide the end of the line, draw line before dots
				canvas_.drawLine(bp1, child_bp, Color.blue);
				canvas_.drawLine(bp2, child_bp, Color.red);
				canvas_.drawPoint(child_bp);
				
				child_point_vec.addElement(child_bp);
			}
			
			// At next loop, generated child points beocme more higher degree points' parent.
			parent_point_vec = child_point_vec;
			// if child_point_vec.size equals 1, this point is the highest degree point.
			if(child_point_vec.size() == 1) {
				BezierPoint bp = child_point_vec.firstElement();
				final_point_vec_.addElement(bp);
			}
		}
		
		// draw all highest_degree points already generated.
		for(BezierPoint bp : final_point_vec_) {
			canvas_.drawPoint(bp);
		}
		// draw latest point with more large size.
		canvas_.drawPoint(final_point_vec_.lastElement(), Bezier.FINAL_RADIUS);
		turn_++;
		
		// When loop max time, stop timer.
		if(turn_ > Bezier.MAX_TURN) {
			changeState(State.Ready);
			timer_.cancel();
		}
		
		canvas_.repaint();
	}
	
	void reset() {
		if(timer_ != null) {
			timer_.cancel();
			timer_ = null;
		}
		changeState(State.Inadequacy);
		base_point_vec_.clear();
		final_point_vec_.clear();
		turn_ = 0;
		canvas_.claer();
		canvas_.repaint();		
	}
	
	void changeState(Bezier.State new_state) {
		state_ = new_state;
		switch(new_state) {
		case Inadequacy:
			start_button_.BeInadequate();
			break;
		case Ready:
			start_button_.BeReady();
			break;
		case Running:
			start_button_.BeRunning();
			break;
		default:
			break;
			
		}
	}

	class BezierCanvas extends JPanel{
		BufferedImage buf_;
		Graphics2D buf_g_;

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public BezierCanvas(int width, int height) {
			setPreferredSize(new Dimension(width, height));
			setVisible(true);
			addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					Point p = e.getPoint();
					Bezier.this.addBezierPoint(new BezierPoint(p.getX(), p.getY(), Bezier.RADIUS));
				}
			});
			setDoubleBuffered(true);
			buf_ = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
			buf_g_ = buf_.createGraphics();
			buf_g_.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			buf_g_.setBackground(getBackground());
			buf_g_.clearRect(0, 0, width, height);
		}
		
		public void drawPoint(BezierPoint bp) {
			buf_g_.setColor(Color.BLACK);
			int diameter = bp.radius_ * 2;
			buf_g_.fillOval(bp.getRoundX() - bp.radius_, bp.getRoundY() - bp.radius_, diameter, diameter);
		}
		
		public void drawPoint(BezierPoint bp, int radius) {
			buf_g_.setColor(Color.BLACK);
			int diameter = radius * 2;
			buf_g_.fillOval(bp.getRoundX() - radius, bp.getRoundY() - radius, diameter, diameter);
		}
		
		public void drawLine(BezierPoint bp1, BezierPoint bp2, Color col) {
			buf_g_.setColor(col);
			buf_g_.drawLine(bp1.getRoundX(), bp1.getRoundY(), bp2.getRoundX(), bp2.getRoundY());
		}
		
		public void claer() {
			buf_g_.clearRect(0, 0, getWidth(), getHeight());			
		}
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(buf_, 0, 0, this);
			buf_g_.clearRect(0, 0, getWidth(), getHeight());
		}
		
	}
	
	class StartButton extends JButton{		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public StartButton() {
			super("Start");
			setForeground(Bezier.DISABLE_COLOR);
			
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					Bezier.this.toggleState();
				}
			});			
		}
		
		void BeInadequate() {
			setForeground(Bezier.DISABLE_COLOR);
			setText("Ready");
		}
		
		void BeReady() {
			setForeground(Bezier.ENABLE_COLOR);
			setText("Ready");
		}
		
		void BeRunning() {
			setForeground(Bezier.ENABLE_COLOR);
			setText("Stop");
		}

	}
	
	class ResetButton extends JButton{		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ResetButton() {
			super("Reset");
			setForeground(Bezier.ENABLE_COLOR);
			
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					Bezier.this.reset();
				}
			});			
		}
	}
	
	class BezierPoint extends Point2D.Double{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public int radius_ = 10;
		
		public BezierPoint(double x, double y, int radius) {
			super(x, y);
			radius_ = radius;
		}
		
		public void drawPoint(Graphics2D g) {
			g.setColor(Color.BLACK);
			int radius = radius_;
			int diameter = radius_ * 2;
			g.fillOval((int)getX() - radius, (int)getY() - radius, diameter, diameter);
		}
		
		public int getRoundX() {
			return (int)Math.round(x);
		}
		
		public int getRoundY() {
			return (int)Math.round(y);
		}
		
		public void resize(int radius) {
			radius_ = radius;
		}
	}
	
	BezierPoint genChildPoint(BezierPoint bp1, BezierPoint bp2, int turn, int max, int radius) {
		int p1 = turn;
		int p2 = max - turn;
		
		double x = ((bp1.getX() * p2 + bp2.getX() * p1)/max);
		double y = ((bp1.getY() * p2 + bp2.getY() * p1)/max);
		
		return new BezierPoint(x, y, radius);
	}
}
