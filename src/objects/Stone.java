package objects;

import pt.iscte.poo.game.Room;

public class Stone extends GameObject{
	
	public Stone(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "stone";
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
		return true;
	}

	@Override
	public boolean hasGravity() {
		return true;
	}
	
	@Override
	public boolean goesTrough(GameObject o) {
		return true;
	}
	
	
}