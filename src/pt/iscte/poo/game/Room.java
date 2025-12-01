package pt.iscte.poo.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import objects.Water;
import objects.Anchor;
import objects.BigFish;
import objects.Bomb;
import objects.Buoy;
import objects.Cup;
import objects.GameCharacter;
import objects.GameObject;
import objects.HoledWall;
import objects.Krab;
import objects.SmallFish;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.Scanner;
import java.io.FileNotFoundException;
import objects.Wall;
import objects.SteelHorizontal;
import objects.Stone;
import objects.Trap;
import objects.Trunk;

public class Room {

	private List<GameObject> objects;
	private String roomName;
	private GameEngine engine;
	private Point2D smallFishStartingPosition;
	private Point2D bigFishStartingPosition;

	public Room() {
		objects = new ArrayList<GameObject>();
	}

	private void setName(String name) {
		roomName = name;
	}

	public String getName() {
		return roomName;
	}

	private void setEngine(GameEngine engine) {
		this.engine = engine;
	}

	public void addObject(GameObject obj) {
		objects.add(obj);
		engine.updateGUI();
	}

	public void removeObject(GameObject obj) {
		objects.remove(obj);
		engine.updateGUI();
	}

	public List<GameObject> getObjects() {
		return objects;
	}

	public void setSmallFishStartingPosition(Point2D heroStartingPosition) {
		this.smallFishStartingPosition = heroStartingPosition;
	}

	public Point2D getSmallFishStartingPosition() {
		return smallFishStartingPosition;
	}

	public void setBigFishStartingPosition(Point2D heroStartingPosition) {
		this.bigFishStartingPosition = heroStartingPosition;
	}

	public Point2D getBigFishStartingPosition() {
		return bigFishStartingPosition;
	}

	public static Room readRoom(File f, GameEngine engine) {
		Room r = new Room();
		r.setEngine(engine);
		r.setName(f.getName());

		List<String> linhas = new ArrayList<>();

		try (Scanner sc = new Scanner(f)) {
			while (sc.hasNextLine()) {
				linhas.add(sc.nextLine());
			}

		} catch (FileNotFoundException e) {

			System.err.println("Ficheiro não encontrado");
			return null;
		}

		int y = 0;

		for (String linha : linhas) { // todas as linhas que guardamos

			for (int x = 0; x < linha.length(); x++) { // vamos ver os caracteres todos da linha
				char c = linha.charAt(x);
				Point2D pos = new Point2D(x, y);

				GameObject water = new Water(r); // pomos agua em todas as posicoes do mapa
				water.setPosition(pos);
				r.addObject(water);

				if (c == 'W') {
					GameObject w = new Wall(r);
					w.setPosition(pos);
					r.addObject(w);
				} else if (c == 'B') {
					GameObject bf = BigFish.getInstance();
					bf.setPosition(pos);
					r.addObject(bf);
					r.setBigFishStartingPosition(pos);
				} else if (c == 'S') {
					GameObject sf = SmallFish.getInstance();
					sf.setPosition(pos);
					r.addObject(sf);
					r.setSmallFishStartingPosition(pos);
				} else if (c == 'H') {
					GameObject sh = new SteelHorizontal(r);
					sh.setPosition(pos);
					r.addObject(sh);
				} else if (c == 'A') {
					GameObject anchor = new Anchor(r);
					anchor.setPosition(pos);
					r.addObject(anchor);
				} else if (c == 'C') {
					GameObject cup = new Cup(r);
					cup.setPosition(pos);
					r.addObject(cup);
				} else if (c == 'b') {
					GameObject bomb = new Bomb(r);
					bomb.setPosition(pos);
					;
					r.addObject(bomb);
				} else if (c == 'X') {
					GameObject holedWall = new HoledWall(r);
					holedWall.setPosition(pos);
					r.addObject(holedWall);
				} else if (c == 'T') {
					GameObject trap = new Trap(r);
					trap.setPosition(pos);
					r.addObject(trap);
				} else if (c == 'R') {
					GameObject stone = new Stone(r);
					stone.setPosition(pos);
					r.addObject(stone);
				} else if (c == 'Y') {
					GameObject trunk = new Trunk(r);
					trunk.setPosition(pos);
					r.addObject(trunk);
				} else if (c == 'V') {
					GameObject buoy = new Buoy(r);
					buoy.setPosition(pos);
					r.addObject(buoy);
				} else if (c == 'k') {
					GameObject krab = new Krab(r);
					krab.setPosition(pos);
					r.addObject(krab);

				}
			}

			y++; // vamos ler as linhas do ficheiro
		}

		return r; // da return no room

	}

	public boolean pushObject(GameCharacter fish, Vector2D dir) {
		Point2D fishPos = fish.getPosition();
		Point2D curPos = fishPos.plus(dir);

		// 1) Construir a cadeia de objetos consecutivos à frente do peixe
		List<GameObject> objectChain = new ArrayList<>();

		while (true) {
			GameObject obj = null;
			for (GameObject o : objects) {
				if (o.getPosition().equals(curPos) && o.getLayer() == 1 && o.isMovable()) {
					obj = o;
					break;
				}
			}
			if (obj == null) {
				break; // acabou a cadeia
			}

			if (objectChain.isEmpty() && obj instanceof Buoy && dir.getX() == 0 && dir.getY() == 1
					&& !(fish instanceof BigFish)) {
				return false;
			}
			objectChain.add(obj);
			curPos = curPos.plus(dir); // próxima casa na mesma direção
		}

		// Se não havia nenhum objeto à frente
		if (objectChain.isEmpty()) {
			System.out.println("nao tem objeto no destino");
			return false;
		}

		if (fish instanceof BigFish && dir.getY() < 0 && objectChain.size() > 1) {
			System.out.println("bigFish nao pode empurrar mais que 1 objeto para cima");
			return false;
		}

		// 2) Regra: SmallFish só pode empurrar 1 objeto
		if (fish instanceof SmallFish && objectChain.size() > 1) {
			System.out.println("pequeno nao pode empurrar mais que 1 objeto");
			return false;
		}

		// 3) Verificar se todos na cadeia são empurráveis para o peixe atual
		for (GameObject o : objectChain) {
			if (!o.isMovable()) {
				System.out.println("objeto nao é movivel: " + o);
				return false;
			}
			if (fish instanceof SmallFish && o.isHeavy()) {
				System.out.println("pequeno nao move obj pesado: " + o);
				return false;
			}
		}

		// 4) Verificar se a posição a seguir ao último da cadeia é válida
		GameObject last = objectChain.get(objectChain.size() - 1);
		Point2D lastNext = last.getPosition().plus(dir);

		for (GameObject o : objects) {
			if (o.getPosition().equals(lastNext) && !(o instanceof Water)) {
				// se o último não conseguir atravessar este objeto, bloqueia
				if (!last.goesTrough(o)) {
					System.out.println("cadeia bloqueada no destino final");
					return false;
				}
			}
		}

		// 5) Movimento: empurrar de trás para a frente
		for (int i = objectChain.size() - 1; i >= 0; i--) {
			GameObject obj = objectChain.get(i);
			Point2D newPos = obj.getPosition().plus(dir);
			obj.setPosition(newPos);
		}

		System.out.println(objectChain.size() + " objetos movidos");
		return true;
	}

	private boolean applyBuoyPhysics(GameObject b) { // ta feito crlh

		Point2D pos = b.getPosition();
		Point2D up = pos.plus(new Vector2D(0, -1));
		Point2D posBelow = pos.plus(new Vector2D(0, 1));

		// ver o que está mesmo por cima da boia
		GameObject aboveBuoy = getTopEntityAt(up);
		boolean hasMovableAbove = (aboveBuoy != null && aboveBuoy.isMovable());

		if (hasMovableAbove && canObjMoveTo(b, posBelow)) {
			// tem objeto em cima e afunda
			b.setPosition(posBelow);
			return true;
		} else if (!hasMovableAbove && canObjMoveTo(b, up)) {
			// não tem nada em cima e flutua
			b.setPosition(up);
			return true;
		}

		return false;
	}

	public boolean checkObjectsOnTop(GameCharacter fish) {

		Point2D pos = fish.getPosition();
		int heavy = 0;
		int light = 0;

		// começamos logo acima do peixe
		Point2D upperPos = pos.plus(new Vector2D(0, -1));

		while (true) {

			GameObject objOnTop = null;

			// procurar objeto EXACTAMENTE em upperPos
			for (GameObject o : objects) {
				if (o.getPosition().equals(upperPos)) {
					objOnTop = o;
					break;
				}
			}

			// se não há nada nessa posição acabou a pilha
			if (objOnTop == null) {
				break;
			}

			// se não é movível (parede, aço, etc.) bloqueia, paramos aqui
			if (!objOnTop.isMovable()) {
				break;
			}

			// contar leves/pesados
			if (objOnTop.isHeavy()) {
				heavy++;
			} else {
				light++;
			}

			// vamos ver a próxima posição acima
			upperPos = upperPos.plus(new Vector2D(0, -1));

		}

		// ---------- REGRAS ----------

		// BIG FISH: morre se tiver 2 ou mais objetos em cima (quaisquer)
		if (fish instanceof BigFish) {
			if (heavy + light >= 2) {
				return false; // esmagado
			}
			return true; // seguro
		}

		// SMALL FISH: morre se tiver 1 pesado OU 2 leves
		if (fish instanceof SmallFish) {
			if (heavy >= 1) {
				return false; // algum pesado
			}
			if (light >= 2) {
				return false; // 2 ou mais leves
			}
			return true; // caso contrário, está seguro
		}

		// outros objetos (se houver) nunca são esmagados

		return true;
	}

	public boolean someFishIsCrushed() {

		if (!checkObjectsOnTop(BigFish.getInstance())) {
			return true;
		}
		if (!checkObjectsOnTop(SmallFish.getInstance())) {
			return true;
		}

		return false;

	}

	public boolean checkObjectsOnTopOfObjects(GameObject obj) {

		if (!(obj instanceof Trunk)) {
			return false;
		}

		// Só nos interessa para troncos; se quiseres, podes alargar depois
		if (!(obj.isHeavy())) {
			return false;
		}

		Point2D pos = obj.getPosition();
		int heavy = 0;

		// começamos logo acima do tronco
		Point2D upperPos = pos.plus(new Vector2D(0, -1));

		while (true) {

			GameObject objOnTop = null;

			// procurar objeto EXACTAMENTE em upperPos
			for (GameObject o : objects) {
				if (o.getPosition().equals(upperPos)) {
					objOnTop = o;
					break;
				}
			}

			// se não há nada nessa posição acabou a pilha
			if (objOnTop == null) {
				break;
			}

			// se não é movível (parede, aço, etc.) bloqueia, paramos aqui
			if (!objOnTop.isMovable()) {
				break;
			}

			// contar leves/pesados
			if (objOnTop.isHeavy()) {
				heavy++;
			}

			// vamos ver a próxima posição acima
			upperPos = upperPos.plus(new Vector2D(0, -1));
		}

		// ---------- REGRAS PARA O TRONCO ----------
		// Exemplo: tronco parte se tiver pelo menos 1 pesado em cima
		if (heavy >= 1) {
			return true; // tronco esmagado
		}
		return false;
	}

	public void checkAdjacentObjectsToBomb(Bomb bomb) {

		Point2D bombPos = bomb.getPosition();
		System.out.println("Bomb a explodir em " + bombPos);

		// Só chamar checkAdjacentObjectsToBomb(bomb) no GameEngine,
		// depois do ciclo em que já fizeste currentRoom.applyGravity() para todos os
		// ticks.

		Point2D belowPos = bombPos.plus(new Vector2D(0, 1));

		boolean hasWaterBelow = false;
		boolean hasNonWaterBelow = false;

		for (GameObject o : objects) {
			if (!o.getPosition().equals(belowPos))
				continue;

			if (o instanceof Water)
				hasWaterBelow = true;
			else
				hasNonWaterBelow = true;
		}

		if (hasWaterBelow && !hasNonWaterBelow) {
			return;
		}

		Point2D left = bombPos.plus(new Vector2D(-1, 0));
		Point2D right = bombPos.plus(new Vector2D(1, 0));
		Point2D top = bombPos.plus(new Vector2D(0, -1));
		Point2D below = bombPos.plus(new Vector2D(0, 1));

		for (GameObject o : new ArrayList<>(objects)) {
			if (o.getPosition().equals(left) && !(o instanceof Water))
				removeObject(o);
			if (o.getPosition().equals(right) && !(o instanceof Water))
				removeObject(o);
			if (o.getPosition().equals(top) && !(o instanceof Water))
				removeObject(o);
			if (o.getPosition().equals(below) && !(o instanceof Water))
				removeObject(o);
		}

		removeObject(bomb);

	}

	public boolean isObjectCrushed(GameObject obj) {
		if (!(obj instanceof Trunk))
			return false;

		System.out.println("A testar tronco em " + obj.getPosition());

		Point2D pos = obj.getPosition();

		for (GameObject o : objects) {
			if (o == obj)
				continue;

			if (o.getPosition().equals(pos)) {
				System.out.println("  Em cima do tronco: " + o + " heavy=" + o.isHeavy());
				if (o.isHeavy()) {
					return true; // há um pesado na mesma casa do tronco
				}
			}
		}
		return false;
	}

	public GameObject getTopEntityAt(Point2D pos) {
		GameObject top = null;

		for (GameObject obj : objects) {
			if (obj.getPosition().equals(pos) && obj.getLayer() > 0) { // ignora água
				if (top == null || obj.getLayer() > top.getLayer()) {
					top = obj;
				}
			}
		}

		return top; // pode ser a bomba, o peixe, etc., nunca a água
	}

	public void applyGravity() {

		for (GameObject o : objects) {

			if (o instanceof Buoy) {
				applyBuoyPhysics(o);
			}

			if (o instanceof Bomb) {
				Bomb b = (Bomb) o;
				Point2D posBelow = b.getPosition().plus(new Vector2D(0, 1));
				if(b.hasGravity() && canObjMoveTo(b, posBelow)) {
					b.setPosition(posBelow);
					checkAdjacentObjectsToBomb(b);
				}
				continue;

			}

			Point2D posBelow = o.getPosition().plus(new Vector2D(0, 1));
			if (o.hasGravity() && canObjMoveTo(o, posBelow)) {
				o.setPosition(posBelow);
			}
		}
	}

	public boolean canMoveTo(GameCharacter fish, Point2D pos) {

		for (GameObject o : objects) {
			if (o.getPosition().equals(pos)) {
				if (o instanceof Wall || o instanceof SteelHorizontal || (SmallFish.isActive() && o.isHeavy())
						|| (!(o instanceof Water) && !(o instanceof HoledWall) && !(o instanceof Trap))) {
					return false; // não pode passar
				}
				if (o instanceof HoledWall) {
					return fish instanceof SmallFish;
				}

			}
		}

		return true; // se não encontrou nada sólido, pode ir
	}

	public boolean canObjMoveTo(GameObject obj, Point2D pos) {
		for (GameObject o : objects) {
			if (o.getPosition().equals(pos)) {
				if ((obj.isHeavy() && o instanceof HoledWall) || o instanceof Trap || o instanceof Wall
						|| o instanceof SteelHorizontal) {
					return false;
				}
				if (!(o instanceof Water) && !(o instanceof HoledWall)) {
					return false;
				}
				if ((!(obj instanceof Cup) && o instanceof HoledWall)) {
					return false;
				}
			}
		}
		return true;
	}

}