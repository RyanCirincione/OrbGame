import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GroundItem extends Entity
{
	Item item;
	
	public GroundItem(String name, Vector location, Vector dimension, Vector velocity, int health, int speed, Item item)
	{
		super(name, location, dimension == null ? new Vector(24, 24) : dimension, velocity, health, speed, 12, 0.2);
		this.item = item;
		ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();
		imgs.add(item.image);
		images.add(imgs);
	}
	
	public void tick()
	{
		super.tick();
		if(location.distance2(Game.player.location) < Math.pow(volume + Game.player.volume, 2))
			if(Game.player.inventory.add(item))
				remove = true;
	}
	
}
