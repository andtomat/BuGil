package com.bugil;


import org.andengine.engine.camera.Camera;
import org.andengine.entity.Entity;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import com.bugil.SceneManager.SceneType;

import android.util.Log;
//import org.andengine.entity.sprite.ButtonSprite;
//import org.andengine.input.touch.TouchEvent;
//import android.view.MotionEvent;

public class MenuMainScene extends BaseScene implements IOnMenuItemClickListener
{
	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------
	
	private MenuScene menuChildScene;
	
	private final int MENU_PLAY = 0;
	private final int MENU_HELP = 1;
	private final int MENU_SOUND = 2;
	
	AutoParallaxBackground pb;
	//---------------------------------------------
	// METHODS FROM SUPERCLASS
	//---------------------------------------------

	@Override
	public void createScene()
	{
		createBackground();
		createMenuChildScene();
		
        //resourcesManager.music.setLooping(true);
        //resourcesManager.music.play();
	}

	@Override
	public void onBackKeyPressed()
	{
		System.exit(0);
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_MENU;
	}
	

	@Override
	public void disposeScene()
	{
		this.detachSelf();
		this.dispose();
	}
	
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
		switch(pMenuItem.getID())
		{
		case MENU_PLAY:
			//Load Game Scene!
			Log.d("Semua Anggota: ", "aa");
			
			SceneManager.getInstance().loadGameScene(engine);
			return true;
		case MENU_HELP:
			//Load Game Scene!
			Log.d("Semua Anggota: ", "BB");
			
			SceneManager.getInstance().createHelpScene();
			return true;
		case MENU_SOUND:
			//Load Game Scene!
			Log.d("Semua Anggota: ", "BB");
			
			SceneManager.getInstance().createInfoScene();
			return true;
		default:
			return false;
		}
	}
	
	//---------------------------------------------
	// CLASS LOGIC
	//---------------------------------------------
	
	private void createBackground()
	{
		pb = new AutoParallaxBackground(0, 0, 0, 40);
		Entity clouds = new Rectangle(0, 0, 1000, 800, vbom);
        clouds.setAnchorCenter(0, 0);
		clouds.setAlpha(0f);
		clouds.attachChild(new Sprite(100, 140, resourcesManager.cloudRegion, vbom));
		clouds.attachChild(new Sprite(300, 350, resourcesManager.cloudRegion, vbom));
		
		clouds.attachChild(new Sprite(500, 200, resourcesManager.cloudRegion, vbom));
		clouds.attachChild(new Sprite(700, 300, resourcesManager.cloudRegion, vbom));
		clouds.attachChild(new Sprite(900, 170, resourcesManager.cloudRegion, vbom));
		
		ParallaxEntity pe = new ParallaxEntity(0.2f, clouds);
		
		pb.attachParallaxEntity(pe);
		
		setBackground(pb);

		attachChild(new Sprite(240, 400, resourcesManager.menu_background_region, vbom)
		{
    		@Override
            protected void preDraw(GLState pGLState, Camera pCamera) 
    		{
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
		});
	}
	
	private void createMenuChildScene()
	{
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);
		
		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 1.2f, 1);
		final IMenuItem helpMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_HELP, resourcesManager.help_region, vbom), 0.8f, 0.7f);
		final IMenuItem soundMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SOUND, resourcesManager.info_region, vbom), 0.8f, 0.7f);
		
		
		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(helpMenuItem);
		menuChildScene.addMenuItem(soundMenuItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		playMenuItem.setPosition(250,240);
		helpMenuItem.setPosition(40,40);
		soundMenuItem.setPosition(444,40);
		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
	}
}