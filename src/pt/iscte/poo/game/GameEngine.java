package pt.iscte.poo.game;

import java.io.File;
import java.util.ArrayList;
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
	private ArrayList<String> roomOrder = new ArrayList<>();
	private int currLevelIndex = 0;
	private boolean gameFinished = false;
	


	public GameEngine() {
		rooms = new HashMap<String, Room>();
		loadGame();
		currentRoom = rooms.get("room0.txt");
		updateGUI();
		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());
	    SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());
	}

	private void loadGame() {
	    File[] files = new File("./rooms").listFiles();
	    java.util.Arrays.sort(files, (a, b) -> a.getName().compareTo(b.getName()));

	    for (File f : files) {
	        rooms.put(f.getName(), Room.readRoom(f, this));
	        roomOrder.add(f.getName());
	    }

	    currLevelIndex = 0;
	    currentRoom = rooms.get(roomOrder.get(currLevelIndex));
	}

	public boolean isAnyFishCrushed() {

		return (isCrushed(SmallFish.getInstance()) || isCrushed(BigFish.getInstance()));

	}

	public boolean isCrushed(GameCharacter fish) {

		return !fish.getRoom().checkObjectsOnTop(fish);

	}

	@Override
	public void update(Observed source) {
		if(gameFinished)
			return;

		if (ImageGUI.getInstance().wasKeyPressed()) {
			int k = ImageGUI.getInstance().keyPressed();

			if (k == 32) { // Codigo ASCII do espaço seu burro
				SmallFish.switchFish();
				return;
			}

			if (k == 82) { // código ASCII para 'R'
				restartLevel();
				return;
			}

			Vector2D dir = Direction.directionFor(k).asVector();

			if (SmallFish.isActive()) {
				Room sfRoom = SmallFish.getInstance().getRoom();
				sfRoom.pushObject(SmallFish.getInstance(), dir);
				SmallFish.getInstance().move(SmallFish.getInstance(), dir);
				sfRoom.krabsMove();
				currentRoom.krabRules();

				if (isAnyFishCrushed()) {
					gameOver();// show
					return;
				}

			} else {
				Room bfRoom = BigFish.getInstance().getRoom();
				bfRoom.pushObject(BigFish.getInstance(), dir);

				BigFish.getInstance().move(BigFish.getInstance(), dir);
				bfRoom.krabsMove();
				currentRoom.krabRules();

				if (isAnyFishCrushed()) {
					gameOver();// show
					return;
				}
			}

		}

		int t = ImageGUI.getInstance().getTicks();
		while (lastTickProcessed < t) {
			processTick();
			System.out.println(t);// meter a fazer show
			currentRoom.applyGravity();

			currentRoom.krabRules();
			if (isAnyFishCrushed()) {
				gameOver();
				return;
			}

		}

//		ArrayList<Bomb> bombsToCheck = new ArrayList<>();
//		for (GameObject o : currentRoom.getObjects()) {
//			if (o instanceof Bomb) {
//				bombsToCheck.add((Bomb) o);
//			}
//		}
//
//		for (Bomb b : bombsToCheck) {
//			currentRoom.checkAdjacentObjectsToBomb(b);
//		}

		if (isAnyFishCrushed()) {
			gameOver();
			return;
		}

		if (!currentRoom.getObjects().contains(BigFish.getInstance())
				|| !currentRoom.getObjects().contains(SmallFish.getInstance())) {
			// algum peixe deixou de existir na room → morreu
			gameOver();
			return;
		}

		Point2D pos = BigFish.getInstance().getPosition();

		ArrayList<GameObject> objsToRemove = new ArrayList<>();

		for (GameObject o : currentRoom.getObjects()) {

			if (o.getPosition().equals(pos) && o instanceof objects.Trap) {
				System.out.println("Game Over");
				gameOver();
				return;
			}
			if (currentRoom.checkObjectsOnTopOfObjects(o)) {
				objsToRemove.add(o); // aqui vão entrar os troncos
			}
		}

		System.out.println("Crush encontrados: " + objsToRemove.size());
		for (GameObject o : objsToRemove) {
			System.out.println("removido" + o);
			currentRoom.removeObject(o);
		}
		
		if(currentRoom.bothFishesOut()) {
			nextLevel();
			return;
		}

		ImageGUI.getInstance().update();
	}

	private void processTick() {

		lastTickProcessed++;
		// meter estatistica, bomba rebentar, 2 ancoras so matam qd caem
		// as 2
		// meter nivel novo
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
		BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());
	    SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());

		updateGUI();

		System.out.println("Reiniciado nível: " + roomName);
	}
	
	private void nextLevel() {
		
	    currLevelIndex++;
			
	    if (currLevelIndex >= roomOrder.size()) {
	        ImageGUI.getInstance().showMessage("Parabéns", "Nivel Concluído");
	        gameFinished = true;  // se já meteste esta flag
	        return;
	    }
			
	    String nextRoomName = roomOrder.get(currLevelIndex);
	    currentRoom = rooms.get(nextRoomName);

	    // dizer aos peixes em que room estão
	    SmallFish.getInstance().setRoom(currentRoom);
	    BigFish.getInstance().setRoom(currentRoom);

	    // 👇 reposicionar peixes nas coords de início desse room
	    BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());
	    SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());
	    

	    updateGUI();
	}


	private void gameOver() {
		ImageGUI.getInstance().showMessage("Game Over", "Game Over");
		restartLevel();
	}

}
