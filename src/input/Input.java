package input;

import rendering.Renderer;

public class Input {
	
	public static void input(float d) {
		Renderer.player.checkForInput(d);
	}
	
}
