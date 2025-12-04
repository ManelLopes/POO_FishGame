package objects;

import pt.iscte.poo.game.Room;

public class Anchor extends GameObject{
	private boolean hasBeenMoved = false;
	
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
//		if(!SmallFish.isActive())
//			return true;
//		return false;
		return !hasBeenMoved;
	}

	@Override
	public boolean hasGravity() {
		return true;
	}
	
	@Override
	public boolean goesTrough(GameObject o) {
		return false;
	}
	
	public boolean getHasBeenMoved() {
		return hasBeenMoved;
	}
	
	public void setHasBeenMoved(boolean hasBeenMoved) {
		this.hasBeenMoved = hasBeenMoved;
	}
	
}