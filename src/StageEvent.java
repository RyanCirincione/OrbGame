import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;


public class StageEvent
{
	public static boolean DARKEN_SCREEN = false, FREEZE_ENTITIES = false, CAMERA_GRAB = false, CUT_SCENE = false;
	public static EntityHandler actors = new EntityHandler(500);
	
	String name;
	
	public StageEvent(String name)
	{
		this.name = name;
	}
	
	public boolean checkTrigger()
	{
		return true;
	}
	
	public void trigger()
	{
		Game.queuedStageEvents.remove(this);
		Game.activeStageEvents.add(this);
	}
	
	public void tick()
	{
		
	}
	
	public void complete()
	{
		Game.activeStageEvents.remove(this);
		Game.eventQueue.addEvent(new Event("stage-event-complete", 1, name));
	}
	
	public static class OpeningStage extends StageEvent
	{
		public OpeningStage()
		{
			super("Opening Stage");
		}
		
		public void trigger()
		{
			FREEZE_ENTITIES = true;
			CUT_SCENE = true;
			
			super.trigger();
			
			try
			{
				Game.stageMessagePanel.createMessage("Narrator", ImageIO.read(new File("res/narrator.png")),
						  "It was 5 years ago in the year 4XC5F when hydrocarbon embedded technology hit the streets. Everyone\n"
						+ "was all over it. The stuff was capable of performing tasks an old HEX computer couldn't dream of.\n"
						+ "A single chip the size of a thumb could run the entirety of Steam's servers during a Summer Sale.\n"
						+ "Naturally we entered our 6th Industrial Revolution. NASA pushed harder than ever on discovering\n"
						+ "and exploring the universe. Eventually, they came out with the Spacial Prospecting Hydrocarbon\n"
						+ "Embedded Research Explorer, or SPHERE. Locked away on a shelf never seeing light except to be\n"
						+ "updated and improved, it waited for the perfect mission...");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		public void tick()
		{
			if(Game.eventHandler.hasEvent("panel-close", "Narrator"))
				complete();
		}
		
		public void complete()
		{
			super.complete();
			
			Game.queuedStageEvents.add(new MappingScene());
		}
	}
	
	public static class MappingScene extends StageEvent
	{
		public MappingScene()
		{
			super("Mapping Scene");
		}
		
		public void trigger()
		{
			super.trigger();
			
			Entity earth = new Entity("Earth", new Vector(-300, -300), new Vector(64, 64), new Vector(), 1, 0, 32, 1);
			earth.flags.add(Entity.Flag.NOCLIP);
			ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();
			try
			{
				imgs.add(ImageIO.read(new File("res/earth.png")));
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			earth.images.add(imgs);
			actors.add(earth);
			
			try
			{
				Game.stageMessagePanel.createMessage("Narrator", ImageIO.read(new File("res/narrator.png")),
						"NASA's planet mapping branch worked tirelessly to discover interesting other planets. For a long while\n"
						+ "their search proved rather fruitless. Eventually, they found a rather intriguing result come up\n"
						+ "on their monitors, and they jumped on the opportunity. With the entire world's TV on them, they sent\n"
						+ "the SPHERE on its first ever mission. At this point, it had been loaded with so many upgrades,\n"
						+ "utilities, and artificial intelligence programs, that it's target planet wouldn't be the only\n"
						+ "thing NASA was exploring.                                                                        ");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		public void tick()
		{
			if(actors.size() < 200 && Math.random() < 0.15)
			{
				Entity star = new Entity("Star", new Vector(Math.random()*Game.S_WIDTH-Game.S_WIDTH/2,
						Math.random()*Game.S_HEIGHT-Game.S_HEIGHT/2), new Vector(7, 7), new Vector(), 1, 2, 7, 1){
					public void tick()
					{
						dimension.doDelta(new Vector(speed, speed));
						
						if(dimension.x == 15)
							speed = -2;
						if(dimension.x == 1)
							speed = 0;
					}
				};
				star.flags.add(Entity.Flag.NOCLIP);
				ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();
				try
				{
					imgs.add(ImageIO.read(new File("res/star.png")));
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				star.images.add(imgs);
				actors.add(star);
			}
			
			if(actors.size() == 100)
			{
				Entity star = new Entity("Star", new Vector(200, 50), new Vector(7, 7), new Vector(), 1, 2, 7, 1){
					public void tick()
					{
						dimension.doDelta(new Vector(speed, speed));
						
						if(dimension.x == 13)
							speed = -2;
						if(dimension.x == 5)
							speed = 2;
					}
				};
				star.flags.add(Entity.Flag.NOCLIP);
				ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();
				try
				{
					imgs.add(ImageIO.read(new File("res/star.png")));
				} catch (IOException e)
				{
					e.printStackTrace();
				}
				star.images.add(imgs);
				actors.add(star);
			}
			
			if(Game.eventHandler.hasEvent("panel-close", "Narrator"))
				complete();
		}
		
		public void complete()
		{
			super.complete();
			
			actors.entities.clear();
			
			Game.queuedStageEvents.add(new EmbarkStage());
		}
	}
	
	public static class EmbarkStage extends StageEvent
	{
		public EmbarkStage()
		{
			super("Embark Scene");
		}
		
		public void trigger()
		{
			super.trigger();
			
			Entity earth = new Entity("Star", new Vector(-300, -100), new Vector(128, 128), new Vector(), 1, 0, 64, 1);
			earth.flags.add(Entity.Flag.NOCLIP);
			ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();
			try
			{
				imgs.add(ImageIO.read(new File("res/earth.png")));
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			earth.images.add(imgs);
			actors.add(earth);
			
			Entity ship = new Entity("Star", new Vector(-290, -100), new Vector(32, 32), new Vector(1, 0), 1, 2, 16, 1){
				public void doControl()
				{
					super.doControl();
					control = new Vector(1, 0);
				}
			};
			ship.flags.add(Entity.Flag.NOCLIP);
			imgs = new ArrayList<BufferedImage>();
			try
			{
				imgs.add(ImageIO.read(new File("res/spaceship.png")));
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			ship.images.add(imgs);
			actors.add(ship);
			
			try
			{
				Game.stageMessagePanel.createMessage("Narrator", ImageIO.read(new File("res/narrator.png")),
						"So in a small, efficient shuttle, the SPHERE was off. Prepared to explore an unknown planet. Or atleast\n"
						+ "so they thought...");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		public void tick()
		{
			if(Game.eventHandler.hasEvent("panel-close", "Narrator"))
				complete();
		}
		
		public void complete()
		{
			super.complete();
			
			actors.entities.clear();
			
			Game.queuedStageEvents.add(new EntryStage());
		}
	}
	
	public static class EntryStage extends StageEvent
	{
		public EntryStage()
		{
			super("Entry Scene");
		}
		
		public void trigger()
		{
			super.trigger();
			
			Entity planet = new Entity("Star", new Vector(300, -100), new Vector(512, 512), new Vector(), 1, 2, 256, 1);
			planet.flags.add(Entity.Flag.NOCLIP);
			ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();
			try
			{
				imgs.add(ImageIO.read(new File("res/planet-shell.png")));
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			planet.images.add(imgs);
			actors.add(planet);
			
			Entity ship = new Entity("Ship", new Vector(-290, -100), new Vector(32, 32), new Vector(1, 0), 1, 2, 16, 1){
				public void doControl()
				{
					super.doControl();
					control = new Vector(1, 0);
				}
			};
			ship.flags.add(Entity.Flag.NOCLIP);
			imgs = new ArrayList<BufferedImage>();
			try
			{
				imgs.add(ImageIO.read(new File("res/spaceship.png")));
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			ship.images.add(imgs);
			actors.add(ship);
			
			try
			{
				Game.stageMessagePanel.createMessage("Player", ImageIO.read(new File("res/player-still.png")),
						"<PROXIMITY TO TARGET WITHIN BOUNDS>\n<INTIATING LANDING PROTOCOL 2.0.1>                                   "
						+ "                                                                                                        "
						+ "                                           \n<DISCREPANCY DETECTED>\nAltitude: 6,000 m\nProximity: 500 m\n"
						+ "<AWAITING CLARIFICATION COMMAND>                                                                        "
						+ "            \n<NO COMMAND ISSUED, PROCEEDING WITH LANDING PROTOCOL 2.0.1>                               "
						+ "\n<ERROR: LANDING SURFACE UNSTABLE>                                                                     ");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		public void tick()
		{
			if(Game.stageMessagePanel.message.equals(Game.stageMessagePanel.bufferedMessage))
				complete();
		}
		
		public void complete()
		{
			super.complete();
			
			actors.entities.clear();
			Game.stageMessagePanel.close();
			
			Game.queuedStageEvents.add(new FallingStage());
		}
	}
	
	public static class FallingStage extends StageEvent
	{
		public FallingStage()
		{
			super("Falling Scene");
		}
		
		public void trigger()
		{
			super.trigger();
			
			Entity planet = new Entity("Planet", new Vector(0, 0), new Vector(Game.S_WIDTH, Game.S_HEIGHT), new Vector(), 1, 0, 1, 1);
			planet.flags.add(Entity.Flag.NOCLIP);
			ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();
			try
			{
				imgs.add(ImageIO.read(new File("res/planet-background.png")));
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			planet.images.add(imgs);
			actors.add(planet);
			
			for(int i = 0; i < 6; i++)
			{
				Entity crystal = new Entity("Crystal", new Vector(-200, -Game.S_HEIGHT/2), new Vector(32, 32),
						new Vector(Math.random()*2, 2), 1, 1, 16, 1){
					public void doControl()
					{
						super.doControl();
						control = velocity.clone();
					}
				};
				crystal.flags.add(Entity.Flag.NOCLIP);
				imgs = new ArrayList<BufferedImage>();
				try
				{
					imgs.add(ImageIO.read(new File("res/falling-crystal.png")));
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
				crystal.images.add(imgs);
				actors.add(crystal);
			}
			
			Entity ship = new Entity("Ship", new Vector(-250, -Game.S_HEIGHT/2-100), new Vector(32, 32), new Vector(1, 2), 1, 1, 16, 1){
				public void doControl()
				{
					super.doControl();
					control = new Vector(1, 2);
				}
			};
			ship.flags.add(Entity.Flag.NOCLIP);
			imgs = new ArrayList<BufferedImage>();
			try
			{
				imgs.add(ImageIO.read(new File("res/crashing-ship.png")));
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
			ship.images.add(imgs);
			actors.add(ship);
			
			try
			{
				Game.stageMessagePanel.createMessage("Narrator", ImageIO.read(new File("res/narrator.png")),
						"Little did NASA know that what they thought was a blue, sparkling, beautiful giant was actually a rather\n"
						+ "barren, lifeless ball of desert, surrounded by a very intriguing crystal shell, whose composition\n"
						+ "has very interesting results on that which it comes in contact with. As the ship came burning to the\n"
						+ "ground, communication with the SPHERE was lost. The crystal which the ship smashed through in an attempt\n"
						+ "to land broke into pieces and scattered across the planet's surface. Some even worked its way between\n"
						+ "the cracks of the damaged ship, straight toward the SPHERE...");
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		public void tick()
		{
			if(Game.eventHandler.hasEvent("panel-close", "Narrator"))
				complete();
		}
		
		public void complete()
		{
			super.complete();
			
			actors.entities.clear();
			CUT_SCENE = false;
			
			Game.queuedStageEvents.add(new CrashedStage());
		}
	}
	
	public static class CrashedStage extends StageEvent
	{
		public CrashedStage()
		{
			super("Crash Landing");
		}
		
		public void trigger()
		{
			DARKEN_SCREEN = true;
			FREEZE_ENTITIES = true;
			
			super.trigger();

			Game.player = new Player("Player", new Vector(), new Vector(64, 64), new Vector(), 200, 5, 30, 0.75, new Vector(15, 12));
			Game.entities.add(Game.player);
			Tree firstTree = new Tree("Tree", new Vector(-300, -300), new Vector(96, 96), new Vector(), 50, 0, 45, 10000);
			Game.entities.add(firstTree);
			Entity debris = new Entity("Debris", new Vector(250, 100), new Vector(64, 64), new Vector(), 300, 0, 35, 0.99){
				public void tick()
				{
					super.tick();
					
					if(remove)
						for(int i = 0; i < 3; i++)
						{
							GroundItem item = new GroundItem("Orb", location.clone(), null, new Vector(), 1, 1,
									new Item("Orb", "res/orb-item.png", new Vector(1, 1)));
							Game.entities.add(item);
						}
				}
				
				public void click()
				{
					super.click();
					
					if(Game.eventQueue.hasEvent("entity-click", this, 3))
						this.health-=5;
				}
			};
			ArrayList<BufferedImage> imgs = new ArrayList<BufferedImage>();
			try
			{
				imgs.add(ImageIO.read(new File("res/debris.png")));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			debris.images.add(imgs);
			Game.entities.add(debris);
			
			Orb firstOrb = new Orb("Orb", new Vector(200, 100), new Vector(), 50, 3, null);
			firstOrb.action = Orb.Action.FIGHTING;
			firstOrb.target = debris;
			Game.entities.add(firstOrb);

			Game.closedPanels.add(new Panel.InventoryPanel("Inventory", new Vector(5, 5), 11));
			Game.closedPanels.add(new Panel.InfoPanel("", new Vector(5, 5), new Vector()));
			Game.debugPanel = new Panel.DebugConsole("Debug", new Vector(0, Game.S_HEIGHT - 174), 300, 15);
			Game.closedPanels.add(Game.debugPanel);
			
			Game.stageMessagePanel.createMessage("Player",Game.player.images.get(0).get(0), "<EMERGENCY AUTO BOOT SEQUENCE INITIATED>\n"
					+ "<STARTING CENTRAL PROCESSING THREAD>\n<SCANNING SURROUNDINGS>\nResult: Barren plains\nResult: Crashed ship\n"
					+ "<INITIATING CRASH LANDING PROTOCOL 7.4.5>");
		}
		
		public void tick()
		{
			if(Game.eventHandler.hasEvent("panel-close", "Player"))
				complete();
		}
		
		public void complete()
		{
			super.complete();
			
			Game.queuedStageEvents.add(new ControlsEvent());
		}
	}
	
	public static class ControlsEvent extends StageEvent
	{
		public ControlsEvent()
		{
			super("Controls");
		}

		public void trigger()
		{
			
			super.trigger();

			Game.stageMessagePanel.createMessage("Controls", Game.player.images.get(0).get(0), "Movement: WASD\nInventory: e"
					+ "\nDebug: `\nSelect: Click + Drag\nCommand: Click\nAction: Right Click\nWhistle: Space");
		}
		
		public void tick()
		{
			if(Game.eventHandler.hasEvent("panel-close", "Controls"))
				complete();
		}
		
		public void complete()
		{
			super.complete();
			
			DARKEN_SCREEN = false;
			FREEZE_ENTITIES = false;
			
			Game.queuedStageEvents.add(new DiscoverOrbEvent());
		}
	}
	
	public static class DiscoverOrbEvent extends StageEvent
	{
		public DiscoverOrbEvent()
		{
			super("Discover Orb");
		}
		
		public boolean checkTrigger()
		{
			for(int i = 1; i < Game.entities.size(); i++)
				if(Game.entities.get(i) instanceof Orb && Calc.distance(Game.player.location, Game.entities.get(i).location) < 150)
					return true;
			return false;
		}

		public void trigger()
		{
			DARKEN_SCREEN = true;
			FREEZE_ENTITIES = true;
			
			super.trigger();

			Game.stageMessagePanel.createMessage("Player", Game.player.images.get(0).get(0), "Unknown entity detected.\nScanning...\n"
					+ "Nonhostile. Small. Round. Eyes. Intelli-\n<LOGICAL ERROR: DISCREPANCY DETECTED>\nForm demonstrates signs of "
					+ "life. No biological matter detected.\nScan protocol 4.3 initialized...                                      "
					+ "\nNo signs of life confirmed. It appears to want to access something covered by the ship's debris."
					+ "\nI should assist it.");
		}
		
		public void tick()
		{
			if(Game.eventHandler.hasEvent("panel-close", "Player"))
				complete();
		}
		
		public void complete()
		{
			super.complete();
			
			DARKEN_SCREEN = false;
			FREEZE_ENTITIES = false;
			
			Game.queuedStageEvents.add(new RemovedDebris());
		}
	}
	
	public static class RemovedDebris extends StageEvent
	{
		public RemovedDebris()
		{
			super("Removed Debris");
		}
		
		public boolean checkTrigger()
		{
			if(Game.eventHandler.hasEvent("entity-remove") && ((Entity)Game.eventHandler.getData("entity-remove").get(0)).name.equals("Debris"))
				return true;
			else
				return false;
		}

		public void trigger()
		{
			DARKEN_SCREEN = true;
			FREEZE_ENTITIES = true;
			
			super.trigger();

			Game.stageMessagePanel.createMessage("Player", Game.player.images.get(0).get(0), "I would have salvaged that for the ship,"
					+ " but most of my utilities were lost in wreck.\nThe object appears to show affection toward me for helping free "
					+ "these... things.\nPerhaps it could be of some use to me...\nI should find some matter to use as fuel, seeing as"
					+ "my reserves were scattered in the crash.\nDoes this guy know where to find sustenance?");
		}
		
		public void tick()
		{
			if(Game.eventHandler.hasEvent("panel-close", "Player"))
				complete();
		}
		
		public void complete()
		{
			super.complete();
			
			DARKEN_SCREEN = false;
			FREEZE_ENTITIES = false;
			
//			Game.queuedStageEvents.add(new ControlsEvent());
		}
	}
}
