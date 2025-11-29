package pt.iscte.poo.game;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import objects.SmallFish;
import objects.BigFish;
import objects.Bomb;
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

		return (isCrushed(SmallFish.getInstance()) || isCrushed(BigFish.getInstance()));

	}

	public boolean isCrushed(GameCharacter fish) {

		return !fish.getRoom().checkObjectsOnTop(fish);

	}

	@Override
	public void update(Observed source) {

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

				if (isAnyFishCrushed()) {
					restartLevel();// show
					return;
				}

			} else {
				Room bfRoom = BigFish.getInstance().getRoom();
				bfRoom.pushObject(BigFish.getInstance(), dir);

				BigFish.getInstance().move(BigFish.getInstance(), dir);

				if (isAnyFishCrushed()) {
					restartLevel();// show
					return;
				}
			}

		}
		int t = ImageGUI.getInstance().getTicks();
		while (lastTickProcessed < t) {
			processTick();
			System.out.println(t);// meter a fazer show
			currentRoom.applyGravity();
			if (isAnyFishCrushed()) {
				restartLevel();
				return;
			}

		}

		Point2D pos = BigFish.getInstance().getPosition();

		ArrayList<GameObject> objsToRemove = new ArrayList<>();

		for (GameObject o : currentRoom.getObjects()) {

			if (o.getPosition().equals(pos) && o instanceof objects.Trap) {
				System.out.println("Game Over");
				restartLevel();
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

		ImageGUI.getInstance().update();
	}
	
//	@Override
//	public void update(Observed source) {
//
//	    if (ImageGUI.getInstance().wasKeyPressed()) {
//	        int k = ImageGUI.getInstance().keyPressed();
//
//	        if (k == 32) { // espaço
//	            SmallFish.switchFish();
//	            return;
//	        }
//
//	        if (k == 82) { // 'R'
//	            restartLevel();
//	            return;
//	        }
//
//	        Vector2D dir = Direction.directionFor(k).asVector();
//
//	        if (SmallFish.isActive()) {
//	            Room sfRoom = SmallFish.getInstance().getRoom();
//	            sfRoom.pushObject(SmallFish.getInstance(), dir);
//	            SmallFish.getInstance().move(SmallFish.getInstance(), dir);
//
//	            if (isAnyFishCrushed()) {
//	                restartLevel();
//	                return;
//	            }
//
//	        } else {
//	            Room bfRoom = BigFish.getInstance().getRoom();
//	            bfRoom.pushObject(BigFish.getInstance(), dir);
//	            BigFish.getInstance().move(BigFish.getInstance(), dir);
//
//	            if (isAnyFishCrushed()) {
//	                restartLevel();
//	                return;
//	            }
//	        }
//	    }
//
//	    // -------- TICKS + GRAVIDADE --------
//	    int t = ImageGUI.getInstance().getTicks();
//	    while (lastTickProcessed < t) {
//	        processTick();
//	        currentRoom.applyGravity();
//	        if (isAnyFishCrushed()) {
//	            restartLevel();
//	            return;
//	        }
//	    }
//
//	    // -------- BOMBA: explode quando afunda e encontra objeto sólido por baixo --------
//	    ArrayList<Bomb> bombsToExplode = new ArrayList<>();
//
//	    for (GameObject o : currentRoom.getObjects()) {
//	        if (!(o instanceof Bomb))
//	            continue;
//
//	        Bomb b = (Bomb) o;
//	        Point2D bp = b.getPosition();
//
//	        // célula imediatamente abaixo da bomba
//	        Point2D below = bp.plus(new Vector2D(0, 1));
//
//	        GameObject objBelow = null;
//	        for (GameObject g : currentRoom.getObjects()) {
//	            if (g.getPosition().equals(below)) {
//	                objBelow = g;
//	                break;
//	            }
//	        }
//
//	        // se não há nada por baixo, ainda pode cair mais noutro tick
//	        if (objBelow == null)
//	            continue;
//
//	        // se por baixo é água, ainda está a afundar → não explode
//	        if (objBelow instanceof objects.Water)
//	            continue;
//
//	        // se por baixo é peixe, não dispara explosão (regra do enunciado)
//	        if (objBelow instanceof BigFish || objBelow instanceof SmallFish)
//	            continue;
//
//	        // chegou aqui: afundou e encontrou um objeto sólido (não peixe, não água) → explode
//	        bombsToExplode.add(b);
//	    }
//
//	    // fazer explodir todas as bombas marcadas
//	    for (Bomb b : bombsToExplode) {
//	        currentRoom.checkAdjacentObjectsToBomb(b);
//	    }
//
//	    // depois da explosão, vê se algum peixe morreu (por peso/explosão)
//	    if (isAnyFishCrushed()) {
//	        restartLevel();
//	        return;
//	    }
//
//	    // -------- TRAP + TRONCOS ESMAGADOS --------
//	    Point2D pos = BigFish.getInstance().getPosition();
//
//	    ArrayList<GameObject> objsToRemove = new ArrayList<>();
//
//	    for (GameObject o : currentRoom.getObjects()) {
//
//	        if (o.getPosition().equals(pos) && o instanceof objects.Trap) {
//	            System.out.println("Game Over");
//	            restartLevel();
//	            return;
//	        }
//	        if (currentRoom.checkObjectsOnTopOfObjects(o)) {
//	            objsToRemove.add(o); // troncos esmagados
//	        }
//	    }
//
//	    System.out.println("Crush encontrados: " + objsToRemove.size());
//	    for (GameObject o : objsToRemove) {
//	        System.out.println("removido" + o);
//	        currentRoom.removeObject(o);
//	    }
//
//	    ImageGUI.getInstance().update();
//	}



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

		updateGUI();

		System.out.println("Reiniciado nível: " + roomName);
	}

}
