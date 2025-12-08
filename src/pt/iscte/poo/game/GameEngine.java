package pt.iscte.poo.game;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
	private int gameTicks = 0;
	private ArrayList<String> roomOrder = new ArrayList<>();
	private int currLevelIndex = 0;
	private boolean gameFinished = false;
	private int moveCount = 0;
	private int[] bestTimes = new int[10];
	private int[] bestMoves = new int[10];
	private boolean highScoresLoaded = false;

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
		Arrays.sort(files, (a, b) -> a.getName().compareTo(b.getName()));

		for (File f : files) {
			rooms.put(f.getName(), Room.readRoom(f, this));
			roomOrder.add(f.getName());
		}

		currLevelIndex = 0;
		currentRoom = rooms.get(roomOrder.get(currLevelIndex));
	}

	public boolean isAnyFishCrushed() {// verifica se algum peixe está esmagado

		return (isCrushed(SmallFish.getInstance()) || isCrushed(BigFish.getInstance()));

	}

	public boolean isCrushed(GameCharacter fish) {// verifica se o peixe esta esmagado

		return currentRoom.someFishIsCrushed();

	}

	@Override
	public void update(Observed source) {// trata de todas as mudancas que ha no tabuleiro a cada tick
		if (gameFinished)
			return;

		if (ImageGUI.getInstance().wasKeyPressed()) {
			int k = ImageGUI.getInstance().keyPressed();

			if (k == 32) {
				SmallFish.switchFish();
				return;
			}

			if (k == 82) {
				if (gameFinished) {
					restartGame();
				} else {
					restartLevel();
				}
				return;
			}

			Vector2D dir = Direction.directionFor(k).asVector();

			if (SmallFish.isActive()) {
				Room sfRoom = SmallFish.getInstance().getRoom();
				sfRoom.pushObject(SmallFish.getInstance(), dir);
				SmallFish.getInstance().move(SmallFish.getInstance(), dir);
				sfRoom.krabsMove();
				currentRoom.krabRules();

				moveCount++;

				if (isAnyFishCrushed()) {
					gameOver();
					return;
				}

			} else {
				Room bfRoom = BigFish.getInstance().getRoom();
				bfRoom.pushObject(BigFish.getInstance(), dir);

				BigFish.getInstance().move(BigFish.getInstance(), dir);
				bfRoom.krabsMove();
				currentRoom.krabRules();

				moveCount++;

				if (isAnyFishCrushed()) {
					gameOver();
					return;
				}
			}

		}

		int t = ImageGUI.getInstance().getTicks();

		while (lastTickProcessed < t) {
			processTick();
			System.out.println(t);
			currentRoom.applyGravity();

			currentRoom.krabRules();
			if (isAnyFishCrushed()) {
				gameOver();
				return;
			}

		}

		if (isAnyFishCrushed()) {
			gameOver();
			return;
		}

		if (!currentRoom.getObjects().contains(BigFish.getInstance())
				|| !currentRoom.getObjects().contains(SmallFish.getInstance())) {
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
				objsToRemove.add(o);
			}
		}

		System.out.println("Crush encontrados: " + objsToRemove.size());
		for (GameObject o : objsToRemove) {
			System.out.println("removido" + o);
			currentRoom.removeObject(o);
		}

		if (currentRoom.bothFishesOut()) {
			nextLevel();
			return;
		}

		ImageGUI.getInstance().setStatusMessage(
				"Nível: " + currentRoom.getName() + "  | Jogadas: " + moveCount + "  | Ticks: " + gameTicks);

		ImageGUI.getInstance().update();

	}

	private void processTick() {

		lastTickProcessed++;
		gameTicks++;

	}

	public void updateGUI() {
		if (currentRoom != null) {
			ImageGUI.getInstance().clearImages();
			ImageGUI.getInstance().addImages(currentRoom.getObjects());
		}
	}

	private void restartLevel() {// reinicia o nivel

		String roomName = currentRoom.getName();
		File f = new File("./rooms/" + roomName);

		Room newRoom = Room.readRoom(f, this);

		currentRoom = newRoom;

		SmallFish.getInstance().setRoom(newRoom);
		BigFish.getInstance().setRoom(newRoom);
		BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());
		SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());

		lastTickProcessed = ImageGUI.getInstance().getTicks();

		updateGUI();

		System.out.println("Reiniciado nível: " + roomName);
	}

	private void nextLevel() {// carrega o proximo nivel

		currLevelIndex++;

		if (currLevelIndex >= roomOrder.size()) {
			initialHighScores();
			registerHighScores();
			System.out.println("HIGHSCORES STRING:\n" + formatHighscores());
			ImageGUI.getInstance().showMessage("Highscores", formatHighscores());
			gameFinished = true;
			restartGame();
			return;
		}

		String nextRoomName = roomOrder.get(currLevelIndex);
		currentRoom = rooms.get(nextRoomName);//usa o hashMap

		SmallFish.getInstance().setRoom(currentRoom);
		BigFish.getInstance().setRoom(currentRoom);

		BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());
		SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());

		updateGUI();
	}

	private void gameOver() {// termina o jogo
		ImageGUI.getInstance().showMessage("Game Over", "Game Over");
		restartLevel();
	}

	private void restartGame() {// reinicia o jogo depois de terminar os niveis todos

		if (!gameFinished) {
			return;
		}

		String roomName = "room0.txt";
		File f = new File("./rooms/" + roomName);

		Room newRoom = Room.readRoom(f, this);
		currentRoom = newRoom;

		currLevelIndex = 0;

		SmallFish.getInstance().setRoom(newRoom);
		BigFish.getInstance().setRoom(newRoom);
		BigFish.getInstance().setPosition(currentRoom.getBigFishStartingPosition());
		SmallFish.getInstance().setPosition(currentRoom.getSmallFishStartingPosition());

		lastTickProcessed = ImageGUI.getInstance().getTicks();
		gameTicks = 0;
		moveCount = 0;
		gameFinished = false;

		updateGUI();

		System.out.println("Reiniciado nível: " + roomName);

	}

	private void initialHighScores() {// regista os highScores iniciais que sao valores maximos que é para puderem
										// comparar com os nossos tempos

		if (highScoresLoaded) {
			return;
		}

		highScoresLoaded = true;

		for (int i = 0; i < 10; i++) {
			bestTimes[i] = Integer.MAX_VALUE;
			bestMoves[i] = Integer.MAX_VALUE;
		}

	}

	private void registerHighScores() {// regista os highScores de cada jogo

		System.out.println("REGISTO SCORE: gameTicks=" + gameTicks + " moves=" + moveCount);
		for (int i = 0; i < 10; i++) {
			System.out.println("ANTES bestTimes[" + i + "]=" + bestTimes[i]);
			if (gameTicks < bestTimes[i]) {
				for (int j = 9; j > i; j--) {//deixar a posicao necessara vazia
					bestTimes[j] = bestTimes[j - 1];
					bestMoves[j] = bestMoves[j - 1];
				}
				bestTimes[i] = gameTicks;
				bestMoves[i] = moveCount;
				break;
			}
		}

	}

	private String formatHighscores() {// exibe o formato desejado na tabela de highScores
		StringBuilder sb = new StringBuilder();
		sb.append(" #  Tempo (ticks)   Movimentos\n");
		for (int i = 0; i < 10; i++) {
			if (bestTimes[i] == Integer.MAX_VALUE)
				break;
			sb.append(String.format("%2d  %12d   %9d\n", i + 1, bestTimes[i], bestMoves[i]));
		}
		return sb.toString();
	}

}
