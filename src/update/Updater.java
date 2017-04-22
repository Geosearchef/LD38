package update;

import game.units.Unit;

import static game.Game.units;

public class Updater {

	public static void update(float d) {
		for (Unit u : units) {
			u.update(d);
		}
	}

}
