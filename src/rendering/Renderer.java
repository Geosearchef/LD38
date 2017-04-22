package rendering;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import de.geosearchef.matella.entities.Entity;
import de.geosearchef.matella.entities.Player;
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
	private static final boolean CAST_SHADOW = true;
	private static final int SHADOW_FRAMEBUFFER_SIZE = 1024;
	
	public static MainLoader loader;
	public static MasterRenderer renderer;
	public static Camera camera;
	public static Player player;
	
	public static void init() {
		
		DisplayManager.createDisplay(TITLE, WIDTH, HEIGHT, FPS_CAP, MSAA, FULLSCREEN, VSYNC);
		loader = new MainLoader();

		renderer = new MasterRenderer(loader, CAST_SHADOW, new Vector2f(SHADOW_FRAMEBUFFER_SIZE, SHADOW_FRAMEBUFFER_SIZE), null, null);
		GPUProfiler.setPROFILING_ENABLED(false);
		
		player = new Player(null, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), false);
		camera = new Camera(player);
		
		
		//Renderer options
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
		
		//Setup scene, load models
		lights.add(new Light(new Vector3f(0f, 50f, 0f), new Vector3f(1f, 1f, 1f)));
		fieldModel = ModelLoader.loadModel("field", loader, null);
		
		//Generate map field entities
		OpenSimplexNoise noise = new OpenSimplexNoise(System.currentTimeMillis());
		for(Field[] fields : Game.fields) {
			for(Field field : fields) {
				fieldEntities.add(new Entity(fieldModel, new Vector3f(field.getPosX(), (float) noise.eval(field.getPosX() / 4f, field.getPosY() / 4f) * 0.4f, field.getPosY()), new Vector3f(0f, 0f, 0f), new Vector3f(1f, 1f, 1f), false));
				fieldEntities.get(fieldEntities.size() - 1).setColor(new Vector4f(field.getPosX() / (float)Game.FIELDS_X, field.getPosY() / (float)Game.FIELDS_Y, 0f, 1f));
				//TODO identifiable
			}
		}
	}
	
	
	public static List<Light> lights = new LinkedList<Light>();
	public static WaterTile waterTile = new WaterTile(new Vector3f(0f, -0.5f, 0f), 100f);
	public static Model fieldModel;
	public static List<Entity> fieldEntities = new LinkedList<Entity>();
	
	public static void render(float d) {
		
		player.getPosition().y = 8f;
		player.getRotation().x = -48f;
		player.getRotation().y = 0f;
		
		camera.updatePosition();
		
		//Scan field entities for update
		float mapSizeX = Game.FIELDS_X * Field.DIMENSIONS.x;
		float mapSizeY = Game.FIELDS_Y * Field.DIMENSIONS.y * 0.75f;
		float mapCenterX =  player.getPosition().x;
		float mapCenterY =  player.getPosition().z - mapSizeY / 2.0f;
		for(Entity entity : fieldEntities) {
			if(Math.abs(mapCenterX - entity.getPosition().x - mapSizeX) < Math.abs(mapCenterX - entity.getPosition().x))
				entity.getPosition().x += mapSizeX;
			if(Math.abs(mapCenterX - entity.getPosition().x + mapSizeX) < Math.abs(mapCenterX - entity.getPosition().x))
				entity.getPosition().x -= mapSizeX;
			if(Math.abs(mapCenterY - entity.getPosition().z - mapSizeY) < Math.abs(mapCenterY - entity.getPosition().z))
				entity.getPosition().z += mapSizeY;
			if(Math.abs(mapCenterY - entity.getPosition().z + mapSizeY) < Math.abs(mapCenterY - entity.getPosition().z))
				entity.getPosition().z -= mapSizeY;
		}
		
		List<Entity> entities = new LinkedList<Entity>();
		entities.addAll(fieldEntities);
//		entities.addAll(Game.units);
		
		List<WaterTile> waterTiles = new LinkedList<WaterTile>();
		waterTiles.add(waterTile);
		
		GPUProfiler.startFrame();
		renderer.fullRender(null, entities, Collections.<Terrain>emptyList(), lights, waterTiles, 0f, camera, null, null);
		GPUProfiler.endFrame();
		GPUProfiler.dumpFrames();
	}
}
