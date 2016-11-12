package com.bugil;

import java.util.Iterator;
import java.util.LinkedList;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.debug.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.bugil.SceneManager.SceneType;


public class GameScene extends BaseScene implements IOnSceneTouchListener, IOnMenuItemClickListener,ContactListener {
	
	private static final long TIME_TO_RESSURECTION = 200;
	PhysicsWorld physics;
	
	enum State {
		NEW, PAUSED, PLAY, DEAD, AFTERLIFE;
	}
	
	int tanda=0,flagdead = 0, flaginit = 0, speedY=0, flagmuterawal=0;
	
	Text infoText;
	Text scoreText;
	Text gameoverText;
	
	TiledSprite dandelion;
	Body dandelionBody;
	AutoParallaxBackground pb;
	
	State state = State.NEW;
	State lastState = state;
	long timestamp = 0;
	
	private int score = 0;
	private boolean scored;
	
	private MenuScene menuChildScene;
	
	private final int MENU_TRY = 0;
	private final int MENU_BACKTOMENU = 1;
	
	LinkedList<Pillar> pillars = new LinkedList<Pillar>();
	
//	protected ResourceManager res = ResourceManager.getInstance();
//	protected VertexBufferObjectManager vbom = ResourceManager.getInstance().vbom;
	
	
	@Override
	public void createScene()
	{
		
		physics = new PhysicsWorld(new Vector2(0, 0), true);
		physics.setContactListener(this);
		PillarFactory.getInstance().create(physics);
		
		createBackground();
		createActor();
		createBounds();
		
		createText();
		
		init();
		
		resourcesManager.camera.setChaseEntity(dandelion);
		
		sortChildren();
		setOnSceneTouchListener(this);
		
		registerUpdateHandler(physics);
		
	}

	public void init() {

		score = 0;
		flagdead = 1;
		flaginit = 1;
		flagmuterawal=0;
		
		infoText.setText(resourcesManager.activity.getString(R.string.tap_to_play));
		infoText.setVisible(true);
		
		scoreText.setText(resourcesManager.activity.getString(R.string.hiscore) + resourcesManager.activity.getHighScore());
		infoText.setVisible(true);

		gameoverText.setVisible(false);
		
	}
	
	@Override
	public void reset() {
		super.reset();
		physics.setGravity(new Vector2(0, 0));
		
		Iterator<Pillar> pi = pillars.iterator();
		while (pi.hasNext()) {
		   Pillar p = pi.next();
		   PillarFactory.getInstance().recycle(p);
		   pi.remove();
		}
		
		
		PillarFactory.getInstance().reset();
		
		dandelionBody.setTransform(200 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
				400 / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, 0);
		
		addPillar();
		addPillar();
		addPillar();
		
	    init();
	    
		sortChildren();	

		unregisterUpdateHandler(physics);
		physics.onUpdate(0);
		
		state = State.NEW;
	}

	
	private void createText() {
		HUD hud = new HUD();
		resourcesManager.camera.setHUD(hud);
		infoText = new Text(Constants.CW / 2 , Constants.CH / 2 - 200, resourcesManager.font, "12345678901234567890", vbom);
		hud.attachChild(infoText);
		
		scoreText = new Text(Constants.CW / 2 , Constants.CH / 2 + 200, resourcesManager.font, "12345678901234567890", vbom);
		hud.attachChild(scoreText);
		
		gameoverText = new Text(Constants.CW / 2 , Constants.CH / 2 + 50, resourcesManager.font, "12345678901234567890", vbom);
		hud.attachChild(gameoverText);
	
	}

	private void createBounds() {
		float bigNumber = 999999;
		resourcesManager.repeatingGroundRegion.setTextureWidth(bigNumber);
		//inisialisasi tanah 
		Sprite ground = new Sprite(0, -100, resourcesManager.repeatingGroundRegion, vbom);
		ground.setAnchorCenter(0, 0);
		ground.setZIndex(10);
		attachChild(ground);
		
		Body groundBody = PhysicsFactory.createBoxBody(
				physics, ground, BodyType.StaticBody, Constants.WALL_FIXTURE);
		groundBody.setUserData(Constants.BODY_WALL);
		
		// just to limit the movement at the top
		@SuppressWarnings("unused")
		Body ceillingBody = PhysicsFactory.createBoxBody(
				physics, bigNumber / 2, 820, bigNumber, 20, BodyType.StaticBody, Constants.CEILLING_FIXTURE);
	}

	private void createActor() {
		dandelion = new TiledSprite(200, 400, resourcesManager.dandelionRegion, vbom);
		dandelion.setZIndex(999);
		dandelion.registerUpdateHandler(new IUpdateHandler() {

			@Override
			public void onUpdate(float pSecondsElapsed) {
				if(flagdead == 0)
				{
					if(flaginit == 1)
					{
						double PI = 3.14159265;
		                double Angle = 0-tanda * 180 / PI;
		                
						if (dandelionBody.getLinearVelocity().y > -0.01) {
							dandelion.setRotation((float)Angle);
							Constants.SPEED_X = 12;
						} else {
							dandelion.setCurrentTileIndex(1);
						}
						if(tanda<=5)
						{
							dandelion.setCurrentTileIndex(0);
							tanda++;
						}
						else if(tanda<=10)
						{
							dandelion.setCurrentTileIndex(1);
							tanda++;
							if(tanda==10)
							{
								tanda=0;
							}
						}
					}
				}
				else
				{
					Vector2 v = dandelionBody.getLinearVelocity();
					v.x = 0;
					v.y = -5;
					dandelionBody.setLinearVelocity(v);
				}
			}

			@Override
			public void reset() { }
		});
		dandelionBody = PhysicsFactory.createCircleBody(
				physics, dandelion, BodyType.DynamicBody, Constants.DANDELION_FIXTURE);
		dandelionBody.setFixedRotation(true);
		dandelionBody.setUserData(Constants.BODY_ACTOR);
		physics.registerPhysicsConnector(new PhysicsConnector(dandelion, dandelionBody));
		attachChild(dandelion);
	}

	private void createBackground() {

        pb = new AutoParallaxBackground(0, 0, 0, 40);
		Entity clouds = new Rectangle(0, 0, 1000, 800, vbom);
        clouds.setAnchorCenter(0, 0);
		clouds.setAlpha(0f);
		clouds.attachChild(new Sprite(100, 500, resourcesManager.cloudRegion, vbom));
		clouds.attachChild(new Sprite(300, 700, resourcesManager.cloudRegion, vbom));
		
		clouds.attachChild(new Sprite(500, 600, resourcesManager.cloudRegion, vbom));
		clouds.attachChild(new Sprite(800, 730, resourcesManager.cloudRegion, vbom));
		
		ParallaxEntity pe = new ParallaxEntity(-0.01f, clouds);
		pb.attachParallaxEntity(pe);
		
		Entity gedung1 = new Rectangle(0, 0, 1000, 800, vbom);
		gedung1.setAnchorCenter(0, 0);
		gedung1.setAlpha(0f);
		gedung1.attachChild(new Sprite(100, 200, resourcesManager.gedungpar1Region, vbom));
		gedung1.attachChild(new Sprite(200, 200, resourcesManager.gedungpar1Region, vbom));
		gedung1.attachChild(new Sprite(300, 200, resourcesManager.gedungpar1Region, vbom));
		gedung1.attachChild(new Sprite(400, 200, resourcesManager.gedungpar1Region, vbom));
		gedung1.attachChild(new Sprite(500, 200, resourcesManager.gedungpar1Region, vbom));
		gedung1.attachChild(new Sprite(600, 200, resourcesManager.gedungpar1Region, vbom));
		gedung1.attachChild(new Sprite(700, 200, resourcesManager.gedungpar1Region, vbom));
		gedung1.attachChild(new Sprite(800, 200, resourcesManager.gedungpar1Region, vbom));
		
		ParallaxEntity pee1 = new ParallaxEntity(-0.05f, gedung1);
		pb.attachParallaxEntity(pee1);
		
		Entity gedung = new Rectangle(0, 0, 1000, 800, vbom);
		gedung.setAnchorCenter(0, 0);
		gedung.setAlpha(0f);
		gedung.attachChild(new Sprite(200, 250, resourcesManager.gedungparRegion, vbom));
		gedung.attachChild(new Sprite(400, 250, resourcesManager.gedungparRegion, vbom));
		gedung.attachChild(new Sprite(600, 250, resourcesManager.gedungparRegion, vbom));
		
		ParallaxEntity pee = new ParallaxEntity(-0.1f, gedung);
		pb.attachParallaxEntity(pee);
		
		
		
		
		setBackground(pb);
	}

	private void addPillar() {
		Pillar p = PillarFactory.getInstance().next();
		pillars.add(p);
		attachIfNotAttached(p);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		Debug.e("IS RUN:" + resourcesManager.engine.isRunning() + " ev: " + pSceneTouchEvent.getAction());
		//saat touch dilakukan
		//Log.d("Semua Anggota: ", state.toString());
		if (pSceneTouchEvent.isActionDown()) {
			Constants.SPEED_X = 25;
			if (state == State.PAUSED) {
				if (lastState != State.NEW) {
					registerUpdateHandler(physics);
				}
				state = lastState;
				Debug.d("->" + state);
			} 
			else if (state == State.NEW) {
				reset();
				registerUpdateHandler(physics);
				state = State.PLAY;
				Debug.d("->PLAY");
				dandelionBody.setLinearVelocity(new Vector2(Constants.SPEED_X, 0));
				
				scoreText.setText("0");
				flagdead = 0;
				infoText.setVisible(false);
			} 
			else if (state == State.DEAD) {
				// don't touch the dead!
			} 
			else if (state == State.AFTERLIFE) {

			} 
			
			else {
				
				//physics.setGravity(new Vector2(0, 15)); // gravity keatas
				if(flagdead==0)
				{
					speedY = 0;
					Vector2 v = dandelionBody.getLinearVelocity();
					v.x = Constants.SPEED_X;
					speedY=speedY+8;
					v.y = speedY;
					dandelionBody.setLinearVelocity(v);
					Debug.d("TAP!");
					resourcesManager.sndFly.play();
				}
			}
			
			if(flagmuterawal==0)
			{
				speedY = 0;
				Vector2 v = dandelionBody.getLinearVelocity();
				v.x = Constants.SPEED_X;
				speedY=speedY+5;
				v.y = speedY;
				dandelionBody.setLinearVelocity(v);
				Debug.d("TAP!");
				resourcesManager.sndFly.play();
			}
		}
		//saat touch dilepas
		if (pSceneTouchEvent.isActionUp()) {
			Constants.SPEED_X = 6;
			//physics.setGravity(new Vector2(0, -10)); // gravity kebawah
			if(flagdead==0)
			{
				flagmuterawal=1;
				speedY = 0;
				Vector2 v = dandelionBody.getLinearVelocity();
				v.x = Constants.SPEED_X;
				speedY=speedY-5;
				v.y = speedY;
				dandelionBody.setLinearVelocity(v);
			}
		}
		return false;
	}

//	public void resume() {
//		Debug.d("Game resumed");
//	}
//
//	public void pause() {
//		unregisterUpdateHandler(physics);
//		lastState = state;
//		state = State.PAUSED;
//	}


	
	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		pb.setParallaxValue(resourcesManager.camera.getCenterX());
		if (scored) {
			addPillar();
			sortChildren();
			scored = false;
			resourcesManager.score.play();
			score++;
			scoreText.setText(String.valueOf(score));
		}
		
		// if first pillar is out of the screen, delete it
		if (!pillars.isEmpty()) {
			Pillar fp = pillars.getFirst();
			if (fp.getX() + fp.getWidth() < resourcesManager.camera.getXMin()) {
				PillarFactory.getInstance().recycle(fp);
				pillars.remove();
			}
		}
		
		if (state == State.DEAD && timestamp + TIME_TO_RESSURECTION < System.currentTimeMillis()) {
			state = State.AFTERLIFE;
			Debug.d("->AFTERLIFE");
		}
	}

	private void attachIfNotAttached(Pillar p) {
		if (!p.hasParent()) {
			attachChild(p);
		}
		
	}

	@Override
	public void beginContact(Contact contact) {
		if (Constants.BODY_WALL.equals(contact.getFixtureA().getBody().getUserData()) ||
				Constants.BODY_WALL.equals(contact.getFixtureB().getBody().getUserData())) {
			//saat dead / gameover ngapain
			createMenuChildScene();
			
			resourcesManager.sndFail.play();
			if (score > resourcesManager.activity.getHighScore()) {
				resourcesManager.activity.setHighScore(score);
			}
			gameoverText.setText(resourcesManager.activity.getString(R.string.gameover));
			gameoverText.setVisible(true);
			
			flagdead=1;
			timestamp = System.currentTimeMillis();
			dandelionBody.setLinearVelocity(0, 0);
			for (Pillar p : pillars) {
				p.getPillarUpBody().setActive(false);
				p.getPillarDownBody().setActive(false);
			}
			
		}
		
	}

	@Override
	public void endContact(Contact contact) {
		if (Constants.BODY_SENSOR.equals(contact.getFixtureA().getBody().getUserData()) ||
				Constants.BODY_SENSOR.equals(contact.getFixtureB().getBody().getUserData())) {
			scored = true;
		}
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}
	
	@Override
	public void onBackKeyPressed()
	{
		disposeScene();
		
		SceneManager.getInstance().createMenuSceneAfterGame();
		
		menuChildScene.dispose();
	}
	
	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene()
	{
		camera.setHUD(null);
		camera.setChaseEntity(null); //TODO
		camera.setCenter(240, 400);
	}
	
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
		switch(pMenuItem.getID())
		{
		case MENU_TRY:
			//Load Game Scene!
			state = State.DEAD;
			Debug.d("->DEAD");
			
			reset();
			
			state = State.NEW;
			Debug.d("->NEW");
			
			menuChildScene.dispose();
			
			return true;
		case MENU_BACKTOMENU:

			disposeScene();
			
			SceneManager.getInstance().createMenuSceneAfterGame();
			
			menuChildScene.dispose();
			resourcesManager.back_music.play();
			return true;
		default:
			return false;
		}
	}
	
	private void createMenuChildScene()
	{
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);
		
		final IMenuItem tryMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_TRY, resourcesManager.try_region, vbom), 1.2f, 1);
		final IMenuItem backMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_BACKTOMENU, resourcesManager.backmenu_region, vbom), 1.2f, 1);
		
		menuChildScene.addMenuItem(tryMenuItem);
		menuChildScene.addMenuItem(backMenuItem);

		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		tryMenuItem.setPosition(150,350);
		backMenuItem.setPosition(330,350);
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
	}
}
