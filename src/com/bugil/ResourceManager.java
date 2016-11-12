package com.bugil;


import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.bitmap.BitmapTextureFormat;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

public class ResourceManager {
	private static final ResourceManager INSTANCE = new ResourceManager();

	//font
	public Font font;
	
	//common objects
	public GameActivity activity;
	public Engine engine;
	public Camera camera;
	public VertexBufferObjectManager vbom;
	
	//gfx
	
	private BitmapTextureAtlas splashTextureAtlas;
	public ITextureRegion splash_region;
	
	public ITextureRegion menu_background_region;
	public ITextureRegion play_region;
	public ITextureRegion help_region;
	public ITextureRegion info_region;
	public ITextureRegion try_region;
	public ITextureRegion backmenu_region;
	public ITextureRegion logo_region;
	public ITextureRegion backtomenu_region;
	public ITextureRegion info_background_region;
	public ITextureRegion help_background_region;
	
	public TextureRegion parallaxBackLayerRegion;
	
	private BitmapTextureAtlas repeatingGroundAtlas;
	public TextureRegion repeatingGroundRegion;
	
	private BuildableBitmapTextureAtlas gameObjectsAtlas;
		
	public TextureRegion gedungparRegion;
	public TextureRegion gedungpar1Region;
	public TextureRegion cloudRegion;
	public TiledTextureRegion dandelionRegion;
	public TextureRegion pillarRegion;
	
	public TextureRegion bannerRegion;
	
	//sfx
	public Sound sndFly;
	public Sound sndFail;
	public Sound score;
	public Sound back_music;
	
	private ResourceManager() {}
	
	public static ResourceManager getInstance() {
		return INSTANCE;
	}

	public void create(GameActivity activity, Engine engine, Camera camera, VertexBufferObjectManager vbom) {
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;
		this.vbom = vbom;
	}
	
	public void loadFont() {
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, Color.WHITE_ABGR_PACKED_INT, 2, Color.BLACK_ABGR_PACKED_INT);
		font.load();
		
	}

	public void unloadFont() {
		font.unload();
	}
	
	public void loadSplashScreen()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
        splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
        splashTextureAtlas.load();	
	}
	
	public void unloadSplashScreen()
	{
		splashTextureAtlas.unload();
		splash_region = null;
	}
	
	public void loadInfoResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");	
		
		repeatingGroundAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
		repeatingGroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(repeatingGroundAtlas, activity, "ground.png", 0, 0);
		repeatingGroundAtlas.load();
				
		gameObjectsAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 
					1024, 1024, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);

		
		info_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity, "backgroundinfo1.png");
		
		backtomenu_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity, "backmenubtn.png");
		
		try {
			gameObjectsAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 0, 2));
			gameObjectsAtlas.load();
			
		} catch (final TextureAtlasBuilderException e) {
			throw new RuntimeException("Error while loading Splash textures", e);
		}	     
	}
	
	public void loadHelpResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");	
		
		repeatingGroundAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
		repeatingGroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(repeatingGroundAtlas, activity, "ground.png", 0, 0);
		repeatingGroundAtlas.load();
				
		gameObjectsAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 
					1024, 1024, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);

		
		help_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity, "helpbackground.png");
		
		backtomenu_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity, "backmenubtn.png");
		
		try {
			gameObjectsAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 0, 2));
			gameObjectsAtlas.load();
			
		} catch (final TextureAtlasBuilderException e) {
			throw new RuntimeException("Error while loading Splash textures", e);
		}	     
	}
	
	public void loadMenuandGameResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");	
		
		repeatingGroundAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.REPEATING_BILINEAR_PREMULTIPLYALPHA);
		repeatingGroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(repeatingGroundAtlas, activity, "ground.png", 0, 0);
		repeatingGroundAtlas.load();
				
		gameObjectsAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 
					1024, 1024, BitmapTextureFormat.RGBA_8888, TextureOptions.BILINEAR);
		
	       
		cloudRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity.getAssets(), "cloud.png");
		
		gedungparRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity.getAssets(), "gedungpar.png");
		
		gedungpar1Region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity.getAssets(), "gedungpar1.png");
			
		pillarRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity.getAssets(), "pillar.png");
			
		dandelionRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				gameObjectsAtlas, activity.getAssets(), "sprite1.png", 2, 1);

		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity, "menu_background1.png");

		
		play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity, "playbtn.png");
        
		help_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity, "helpbtn.png");
		
		info_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity, "infobtn.png");
		
		try_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity, "trybtn.png");
		
		backmenu_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity, "cmenubtn.png");
		
		logo_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameObjectsAtlas, activity, "logo.png");
			
		
		try {
			gameObjectsAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(2, 0, 2));
			gameObjectsAtlas.load();
			
		} catch (final TextureAtlasBuilderException e) {
			throw new RuntimeException("Error while loading Splash textures", e);
		}		
		
		try {
			sndFly = SoundFactory.createSoundFromAsset(activity.getEngine().getSoundManager(), activity, "sfx/fly.wav");
			sndFail = SoundFactory.createSoundFromAsset(activity.getEngine().getSoundManager(), activity, "sfx/fail.wav");
			score = SoundFactory.createSoundFromAsset(activity.getEngine().getSoundManager(), activity, "sfx/ambil_kunci.wav");
			back_music = SoundFactory.createSoundFromAsset(activity.getEngine().getSoundManager(), activity, "sfx/back_music.wav");
			
		} catch (Exception e) {
			throw new RuntimeException("Error while loading sounds", e);
		} 
	}		
	
	public void unloadMenuTextures()
	{
		gameObjectsAtlas.unload();
	}
	
	public void loadMenuTextures()
	{
		gameObjectsAtlas.load();
	}
	
}
