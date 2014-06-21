package visual;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.*;


/* Basic Swing example. */
public class SwingWindow
{
	private JFrame frame;
	
	private static final int WIDTH = 1400;
	private static final int HEIGHT = 900;
	private ArrayList<Chart> charts;
	
	public class MyJPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g)
		{
			super.paint(g);
			
			g.setColor(Color.BLUE);
			
			for ( Chart chart : charts )
			{
				g.setColor(chart.getColor());
				g.drawPolyline(chart.getXWindowPoints(), chart.getYPoints(), chart.getPoints());
			}
			
			g.dispose();
		}
	}
	
	public SwingWindow(ArrayList<Chart> charts)
	{
		// Initialize variables
		this.charts = charts;
		
		// Create a Swing Window
		frame = new JFrame("Data");
		
		//Add Components
		MyJPanel myJPanel = new MyJPanel();
		//myJPanel.setBackground(Color.WHITE);
		frame.add(myJPanel);

		// "Pack" the window, making it "just big enough".
		 //f.pack();
		frame.setSize(WIDTH, HEIGHT);

		// Set the default close operation for the window
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Make the window visible
		frame.setVisible(true);
	}
	
	public void repaintPanel()
	{
		frame.repaint();
	}
	
	public int getHeight()
	{
		return frame.getHeight();
	}
	
	public int getWidth()
	{
		return frame.getWidth();
	}

	public static void main(String[] args)
	{
		new SwingWindow(null);
	}
}
