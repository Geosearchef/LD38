package update;

import game.Field;
import game.Game;
import game.units.Unit;
import lombok.Getter;
import rendering.Renderer;
import util.pathfinding.Path;
import util.pathfinding.Pathfinder;

import game.*;

import org.lwjgl.input.Mouse;

import de.geosearchef.matella.toolbox.EntityIntersection;
import de.geosearchef.matella.toolbox.MousePicker;

public class Updater {
	
	public static void update(float d) {
		
		//Unit update
		synchronized (Game.units) {
			for (Unit u : Game.units) {
				u.update(d);
			}
		}
	}
}
