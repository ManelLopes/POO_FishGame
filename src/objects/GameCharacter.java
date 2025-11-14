package objects;



import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class GameCharacter extends GameObject {
	
	public GameCharacter(Room room) {
		super(room);
	}
	
	public void move(Vector2D dir) {
		Point2D pos = getPosition();
		
	    Point2D newPos = pos.plus(dir);
		
	    if (getRoom().canMoveTo(newPos)) {  
	        setPosition(newPos);
	    }
		
	}

	@Override
	public int getLayer() {
		return 2;
	}
	
}