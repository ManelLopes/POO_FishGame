package pt.iscte.poo.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import objects.Water;
import objects.Anchor;
import objects.BigFish;
import objects.Bomb;
import objects.Cup;
import objects.GameCharacter;
import objects.GameObject;
import objects.HoledWall;
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
				}

			}

			y++; // vamos ler as linhas do ficheiro
		}

		return r; // da return no room

	}

//	public boolean pushObject(GameCharacter fish, Vector2D dir) {
//	    Point2D fishPos = fish.getPosition();
//	    Point2D objPos = fishPos.plus(dir);
//
//	    // 1º objeto à frente do peixe
//	    GameObject obj = null;
//	    for (GameObject o : objects) {
//	        if (o.getPosition().equals(objPos) && o.getLayer() == 1) {
//	            obj = o;
//	            break;
//	        }
//	    }
//	    if (obj == null) {
//	        System.out.println("nao tem objeto no destino");
//	        return false; // não há objeto à frente
//	    }
//
//	    if (!obj.isMovable()) {
//	        System.out.println("nao pode mover este tipo de objetos");
//	        return false;
//	    }
//
//	    // SmallFish não empurra objetos pesados
//	    if (fish instanceof SmallFish && obj.isHeavy()) {
//	        System.out.println("pequeno nao move obj pesado");
//	        return false;
//	    }
//
//	    Point2D nextPos = objPos.plus(dir);
//
//	    // Tentar descobrir um 2º objeto logo a seguir ao primeiro
//	    GameObject second = null;
//	    for (GameObject o : objects) {
//	        if (o.getPosition().equals(nextPos) && o.getLayer() == 1 && !(o instanceof Water)) {
//	            second = o;
//	            break;
//	        }
//	    }
//
//	    // ---- CASO COM 2 OBJETOS EM LINHA ----
//	    if (second != null) {
//	        // se o segundo não for movível, bloqueia
//	        if (!second.isMovable()) {
//	            System.out.println("2º objeto nao é movivel");
//	            return false;
//	        }
//	        // se algum for pesado e o peixe for pequeno, não empurra
//	        if (fish instanceof SmallFish && second.isHeavy()) {
//	            System.out.println("pequeno nao move cadeia com objeto pesado");
//	            return false;
//	        }
//	        Point2D secondNext = nextPos.plus(dir);
//	        // Verificar se a casa depois do 2º está livre/atravessável
//	        for (GameObject o : objects) {
//	            if (o.getPosition().equals(secondNext) && !(o instanceof Water)) {
//	                if (!second.goesTrough(o)) {
//	                    System.out.println("2º objeto bloqueado no destino");
//	                    return false;
//	                }
//	            }
//	        }
//	        // Movimento em cadeia: mexer primeiro o 2º, depois o 1º
//	        second.setPosition(secondNext);
//	        obj.setPosition(nextPos);
//	        System.out.println("2 objetos movidos");
//	        return true;
//	    }
//
//	    // ---- CASO NORMAL: SÓ 1 OBJETO ----
//	    // Verificar se a posição para onde o objeto vai já está ocupada
//	    for (GameObject o : objects) {
//	        if (o.getPosition().equals(nextPos) && !(o instanceof Water)) {
//	            if (!obj.goesTrough(o)) {
//	                System.out.println("posicao ocupada");
//	                return false;
//	            }
//	        }
//	    }
//
//	    obj.setPosition(nextPos);
//	    System.out.println("obj movido");
//	    return true;
//	}

	public boolean pushObject(GameCharacter fish, Vector2D dir) {
		Point2D fishPos = fish.getPosition();
		Point2D curPos = fishPos.plus(dir);

		// 1) Construir a cadeia de objetos consecutivos à frente do peixe
		List<GameObject> objectChain = new ArrayList<>();

		while (true) {
			GameObject obj = null;
			for (GameObject o : objects) {
				if (o.getPosition().equals(curPos) && o.getLayer() == 1) {
					obj = o;
					break;
				}
			}
			if (obj == null) {
				break; // acabou a cadeia
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

//	public boolean checkObjectsOnTop(GameCharacter fish) {
//
//		Point2D pos = fish.getPosition();
//		int heavy = 0;
//		int light = 0;
//
//		Point2D upperPos = pos.plus(new Vector2D(0, -1));
//
//		// 1) CONTAR OBJETOS ACIMA
//		while (true) {
//
//			GameObject objOnTop = null;
//
//			for (GameObject o : objects) {
//				if (o.getPosition().equals(upperPos)) {
//					objOnTop = o;
//					break;
//				}
//			}
//
//			// nada nesta posição → acabou a pilha
//			if (objOnTop == null) {
//				break;
//			}
//
//			// parede / aço / etc → bloqueia tudo → não interessa o que vem acima
//			if (!objOnTop.isMovable()) {
//				break;
//			}
//
//			// objeto movível → contar leve/pesado
//			if (objOnTop.isHeavy()) {
//				heavy++;
//				System.out.println("pesado aumenta " + heavy);
//			} else {
//				light++;
//				System.out.println("leve aumenta " + light);
//			}
//
//			// próxima posição acima
//			upperPos = upperPos.plus(new Vector2D(0, -1));
//		}
//
//		// 2) APLICAR AS REGRAS
//
//		if (fish instanceof SmallFish) {
//			// morre se tiver 1 pesado OU 2 leves
//			if (heavy >= 1 || light >= 2) {
//				System.out.println("SmallFish esmagado: heavy=" + heavy + " light=" + light);
//				return false;
//			}
//			return true;
//		}
//		
//		if (fish instanceof BigFish) {
//			// morre se tiver 2 ou mais objetos
//			if (heavy + light >= 2) {
//				System.out.println("BigFish esmagado: heavy=" + heavy + " light=" + light);
//				return false;
//			}
//			return true;
//		}
//
//		
//		return true;
//
//	}

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