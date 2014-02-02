package calpoly.cpe123.lucal_2;

import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

public class MovingSprite extends AnimatedSprite {

	//inserted this just to make Eclipse and Java happy
	public MovingSprite(float pX, float pY,
			TiledTextureRegion pTiledTextureRegion) {
		super(pX, pY, pTiledTextureRegion);
		//this.animate(200);
		this.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				// TODO Auto-generated method stub
				if (moveDir == NORTH) {
//					&& LucalActivity_v2.gameInstance.background.getCell(mX, mY - 1).IsPassable() == true) {
				mY -= MOVESPEED;
			} else if (moveDir == SOUTH) {
//					&& LucalActivity_v2.gameInstance.background.getCell(mX, mY + 1).IsPassable() == true) {
				mY += MOVESPEED;
			} else if (moveDir == EAST) {
//					&& LucalActivity_v2.gameInstance.background.getCell(mX + 1, mY).IsPassable() == true) {
				mX += MOVESPEED;
			} else if (moveDir == WEST) {
//					&& LucalActivity_v2.gameInstance.background.getCell(mX - 1, mY).IsPassable() == true) {
				mX -= MOVESPEED;
			}

			/*
			 * +1s and -1s are to check for coordinates that the sprite is about to
			 * move into
			 */
			}
		});
	}



	public static final int 
	NORTH = 1,
	SOUTH = 2,
	EAST = 3,
	WEST = 4,
	NONE = 0,
	MOVESPEED = 6;
	private int moveDir = NONE;


	
	public void faceSprite(int Direction, Body body) { // insert code that would rotate
											// sprite to face the direction
											// passed into the function

		if (Direction == SOUTH) {

			//this.setRotation(180);
			body.setTransform(body.getWorldCenter(), (float) (2f/2f*Math.PI));


		} else if (Direction == NORTH) {

			//this.setRotation(0);
			body.setTransform(body.getWorldCenter(), (float) (0f/2f*Math.PI));


		} else if (Direction == EAST) {

			//this.setRotation(90);
			body.setTransform(body.getWorldCenter(), (float) (1f/2f*Math.PI));


		} else if (Direction == WEST) {

			//this.setRotation(270);
			body.setTransform(body.getWorldCenter(), (float) (3f/2f*Math.PI));

		}
	}

	public void moveSprite(int Direction, Body spriteBody) {

		faceSprite(Direction, spriteBody);
		spriteBody.setLinearVelocity(
				Direction == EAST ? MOVESPEED : Direction == WEST ? -MOVESPEED : 0,
				Direction == SOUTH ? MOVESPEED : Direction == NORTH ? -MOVESPEED : 0);
		moveDir = Direction;
	}
	
}
