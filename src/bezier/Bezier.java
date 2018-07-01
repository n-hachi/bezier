package bezier;

import java.awt.BorderLayout;
import java.awt.Dimension;

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
		}
	}
}
