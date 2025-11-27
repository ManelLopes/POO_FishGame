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

		System.out.println("peixe esmagado(grande ou pequeno)");
		return (isCrushed(SmallFish.getInstance()) || isCrushed(BigFish.getInstance()));

	}

	public boolean isCrushed(GameCharacter fish) {

		System.out.println("esmagou");
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
					restartLevel();
					return;
				}

			} else {
				Room bfRoom = BigFish.getInstance().getRoom();
				bfRoom.pushObject(BigFish.getInstance(), dir);

				BigFish.getInstance().move(BigFish.getInstance(), dir);

				if (isAnyFishCrushed()) {
					restartLevel();
					return;
				}
			}

			Point2D pos = BigFish.getInstance().getPosition();

			for (GameObject o : currentRoom.getObjects()) {
				if (o.getPosition().equals(pos) && o instanceof objects.Trap) {
					System.out.println("Game Over");
					restartLevel();
					return;
				}
			}

		}
		int t = ImageGUI.getInstance().getTicks();
		while (lastTickProcessed < t) {
			processTick();
			currentRoom.applyGravity();
			if (isAnyFishCrushed()) {
				restartLevel();
				return;
			}

		}

		
		ImageGUI.getInstance().update();
	}

	private void processTick() {
		lastTickProcessed++;
		//meter estatistica, bomba rebentar, partir tronco, 2 ancoras so matam qd caem as 2
		//virar peixe quando clicamos esquerda ou direita, meter imagem do sangue
		//meter nivel novo
		//parede com buraco so e atravessada pela caneca
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
