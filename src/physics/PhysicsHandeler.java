package physics;

import java.util.ArrayList;
import java.util.List;

public class PhysicsHandeler {



	private List<Collideable> collideables = new ArrayList<>();
	
	public void update() {
		
		for (int i = 0; i < collideables.size()-1; i++) {
			//for (int j = i+1)
		}
	}
	
	public void addCollideable(Collideable unit) {
		collideables.add(unit);
	}
	public void removeCollideable(Collideable unit) {
		collideables.remove(unit);
	}
	
	public static boolean isCollision(Collideable coll1, Collideable coll2) {
		return isCollision(coll1.getPhShape(), coll2.getPhShape());
	}
	
	public static boolean isCollision( PhShape shape1, PhShape shape2 ) {
		if (shape1 instanceof PhCircle && shape2 instanceof PhCircle) {
			return isCollision((PhCircle)shape1, (PhCircle)shape2);
		}
		else if (shape1 instanceof PhCircle && shape2 instanceof PhRectangle) {
			return isCollision((PhCircle)shape1, (PhRectangle)shape2);
		}
		else if (shape1 instanceof PhRectangle && shape2 instanceof PhCircle) {
			return isCollision((PhCircle)shape2, (PhRectangle)shape1);
		}
		else if (shape1 instanceof PhRectangle && shape2 instanceof PhRectangle) {
			return isCollision((PhRectangle)shape1, (PhRectangle)shape2);
		}
		throw new UnsupportedOperationException("Cannot check collision between given shapes");
	}
	
	public static boolean isCollision(PhCircle circ1, PhCircle circ2) {
		float r1 = circ1.getRadius();
		float x1 = circ1.getX();
		float y1 = circ1.getY();
		float r2 = circ2.getRadius();
		float x2 = circ2.getX();
		float y2 = circ2.getY();
		
		float maxDistSquared = r1 + r2;
		float distSquared = (float)( Math.pow((float)(y2-y1), 2) + Math.pow((float)(x2-x1), 2) );
		return distSquared < maxDistSquared;
	}
	public static boolean isCollision(PhCircle circ1, PhRectangle rect2) {
		throw new UnsupportedOperationException("Circ-rect collision not supported yet");
//		float r1 = circ1.getRadius();
//		float x1 = circ1.getX();
//		float y1 = circ1.getY();
//		float x2 = rect2.getX();
//		float y2 = rect2.getY();
//		float w2 = rect2.getWidth();
//		float h2 = rect2.getHeight();
		
		
	}
	public static boolean isCollision(PhRectangle rect1, PhRectangle rect2) {
		float x1 = rect1.getX();
		float y1 = rect1.getY();
		float w1 = rect1.getWidth();
		float h1 = rect1.getHeight();
		float x2 = rect2.getX();
		float y2 = rect2.getY();
		float w2 = rect2.getWidth();
		float h2 = rect2.getHeight();
		
		return ( (x2 < x1+w1 && x2+w2 > x1) && (y2 < y1+h1 && y2+h2 >y1) );
	}
}
