package update;

import game.Field;
import game.Game;
import game.units.Alliance;
import game.units.Unit;
import game.units.tasks.AICalculation;
import lombok.Getter;
import rendering.Renderer;
import util.pathfinding.Path;
import util.pathfinding.Pathfinder;

import game.*;

import java.lang.reflect.InvocationTargetException;

import org.lwjgl.input.Mouse;

import de.geosearchef.matella.toolbox.EntityIntersection;
import de.geosearchef.matella.toolbox.MousePicker;

public class Updater {
	
	private static final int UNIT_SPAWN_COOLDOWN = 500;
	private static long nextUnit = System.currentTimeMillis() + 1000l;
	
	
	public static void update(float d) {
		
		
		
		//Unit update
		synchronized (Game.units) {
			for (Unit u : Game.units) {
				u.update(d);
			}
		}
		
		
		if(nextUnit < System.currentTimeMillis()) {
			nextUnit += UNIT_SPAWN_COOLDOWN;
			
			try {
				Game.units.add((Unit) AICalculation.getTeamComposition(Alliance.LIGHT).getUnitClass().getConstructor(new Class[]{Alliance.class, Field.class}).newInstance(Alliance.LIGHT, Game.fields[(int) Alliance.LIGHT.getSpawnPos().x][(int) Alliance.LIGHT.getSpawnPos().y]));
				Game.units.add((Unit) AICalculation.getTeamComposition(Alliance.DARK).getUnitClass().getConstructor(new Class[]{Alliance.class, Field.class}).newInstance(Alliance.DARK, Game.fields[(int) Alliance.DARK.getSpawnPos().x][(int) Alliance.DARK.getSpawnPos().y]));
			} catch (Exception e) {
				
			}
			
		}
	}
}
