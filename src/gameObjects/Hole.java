package gameObjects;

import com.sun.xml.internal.stream.Entity;

import physics.Collideable;
import physics.PhRectangle;
import physics.PhShape;

public class Hole extends Entity implements Collideable{

	float width, height;
	private PhShape shape;
	
	public Hole(PhShape shape) {
		this.shape = shape;
		
	}
	
	@Override
	public PhShape getPhShape() {
		return shape;
	}
	
	@Override
	public boolean isExternal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUnparsed() {
		// TODO Auto-generated method stub
		return false;
	}

}
