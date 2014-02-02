//https://github.com/bobsomers/lua-love-redux
package calpoly.cpe123.lucal_2;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.audio.music.*;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.FixedStepEngine;
import org.anddev.andengine.engine.camera.SmoothCamera;
import org.anddev.andengine.engine.camera.hud.controls.*;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.exception.MultiTouchException;
import org.anddev.andengine.extension.physics.box2d.*;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.layer.tiled.tmx.*;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.anddev.andengine.entity.layer.tiled.tmx.util.exception.TMXLoadException;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.ui.activity.ProgressGameActivity;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.MathUtils;
import org.anddev.andengine.opengl.texture.region.*;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.widget.Button;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class LucalActivity_v2 extends
// BaseGameActivity
		ProgressGameActivity
		implements IOnSceneTouchListener {

	//
	// <DEBUG>
	//
	static Vibrator v;
	//
	// </DEBUG>
	//

	public static final boolean HARD_CODED_PATH = true;
	private static final float CAMERA_SPEED = 200f;
	private static final String SPRITE_LUCAL = "sprite_lucal.png";
	private static final String SPRITE_WIN = "anubis_black.png";
	private static final String LIGHT_IMAGE = "light.png";
	private static final String BACKGROUND_MUSIC = "Background.ogg";
	private static final String CONTROL_BASE = "onscreen_control_base.png";
	private static final String LIGHT_BASE = "light_control_blank.png";
	private static final String CONTROL_KNOB = "onscreen_control_knob.png";
	private static final String LIGHT_KNOB = "light_control_blankknob.png";
	private static final String TMX_FLOOR_BACKGROUND = "tmx/floor_64_by_64.tmx";

	private static final int CAMERA_WIDTH = 800, CAMERA_HEIGHT = 480,
			CELL_SIZE = 32, GRID_WIDTH = 64, GRID_HEIGHT = 64;
	private static final int ANUBIX_XY = 58*CELL_SIZE;
	private static final int WIN_DISTANCE = 55;

	public static LucalActivity_v2 gameInstance;
	MovingSprite player;
	MovingSprite winSprite;
	TiledTextureRegion playerTexture;
	TiledTextureRegion winTexture;
	SmoothCamera camera;
	Scene gameScene;
	DigitalOnScreenControl hud;
	Sprite light;

	TMXWorld tmxWorld = new TMXWorld(GRID_WIDTH);
	public TMXTiledMap background;
	BitmapTextureAtlas playerAtlas;
	BitmapTextureAtlas winAtlas;

	int winX;
	int winY;
	public boolean gameIsOver = false;

	boolean[][] passables = new boolean[GRID_WIDTH][GRID_HEIGHT];

	BitmapTextureAtlas mOnScreenControlTexture;
	TextureRegion mOnScreenControlBaseTextureRegion;
	TextureRegion mOnScreenControlKnobTextureRegion;

	DigitalOnScreenControl mDigitalOnScreenControl;

	PhysicsHandler handler;
	PhysicsWorld world;

	// private AnalogOnScreenControl mLightControl;

	private TextureRegion mLightControlBaseTextureRegion;
	private TextureRegion mLightControlKnobTextureRegion;
	private BitmapTextureAtlas mLightControlTexture;

	private Music mBackMusic;

	private BitmapTextureAtlas lightAtlas;

	private Body playerBody;

	@Override
	public Engine onLoadEngine() {
		
		v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		gameInstance = this;
		// camera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 5, 5,
		// 1);
		camera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT,
				CAMERA_SPEED, CAMERA_SPEED, 2f);
		// camera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 0,
		// CELL_SIZE * GRID_WIDTH, 0, CELL_SIZE * GRID_HEIGHT);
		Engine toReturn = new FixedStepEngine(
				new EngineOptions(true, ScreenOrientation.LANDSCAPE,
						new RatioResolutionPolicy(800, 480), camera)
						.setNeedsMusic(true),
				30);
		try {
			if (MultiTouch.isSupported(this)) {
				toReturn.setTouchController(new MultiTouchController());
			}
		} catch (final MultiTouchException e) {
		}

		return toReturn;

	}

	@Override
	public void onLoadResources() {
		//
		// <DEBUG>
		//
		// Looper.prepare();
		// Toast.makeText(this, "Starting game", 3000).show();
		// tmxWorld.resetWorld(this);
		//
		// </DEBUG>
		//

		playerAtlas = new BitmapTextureAtlas(256, 256);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		playerTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(playerAtlas, this, SPRITE_LUCAL, 0, 0, 4,
						4);
		player = new MovingSprite(128, 128, playerTexture);
		lightAtlas = new BitmapTextureAtlas(1024, 1024);
		light = new Sprite(400 - 1024 / 2f, 240 - 1024 / 2f,
				BitmapTextureAtlasTextureRegionFactory.createFromAsset(
						lightAtlas, this, LIGHT_IMAGE, 0, 0));

		winAtlas = new BitmapTextureAtlas(512, 512);
		winTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(winAtlas, this, SPRITE_WIN, 0, 0, 4, 4);

		winX = winY = ANUBIX_XY;
		winSprite = new MovingSprite(winX, winY, winTexture);
		winX += winSprite.getWidth()/2;
		winY += winSprite.getHeight()/2;
		
		camera.setChaseEntity(player);

		MusicFactory.setAssetBasePath("music/");
		try {
			this.mBackMusic = MusicFactory.createMusicFromAsset(
					this.mEngine.getMusicManager(), this, BACKGROUND_MUSIC);
			this.mBackMusic.setLooping(true);
		} catch (final IOException ex) {
			Debug.e("Error", ex);
		}

		this.mEngine.getTextureManager().loadTexture(playerAtlas);
		this.mEngine.getTextureManager().loadTexture(winAtlas);
		this.mEngine.getTextureManager().loadTexture(lightAtlas);
	}

	@Override
	public Scene onLoadScene() {
		Scene toReturn = createGameScene();
		camera.setCenter(player.getX(), player.getY());
		camera.setBounds(0, GRID_WIDTH * CELL_SIZE, 0, GRID_HEIGHT * CELL_SIZE);
		toReturn.setOnSceneTouchListener(this);
		return toReturn;
	}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub
		
	}
	
	boolean lightIsOn = true;
	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event)
	{
		if(event.getKeyCode() == KeyEvent.KEYCODE_MENU)
		{
			synchronized(gameScene)
			{
			if(lightIsOn)
			{
			gameScene.detachChild(light);
			lightIsOn = false;
			}
			else
			{
				gameScene.attachChild(light);
				lightIsOn = true;
			}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void clearUnwalkableObjects(TMXTiledMap map)
	{
		for(Body b : walls)
		{
			world.destroyBody(b);
		}
	}
	
	ArrayList<Body> walls = new ArrayList<Body>();
	
	//
	// kblood
	// http://www.andengine.org/forums/tutorials/collision-objects-from-tmx-map-t3907-20.html
	//
	private void createUnwalkableObjects(TMXTiledMap map) {
		// Loop through the object groups
		for (final TMXLayer layer : map.getTMXLayers()) {
			// This is our "wall" layer. Create the boxes from it
			for (final TMXTile[] line : layer.getTMXTiles()) {
				for (final TMXTile cell : line) {
					if (cell.getGlobalTileID() == 2) {
						final Rectangle rect = new Rectangle(cell.getTileX(),
								cell.getTileY(), cell.getTileWidth(),
								cell.getTileHeight());
						final FixtureDef boxFixtureDef = PhysicsFactory
								.createFixtureDef(0, 0, 1f);
						walls.add(PhysicsFactory.createBoxBody(this.world, rect,
								BodyType.StaticBody, boxFixtureDef));
//						rect.setVisible(false);
//						gameScene.attachChild(rect);
						// System.out.println(rect.toString());
					}
				}
			}
		}
	}

	private void isGameOver(Intent gameOver) { // new crap
//		if (((Math.abs(player.getX()) / 2 - winX / 2) < (winSprite.getWidth()) / 2 - 55)
//				&& ((Math.abs(player.getY()) / 2 - winY / 2) < (winSprite
//						.getHeight()) / 2 - 55))
		if (Math.sqrt(Math.pow((player.getX() + player.getWidth() / 2) - winX, 2)
				+ Math.pow((player.getY() + player.getHeight() / 2) - winY, 2))
				< WIN_DISTANCE)
		{
			finish();
			startActivity(gameOver);
			gameIsOver = true;
		}
	}

	//
	// kblood
	// http://www.andengine.org/forums/tutorials/collision-objects-from-tmx-map-t3907-20.html
	//
	private void addBounds(float width, float height) {
		final Shape bottom = new Rectangle(-2, height - 2, width+4, 2);
		bottom.setVisible(false);
		final Shape top = new Rectangle(-2, -2, width+4, 2);
		top.setVisible(false);
		final Shape left = new Rectangle(-2, -2, 2, height+4);
		left.setVisible(false);
		final Shape right = new Rectangle(width - 2, -2, 2, height+4);
		right.setVisible(false);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0,
				1f);
		PhysicsFactory.createBoxBody(this.world, bottom, BodyType.StaticBody,
				wallFixtureDef);
		PhysicsFactory.createBoxBody(this.world, top, BodyType.StaticBody,
				wallFixtureDef);
		PhysicsFactory.createBoxBody(this.world, left, BodyType.StaticBody,
				wallFixtureDef);
		PhysicsFactory.createBoxBody(this.world, right, BodyType.StaticBody,
				wallFixtureDef);

		this.gameScene.attachChild(bottom);
		this.gameScene.attachChild(top);
		this.gameScene.attachChild(left);
		this.gameScene.attachChild(right);
	}

	private Scene createGameScene() {

		final Intent gameOver = new Intent(LucalActivity_v2.this,
				GameOver.class);

		gameScene = new Scene();

		mBackMusic.play();

		this.mOnScreenControlTexture = new BitmapTextureAtlas(128, 128,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						CONTROL_BASE, 0, 0);

		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						CONTROL_KNOB,
						// 64 - 16
						0, 0);

		this.mEngine.getTextureManager().loadTextures(
				this.mOnScreenControlTexture); // Loads the graphics to the
												// screen

		//
		// Digital control
		//
		this.mDigitalOnScreenControl = new DigitalOnScreenControl(0,
				CAMERA_HEIGHT
						- this.mOnScreenControlBaseTextureRegion.getHeight(),
				this.camera, this.mOnScreenControlBaseTextureRegion,
				this.mOnScreenControlKnobTextureRegion, 0.1f,
				new IOnScreenControlListener() {
					@Override
					public void onControlChange(
							final BaseOnScreenControl pBaseOnScreenControl,
							final float pValueX, final float pValueY) {

						// http://www.andengine.org/forums/development/move-sprite-with-digital-controller-t4196.html
						// http://www.andengine.org/forums/physics-box2d-extension/analog-control-physic-t5215.html

						if (Math.abs(pValueX) > Math.abs(pValueY)) {
							if (pValueX > 0)
								player.moveSprite(MovingSprite.EAST, playerBody);
							else
								player.moveSprite(MovingSprite.WEST, playerBody);
						} else if (Math.abs(pValueX - pValueY) > 0.1) {
							if (pValueY > 0)
								player.moveSprite(MovingSprite.SOUTH,
										playerBody);
							else
								player.moveSprite(MovingSprite.NORTH,
										playerBody);
						} else {
							player.moveSprite(MovingSprite.NONE, playerBody);
						}
						camera.updateChaseEntity();
						light.setPosition(player.getX() + player.getWidth()
								/ 2f - light.getWidth() / 2f, player.getY()
								+ player.getHeight() / 2f - light.getHeight()
								/ 2f);
						// light.setRotation(player.getRotation());
						TMXTile t = background.getTMXLayers().get(0).getTMXTileAt(player.getX() + player.getWidth()/2f, player.getY() + player.getHeight()/2f);

						if (!HARD_CODED_PATH) {
							if (tmxWorld.registerPlayerPosition(
									t.getTileColumn(), t.getTileRow())) {
								background = tmxWorld.drawTMX(background);
								clearUnwalkableObjects(background);
								createUnwalkableObjects(background);
								v.vibrate(200);
							}
						}
						
						
						if (gameIsOver == false) {
							isGameOver(gameOver);
						}
					}

				});
		this.mDigitalOnScreenControl.getControlBase().setBlendFunction(
				GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mDigitalOnScreenControl.getControlBase().setAlpha(0.5f);
		this.mDigitalOnScreenControl.getControlBase().setScaleCenter(0, 128); // 0
		this.mDigitalOnScreenControl.getControlBase().setScale(1.75f);
		this.mDigitalOnScreenControl.getControlKnob().setScale(1.75f);
		this.mDigitalOnScreenControl.refreshControlKnobPosition();

		// this.mLightControl = new AnalogOnScreenControl(0, 0, camera,
		// mLightControlBaseTextureRegion, mLightControlKnobTextureRegion, 0.1f,
		// new IAnalogOnScreenControlListener() {
		//
		// @Override
		// public void onControlClick(
		// AnalogOnScreenControl pAnalogOnScreenControl) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onControlChange(
		// BaseOnScreenControl pBaseOnScreenControl,
		// float pValueX, float pValueY) {
		// // TODO Auto-generated method stub
		// light.setRotation(MathUtils.radToDeg(MathUtils.atan2(pValueY,
		// pValueX)));
		// }
		//
		// });

		// gameScene.setChildScene(this.mLightControl);
		gameScene.setChildScene(this.mDigitalOnScreenControl);
		//
		// TMX Tile map
		//
		background = null;
		try {

			final TMXLoader tmxLoader = new TMXLoader(this,
					this.mEngine.getTextureManager(),
					TextureOptions.BILINEAR_PREMULTIPLYALPHA,
					new ITMXTilePropertiesListener() {
						@Override
						public void onTMXTileWithPropertiesCreated(
								TMXTiledMap pTMXTiledMap,
								TMXLayer pTMXLayer,
								TMXTile pTMXTile,
								TMXProperties<TMXTileProperty> pTMXTileProperties) {
							// TODO Auto-generated method stub

						}
					});
			background = tmxLoader.loadFromAsset(this, TMX_FLOOR_BACKGROUND);

		} catch (final TMXLoadException tmxle) {
			Debug.e(tmxle);
		}

		tmxWorld.resetWorld();
		background = tmxWorld.drawTMX(background);


		LucalActivity_v2.v.vibrate(50);
		final TMXLayer tmxLayer = background.getTMXLayers().get(0);
		// Rectangle r = new Rectangle(3, 3, 4, 4);
		// for (int y = 0; y < map.getTileRows(); y++) {
		// for (int x = 0; x < map.getTileColumns(); x++) {
		// if (x >= r.getX() && x < r.getX() + r.getWidth() && y >= r.getY()
		// && y < r.getY() + r.getHeight())
		// map.getTMXLayers().get(0).getTMXTile(x, y)
		// .setGlobalTileID(map, 1);
		// else
		// map.getTMXLayers().get(0).getTMXTile(x, y)
		// .setGlobalTileID(map, 2);
		//
		// }
		// }

		gameScene.attachChild(tmxLayer);
		LucalActivity_v2.v.vibrate(50);

		gameScene.attachChild(winSprite);

		gameScene.attachChild(player);

		gameScene.attachChild(light);

		world = new FixedStepPhysicsWorld(30, new Vector2(), false);
		FixtureDef playerFixDef = PhysicsFactory.createFixtureDef(1f, 0.1f,
				0.1f);
		playerBody = PhysicsFactory.createBoxBody(world, player,
				BodyType.DynamicBody, playerFixDef);
		playerBody.setFixedRotation(true);
		world.registerPhysicsConnector(new PhysicsConnector(player, playerBody));
		createUnwalkableObjects(background);
		addBounds(GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
		gameScene.registerUpdateHandler(world);
		Rectangle test = new Rectangle(playerBody.getPosition().x,
				playerBody.getPosition().y, player.getWidth(),
				player.getHeight());

		return gameScene;
	}

	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

		// TODO Auto-generated method stub

		float touchX = pSceneTouchEvent.getX();

		float touchY = pSceneTouchEvent.getY();

		float x_length = touchX - player.getX();// b_touchX is x point for the
												// center of sprite

		float y_length = touchY - player.getY();// b_touchY is y point for the
												// center of sprite

		if (pSceneTouchEvent.getX() != 0 && pSceneTouchEvent.getY() != 0) {

			light.setRotation(MathUtils.radToDeg((float) Math.atan2(y_length,
					x_length)) + 90);

			return true;

		} else {
			return false;
		}
	}
}