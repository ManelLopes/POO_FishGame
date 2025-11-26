package objects;

import pt.iscte.poo.game.Room;

public class HoledWall extends GameObject{

	public HoledWall(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "holedWall";
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean isMovable() {
		return false;
	}

	@Override
	public boolean hasGravity() {
		return false;
	}

	
	
}
