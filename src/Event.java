import java.util.ArrayList;

public class Event
{
	String name;
	int life;
	ArrayList<Object> data;
	
	public Event (String event, int l, Object... data)
	{
		name = event;
		life = l;
		this.data = new ArrayList<Object>();
		for(int i = 0; i < data.length; i++)
			this.data.add(data[i]);
	}

	public String getName()
	{
		return name;
	}

	public int getLife()
	{
		return life;
	}

	public ArrayList<Object> getData()
	{
		return data;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setLife(int life)
	{
		this.life = life;
	}

	public void setData(ArrayList<Object> data)
	{
		this.data = data;
	}
	
	public String toString ()
	{
		return name + (data.isEmpty() ? "" : ": ") + data.toString().substring(1, data.toString().length()-1);
	}
	
	public boolean equals (Event e)
	{
		return name.equals(e.getName());
	}
}
