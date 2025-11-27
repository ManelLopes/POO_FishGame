package pt.iscte.poo.game;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import objects.SmallFish;
import objects.BigFish;
import objects.GameCharacter;
import objects.GameObject;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.observer.Observed;
import pt.iscte.poo.observer.Observer;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class GameEngine implements Observer {

	private Map<String, Room> rooms;
	private Room currentRoom;
	private int lastTickProcessed = 0;

	public GameEngine() {
		rooms = new HashMap<String, Room>();
		loadGame();
		currentRoom = rooms.get("room0.txt");
		updateGUI();
		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);
	}

	private void loadGame() {
		File[] files = new File("./rooms").listFiles();
		for (File f : files) {
			rooms.put(f.getName(), Room.readRoom(f, this));
		}
	}
	
	public boolean isAnyFishCrushed() {		
		
		System.out.println("peixe grande esmagado");
		return (isCrushed(SmallFish.getInstance()) || isCrushed(BigFish.getInstance()));
			

	}
	
	public boolean isCrushed(GameCharacter fish) {
		
		return !fish.getRoom().checkObjectsOnTop(fish);
		
	}

//	@Override
//	public void update(Observed source) {
//
//		if (ImageGUI.getInstance().wasKeyPressed()) {
//			int k = ImageGUI.getInstance().keyPressed();
//
//			if (k == 32) { // Codigo ASCII do espaço seu burro
//				SmallFish.switchFish();
//				return;
//			}
//
//			if (k == 82) { // código ASCII para 'R'
//				restartLevel();
//				return;
//			}
//
//			Vector2D dir = Direction.directionFor(k).asVector();
//			
//			if (SmallFish.isActive()) {
//				//Vector2D dir = Direction.directionFor(k).asVector();
//				Room sfRoom = SmallFish.getInstance().getRoom();
//				sfRoom.pushObject(SmallFish.getInstance(), dir);
//				SmallFish.getInstance().move(SmallFish.getInstance(), dir);
//				
//				if (someFishIsCrushed()) {
//					restartLevel();
//					return;
//				}
////				if (currentRoom.someFishIsCrushed()) {
////					restartLevel();
////					return;
////				}
////				return;
//			}else {
//				Room bfRoom = BigFish.getInstance().getRoom();
//				bfRoom.pushObject(BigFish.getInstance(), dir);
//
//				BigFish.getInstance().move(BigFish.getInstance(), dir);
//				
//				if (someFishIsCrushed()) {
//					restartLevel();
//					return;
//				}
//			}
//
//				
//				
//
//				Point2D pos = BigFish.getInstance().getPosition();
//
//				for (GameObject o : currentRoom.getObjects()) {
//					if (o.getPosition().equals(pos) && o instanceof objects.Trap) {
//						System.out.println("Game Over");
//						restartLevel();
//						return;
//					}
//				}
//			
//
//			
//
//		}
//		int t = ImageGUI.getInstance().getTicks();
//		while (lastTickProcessed < t) {
//			processTick();
//			currentRoom.applyGravity();
//
//		}
//		
//		if (someFishIsCrushed()) {
//			    restartLevel();
//			    return;
//			}
//		ImageGUI.getInstance().update();
//	}
	
	@Override
	public void update(Observed source) {
	    ImageGUI gui = ImageGUI.getInstance();

	    // ---------- TECLAS ----------
	    if (gui.wasKeyPressed()) {
	        int k = gui.keyPressed();

	        // espaço -> troca peixe activo
	        if (k == ' ') {
	            SmallFish.switchFish();
	            return;
	        }

	        // 'R' -> restart
	        if (k == 'R') {
	            restartLevel();
	            return;
	        }

	        Direction d = Direction.directionFor(k);
	        if (d != null) {
	            Vector2D dir = d.asVector();

	            // mexe SÓ o peixe activo
	            if (SmallFish.isActive()) {
	                Room sfRoom = SmallFish.getInstance().getRoom();
	                sfRoom.pushObject(SmallFish.getInstance(), dir);
	                SmallFish.getInstance().move(SmallFish.getInstance(), dir);
	            } else {
	                Room bfRoom = BigFish.getInstance().getRoom();
	                bfRoom.pushObject(BigFish.getInstance(), dir);
	                BigFish.getInstance().move(BigFish.getInstance(), dir);
	            }

	            // depois de QUALQUER movimento: verificar os DOIS peixes
	            if (isAnyFishCrushed()) {
	                restartLevel();
	                return;
	            }

	            // trap só depende da posição do BigFish
	            Point2D pos = BigFish.getInstance().getPosition();
	            for (GameObject o : currentRoom.getObjects()) {
	                if (o.getPosition().equals(pos) && o instanceof objects.Trap) {
	                    System.out.println("Game Over");
	                    restartLevel();
	                    return;
	                }
	            }
	        }
	    }

	    // ---------- TICKS / GRAVIDADE ----------
	    int t = gui.getTicks();
	    while (lastTickProcessed < t) {
	        lastTickProcessed++;          // MUITO IMPORTANTE!

	        processTick();
	        currentRoom.applyGravity();

	        // depois da gravidade: verificar de novo os DOIS
	        if (isAnyFishCrushed()) {
	            restartLevel();
	            return;
	        }
	    }

	    gui.update();
	}

	private void processTick() {
		lastTickProcessed++;
	}

	public void updateGUI() {
		if (currentRoom != null) {
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
