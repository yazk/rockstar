package visual;
import java.awt.Color;

public class Chart
{
	private int[] xPoints;
	private int[] yPoints;
	private int[] xWindowPoints;
	private int points;
	private int width;
	private int height;
	private Color color;
	
	public Chart(int width, int height, Color color)
	{
		this.xPoints = new int[0];
		this.yPoints = new int[0];
		this.xWindowPoints = new int[0];
		this.points = 0;
		
		this.width = width;
		this.height = height;
		
		this.color = color;
	}
	
	public void setYPoints(int[] yPoints)
	{
		this.yPoints = yPoints;
	}
	
	public void setPoints(int points)
	{
		if ( this.points != points )
		{
			this.xWindowPoints = new int[points];
			
			for ( int i = 0; i < points; ++i )
			{
				this.xWindowPoints[i] = (int) (i * ((float)width / points));
			}
		}
		
		this.points = points;
	}
	
	public void setXPoints(int[] xPoints)
	{
		this.xPoints = xPoints;
	}
	
	public int[] getXWindowPoints()
	{
		return xWindowPoints;
	}
	
	public int[] getXPoints()
	{
		return xPoints;
	}
	
	public int[] getYPoints()
	{
		return yPoints;
	}
	
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getPoints()
	{
		return this.points;
	}

	public Color getColor()
	{
		return color;
	}
}
