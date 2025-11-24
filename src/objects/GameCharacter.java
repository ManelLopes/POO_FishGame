package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class GameCharacter extends GameObject {

	public GameCharacter(Room room) {
		super(room);
	}

//	public boolean move(Vector2D dir) {
//		Point2D pos = getPosition();
//
//		Point2D newPos = pos.plus(dir);
//
//		if (getRoom().canMoveTo(newPos)) {
//			setPosition(newPos);
//			return true;
//		}
//		return false;
//
//	}
	
	public void move(Vector2D dir) {
	    System.out.println("MOVE peixe " + this + " dir=" + dir);

	    Room room = getRoom();
	    Point2D pos    = getPosition();
	    Point2D newPos = pos.plus(dir);

	    if (room.hasObjectAt(newPos)) {
	        System.out.println("Há objeto em " + newPos + ", vou tentar empurrar");
	        boolean pushed = room.pushObject(this, dir);
	        System.out.println("Resultado push = " + pushed);
	        if (!pushed) return;
	    }

	    if (!room.canMoveTo(newPos)) {
	        System.out.println("canMoveTo deu false para " + newPos);
	        return;
	    }

	    setPosition(newPos);
	}

	@Override
	public int getLayer() {
		return 2;
	}

}