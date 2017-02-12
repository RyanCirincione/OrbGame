import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Item
{
	public String name;
	public BufferedImage image;
	public Vector size;
	public ArrayList<Object> data;
	
	public Item(String name, String imgPath, Vector size)
	{
		this.name = name;
		try
		{
			image = ImageIO.read(new File(imgPath));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		this.size = size;
		data = new ArrayList<Object>();
	}
	
	public boolean equals(Item item)
	{
		return name.equals(item.name) && data.equals(item.data);
	}
	
	public String toString()
	{
		return name;
	}
}
