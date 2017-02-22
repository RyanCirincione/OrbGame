import java.util.HashMap;

public class Control
{
	/**
	 * A HashMap of strings to ints, representing key inputs
	 * Standard for values in the keys:
	 *  * 0: The key is not pressed
	 *  * >=1: The key is pressed, and has been for [value] frames (up to Integer.MAX_VALUE)
	 *  * -1: The key is not pressed, but was last frame
	 */
	
	HashMap<String, Integer> map;
	public Vector mouse;
	
	public Control()
	{
		map = new HashMap<String, Integer>();
		mouse = new Vector();
	}
	
	public void tick()
	{
		for(String s : map.keySet())
			if(map.get(s) != 0 && map.get(s) < Integer.MAX_VALUE)
				map.put(s, map.get(s)+1);
	}
	
	public void press(String s)
	{
		safe(s);
		if(map.get(s) < 1)
			map.put(s, 1);
	}
	
	public void release(String s)
	{
		map.put(s, -1);
	}
	
	public int isPressedTime(String s)
	{
		safe(s);
		return map.get(s);
	}
	
	public boolean isPressed(String s)
	{
		safe(s);
		return map.get(s) > 0;
	}
	
	public boolean isJustReleased(String s)
	{
		safe(s);
		return map.get(s) < 0;
	}
	
	private void safe(String s)
	{
		if(map.get(s) == null)
			map.put(s, 0);
	}
}
