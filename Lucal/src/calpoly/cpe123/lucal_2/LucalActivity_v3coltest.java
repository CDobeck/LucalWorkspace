//https://github.com/bobsomers/lua-love-redux
package calpoly.cpe123.lucal_2;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.FixedStepEngine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.camera.SmoothCamera;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl.IOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObject;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObjectGroup;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXProperties;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTile;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTileProperty;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMapProperty;
import org.anddev.andengine.entity.layer.tiled.tmx.util.exception.TMXLoadException;
import org.anddev.andengine.extension.physics.box2d.*;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.opengl.texture.region.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class LucalActivity_v3coltest extends BaseGameActivity {
	private static final int CAMERA_WIDTH = 800, CAMERA_HEIGHT = 480,
			CELL_SIZE = 32, GRID_WIDTH = 128, GRID_HEIGHT = 128;

	private TMXTiledMap map = null;  // these two variables are used in the createUnwalkableObjects method
	private PhysicsWorld world;      // 

	
	public static LucalActivity_v3coltest gameInstance;
	MovingSprite player;
	TiledTextureRegion playerTexture;
	BoundCamera camera;
	Scene scene;
	DigitalOnScreenControl hud;

	TMXTiledMap background;
	BitmapTextureAtlas atlas;

	boolean[][] passables = new boolean[GRID_WIDTH][GRID_HEIGHT];

	BitmapTextureAtlas mOnScreenControlTexture;
	TextureRegion mOnScreenControlBaseTextureRegion;
	TextureRegion mOnScreenControlKnobTextureRegion;

	DigitalOnScreenControl mDigitalOnScreenControl;

	PhysicsHandler handler;

	@Override
	public Engine onLoadEngine() {
		gameInstance = this;
		// camera = new SmoothCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 5, 5,
		// 1);
		camera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, 0,
				CELL_SIZE * GRID_WIDTH, 0, CELL_SIZE * GRID_HEIGHT);
		return new FixedStepEngine(new EngineOptions(true,
				ScreenOrientation.LANDSCAPE,
				new RatioResolutionPolicy(800, 480), camera), 30);
	}

	@Override
	public void onLoadResources() {
		atlas = new BitmapTextureAtlas(128, 128);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		playerTexture = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(atlas, this, "sample_sprite.png", 0, 0,
						4, 4);
		player = new MovingSprite(64, 64, playerTexture);
		camera.setChaseEntity(player);
		// tells
		// the
		// game
		// where
		// all
		// the
		// BitmapTextureAtlas
		// (graphics)
		// are
		this.mEngine.getTextureManager().loadTexture(atlas);
	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger()); //new
		
		world = new FixedStepPhysicsWorld(30, new Vector2(0,0), false); //new
		scene = new Scene();
		scene.registerUpdateHandler(world); //new


		this.mOnScreenControlTexture = new BitmapTextureAtlas(128, 128,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		// TODO Create base graphic
		this.mOnScreenControlBaseTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_base.png", 0, 0);
		// TODO Create knob graphic
		this.mOnScreenControlKnobTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(this.mOnScreenControlTexture, this,
						"onscreen_control_knob.png", 64 - 16, 0);

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
						if (Math.abs(pValueX) > Math.abs(pValueY)) {
							if (pValueX > 0)
								player.moveSprite(MovingSprite.EAST);
							else
								player.moveSprite(MovingSprite.WEST);
						} else if (Math.abs(pValueX - pValueY) > 0.1) {
							if (pValueY > 0)
								player.moveSprite(MovingSprite.SOUTH);
							else
								player.moveSprite(MovingSprite.NORTH);
						}
						else
						{
							player.moveSprite(MovingSprite.NONE);
						}
					}

				});
		this.mDigitalOnScreenControl.getControlBase().setBlendFunction(
				GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		this.mDigitalOnScreenControl.getControlBase().setAlpha(0.5f);
		this.mDigitalOnScreenControl.getControlBase().setScaleCenter(0, 128);
		this.mDigitalOnScreenControl.getControlBase().setScale(1.25f);
		this.mDigitalOnScreenControl.getControlKnob().setScale(1.25f);
		this.mDigitalOnScreenControl.refreshControlKnobPosition();

		scene.setChildScene(this.mDigitalOnScreenControl);

		//
		// TMX Tile map
		//
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
			map = tmxLoader.loadFromAsset(this, "tmx/floor_background.tmx");
			
		} catch (final TMXLoadException tmxle) {
			Debug.e(tmxle);
		}
		
		
		final TMXLayer tmxLayer = map.getTMXLayers().get(0);
		for(int y = 0; y < map.getTileRows(); y++)
		{
			for(int x= 0; x < map.getTileColumns(); x++)
			{
				map.getTMXLayers().get(0).getTMXTile(x, y).setGlobalTileID(map, 2);
			}
		}
//		for(int y = 0; y < map.getTMXLayers().get(0).getTileRows(); y++)
//		{
//			for(int x = 0; x < map.getTMXLayers().get(0).getTileColumns(); x++)
//			{
//				passables[x][y] = map.getTMXLayers().get(0).getTMXTile(x, y) == 1;
//			}
//		}
		
        this.createUnwalkableObjects(map); //new
        final FixtureDef playerFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0.5f);
        Body mPlayerBody = PhysicsFactory.createBoxBody(world, player, BodyType.DynamicBody, playerFixtureDef);
        world.registerPhysicsConnector(new PhysicsConnector(player, mPlayerBody, true, false) {
            @Override
            public void onUpdate(float pSecondsElapsed){
                    super.onUpdate(pSecondsElapsed);
                    camera.updateChaseEntity();
            }
        });  // new
        
		scene.attachChild(tmxLayer);

		scene.attachChild(player);

		return scene;
	}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub

	}
    private void createUnwalkableObjects(TMXTiledMap map){ //new
        // Loop through the object groups
         for(final TMXObjectGroup group: this.map.getTMXObjectGroups()) {
                 if(group.getTMXObjectGroupProperties().containsTMXProperty("wall", "true")){
                         // This is our "wall" layer. Create the boxes from it
                         for(final TMXObject object : group.getTMXObjects()) {
                                final Rectangle rect = new Rectangle(object.getX(), object.getY(),object.getWidth(), object.getHeight());
                                final FixtureDef boxFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 1f);
                                PhysicsFactory.createBoxBody(this.world, rect, BodyType.StaticBody, boxFixtureDef);
                                rect.setVisible(false);
                                scene.attachChild(rect);
                         }
                 }
         }
}

}