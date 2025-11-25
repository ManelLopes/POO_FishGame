package objects;

import pt.iscte.poo.game.Room;

public class Bomb extends GameObject{

	public Bomb(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "bomb";
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean isHeavy() {
		return false;
	}

	@Override
	public boolean isMovable() {
		return true;
	}

	@Override
	public boolean hasGravity() {
		return true;
	}
	
	@Override
	public boolean goesTrough(GameObject o) {
		if(o instanceof HoledWall)
			return true;
		return false;
	}
	
	
	
	

	

	
	
	
	
	
}
