package gameObjects;


public class BasicBullet extends Bullet {

	public static final byte BULLET_NR = 0;
	public static final float RADIUS = 1;
	public static final int DAMAGE = 10;
	public static final float KNOCKBACK = 100;
	public static final int TIMER = 60;
	
	private static final float SPEED = 19.5f;
	private static float RANDOM_SPEED_ADDITION = 2f;
	
	
	public BasicBullet(float startX, float startY, float direction) {
		super(BULLET_NR, RADIUS, DAMAGE, KNOCKBACK, startX, startY, direction, SPEED+(float)(Math.random()*RANDOM_SPEED_ADDITION), TIMER);
	}

	@Override
	public void onPlayerCollision(Character c) {
		c.addPercent(DAMAGE);
		c.addKnockback(super.rotation, KNOCKBACK);
		destroy();
	}

	
}
