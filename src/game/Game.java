package game;

import de.geosearchef.matella.worlds.World;
import rendering.Renderer;

public class Game {
	
	public static World world;
	
	
	public static void init() {
		world = new World(Renderer.loader);
		
		
	}
}
