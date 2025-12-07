package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Vector2D;

public class SmallFish extends GameCharacter {

	private static SmallFish sf = new SmallFish(null);
	private static boolean isActive = true;
	private String imageName = "smallFishLeft";
	
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
		return imageName;
	}
	
	public void setImageName(String name) {
        this.imageName = name;
    }

	@Override
	public int getLayer() {
		return 2;
	}
	
	@Override
	public boolean goesTrough(GameObject o) {
		return true;
	}
	
	public void move(GameCharacter fish, Vector2D dir) {
        super.move(fish, dir);

        if (dir.getX() < 0) {
            setImageName("smallFishLeft");
        } else if (dir.getX() > 0) {
            setImageName("smallFishRight");
        }
    }
	


}
