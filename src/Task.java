import java.util.ArrayList;

public class Task
{
	public static final int UNKNOWN = 0, CARRY = 1, HARVEST = 2, FIGHT = 3;
	public Vector location;
	public ArrayList<Object> data;
	public int type;
	
	public Task(Vector loc)
	{
		type = UNKNOWN;
		location = loc.clone();
		data = new ArrayList<Object>();
	}
	
	public Task(Vector loc, int type, Object... dat)
	{
		this(loc);
		for(int i = 0; i < dat.length; i++)
			data.add(dat[i]);
		this.type = type;
	}
}
