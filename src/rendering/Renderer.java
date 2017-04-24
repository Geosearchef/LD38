package rendering;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.geosearchef.matella.entities.Entity;
import de.geosearchef.matella.entities.Player;
import de.geosearchef.matella.guis.GuiRenderer;
import de.geosearchef.matella.models.Model;
import de.geosearchef.matella.models.ModelLoader;
import de.geosearchef.matella.profiling.GPUProfiler;
import de.geosearchef.matella.renderEngine.Camera;
import de.geosearchef.matella.renderEngine.DisplayManager;
import de.geosearchef.matella.renderEngine.Light;
import de.geosearchef.matella.renderEngine.MainLoader;
import de.geosearchef.matella.renderEngine.MasterRenderer;
import de.geosearchef.matella.terrains.Terrain;
import de.geosearchef.matella.water.WaterTile;
import game.Field;
import game.Game;
import input.Input;
import lombok.Getter;
import lombok.NonNull;
import util.OpenSimplexNoise;

public class Renderer {

	private static final String TITLE = "LD38";

	private static final int WIDTH = 1920;
	private static final int HEIGHT = 1080;
	private static final int FPS_CAP = 60;
	private static final int MSAA = 16;
	private static final boolean FULLSCREEN = false;
	private static final boolean VSYNC = true;
	private static final boolean CAST_SHADOW = false;
	private static final int SHADOW_FRAMEBUFFER_SIZE = 1024;

	public static MainLoader loader;
	public static MasterRenderer renderer;
	public static Camera camera;
	public static Player player;
	
	public static GuiRenderer guiRenderer;
	public static int meleeIcon;
	public static int rangedIcon;
	public static int farmerIcon;
	public static int farmModeIcon;
	public static int wallModeIcon;

	public static void init() {

		DisplayManager.createDisplay(TITLE, WIDTH, HEIGHT, FPS_CAP, MSAA, FULLSCREEN, VSYNC);
		loader = new MainLoader();

		renderer = new MasterRenderer(loader, CAST_SHADOW, new Vector2f(SHADOW_FRAMEBUFFER_SIZE, SHADOW_FRAMEBUFFER_SIZE), new LDEntityShaderHook(), null);
		GPUProfiler.setPROFILING_ENABLED(false);
		
		guiRenderer = new GuiRenderer(loader);

		player = new Player(null, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), false);
		player.setMovementSpeed(10);
		player.setCtrlMultiplier(1);
		camera = new Camera(player);

		// Renderer options
		renderer.setFarPlane(1000.0f);
		renderer.setAmbientLighting(0.2f);
		renderer.setFogDensity(0.0015f);
		renderer.setFogGradient(5.0f);
		renderer.setSkyColor(new Vector3f(89f / 255f, 119f / 255f, 143f / 255f));
		renderer.setWaterWaveSpeed(0.03f);
		renderer.setWaterWaveStrength(0.04f);
		renderer.setWaterShineDamper(20.0f);
		renderer.setWaterReflectivity(0.5f);

		renderer.setRenderWaterTextures(true);

		// Setup scene, load models
		lights.add(new Light(new Vector3f(0f, 50f, 0f), new Vector3f(1f, 1f, 1f)));
		lights.add(new Light(new Vector3f(0f, 50f, 0f), new Vector3f(1f, 1f, 1f)));

		loadAssets();
	}

	public static List<Model> models = new LinkedList<Model>();
	public static List<Light> lights = new LinkedList<Light>();
	public static WaterTile waterTile = new WaterTile(new Vector3f(0f, -0.25f, 0f), 100f);
	public static Model fieldModel;
	public static List<Entity> fieldEntities = new LinkedList<Entity>();

	public static void render(float d) {

		player.getPosition().y = 8f;
		player.getRotation().x = -48f;
		player.getRotation().y = 0f;

		camera.updatePosition();
		lights.get(1).setPosition(new Vector3f(camera.getPosition().x + 50f, lights.get(0).getPosition().y, camera.getPosition().z + 50f));
		lights.get(0).setPosition(new Vector3f(camera.getPosition().x + 0f, lights.get(0).getPosition().y, camera.getPosition().z - 50f));
		lights.get(0).setColor(new Vector3f(1, 1, 1));
		renderer.setShadowMapCenter(new Vector3f(camera.getPosition()));

		float mapSizeX = Game.MAP_SIZE_X;
		float mapSizeY = Game.MAP_SIZE_Y;
		float mapCenterX = player.getPosition().x;
		float mapCenterY = player.getPosition().z - mapSizeY / 2.0f;

		// waterTile.setPosition(new Vector3f(player.getPosition().x / (16f /
		// 100f), -0.4f, player.getPosition().z));
		if (Math.abs(mapCenterX - waterTile.getPosition().x - 100f / 16f) < Math.abs(mapCenterX - waterTile.getPosition().x))
			waterTile.getPosition().x += 100f / 16f;
		if (Math.abs(mapCenterX - waterTile.getPosition().x + 100f / 16f) < Math.abs(mapCenterX - waterTile.getPosition().x))
			waterTile.getPosition().x -= 100f / 16f;
		if (Math.abs(mapCenterY - waterTile.getPosition().z - 100f / 16f) < Math.abs(mapCenterY - waterTile.getPosition().z))
			waterTile.getPosition().z += 100f / 16f;
		if (Math.abs(mapCenterY - waterTile.getPosition().z + 100f / 16f) < Math.abs(mapCenterY - waterTile.getPosition().z))
			waterTile.getPosition().z -= 100f / 16f;

		// Scan field entities for update
		for (Entity entity : fieldEntities) {
			if (Math.abs(mapCenterX - entity.getPosition().x - mapSizeX) < Math.abs(mapCenterX - entity.getPosition().x))
				entity.getPosition().x += mapSizeX;
			if (Math.abs(mapCenterX - entity.getPosition().x + mapSizeX) < Math.abs(mapCenterX - entity.getPosition().x))
				entity.getPosition().x -= mapSizeX;
			if (Math.abs(mapCenterY - entity.getPosition().z - mapSizeY) < Math.abs(mapCenterY - entity.getPosition().z))
				entity.getPosition().z += mapSizeY;
			if (Math.abs(mapCenterY - entity.getPosition().z + mapSizeY) < Math.abs(mapCenterY - entity.getPosition().z))
				entity.getPosition().z -= mapSizeY;
		}

		List<Entity> entities = new LinkedList<Entity>();
		entities.addAll(fieldEntities);
		synchronized (Game.units) {
			entities.addAll(Game.units);
		}

		List<WaterTile> waterTiles = new LinkedList<WaterTile>();
		waterTiles.add(waterTile);

		GPUProfiler.startFrame();
		renderer.fullRender(null, entities, Collections.<Terrain>emptyList(), lights, waterTiles, waterTile.getPosition().y, camera, null, null);
		
		renderGUI();
		
		GPUProfiler.endFrame();
		GPUProfiler.dumpFrames();
	}
	
	public static void renderGUI() {
		float aspect = DisplayManager.getHEIGHT() / DisplayManager.getWIDTH();
		
		guiRenderer.begin();
		
		guiRenderer.render(meleeIcon, new Vector2f(-0.97f, -0.95f), 0f, new Vector2f(0.04f * aspect, 0.04f), new Vector4f(1f, 1f, 1f, 1f));
		guiRenderer.render(rangedIcon, new Vector2f(-0.97f, -0.85f), 0f, new Vector2f(0.04f * aspect, 0.04f), new Vector4f(1f, 1f, 1f, 1f));
		guiRenderer.render(farmerIcon, new Vector2f(-0.97f, -0.75f), 0f, new Vector2f(0.04f * aspect, 0.04f), new Vector4f(1f, 1f, 1f, 1f));
		
		guiRenderer.render(meleeIcon, new Vector2f(+0.97f, -0.95f), 0f, new Vector2f(0.04f * aspect, 0.04f), new Vector4f(0f, 0f, 0f, 1f));
		guiRenderer.render(rangedIcon, new Vector2f(+0.97f, -0.85f), 0f, new Vector2f(0.04f * aspect, 0.04f), new Vector4f(0f, 0f, 0f, 1f));
		guiRenderer.render(farmerIcon, new Vector2f(+0.97f, -0.75f), 0f, new Vector2f(0.04f * aspect, 0.04f), new Vector4f(0f, 0f, 0f, 1f));
		
		guiRenderer.render(farmModeIcon, new Vector2f(-0.88f, 0.93f), 0f, new Vector2f(0.05f * aspect, 0.05f), Input.farmlandBuildMode ? new Vector4f(1f, 1f, 1f, 1f) : new Vector4f(0.7f, 0.7f, 0.7f, 1f));
		guiRenderer.render(wallModeIcon, new Vector2f(-0.95f, 0.93f), 0f, new Vector2f(0.05f * aspect, 0.05f), Input.wallBuildMode ? new Vector4f(1f, 1f, 1f, 1f) : new Vector4f(0.7f, 0.7f, 0.7f, 1f));
		
		guiRenderer.end();
	}

	public static void loadAssets() {
		models.add(ModelLoader.loadModel("unit0", loader, null));
		
		meleeIcon = loader.loadGUITexture("gui/square");
		rangedIcon = loader.loadGUITexture("gui/ranged");
		farmerIcon = loader.loadGUITexture("gui/circle");
		farmModeIcon = loader.loadGUITexture("gui/farmlandBuildMode");
		wallModeIcon = loader.loadGUITexture("gui/wallBuildMode");
	}
}
