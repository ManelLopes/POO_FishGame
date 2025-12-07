package objects;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public class BigFish extends GameCharacter {

	private static BigFish bf = new BigFish(null);
	private List<GameObject> objects;
	private String imageName = "bigFishLeft";

	
	private BigFish(Room room) {
		super(room);
		objects = new ArrayList<GameObject>();
	}

	public static BigFish getInstance() {
		return bf;
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
	
	public boolean canMoveTo(Point2D pos) {
		
		for(GameObject o : objects) {
			if(o.getPosition().equals(pos)) {
				if(o instanceof HoledWall) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean goesTrough(GameObject o) {
		return false;
	}
	
	public void move(GameCharacter fish, Vector2D dir) {
        super.move(fish, dir);

        if (dir.getX() < 0) {
            setImageName("bigFishLeft");
        } else if (dir.getX() > 0) {
            setImageName("bigFishRight");
        }
    }



	
	
	
	
	
	
}
