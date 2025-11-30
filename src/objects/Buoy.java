package objects;

import pt.iscte.poo.game.Room;

public class Buoy extends GameObject{

	public Buoy(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "buoy";
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
		return false;
		
	}
	
	@Override
	public boolean goesTrough(GameObject o) {
		return false;
	}

		
	
}