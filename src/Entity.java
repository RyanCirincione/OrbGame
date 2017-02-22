import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Entity
{
	static enum Flag {
		FRIENDLY, NEUTRAL, HOSTILE, ORB, FLYING, STUNNED, IMMOBILE, NOCLIP, INVINCIBLE
	}
	
	public ArrayList<Flag> flags;
	public ArrayList<ArrayList<BufferedImage>> images;
	public String name;
	public Vector location, velocity, control, dimension, graphics;
	public int health, maxHealth, volume, speed;
	public double mass;
	public boolean remove;
	
	/**
	 * 
	 * @param name A string used for representing the entity, typically for use where instanceof won't work
	 * @param location Starting position
	 * @param dimension Rectangular dimension for rendering
	 * @param velocity Initial speed of entity
	 * @param health Initial and max health
	 * @param speed Max speed of the entity
	 * @param volume Radius of the circle representing the entity's hitbox
	 * @param mass Used in the collision resolution algorithm; 0 <= mass <= 1
	 */
	public Entity(String name, Vector location, Vector dimension, Vector velocity, int health, int speed, int volume, double mass)
	{
		this.name = name;
		this.location = location;
		this.dimension = dimension;
		this.velocity = velocity;
		control = new Vector();
		images = new ArrayList<ArrayList<BufferedImage>>();
		flags = new ArrayList<Flag>();
		graphics = new Vector();
		this.health = maxHealth = health;
		this.speed = speed;
		this.volume = volume;
		this.mass = mass;
		remove = false;
	}
	
	public void click()
	{
		
	}
	
	public void tick()
	{
		this.doControl();
		
		if(new Vector().distance2(control) > speed*speed)
		{
			double distance = new Vector().distance(control);
			control.x = control.x * speed / distance;
			control.y = control.y * speed / distance;
		}
		
		velocity.doDeltaTo(new Vector(), new Vector(1, 1));
		velocity.doDeltaTo(control, control.toPositive());
		
		if(this.hasFlag(Flag.IMMOBILE))
			velocity = new Vector();
		
		location.doDelta(velocity);
		
		if( health <= 0 && !this.hasFlag(Flag.INVINCIBLE))
			remove = true;
	}
	
	public void checkCollide(Entity ent)
	{
		if(!this.hasFlag(Flag.NOCLIP) && !ent.hasFlag(Flag.NOCLIP) &&
				location.distance2(ent.location) < Math.pow(volume + ent.volume, 2) && this != ent)
			if(location.equals(ent.location))
			{
				location.doDelta(new Vector(Math.random()-0.5, Math.random()-0.5));
				ent.location.doDelta(new Vector(Math.random()-0.5, Math.random()-0.5));
			}
			else
			{
				location.x = location.x + ((location.x - ent.location.x) * (volume + ent.volume) /
						(location.distance(ent.location)) - (location.x - ent.location.x)) * ent.mass * (1 - mass);
				location.y = location.y + ((location.y - ent.location.y) * (volume + ent.volume) /
						(location.distance(ent.location)) - (location.y - ent.location.y)) * ent.mass * (1 - mass);
				
				ent.location.x = ent.location.x + ((ent.location.x - location.x) * (volume + ent.volume) /
						(ent.location.distance(location)) - (ent.location.x - location.x)) * mass * (1 - ent.mass);
				ent.location.y = ent.location.y + ((ent.location.y - location.y) * (volume + ent.volume) /
						(ent.location.distance(location)) - (ent.location.y - location.y)) * mass * (1 - ent.mass);
			}
	}
	
	public void doControl()
	{
		control = new Vector();
	}
	
	public void paint(Graphics2D g)
	{
		if(images.size() == 0)
		{
			try
			{
				g.drawImage(ImageIO.read(new File("res/unknown-entity.png")),
						(int)(location.x-dimension.x/2-Game.cameraLoc.x+Game.S_WIDTH/2),
						(int)(location.y-dimension.y/2-Game.cameraLoc.y+Game.S_HEIGHT/2),
						(int)(dimension.x), (int)(dimension.y), null);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			g.drawImage(images.get((int)(graphics.x)).get((int)(graphics.y)),
					(int)(location.x-dimension.x/2-Game.cameraLoc.x+Game.S_WIDTH/2),
					(int)(location.y-dimension.y/2-Game.cameraLoc.y+Game.S_HEIGHT/2),
					(int)(dimension.x), (int)(dimension.y), null);
		}
		
		graphics.y++;
		if(graphics.y == images.size())
			graphics.y = 0;
	}
	
	@SuppressWarnings("unchecked")
	public Entity clone()
	{
		Entity ent = new Entity(name, location.clone(), dimension.clone(), velocity.clone(), maxHealth, speed, volume, mass);
		ent.flags = (ArrayList<Flag>) flags.clone();
		ent.images = (ArrayList<ArrayList<BufferedImage>>) images.clone();
		ent.control = control.clone();
		ent.graphics = graphics.clone();
		
		return ent;
	}
	
	public boolean hasFlag(Flag flag)
	{
		return flags.contains(flag);
	}
}
