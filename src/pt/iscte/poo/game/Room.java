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
import objects.Trap;

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
				}
			}

			y++; // vamos ler as linhas do ficheiro
		}

		return r; // da return no room

	}

//	public boolean pushObject(GameCharacter fish, Vector2D dir) {
//		Point2D fishPos = fish.getPosition();
//		Point2D objPos = fishPos.plus(dir);
//		GameObject obj = null;
//		for (GameObject o : objects) {
//			if (o.getPosition().equals(objPos) && o.getLayer() == 1) {
//				obj = o;
//				break;
//			}
//		}
//		if (obj == null) {
//			System.out.println("nao tem objeto no destino");
//			return false; // não há objeto à frente
//		}
//
//		if (!obj.isMovable()) {
//			System.out.println("nao pode mover este tipo de objetos");
//			return false;
//		}
//
//		Point2D nextPos = objPos.plus(dir);
//
//		// Verifica se SmallFish pode empurrar este objeto (peso)
//		if (fish instanceof SmallFish && obj.isHeavy()) {
//			System.out.println("pequeno nao move obj pesado");
//			return false;
//		}
//
//		// Verificar se a posição para onde o objeto vai já está ocupada
//		for (GameObject o : objects) {
//			if (o.getPosition().equals(nextPos) && !(o instanceof Water)) {
//
//				if (!obj.goesTrough(o)) {
//					System.out.println("posicao ocupada"); // posição ocupada
//					return false;
//				}
//			}
//
//		}
//
//		obj.setPosition(nextPos);
//		System.out.println("obj movido");
//		return true;
//
//	}

	public boolean pushObject(GameCharacter fish, Vector2D dir) {
	    Point2D fishPos = fish.getPosition();
	    Point2D objPos = fishPos.plus(dir);

	    // 1º objeto à frente do peixe
	    GameObject obj = null;
	    for (GameObject o : objects) {
	        if (o.getPosition().equals(objPos) && o.getLayer() == 1) {
	            obj = o;
	            break;
	        }
	    }
	    if (obj == null) {
	        System.out.println("nao tem objeto no destino");
	        return false; // não há objeto à frente
	    }

	    if (!obj.isMovable()) {
	        System.out.println("nao pode mover este tipo de objetos");
	        return false;
	    }

	    // SmallFish não empurra objetos pesados
	    if (fish instanceof SmallFish && obj.isHeavy()) {
	        System.out.println("pequeno nao move obj pesado");
	        return false;
	    }

	    Point2D nextPos = objPos.plus(dir);

	    // Tentar descobrir um 2º objeto logo a seguir ao primeiro
	    GameObject second = null;
	    for (GameObject o : objects) {
	        if (o.getPosition().equals(nextPos) && o.getLayer() == 1 && !(o instanceof Water)) {
	            second = o;
	            break;
	        }
	    }

	    // ---- CASO COM 2 OBJETOS EM LINHA ----
	    if (second != null) {
	        // se o segundo não for movível, bloqueia
	        if (!second.isMovable()) {
	            System.out.println("2º objeto nao é movivel");
	            return false;
	        }
	        // se algum for pesado e o peixe for pequeno, não empurra
	        if (fish instanceof SmallFish && second.isHeavy()) {
	            System.out.println("pequeno nao move cadeia com objeto pesado");
	            return false;
	        }
	        Point2D secondNext = nextPos.plus(dir);
	        // Verificar se a casa depois do 2º está livre/atravessável
	        for (GameObject o : objects) {
	            if (o.getPosition().equals(secondNext) && !(o instanceof Water)) {
	                if (!second.goesTrough(o)) {
	                    System.out.println("2º objeto bloqueado no destino");
	                    return false;
	                }
	            }
	        }
	        // Movimento em cadeia: mexer primeiro o 2º, depois o 1º
	        second.setPosition(secondNext);
	        obj.setPosition(nextPos);
	        System.out.println("2 objetos movidos");
	        return true;
	    }

	    // ---- CASO NORMAL: SÓ 1 OBJETO ----
	    // Verificar se a posição para onde o objeto vai já está ocupada
	    for (GameObject o : objects) {
	        if (o.getPosition().equals(nextPos) && !(o instanceof Water)) {
	            if (!obj.goesTrough(o)) {
	                System.out.println("posicao ocupada");
	                return false;
	            }
	        }
	    }

	    obj.setPosition(nextPos);
	    System.out.println("obj movido");
	    return true;
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
			if ( o.hasGravity() && canObjMoveTo(o, posBelow)) {
				o.setPosition(posBelow);
				System.out.println("objeto cai");
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
				if(o instanceof HoledWall) {
					return fish instanceof SmallFish;
				}
				
			}
		}

		return true; // se não encontrou nada sólido, pode ir
	}

	public boolean canObjMoveTo(GameObject obj, Point2D pos) {
		for (GameObject o : objects) {
			if (o.getPosition().equals(pos)) {
				if ((obj.isHeavy() && o instanceof HoledWall) || o instanceof Trap || o instanceof Wall || o instanceof SteelHorizontal) {
					return false;
				}
			}
		}
		return true;
	}

}