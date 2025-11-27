package objects;

import pt.iscte.poo.game.Room;

public class Trunk extends GameObject{
	
	public Trunk(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "trunk";
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean isHeavy() {
		return true;
	}

	@Override
	public boolean isMovable() {
		return false;
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