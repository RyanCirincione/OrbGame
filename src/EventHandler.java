import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class EventHandler
{
	LinkedList<Event> events;
	
	public EventHandler()
	{
		events = new LinkedList<Event>();
	}
	
	public void addEvent (Event e)
	{
		if(!this.hasEvent(e))
			events.add(e);
	}
	
	public void removeEvent(String name)
	{
		for(int i = 0; i < events.size(); i++)
			if(events.get(i).getName().equals(name))
			{
				events.remove(i);
				i--;
			}
	}

	public void removeEvent(String name, Object... data)
	{
		Event e = new Event(name, 1);
		for(Object obj : data)
			e.getData().add(obj);
		
		removeEvent(e);
	}
	
	public void removeEvent (Event e)
	{
		Iterator<Event> list = events.iterator();
		while(list.hasNext())
			if(list.next().equals(e))
				list.remove();
	}
	
	public void tick ()
	{
		for(int i = 0; i < events.size(); i++)
		{
			Event e = events.get(i);
			if(e.getLife() > 0)
				e.setLife(e.getLife()-1);
			if(e.getLife() == 0)
			{
				events.remove(i);
				i--;
			}
		}
	}
	
	public boolean hasEvent(String name)
	{
		for(int i = 0; i < events.size(); i++)
			if(events.get(i).getName().equals(name))
				return true;
		return false;
	}
	
	public boolean hasEvent (String str, Object... data)
	{
		if(!this.hasEvent(str))
			return false;
		
		if(data.length != getData(str).size())
			return false;
		
		for(int i = 0; i < data.length; i++)
			if(!data[i].equals(getData(str).get(i)))
				return false;
		
		return true;
	}
	
	public boolean hasEvent(Event e)
	{
		for(int i = 0; i < events.size(); i++)
			if(events.get(i).equals(e))
				return true;
		return false;
	}
	
	public ArrayList<Object> getData(String name)
	{
		Event event = null;
		for(int i = 0; i < events.size() && event == null; i++)
			if(events.get(i).getName().equals(name))
				event = events.get(i);
		
		return event.getData();
	}
	
	public void transferTo(EventHandler target)
	{
		while(events.size() > 0)
			target.addEvent(events.remove());
	}
	
	public String toString ()
	{
		String result = "";
		for(Object d : events.toArray())
			result += ((Event) d) + "\n";
		return result;
	}
}
