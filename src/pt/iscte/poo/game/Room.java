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

import java. util. Scanner;
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

		for (String linha : linhas) {	//todas as linhas que guardamos 

		    for (int x = 0; x < linha.length(); x++) {	// vamos ver os caracteres todos da linha
		        char c = linha.charAt(x);
		        Point2D pos = new Point2D(x, y);
		        
		        GameObject water = new Water(r);	// pomos agua em todas as posicoes do mapa 
		        water.setPosition(pos);
		        r.addObject(water);
		        
		        if (c == 'W') {
		            GameObject w = new Wall(r);
		            w.setPosition(pos);
		            r.addObject(w);
		        }
		        else if (c == 'B') {
		            GameObject bf = BigFish.getInstance();
		            bf.setPosition(pos);
		            r.addObject(bf);
		            r.setBigFishStartingPosition(pos);
		        }
		        else if (c == 'S') {
		            GameObject sf = SmallFish.getInstance();
		            sf.setPosition(pos);
		            r.addObject(sf);
		            r.setSmallFishStartingPosition(pos);
		        }
		        else if (c == 'H') {
		            GameObject sh = new SteelHorizontal(r);
		            sh.setPosition(pos);
		            r.addObject(sh);
		        }
		        else if(c == 'A') {
		        	GameObject anchor = new Anchor(r);
		        	anchor.setPosition(pos);
		        	r.addObject(anchor);
		        }
		        else if(c == 'C') {
		        	GameObject cup = new Cup(r);
		        	cup.setPosition(pos);
		        	r.addObject(cup);
		        }
		        else if(c == 'b') {
		        	GameObject bomb = new Bomb(r);
		        	bomb.setPosition(pos);;
		        	r.addObject(bomb);
		        }
		        else if(c == 'X') {
		        	GameObject holedWall = new HoledWall(r);
		        	holedWall.setPosition(pos);
		        	r.addObject(holedWall);
		        }
		        else if(c == 'T') {
		        	GameObject trap = new Trap(r);
		        	trap.setPosition(pos);
		        	r.addObject(trap);
		        }
		    }

		    y++; // vamos ler as linhas do ficheiro
		}

		
		return r;		// da return no room
		
	}
	
//	public boolean pushObject(GameCharacter fish, Vector2D dir) {
//		
//		Point2D fishPos = fish.getPosition();
//		Point2D objPos = fishPos.plus(dir);
//		
//		GameObject obj = null;
//		
//		for(GameObject o: objects) {//procurar objeto na posicao de destino
//			if(o.getPosition().equals(objPos)) {
//				obj = o;
//				break;
//			}
//		}
//		
//		if(obj == null) {
//			return false;// nao havia objeto na posicao seguinte
//		}
//		
//		Point2D nextPos = objPos.plus(dir);//posicao futura do objeto
//		
//		for(GameObject o: objects) {
//			if(o.getPosition().equals(nextPos)) {
//				return false;
//			}
//			if(SmallFish.isActive() && o.isHeavy()) {
//				return false;
//			}
//		}
//		
//		
//		
//		obj.setPosition(nextPos);
//		return true;		
//	}

	public boolean pushObject(GameCharacter fish, Vector2D dir) {
	    Point2D fishPos = fish.getPosition();
	    Point2D objPos  = fishPos.plus(dir);

	    GameObject obj = null;

	    // 1) encontrar o objeto mesmo à frente do peixe
	    for (GameObject o : objects) {
	        if (o.getPosition().equals(objPos)) {
	            obj = o;
	            break;
	        }
	    }

	    if (obj == null) {
	        return false; // não há nada para empurrar
	    }

	    // 2) se o peixe é pequeno e o objeto é pesado, não dá
	    if (SmallFish.isActive() && obj.isHeavy()) {
	        return false;
	    }

	    // 3) posição futura do objeto
	    Point2D nextPos = objPos.plus(dir);

	    // ver se a casa para onde o objeto vai está ocupada
	    for (GameObject o : objects) {
	        if (o.getPosition().equals(nextPos)) {
	            return false;
	        }
	    }

	    // 4) empurra mesmo
	    obj.setPosition(nextPos);
	    System.out.println("Empurrei " + obj + " para " + nextPos); // debug
	    return true;
	}
	
	public boolean canMoveTo(Point2D pos) {

		for (GameObject o : objects) {   
	        if (o.getPosition().equals(pos)) {
	            if (o instanceof Wall || o instanceof SteelHorizontal || (!SmallFish.isActive() && o instanceof HoledWall) || (SmallFish.isActive() && o.isHeavy())) { 
	                return false;        // não pode passar
	            }
	        }
	    }
	    return true; // se não encontrou nada sólido, pode ir
	}
	
}