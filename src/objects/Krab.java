package objects;

import pt.iscte.poo.game.Room;

public class Krab extends GameObject{

	public Krab(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "krab";
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
		return false;
	}

	@Override
	public boolean hasGravity() {
		return true;
	}
	
	@Override
	public boolean goesTrough(GameObject o) {
		return false;
	}

		
	
}