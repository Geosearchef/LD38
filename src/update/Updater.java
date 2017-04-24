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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.input.Mouse;

import de.geosearchef.matella.toolbox.EntityIntersection;
import de.geosearchef.matella.toolbox.MousePicker;

public class Updater {

	// private static final int UNIT_SPAWN_COOLDOWN = 500;
	// private static long nextUnit = System.currentTimeMillis() + 1000l;
	private static Queue<Runnable> scheduled = new LinkedList<Runnable>();

	public static void update(float d) {
		if (Game.gameFinish != 0)
			return;

		// Unit update
		synchronized (Game.units) {
			for (Unit u : Game.units) {
				u.update(d);
			}
			Iterator<Unit> iterator = Game.units.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().isDead())
					iterator.remove();
			}
			while (!scheduled.isEmpty())
				scheduled.poll().run();
		}

		int countLight = 0;
		int countDark = 0;
		for (Unit u : Game.units) {
			if (u.getAlliance() == Alliance.DARK)
				countDark++;
			else if (u.getAlliance() == Alliance.LIGHT)
				countLight++;
		}
		if (countDark == 0 || countLight == 0)
			Game.gameFinish = System.currentTimeMillis();

		for (Field[] fields : Game.fields) {
			for (Field f : fields) {
				f.update(d);
			}
		}

		// if(nextUnit < System.currentTimeMillis()) {
		// nextUnit += UNIT_SPAWN_COOLDOWN;
		//
		// try {
		// synchronized (Game.units) {
		//
		// Game.units.add((Unit)
		// AICalculation.getTeamComposition(Alliance.DARK).getUnitClass().getConstructor(new
		// Class[]{Alliance.class, Field.class}).newInstance(Alliance.DARK,
		// Game.fields[(int) Alliance.DARK.getSpawnPos().x][(int)
		// Alliance.DARK.getSpawnPos().y]));
		// }
		// } catch (Exception e) {
		//
		// }
		//
		// }
		
		ResourceManager.update(d);
	}

	public static void scheduleAfterUpdate(Runnable runnable) {
		scheduled.add(runnable);
	}
}
