
public class Inventory
{
	public Item[][] items;
	public Vector size;
	
	public Inventory(Vector size)
	{
		this.size = size;
		items = new Item[(int)size.x][(int)size.y];
	}
	
	public Item takeItem(Vector loc)
	{
		Item item = items[(int)loc.x][(int)loc.y];
		for(int x = (int)loc.x; x < loc.x + item.size.x; x++)
			for(int y = (int)loc.y; y < loc.y + item.size.y; y++)
				items[x][y] = null;
		
		return item;
	}
	
	public Item takeItem(Item item)
	{
		for(int x = 0; x < items.length; x++)
			for(int y = 0; y < items[0].length; y++)
				if(items[x][y].equals(item))
					return takeItem(new Vector(x, y));
		
		return null;
	}
	
	public boolean contains(Item item)
	{
		for(int x = 0; x < items.length; x++)
			for(int y = 0; y < items[0].length; y++)
				if(items[x][y].equals(item))
					return true;
		
		return false;
	}
	
	public boolean add(Item item, Vector loc)
	{
		boolean flag = true;
		//Check space item needs, and fail if it's occupied
		for(int x = (int)loc.x; x < loc.x + item.size.x && flag; x++)
			for(int y = (int)loc.y; y < loc.y + item.size.y && flag; y++)
				if(items[x][y] != null)
					flag = false;
		//If space is available, occupy it
		if(flag)
			for(int x = (int)loc.x; x < loc.x + item.size.x; x++)
				for(int y = (int)loc.y; y < loc.y + item.size.y; y++)
					items[x][y] = item;
		return flag;
	}
	
	public boolean add(Item item)
	{
		boolean flag = false;
		//Attempt to place item in each potential top left corner until success
		for(int y = 0; y < items[0].length - item.size.y + 1 && !flag; y++)
			for(int x = 0; x < items.length - item.size.x + 1 && !flag; x++)
				if(items[x][y] == null)
					flag = add(item, new Vector(x, y));
		return flag;
	}
	
	public void refreshSize()
	{
		Item[][] temp = new Item[(int)size.x][(int)size.y];
		for(int x = 0; x < items.length; x++)
			for(int y = 0; y < items[0].length; y++)
				temp[x][y] = items[x][y];
		items = temp;
	}
	
	public boolean equals(Inventory inv)
	{
		if(!size.equals(inv.size))
			return false;
		else
			for(int x = 0; x < items.length; x++)
				for(int y = 0; y < items[0].length; y++)
					if(!items[x][y].equals(inv.items[x][y]))
						return false;
		
		return true;
	}
}
