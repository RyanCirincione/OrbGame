import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JPanel
{
	private static final long serialVersionUID = -6105089567534097250L;
	static Timer loop = new Timer();
	static Game panel;
	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Orb Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new Game();
		frame.getContentPane().add(panel);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		loop.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				panel.tick();
				panel.repaint();
				
				if(!panel.isOpen())
					loop.cancel();
			}
		}, 0, 1000/60);
	}
	
	public static final int S_WIDTH = 1000, S_HEIGHT = 675, WHISTLE_RANGE = 75;
	public static int step = 0;
	public static EntityHandler entities;
	public static ArrayList<Entity> selectedEntities;
	public static EventHandler eventHandler, eventQueue;
	public static Player player;
	public static Vector cameraLoc, mouseLoc, selectLocStart;
	public static ArrayList<Panel> openPanels, closedPanels;
	public static ArrayList<StageEvent> queuedStageEvents, activeStageEvents;
	public static Panel.DebugConsole debugPanel;
	public static Panel.StageMessage stageMessagePanel;
	BufferedImage background = null;
	
	public Game()
	{
		entities = new EntityHandler(300);
		selectedEntities = new ArrayList<Entity>();
		eventHandler = new EventHandler();
		eventQueue = new EventHandler();
		cameraLoc = new Vector();
		mouseLoc = new Vector();
		selectLocStart = null;

		openPanels = new ArrayList<Panel>();
		closedPanels = new ArrayList<Panel>();
		openPanels.add(new Panel.MainMenu("Main Menu"));

		queuedStageEvents = new ArrayList<StageEvent>();
		activeStageEvents = new ArrayList<StageEvent>();
		
		try
		{
			background = ImageIO.read(new File("res/sand-background.png"));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		this.setPreferredSize(new Dimension(S_WIDTH, S_HEIGHT));
		this.setFocusable(true);
		this.requestFocus();
		
		this.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e)
			{
				String key = "";
				switch (e.getKeyCode())
				{
					case KeyEvent.VK_ESCAPE:
						key = "esc";
						break;
					default:
						key = e.getKeyChar()+"";
						break;
				}
				eventQueue.addEvent(new Event("key-press_"+key, 1));
				eventQueue.addEvent(new Event("key-down_"+key, -1));
			}
			
			public void keyReleased(KeyEvent e)
			{
				String key = "";
				switch (e.getKeyCode())
				{
					case KeyEvent.VK_ESCAPE:
						key = "esc";
						break;
					default:
						key = e.getKeyChar()+"";
						break;
				}
				eventQueue.addEvent(new Event("key-release_"+key, 1));
				eventHandler.removeEvent("key-down_"+key);
			}
		});
		
		this.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e)
			{
				eventQueue.addEvent(new Event("mouse-press_"+e.getButton(), 1, new Vector(e.getX(), e.getY())));
				eventQueue.addEvent(new Event("mouse-down_"+e.getButton(), -1, new Vector(e.getX(), e.getY())));
			}
			
			public void mouseClicked(MouseEvent e)
			{
				eventQueue.addEvent(new Event("mouse-click_"+e.getButton(), 1, new Vector(e.getX(), e.getY())));
			}
			
			public void mouseReleased(MouseEvent e)
			{
				eventQueue.addEvent(new Event("mouse-release_"+e.getButton(), 1, new Vector(e.getX(), e.getY())));
				eventHandler.removeEvent("mouse-down_"+e.getButton());
			}
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter(){
			public void mouseMoved(MouseEvent e)
			{
				eventQueue.addEvent(new Event("mouse-move_"+e.getButton(), 1, new Vector(e.getX(), e.getY())));
				mouseLoc.x = e.getX();
				mouseLoc.y = e.getY();
			}
			
			public void mouseDragged(MouseEvent e)
			{
				int button = 0;
				if(eventHandler.hasEvent("mouse-down_1"))
					button = 1;
				else if(eventHandler.hasEvent("mouse-down_2"))
					button = 2;
				else if(eventHandler.hasEvent("mouse-down_3"))
					button = 3;
				eventQueue.addEvent(new Event("mouse-drag_"+button, 1, new Vector(e.getX(), e.getY())));
				mouseLoc.x = e.getX();
				mouseLoc.y = e.getY();
			}
		});
	}
	
	public boolean isOpen()
	{
		return true;
	}
	
	public void tick()
	{
		for(int i = 0; i < closedPanels.size(); i++)
			if(closedPanels.get(i).checkOpen())
				closedPanels.get(i).open();
		if(eventHandler.hasEvent("mouse-press_1") || eventHandler.hasEvent("mouse-press_3"))
			for(int i = openPanels.size()-1; i >= 0 && !Game.eventHandler.hasEvent("panel-click"); i--)
				if(Calc.pointInRect(mouseLoc, openPanels.get(i).location, openPanels.get(i).minimized ?
						new Vector(openPanels.get(i).location.x + openPanels.get(i).size.x, openPanels.get(i).location.y + 21) :
						new Vector(openPanels.get(i).location.x + openPanels.get(i).size.x,
								openPanels.get(i).location.y + openPanels.get(i).size.y + 1)))
					openPanels.get(i).click();
		for(int i = 0; i < openPanels.size(); i++)
			openPanels.get(i).tick();
		
		for(int i = 0; i < queuedStageEvents.size(); i++)
			if(queuedStageEvents.get(i).checkTrigger())
			{
				queuedStageEvents.get(i).trigger();
				i--;
			}
		
		for(int i = 0; i < activeStageEvents.size(); i++)
			activeStageEvents.get(i).tick();

		if(eventHandler.hasEvent("mouse-press_1") || eventHandler.hasEvent("mouse-press_3"))
			for(int i = entities.size()-1; i >= 0 && !Game.eventHandler.hasEvent("panel-click"); i--)
				if(Calc.pointInRect(new Vector(mouseLoc.x + cameraLoc.x - S_WIDTH/2, mouseLoc.y + cameraLoc.y - S_HEIGHT/2),
						new Vector(entities.get(i).location.x - entities.get(i).dimension.x/2,
						entities.get(i).location.y - entities.get(i).dimension.y/2),
						new Vector(entities.get(i).location.x + entities.get(i).dimension.x/2,
						entities.get(i).location.y + entities.get(i).dimension.y/2 + 1)))
					entities.get(i).click();
		if(!StageEvent.FREEZE_ENTITIES)
		{
			entities.tick();
			entities.cleanUp();
		}
		
		if(StageEvent.CUT_SCENE)
			StageEvent.actors.tick();
		
		if(eventHandler.hasEvent("key-press_esc") || eventHandler.hasEvent("mouse-click_1") || eventHandler.hasEvent("mouse-drag_1") &&
				!eventQueue.hasEvent("panel-press"))
			selectedEntities.clear();
		if(eventHandler.hasEvent("mouse-press_1") && !eventQueue.hasEvent("panel-press"))
			selectLocStart = ((Vector) eventHandler.getData("mouse-press_1").get(0)).clone();
		if(eventHandler.hasEvent("mouse-release_1") && selectLocStart != null)
		{
			Vector selectLocEnd = (Vector) eventHandler.getData("mouse-release_1").get(0);
			for(int i = 0; i < entities.size(); i++)
				if(Calc.rectOverlapsRect(new Vector(selectLocStart.x-S_WIDTH/2+cameraLoc.x, selectLocStart.y - S_HEIGHT/2 + cameraLoc.y),
						new Vector(selectLocEnd.x - S_WIDTH/2 + cameraLoc.x, selectLocEnd.y - S_HEIGHT/2 + cameraLoc.y),
						new Vector(entities.get(i).location.x - entities.get(i).dimension.x/2,
								entities.get(i).location.y - entities.get(i).dimension.y/2),
						new Vector(entities.get(i).location.x + entities.get(i).dimension.x/2,
								entities.get(i).location.y + entities.get(i).dimension.y/2)))
					selectedEntities.add(entities.get(i));
			selectLocStart = null;
		}

		eventHandler.tick();
		eventQueue.transferTo(eventHandler);
		
		if(!StageEvent.CAMERA_GRAB && player != null)
			cameraLoc.doDelta(new Vector((player.location.x - cameraLoc.x)/8, (player.location.y - cameraLoc.y)/8));

		step++;
	}
	
	public void paintComponent(Graphics gr)
	{
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;

		if(!StageEvent.CUT_SCENE)
		{
			for(int x = -1; x < S_WIDTH / background.getWidth() + 2; x++)
				for(int y = -1; y < S_HEIGHT / background.getHeight() + 2; y++)
					g.drawImage(background, x * background.getWidth() - (int)cameraLoc.x % background.getWidth(),
							y * background.getHeight() - (int)cameraLoc.y % background.getHeight(), null);
	
			entities.paint(g);
			
			if(selectLocStart != null)
			{
				g.setColor(Color.cyan);
				g.drawRect((int)(selectLocStart.x < mouseLoc.x ? selectLocStart.x : mouseLoc.x),
						(int)(selectLocStart.y < mouseLoc.y ? selectLocStart.y : mouseLoc.y),
						(int)((selectLocStart.x < mouseLoc.x) ? (mouseLoc.x - selectLocStart.x) : (selectLocStart.x - mouseLoc.x)),
						(int)((selectLocStart.y < mouseLoc.y) ? (mouseLoc.y - selectLocStart.y) : (selectLocStart.y - mouseLoc.y)));
			}
			
			if(eventHandler.hasEvent("key-down_ "))
			{
				g.setColor(Color.orange);
				g.drawOval((int)(mouseLoc.x-WHISTLE_RANGE), (int)(mouseLoc.y-WHISTLE_RANGE), WHISTLE_RANGE*2, WHISTLE_RANGE*2);
			}
		}
		else
		{
			g.setColor(Color.black);
			g.fillRect(0, 0, S_WIDTH, S_HEIGHT);
			StageEvent.actors.paint(g);
		}

		if(StageEvent.DARKEN_SCREEN)
		{
			g.setColor(new Color(0, 0, 0, 128));
			g.fillRect(0, 0, S_WIDTH, S_HEIGHT);
		}
		
		for(int i = 0; i < openPanels.size(); i++)
			openPanels.get(i).paint(g);
	}
}

//FIXME event concurrent mod
//FIXME whistling too strong, limit to more scenarios (StageEvents, etc)
//FIXME selecting too strong, limit to more scenarios (StageEvents, etc)

//TODO Inventory interaction
//TODO Control constants
//TODO Status panel
//TODO Rock
//TODO Enemy
//TODO Food/plants
//TODO Life crystal
/*TODO Graphics
 */
