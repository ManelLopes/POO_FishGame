package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;

public abstract class GameObject implements ImageTile{
	
	private Point2D position;
	private Room room;
	
	public GameObject(Room room) {
		this.room = room;
	}
	
	public GameObject(Point2D position, Room room) {
		this.position = position;
		this.room = room;
	}

	public void setPosition(int i, int j) {
		position = new Point2D(i, j);
	}
	
	public void setPosition(Point2D position) {
		this.position = position;
	}

	@Override
	public Point2D getPosition() {
		return position;
	}
	
	public Room getRoom() {
		return room;
	}
	
	public void setRoom(Room room) {
		this.room = room;
	}
	
	public  boolean isHeavy() {//verifica se o objeto é pesado
		return false;
	}
	
	public boolean isMovable() {//verifica se o objeto é movivel
		return false;
	}
	
	public boolean hasGravity() {//verifica se o objeto tem gravidade
		return false;
	}
	
	public boolean goesTrough(GameObject o) {//determina se passa pelo objeto o
		return false;
	}
	

	
	
}
