package objects;

import pt.iscte.poo.game.Room;

public class Trap extends GameObject{

	public Trap(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "trap";
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean isMovable() {
		return false;
	}

//	@Override
//	public boolean hasGravity() {
//		return false;
//	}

	@Override
	public boolean goesTrough(GameObject o) {
		return true;
	}
	
	
	
	
	
	

	
	
	
	
	
	
	
}
