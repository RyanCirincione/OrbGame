import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class EntityHandler
{
	ArrayList<Entity> entities;
	
	public EntityHandler(int chunkSize)
	{
		entities = new ArrayList<Entity>();
	}
	
	public Entity get(int i)
	{
		return entities.get(i);
	}
	
	public int size()
	{
		return entities.size();
	}
	
	public void add(Entity entity)
	{
		entities.add(entity);
	}
	
	public void remove(Entity entity)
	{
		entities.remove(entity);
	}
	
	public void tick()
	{
		for(int i = 0; i < entities.size(); i++)
			entities.get(i).tick();
		
		for(int i = 0; i < entities.size()-1; i++)
			for(int j = i+1; j < entities.size(); j++)
				entities.get(i).checkCollide(entities.get(j));
	}
	
	public void cleanUp()
	{
		for(int i = 0; i < entities.size(); i++)
			if(entities.get(i).remove)
			{
				Game.eventQueue.addEvent(new Event("entity-remove", 1, entities.get(i)));
				Game.selectedEntities.remove(entities.get(i));
				entities.remove(i);
				i--;
			}
	}
	
	public void paint(Graphics2D g)
	{
		for(int i = 0; i < entities.size(); i++)
			entities.get(i).paint(g);
		
		if(Game.selectedEntities.size() != 0)
			for(int i = 0; i < entities.size(); i++)
				if(Game.selectedEntities.contains(entities.get(i)))
				{
					g.setColor(Color.cyan);
					g.drawRect((int)(entities.get(i).location.x-1 + Game.S_WIDTH/2 - Game.cameraLoc.x - entities.get(i).dimension.x/2),
							(int)(entities.get(i).location.y-1 + Game.S_HEIGHT/2 - Game.cameraLoc.y - entities.get(i).dimension.y/2),
							(int)(entities.get(i).dimension.x+1), (int)(entities.get(i).dimension.y+1));
				}
	}
}
