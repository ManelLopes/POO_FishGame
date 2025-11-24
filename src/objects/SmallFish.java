package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public class SmallFish extends GameCharacter {

	private static SmallFish sf = new SmallFish(null);
	private static boolean isActive = true;
	
	private SmallFish(Room room) {
		super(room);
	}

	public static SmallFish getInstance() {
		return sf;
	}
	
	public static void switchFish() {
		isActive = !isActive;
		System.out.println("Agora a controlar: " + (isActive ? "SmallFish" : "BigFish"));
	}
	
	
	public static boolean isActive() {
		return isActive;
	}
	
	@Override
	public String getName() {
		return "smallFishLeft";
	}

	@Override
	public int getLayer() {
		return 1;
	}
	


}
