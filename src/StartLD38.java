import de.geosearchef.matella.logging.Log;
import game.Game;
import game.ResourceManager;
import input.Input;
import rendering.Renderer;
import update.Updater;

public class StartLD38 {

	public static void main(String args[]) {

		Game.init();
		Renderer.init();
		Game.initFieldEntities();
		ResourceManager.init();
		Game.initUnits();
		
		Input.init();
		
		GameLoop.loop();

	}

}
