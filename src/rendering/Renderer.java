package rendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.geosearchef.matella.entities.Player;
import de.geosearchef.matella.renderEngine.Camera;
import de.geosearchef.matella.renderEngine.DisplayManager;
import de.geosearchef.matella.renderEngine.MainLoader;
import de.geosearchef.matella.renderEngine.MasterRenderer;
import game.Game;

public class Renderer {
	
	private static final String TITLE = "LD38";

	private static final int WIDTH = 1366;
	private static final int HEIGHT = 768;
	private static final int FPS_CAP = 60;
	private static final int MSAA = 1;
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
		
		player = new Player(null, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), false);
		camera = new Camera(player);
		
		//TODO: renderer world init
	}
	
	public static void render(float d) {
		
		//TODO world render coder
		
		
		renderer.fullRender(null, Game.entities, null, Game.lights, waterTiles, 0f, camera, null, null);
		Game.world.render(renderer, camera, null);
	}
}
