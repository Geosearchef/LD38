package update;

import game.Field;
import game.Game;
import game.units.Unit;
import lombok.Getter;
import rendering.Renderer;
import util.pathfinding.Path;
import util.pathfinding.Pathfinder;

import static game.Game.units;

import org.lwjgl.input.Mouse;

import de.geosearchef.matella.toolbox.EntityIntersection;
import de.geosearchef.matella.toolbox.MousePicker;

public class Updater {
	
	private static MousePicker mousePicker;
	private static @Getter Field mouseField = null;
	
	public static void update(float d) {
		
		
		//Mouse picker
		mousePicker.updateForMouse();
		EntityIntersection intersection = mousePicker.getEntityIntersection(Renderer.fieldEntities, null, false);
		if(intersection != null) {
			mouseField = Game.fields[Integer.parseInt(intersection.getEntity().getName().split(",")[0])][Integer.parseInt(intersection.getEntity().getName().split(",")[1])];
		}
		
		//Unit update
		for (Unit u : units) {
			u.update(d);
		}
	}
	
	public static void init() {
		mousePicker = new MousePicker(Renderer.camera, Renderer.renderer.getProjectionMatrix());
	}

}
