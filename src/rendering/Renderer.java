package rendering;

import de.geosearchef.matella.renderEngine.Camera;
import de.geosearchef.matella.renderEngine.MainLoader;
import de.geosearchef.matella.renderEngine.MasterRenderer;

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
	
	
	public static void init() {
		
	}
	
	public static void render() {
		
	}
}
