package gameObjects;

public class SpecialBullet extends Bullet{

	public static final byte BULLET_NR = 1;
	public static final float RADIUS = 1;
	public static final int DAMAGE = 80;
	public static final float KNOCKBACK = 0;
	public static final int TIMER = 60;
	
	private static final float SPEED = 9.6f;
	private static float RANDOM_SPEED_ADDITION = 1f;
	
	
	public SpecialBullet(float startX, float startY, float direction) {
		super(BULLET_NR, RADIUS, DAMAGE, KNOCKBACK, startX, startY, direction, SPEED+(float)(Math.random()*RANDOM_SPEED_ADDITION), TIMER);
	}

	@Override
	public void onPlayerCollision(Character c) {
		applyDamage(c, DAMAGE);
		
	}
	
}
