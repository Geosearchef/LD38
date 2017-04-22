import org.lwjgl.opengl.Display;

import de.geosearchef.matella.renderEngine.DisplayManager;
import de.geosearchef.matella.toolbox.Timer;
import input.Input;
import rendering.Renderer;
import update.Updater;

public class GameLoop {
	
	
	public static void loop() {
		
		Timer.init();
		
		while(! Display.isCloseRequested()) {
			
			float d = Timer.getTime();
			
			Input.input(d);
			Updater.update(d);
			Renderer.render(d);
			
			DisplayManager.updateDisplay();
		}
		
		
		
	}
	
}
