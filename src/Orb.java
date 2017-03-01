import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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
		IDLE, FOLLOWING, TRAVELLING, FIGHTING, CARRYING, HARVESTING
	}
	
	static final int FOLLOWING_RANGE = 150, TRAVELING_RANGE = 50, TASK_RANGE = 200;
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
		
		if(Game.control.isPressed("Space") && location.distance2(
				new Vector(Game.control.mouse.x - Game.S_WIDTH/2 + Game.cameraLoc.x,
				Game.control.mouse.y - Game.S_HEIGHT/2 + Game.cameraLoc.y)) <= Game.WHISTLE_RANGE * Game.WHISTLE_RANGE)
			action = Action.FOLLOWING;
	}
	
	public void doControl()
	{
		super.doControl();
		
		switch (action)
		{
		case IDLE:
			checkTasks();
			break;
		case FOLLOWING:
			if(location.distance2(Game.player.location) > FOLLOWING_RANGE*FOLLOWING_RANGE)
				target = Game.player.location.clone();
			else
				target = null;
			
			if(Game.control.isPressed("M1") && Game.player.chargeCooldown == 0)
			{
				Game.player.chargeCooldown = 3; //TODO make const
				action = Action.TRAVELLING;
				target = new Vector(Game.control.mouse.x - Game.S_WIDTH/2 + Game.cameraLoc.x,
						Game.control.mouse.y - Game.S_HEIGHT/2 + Game.cameraLoc.y);
			}
			break;
		case TRAVELLING:
			if(location.distance2((Vector) target) <= TRAVELING_RANGE*TRAVELING_RANGE)
			{
				target = null;
				action = Action.IDLE;
			}
			checkTasks();
			break;
		case FIGHTING:
			if(location.distance2(((Entity)target).location) <= Math.pow(volume + ((Entity)target).volume, 2) && cooldown == 0)
			{
				cooldown = 30;
				((Entity)target).health -= type == Type.FIGHTER ? 5 : 2;
				if(((Entity)target).remove)
				{
					action = Action.IDLE;
					target = null;
				}
			}
			break;
		case HARVESTING:
			if(location.distance2(((Entity)target).location) < Math.pow(volume + ((Entity)target).volume + 5, 2))
				((Harvestable)target).harvest(type == Type.HARVESTER ? 1 : 0);
			break;
		default:
			break;
		}
		
		if(Game.control.isJustReleased("M1") && Game.selectedEntities.contains(this))
		{
			action = Action.TRAVELLING;
			Vector click = ((Vector) Game.control.mouse.clone());
			target = new Vector(click.x - Game.S_WIDTH/2 + Game.cameraLoc.x, click.y - Game.S_HEIGHT/2 + Game.cameraLoc.y);
		}

		
		switch(action)
		{
			case FOLLOWING:
			case TRAVELLING:
				if(target != null)
					control = new Vector(((Vector)target).x - location.x, ((Vector)target).y - location.y);
				break;
			case CARRYING:
				Vector goal = new Vector(((Entity)((Task)target).data.get(0)).location.x -
						((Vector)((Task)target).data.get(1)).x, ((Entity)((Task)target).data.get(0)).location.y -
						((Vector)((Task)target).data.get(1)).y).normalize();
				goal.scalar(((Entity)((Task)target).data.get(0)).volume/2);
				control = new Vector(goal.x - location.x, goal.y - location.y);
				break;
			case FIGHTING:
				if(cooldown == 0)
					control = new Vector(((Entity)target).location.x - location.x, ((Entity)target).location.y - location.y);
				else if(location.distance2(((Entity)target).location) < Math.pow(volume + ((Entity)target).volume * 2, 2))
					control = new Vector(location.x - ((Entity)target).location.x, location.y - ((Entity)target).location.y);
				else if(location.distance2(((Entity)target).location) > Math.pow(volume + ((Entity)target).volume * 3, 2))
					control = new Vector(((Entity)target).location.x - location.x, ((Entity)target).location.y - location.y);
			case HARVESTING:
				control = new Vector(((Entity)target).location.x - location.x, ((Entity)target).location.y - location.y);
				break;
			default:
				break;
		}
	}
	
	private void checkTasks()
	{
		Iterator<Task> iter = Game.tasks.iterator();
		while(iter.hasNext())
		{
			Task t = iter.next();
			if(((Entity)t.data.get(0)).remove)
			{
				iter.remove();
				continue;
			}
			if(location.distance2(t.location) < TASK_RANGE*TASK_RANGE)
			{
				switch(t.type)
				{
				case Task.CARRY:
					action = Action.CARRYING;
					target = t;
					break;
				case Task.HARVEST:
					action = Action.HARVESTING;
					target = t.data.get(0);
					break;
				case Task.FIGHT:
					action = Action.FIGHTING;
					target = t.data.get(0);
					break;
				default:
					break;
				}
				break;
			}
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
