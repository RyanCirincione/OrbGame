import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Orb extends Entity
{
	public enum Type {
		WORKER("res/worker-orb-tint.png"),
		FIGHTER("res/fighter-orb-tint.png"),
		MINER("res/miner-orb-tint.png"),
		HARVESTER("res/harvester-orb-tint.png");
		
		public BufferedImage tint;
		
		Type(String tintPath)
		{
			try
			{
				tint = ImageIO.read(new File(tintPath));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public enum Action {
		IDLE, FOLLOWING, TRAVELING, FIGHTING, CARRYING, MINING, HARVESTING
	}
	
	static final int FOLLOWING_RANGE = 150, TRAVELING_RANGE = 50;
	public int cooldown;
	public Type type;
	public Action action;
	public Object target;
	
	public Orb(String name, Vector location, Vector velocity, int health, int speed, Type type)
	{
		this(name, location, new Vector(32, 32), velocity, health, speed, 16, 0.33, type);
	}
	
	public Orb(String name, Vector location, Vector dimension, Vector velocity, int health, int speed, int volume, double mass, Type type)
	{
		super(name, location, dimension, velocity, health, speed, volume, mass);
		ArrayList<BufferedImage> tempList = new ArrayList<BufferedImage>();
		try
		{
			tempList.add(ImageIO.read(new File("res/blank-orb.png")));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		images.add(tempList);
		this.type = type;
		action = Action.IDLE;
		target = null;
		cooldown = 0;
		flags.add(Flag.ORB);
	}
	
	public void tick()
	{
		super.tick();
		
		if(cooldown > 0)
			cooldown--;
		
		if(Game.eventHandler.hasEvent("key-down_ ") && Calc.distance(location,
				new Vector(Game.mouseLoc.x - Game.S_WIDTH/2 + Game.cameraLoc.x,
				Game.mouseLoc.y - Game.S_HEIGHT/2 + Game.cameraLoc.y)) <= Game.WHISTLE_RANGE)
			action = Action.FOLLOWING;
		
		if(target instanceof Entity && ((Entity)target).remove)
			action = Action.IDLE;
	}
	
	public void doControl()
	{
		super.doControl();
		
		switch (action)
		{
			case IDLE:
				target = null;
			case FOLLOWING:
				if(Calc.distance(location, Game.player.location) > FOLLOWING_RANGE)
					target = Game.player.location.clone();
				else
					target = null;
				
				if(Game.eventHandler.hasEvent("entity-click") && Game.eventHandler.getData("entity-click").get(0)
						instanceof Harvestable && Game.eventHandler.getData("entity-click").get(1).equals(3))
				{
					target = Game.eventHandler.getData("entity-click").get(0);
					action = Action.HARVESTING;
				}
				break;
			case TRAVELING:
				if(Calc.distance(location, (Vector) target) <= TRAVELING_RANGE)
				{
					target = null;
					action = Action.IDLE;
				}
				break;
			case FIGHTING:
				if(Calc.distance(location, ((Entity)target).location) <= volume + ((Entity)target).volume && cooldown == 0)
				{
					cooldown = 30;
					((Entity)target).health -= type == Type.FIGHTER ? 5 : 2;
				}
				break;
			case HARVESTING:
				//TODO Make harvest range a const
				if(Calc.distance(location, ((Entity)target).location) < volume + ((Entity)target).volume + 5)
					((Harvestable)target).harvest(type == Type.HARVESTER ? 1 : 0);
				break;
			default:
				break;
		}
		
		if(Game.eventHandler.hasEvent("mouse-click_1") && Game.selectedEntities.contains(this) &&
				!Game.eventHandler.hasEvent("panel-click"))
		{
			action = Action.TRAVELING;
			Vector click = ((Vector) Game.eventHandler.getData("mouse-click_1").get(0)).clone();
			target = new Vector(click.x - Game.S_WIDTH/2 + Game.cameraLoc.x, click.y - Game.S_HEIGHT/2 + Game.cameraLoc.y);
		}

		
		switch(action)
		{
			case FOLLOWING:
			case TRAVELING:
				if(target != null)
					control = new Vector(((Vector)target).x - location.x, ((Vector)target).y - location.y);
				break;
			case FIGHTING:
				if(cooldown == 0)
					control = new Vector(((Entity)target).location.x - location.x, ((Entity)target).location.y - location.y);
				else if(Calc.distance(location, ((Entity)target).location) < volume + ((Entity)target).volume * 2)
					control = new Vector(location.x - ((Entity)target).location.x, location.y - ((Entity)target).location.y);
				else if(Calc.distance(location, ((Entity)target).location) > volume + ((Entity)target).volume * 3)
					control = new Vector(((Entity)target).location.x - location.x, ((Entity)target).location.y - location.y);
			case HARVESTING:
				control = new Vector(((Entity)target).location.x - location.x, ((Entity)target).location.y - location.y);
				break;
			default:
				break;
		}
	}
	
	public void paint(Graphics2D g)
	{
		super.paint(g);
		
		if(type != null)
		{
			g.drawImage(type.tint, (int)(location.x-dimension.x/2-Game.cameraLoc.x+Game.S_WIDTH/2),
					(int)(location.y-dimension.y/2-Game.cameraLoc.y+Game.S_HEIGHT/2),
					(int)(dimension.x), (int)(dimension.y), null);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Orb clone()
	{
		Orb orb = new Orb(name, location.clone(), dimension.clone(), velocity.clone(), maxHealth, speed, volume, mass, type);
		orb.flags = (ArrayList<Flag>) flags.clone();
		orb.images = (ArrayList<ArrayList<BufferedImage>>) images.clone();
		orb.type = type;
		orb.action = action;
		orb.target = target;
		
		return orb;
	}
}
