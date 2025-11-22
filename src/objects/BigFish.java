package objects;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class BigFish extends GameCharacter {

	private static BigFish bf = new BigFish(null);
	private List<GameObject> objects;

	
	private BigFish(Room room) {
		super(room);
		objects = new ArrayList<GameObject>();
	}

	public static BigFish getInstance() {
		return bf;
	}
	
	
	@Override
	public String getName() {
		return "bigFishLeft";
	}

	@Override
	public int getLayer() {
		return 1;
	}
	
	public boolean canMoveTo(Point2D pos) {
		
		for(GameObject o : objects) {
			if(o.getPosition().equals(pos)) {
				if(o instanceof HoledWall) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void move(Vector2D dir) {
		
		Point2D pos = getPosition();
		Point2D newPos = pos.plus(dir);
		Room room = getRoom();
		
		for(GameObject o: room.getObjects()) {
			if(o.getPosition().equals(newPos) && o instanceof HoledWall) {
				return;
			}
		}		
		
		super.move(dir);
		
	}
	
	
	
	
	
}
