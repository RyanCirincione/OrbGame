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
		
		if(Game.control.isPressed("D"))
			control.x = speed;
		if(Game.control.isPressed("S"))
			control.y = speed;
		if(Game.control.isPressed("A"))
			control.x = -speed;
		if(Game.control.isPressed("W"))
			control.y = -speed;
	}
	
	public void tick()
	{
		super.tick();
	}
}
