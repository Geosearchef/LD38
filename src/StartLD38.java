import de.geosearchef.matella.logging.Log;
import game.Game;
import rendering.Renderer;
import update.Updater;

public class StartLD38 {

	public static void main(String args[]) {

		Game.init();
		Renderer.init();
		Game.initFieldEntities();
		Game.initUnits();
		
		Updater.init();
		
		GameLoop.loop();

	}

}
