
public class Calc
{
	public static double distance(Vector loc1, Vector loc2)
	{
		return Math.sqrt(Math.pow(loc1.x - loc2.x, 2) + Math.pow(loc1.y - loc2.y, 2));
	}
	
	public static boolean pointInRect(Vector point, Vector loc1, Vector loc2)
	{
		if(loc1.x > loc2.x)
		{
			double t = loc2.x;
			loc2.x = loc1.x;
			loc1.x = t;
		}
		if(loc1.y > loc2.y)
		{
			double t = loc2.y;
			loc2.y = loc1.y;
			loc1.y = t;
		}
		
		return point.x > loc1.x && point.x < loc2.x && point.y > loc1.y && point.y < loc2.y;
	}
	
	public static boolean rectOverlapsRect(Vector loc1, Vector loc2, Vector loc3, Vector loc4)
	{
		if(loc1.x > loc2.x)
		{
			double t = loc2.x;
			loc2.x = loc1.x;
			loc1.x = t;
		}
		if(loc1.y > loc2.y)
		{
			double t = loc2.y;
			loc2.y = loc1.y;
			loc1.y = t;
		}
		if(loc3.x > loc4.x)
		{
			double t = loc4.x;
			loc4.x = loc3.x;
			loc3.x = t;
		}
		if(loc3.y > loc4.y)
		{
			double t = loc4.y;
			loc4.y = loc3.y;
			loc3.y = t;
		}
		
		return loc1.x < loc4.x && loc2.x > loc3.x && loc1.y < loc4.y && loc2.y > loc3.y;
	}
}
