import de.geosearchef.matella.logging.Log;
import game.Game;
import input.Input;
import rendering.Renderer;
import update.Updater;

public class StartLD38 {

	public static void main(String args[]) {

		Game.init();
		Renderer.init();
		Game.initFieldEntities();
		Game.initUnits();
		
		Input.init();
		
		GameLoop.loop();

	}

}
