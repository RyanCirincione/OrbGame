
public class Tree extends Entity implements Harvestable
{
	int growth, maxGrowth;
	
	public Tree(String name, Vector location, Vector dimension, Vector velocity, int health, int speed, int volume, int grow)
	{
		super(name, location, dimension, velocity, health, speed, volume, 1);
		growth = maxGrowth = grow;
	}

	@Override
	public void harvest(int harvestLevel)
	{
		if(growth >= 1500 + 1000*harvestLevel)
		{
			growth -= 1500 + 1000*harvestLevel;
			Game.entities.add(new GroundItem("Tree Essence", this.location.clone(), null, new Vector(0, 0), 1, 0,
					new Item("Tree Essence", "res/unknown-entity.png", new Vector(1, 1))));
			Game.entities.add(new GroundItem("Tree Essence", this.location.clone(), null, new Vector(0, 0), 1, 0,
					new Item("Tree Essence", "res/unknown-entity.png", new Vector(1, 1))));
			if(harvestLevel > 0)
				Game.entities.add(new GroundItem("Tree Essence", this.location.clone(), null, new Vector(0, 0), 1, 0,
						new Item("Tree Essence", "res/unknown-entity.png", new Vector(1, 1))));
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		if(growth < maxGrowth)
			growth++;
	}
}
