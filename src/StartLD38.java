import de.geosearchef.matella.logging.Log;
import game.Game;
import rendering.Renderer;

public class StartLD38 {
	
	public static void main(String args[]) {
		
		Game.init();
		Renderer.init();
		
		GameLoop.loop();
		
	}
	
}
