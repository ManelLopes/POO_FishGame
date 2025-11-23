package objects;

import pt.iscte.poo.game.Room;

public class Cup extends GameObject{

	public Cup(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "cup";
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean isHeavy() {
		return false;
	}
		
	
}
