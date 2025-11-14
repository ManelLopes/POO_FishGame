package pt.iscte.poo.game;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import objects.SmallFish;
import objects.BigFish;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;

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
				
			
			BigFish.getInstance().move(Direction.directionFor(k).asVector());
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
