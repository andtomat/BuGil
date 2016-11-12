package com.bugil;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

public class SceneManager {
	//---------------------------------------------
		// SCENES
		//---------------------------------------------
		
		private BaseScene splashScene;
		private BaseScene gameScene;
		private BaseScene menuScene;
		private BaseScene infoScene;
		private BaseScene helpScene;
		
		//---------------------------------------------
		// VARIABLES
		//---------------------------------------------
		
		private static final SceneManager INSTANCE = new SceneManager();
		
		private SceneType currentSceneType = SceneType.SCENE_SPLASH;
		
		private BaseScene currentScene;
		
		private Engine engine = ResourceManager.getInstance().engine;
		
		public enum SceneType
		{
			SCENE_SPLASH,
			SCENE_MENU,
			SCENE_INFO,
			SCENE_GAME,
			SCENE_HELP
		}
		
		//---------------------------------------------
		// CLASS LOGIC
		//---------------------------------------------
		
		public void setScene(BaseScene scene)
		{
			engine.setScene(scene);
			currentScene = scene;
			currentSceneType = scene.getSceneType();
		}
		
		public void setScene(SceneType sceneType)
		{
			switch (sceneType)
			{
				case SCENE_MENU:
					setScene(menuScene);
					break;
				case SCENE_GAME:
					setScene(gameScene);
					break;
				case SCENE_SPLASH:
					setScene(splashScene);
					break;
				case SCENE_INFO:
					setScene(infoScene);
					break;
				case SCENE_HELP:
					setScene(helpScene);
					break;
				default:
					break;
			}
		}
		
		public void createMenuScene()
		{
			ResourceManager.getInstance().loadMenuandGameResources();
			menuScene = new MenuMainScene();
	        SceneManager.getInstance().setScene(menuScene);
		}
		
		public void createMenuSceneAfterGame()
		{
			ResourceManager.getInstance().loadMenuandGameResources();
			menuScene = new MenuMainScene();
	        SceneManager.getInstance().setScene(menuScene);
	        disposeGameScene();
		}
		
		private void disposeMenuScene()
		{
			menuScene.disposeScene();
			menuScene = null;
		}
		
		
		public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback)
		{
			ResourceManager.getInstance().loadSplashScreen();
			splashScene = new SplashScene();
			currentScene = splashScene;
			pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
		}
		
//		private void disposeSplashScene()
//		{
//			ResourceManager.getInstance().unloadSplashScreen();
//			splashScene.disposeScene();
//			splashScene = null;
//		}
		
		private void disposeGameScene()
		{
			gameScene.disposeScene();
			gameScene = null;
		}
		
		public void loadGameScene(final Engine mEngine)
		{
			//setScene(loadingScene);
			//ResourceManager.getInstance().unloadMenuTextures();

			disposeMenuScene();

			mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
			{
	            public void onTimePassed(final TimerHandler pTimerHandler) 
	            {
	            	mEngine.unregisterUpdateHandler(pTimerHandler);
	            	ResourceManager.getInstance().loadMenuandGameResources();
	        		gameScene = new GameScene();
	        		setScene(gameScene);
	            }
			}));
		}
		
		public void createInfoScene()
		{
			ResourceManager.getInstance().unloadMenuTextures();
			ResourceManager.getInstance().loadInfoResources();
			infoScene = new InfoScene();
	        SceneManager.getInstance().setScene(infoScene);
	        disposeMenuScene();
		}
		
		public void createHelpScene()
		{
			ResourceManager.getInstance().unloadMenuTextures();
			ResourceManager.getInstance().loadHelpResources();
			helpScene = new HelpScene();
	        SceneManager.getInstance().setScene(helpScene);
	        disposeMenuScene();
		}
		
//		private void disposeInfoScene()
//		{
//			infoScene.disposeScene();
//			infoScene = null;
//		}
		//---------------------------------------------
		// GETTERS AND SETTERS
		//---------------------------------------------
		
		public static SceneManager getInstance()
		{
			return INSTANCE;
		}
		
		public SceneType getCurrentSceneType()
		{
			return currentSceneType;
		}
		
		public BaseScene getCurrentScene()
		{
			return currentScene;
		}
	}
