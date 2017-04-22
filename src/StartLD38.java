import game.Game;
import rendering.Renderer;

public class StartLD38 {
	
	public static void main(String args[]) {
		
		Renderer.init();
		Game.init();
		
		GameLoop.loop();
		
	}
	
}
