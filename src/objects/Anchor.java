package objects;

import pt.iscte.poo.game.Room;

public class Anchor extends GameObject{
	
	public Anchor(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "anchor";
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
		if(!SmallFish.isActive())
			return true;
		return false;
	}

	@Override
	public boolean hasGravity() {
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}