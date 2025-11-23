package pt.iscte.poo.game;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import objects.SmallFish;
import objects.BigFish;
import objects.GameObject;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class GameEngine implements Observer {
	
	private Map<String,Room> rooms;
	private Room currentRoom;
	private int lastTickProcessed = 0;
	
	public GameEngine() {
		rooms = new HashMap<String,Room>();
		loadGame();
		currentRoom = rooms.get("room0.txt");
		updateGUI();		
		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);
	}

	private void loadGame() {
		File[] files = new File("./rooms").listFiles();
		for(File f : files) {
			rooms.put(f.getName(),Room.readRoom(f,this));
		}
	}

	@Override
	public void update(Observed source) {

		if (ImageGUI.getInstance().wasKeyPressed()) {
			int k = ImageGUI.getInstance().keyPressed();
			
			if (k == 32) {  // Codigo ASCII do espaço seu burro
		        SmallFish.switchFish();
		        return;
		    }
			

			if (k == 82) {  // código ASCII para 'R'
			    restartLevel();
			    return;
			}
			
			if(SmallFish.isActive()==true) {
				SmallFish.getInstance().move(Direction.directionFor(k).asVector());
				return;
			}
				
			
			//BigFish.getInstance().move(Direction.directionFor(k).asVector());//tirei porque ele tava a mexer se 2 vezes
			
			Vector2D dir = Direction.directionFor(k).asVector();
			Point2D destination = BigFish.getInstance().getPosition().plus(dir);
			
			Room room = BigFish.getInstance().getRoom();
			boolean blocked = false;
			for(GameObject o: room.getObjects()) {
				if(o.getPosition().equals(destination) && o instanceof objects.HoledWall){
					blocked = true;
					break;
				}
			}
			if(!blocked) {
			    BigFish.getInstance().move(dir);
			    
			    Point2D pos = BigFish.getInstance().getPosition();
			    
			    for(GameObject o: currentRoom.getObjects()) {
			    	if(o.getPosition().equals(pos) && o instanceof objects.Trap) {
			    		System.out.println("Game Over");
			    		restartLevel();
			    		return;
			    	}
			    }
			}
				
			
		}
		int t = ImageGUI.getInstance().getTicks();
		while (lastTickProcessed < t) {
			processTick();
		}
		ImageGUI.getInstance().update();
	}

	private void processTick() {		
		lastTickProcessed++;
	}

	public void updateGUI() {
		if(currentRoom!=null) {
			ImageGUI.getInstance().clearImages();
			ImageGUI.getInstance().addImages(currentRoom.getObjects());
		}
	}
	
	private void restartLevel() {

	    String roomName = currentRoom.getName();  
	    File f = new File("./rooms/" + roomName);

	    
	    Room newRoom = Room.readRoom(f, this);

	 
	    currentRoom = newRoom;

	    SmallFish.getInstance().setRoom(newRoom);
	    BigFish.getInstance().setRoom(newRoom);

	    updateGUI();

	    System.out.println("Reiniciado nível: " + roomName);
	}
	
}
