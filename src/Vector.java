import java.text.DecimalFormat;

public class Vector implements java.io.Serializable
{
	private static final long serialVersionUID = -3016580506510438526L;
	public double x, y;
	
	public Vector()
	{
		this(0,0);
	}
	
	public Vector(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void doDelta(Vector pair)
	{
		x += pair.x;
		y += pair.y;
	}
	
	public void doDeltaTo(Vector target, Vector delta)
	{
		if(x < target.x)
			if(x + delta.x > target.x)
				x = target.x;
			else
				x += delta.x;
		else if(x > target.x)
			if(x - delta.x < target.x)
				x = target.x;
			else
				x -= delta.x;

		if(y < target.y)
			if(y + delta.y > target.y)
				y = target.y;
			else
				y += delta.y;
		else if(y > target.y)
			if(y - delta.y < target.y)
				y = target.y;
			else
				y -= delta.y;
	}
	
	public double distance(Vector v)
	{
		return Math.sqrt(Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2));
	}
	
	public double distance2(Vector v)
	{
		return Math.pow(x - v.x, 2) + Math.pow(y - v.y, 2);
	}
	
	public Vector toPositive()
	{
		return new Vector(Math.abs(x), Math.abs(y));
	}
	
	public boolean equals(Vector pair)
	{
		return x == pair.x && y == pair.y;
	}
	
	public Vector clone()
	{
		return new Vector(x, y);
	}
	
	public String toString()
	{
		DecimalFormat f = new DecimalFormat("0.###");
		return "<" + f.format(x) + ", " + f.format(y) + ">";
	}
}
