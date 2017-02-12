import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Player extends Entity
{
	public Inventory inventory;
	
	public Player(String name, Vector location, Vector dimension, Vector velocity,
			int health, int speed, int volume, double mass, Vector invSize)
	{
		super(name, location, dimension, velocity, health, speed, volume, mass);
		flags.add(Flag.FRIENDLY);
		inventory = new Inventory(invSize);
		ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();
		try
		{
			imgs.add(ImageIO.read(new File("res/player-still.png")));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		images.add(imgs);
	}
	
	public void doControl()
	{
		super.doControl();
		
		if(Game.eventHandler.hasEvent("key-down_d"))
			control.x = speed;
		if(Game.eventHandler.hasEvent("key-down_s"))
			control.y = speed;
		if(Game.eventHandler.hasEvent("key-down_a"))
			control.x = -speed;
		if(Game.eventHandler.hasEvent("key-down_w"))
			control.y = -speed;
	}
	
	public void tick()
	{
		super.tick();
	}
}
