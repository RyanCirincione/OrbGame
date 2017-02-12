import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Panel
{
	public static final Color BORDER = Color.cyan.darker(), BACKGROUND = new Color(200, 200, 200), X_OUT = Color.red.darker(),
			X_OUT_BG = Color.red, MINIMIZE = Color.blue, MINIMIZE_BG = Color.cyan, TITLE = Color.green.darker();
	
	public boolean minimized, minimizable, closable;
	public String title;
	public Vector location, size, anchor;
	
	public Panel(String title, Vector location, Vector size)
	{
		this.title = title;
		this.location = location;
		this.size = size;
		anchor = null;
		minimized = false;
		minimizable = true;
		closable = true;
	}
	
	public boolean checkOpen()
	{
		return false;
	}
	
	public void open()
	{
		Game.eventQueue.addEvent(new Event("panel-open", 1, title));
		Game.openPanels.add(this);
		Game.closedPanels.remove(this);
	}
	
	public void click()
	{
		Game.eventQueue.addEvent(new Event("panel-click", 1, this));
		
		if(minimizable && Calc.pointInRect(Game.mouseLoc, new Vector(location.x, location.y), new Vector(location.x+20, location.y+20)) &&
				Game.eventHandler.hasEvent("mouse-click_1"))
			minimized = !minimized;
		
		if(closable && Calc.pointInRect(Game.mouseLoc, new Vector(location.x + (int)size.x - 20, location.y),
				new Vector(location.x + (int)size.x, location.y + 20)) && Game.eventHandler.hasEvent("mouse-click_1"))
			close();
		
		if(Calc.pointInRect(Game.mouseLoc, new Vector(location.x, location.y), new Vector(location.x + (int)size.x, location.y + 20)))
			anchor = new Vector(Game.mouseLoc.x - location.x, Game.mouseLoc.y - location.y);
	}
	
	public void tick()
	{
		if(Game.eventHandler.hasEvent("mouse-drag_1") && anchor != null)
			location = new Vector(Game.mouseLoc.x - anchor.x, Game.mouseLoc.y - anchor.y);
		
		if(Game.eventHandler.hasEvent("mouse-release_1") && anchor != null)
			anchor = null;
	}
	
	public void paint(Graphics2D g)
	{
		g.setColor(BACKGROUND);
		g.fillRect((int)location.x, (int)location.y, (int)size.x, 20);
		g.setColor(BORDER);
		g.drawRect((int)location.x, (int)location.y, (int)size.x, 20);
		
		g.setColor(TITLE);
		g.drawString(title, (int)location.x + 21, (int)location.y + 18);
		
		if(minimizable)
		{
			g.setColor(MINIMIZE_BG);
			g.fillRect((int)location.x+1, (int)location.y+1, 19, 19);
			g.setColor(MINIMIZE);
			g.fillRect((int)location.x+2, (int)location.y+16, 17, 3);
		}

		if(closable)
		{
			g.setColor(X_OUT_BG);
			g.fillRect((int)location.x + (int)size.x - 19, (int)location.y+1, 19, 19);
			g.setColor(X_OUT);
			g.drawLine((int)location.x + (int)size.x - 17, (int)location.y+3, (int)location.x + (int)size.x - 3, (int)location.y+17);
			g.drawLine((int)location.x + (int)size.x - 3, (int)location.y+3, (int)location.x + (int)size.x - 17, (int)location.y+17);
		}
		
		if(!minimized)
		{
			g.setColor(BACKGROUND);
			g.fillRect((int)location.x, (int)location.y + 21, (int)size.x, (int)size.y - 20);
			g.setColor(BORDER);
			g.drawRect((int)location.x, (int)location.y + 20, (int)size.x, (int)size.y - 19);
			
			paintDetail(g);
		}
	}
	
	public void close()
	{
		Game.eventQueue.addEvent(new Event("panel-close", 1, title));
		Game.closedPanels.add(this);
		Game.openPanels.remove(this);
	}
	
	public void paintDetail(Graphics2D g)
	{
		
	}
	
	public static class InventoryPanel extends Panel
	{
		public int tileSize;
		
		public InventoryPanel(String title, Vector location, int tileSize)
		{
			super(title, location, new Vector(Game.player.inventory.size.x * (1 + tileSize),
					19 + Game.player.inventory.size.y * (1 + tileSize)));
			this.tileSize = tileSize;
		}

		public boolean checkOpen()
		{
			return Game.eventHandler.hasEvent("key-press_e");
		}
		
		public void click()
		{
			super.click();

			if(Calc.pointInRect(Game.mouseLoc,
					new Vector(location.x-1, location.y+20), new Vector(location.x+size.x+1, location.y+(minimized?20:size.y)+1)))
			{
				//TODO Add inventory interaction
			}
		}
		
		public void tick()
		{
			super.tick();
			
			if(Game.eventHandler.hasEvent("key-press_e") && !Game.eventQueue.hasEvent("panel-open", title))
				close();
		}
		
		public void paintDetail(Graphics2D g)
		{
			Inventory inv = Game.player.inventory;
			
			g.setColor(BORDER);
			for(int x = 1; x < inv.size.x; x++)
				g.drawLine((int)location.x + x * (int)(size.x / inv.size.x), (int)location.y + 20,
						(int)location.x + x * (int)(size.x / inv.size.x), (int)location.y + (int)size.y);
			for(int y = 1; y < inv.size.y; y++)
				g.drawLine((int)location.x, (int)location.y + y * (int)((size.y - 19) / inv.size.y) + 20,
						(int)location.x + (int)size.x, (int)location.y + y * (int)((size.y - 19) / inv.size.y) + 20);
			
			g.setColor(Color.gray.darker());
			boolean[][] checks = new boolean[(int)inv.size.x][(int)inv.size.y];
			for(int x = 0; x < inv.size.x; x++)
				for(int y = 0; y < inv.size.y; y++)
					if(!checks[x][y])
						if(inv.items[x][y] == null)
						{
							checks[x][y] = true;
							g.fillRect((int)location.x + 1 + x * (tileSize + 1), (int)location.y + 21 + y * (tileSize + 1),
									tileSize, tileSize);
						}
						else
						{
							for(int i = x; i < x + inv.items[x][y].size.x; i++)
								for(int j = y; j < y + inv.items[x][y].size.y; j++)
									checks[i][j] = true;
							g.drawImage(inv.items[x][y].image, (int)location.x + 1 + x * (tileSize + 1),
									(int)location.y + 21 + y * (tileSize + 1), (int)inv.items[x][y].size.x * (tileSize + 1) - 1,
									(int)inv.items[x][y].size.y * (tileSize + 1) - 1, null);
						}
		}
	}
	
	public static class DebugConsole extends Panel
	{
		public int maxLines;
		ArrayList<String> logs;
		
		public DebugConsole(String title, Vector location, int width, int maxLines)
		{
			super(title, location, new Vector(width, 22 + 10 * maxLines));
			logs = new ArrayList<String>();
			this.maxLines = maxLines;
		}
		
		public boolean checkOpen()
		{
			return Game.eventHandler.hasEvent("key-press_`");
		}
		
		public void log(String string)
		{
			logs.add(string);
			if(logs.size() > maxLines)
				logs.remove(0);
		}
		
		public void tick()
		{
			super.tick();
			
			if(Game.eventHandler.hasEvent("key-press_`") && !Game.eventQueue.hasEvent("panel-open", title))
				close();
		}
		
		public void paintDetail(Graphics2D g)
		{
			g.setColor(Color.red);
			for(int i = 0; i < logs.size(); i++)
				g.drawString(logs.get(i), (int)location.x + 2, (int)location.y + 21 + 10 * (i + 1));
		}
	}
	
	public static class StageMessage extends Panel
	{
		BufferedImage image;
		String message, bufferedMessage;
		
		public StageMessage(String title, Vector location, Vector size)
		{
			super(title, location, size);
			message = null;
			bufferedMessage = "";
			image = null;
			closable = false;
			minimizable = false;
		}
		
		public boolean checkOpen()
		{
			return message != null;
		}
		
		public void createMessage(String title, BufferedImage image, String message)
		{
			this.title = title;
			this.image = image;
			this.message = message;
			bufferedMessage = "";
		}
		
		public void tick()
		{
			super.tick();
			
			if(!message.equals(bufferedMessage))
				bufferedMessage += message.charAt(bufferedMessage.length());
			
			if(Game.eventHandler.hasEvent("key-press_ ") && !Game.eventQueue.hasEvent("panel-open", title))
				if(!message.equals(bufferedMessage))
					bufferedMessage += message.substring(bufferedMessage.length(), bufferedMessage.length() + 10 > message.length() ?
							bufferedMessage.length() + 10 : message.length());
				else
					close();
		}
		
		public void close()
		{
			super.close();
			title = null;
			image = null;
			message = null;
			bufferedMessage = "";
		}
		
		public void paintDetail(Graphics2D g)
		{
			g.drawImage(image, (int)location.x + 1, (int)location.y + 21, 128, 128, null);
			
			g.setColor(Color.green.darker());
			String[] messages = bufferedMessage.split("\n");
			for(int i = 0; i < messages.length; i++)
				g.drawString(messages[i], (int)location.x + 135, (int)location.y + 31 + i * 12);
			
			g.setColor(Color.blue.darker());
			if(image != null && message.equals(bufferedMessage))
				g.drawString("Press space to close", (int)location.x + 135, (int)location.y + (int)size.y);
		}
	}
	
	public static class MainMenu extends Panel
	{
		String[] options = {"Intro", "Jump Right In"};
		StageEvent[] starts = {new StageEvent.OpeningStage(), new StageEvent.CrashedStage()};
		
		public MainMenu(String title)
		{
			super(title, new Vector(50, 50), new Vector(Game.S_WIDTH-100, Game.S_HEIGHT-100));
			minimizable = false;
			closable = false;
			StageEvent.CUT_SCENE = true;
		}
		
		public void click()
		{
			for(int i = 0; i < options.length && StageEvent.CUT_SCENE; i++)
				if(Calc.pointInRect(Game.mouseLoc, new Vector((int)location.x + 2, (int)location.y + 22 + i * 15),
						new Vector((int)location.x + 6 + 6 * options[i].length(), (int)location.y + 35 + i * 15)))
				{
					close();
					Game.queuedStageEvents.add(starts[i]);
				}
		}
		
		public void close()
		{
			super.close();
			
			StageEvent.CUT_SCENE = false;
			
			Game.stageMessagePanel = new Panel.StageMessage(null, new Vector(0, Game.S_HEIGHT - 200), new Vector(Game.S_WIDTH-1, 198));
			Game.closedPanels.add(Game.stageMessagePanel);
		}
		
		public void paintDetail(Graphics2D g)
		{
			g.setColor(Color.magenta.darker());
			for(int i = 0; i < options.length; i++)
			{
				g.drawRect((int)location.x + 2, (int)location.y + 22 + i * 15, 4 + 6 * options[i].length(), 13);
				g.drawString(options[i], (int)location.x + 4, (int)location.y + 33 + i * 15);
			}
		}
	}
	
	public static class InfoPanel extends Panel
	{
		Entity entity = null;
		
		public InfoPanel(String title, Vector location, Vector size)
		{
			super(title, location, size);
		}
		
		public boolean checkOpen()
		{
			return Game.selectedEntities.size() == 1 && Game.selectedEntities.get(0) != Game.player;
		}
		
		public void open()
		{
			super.open();
			
			entity = Game.selectedEntities.get(0);
			size = new Vector(entity.images.get(0).get(0).getWidth() + 12, entity.images.get(0).get(0).getHeight() + 50);
			
			title = entity.name;
		}
		
		public void tick()
		{
			if(!checkOpen())
				close();
		}
		
		public void close()
		{
			super.close();
			
			entity = null;
		}
		
		public void paintDetail(Graphics2D g)
		{
			g.drawImage(entity.images.get(0).get(0), (int)location.x + 5, (int)location.y + 21, null);
			g.setColor(Color.red);
			g.fillRect((int)location.x + 6, (int)location.y + entity.images.get(0).get(0).getHeight() + 31,
					entity.images.get(0).get(0).getWidth(), 11);
			g.setColor(Color.green);
			g.fillRect((int)location.x + 6, (int)location.y + entity.images.get(0).get(0).getHeight() + 31,
					(int)(entity.images.get(0).get(0).getWidth() * (double)entity.health / entity.maxHealth), 11);
			g.setColor(Color.green.darker());
			g.drawString(entity.health + "/" + entity.maxHealth, (int)location.x + 7,
					(int)location.y + entity.images.get(0).get(0).getHeight() + 41);
		}
	}
}
