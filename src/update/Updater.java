package update;

import game.Field;
import game.FieldType;
import game.Game;
import game.units.Unit;

public class Updater {
	
	public static void update(float d) {
		
		//Unit update
		synchronized (Game.units) {
			for (Unit u : Game.units) {
				u.update(d);
			}
		}

		for(Field[] fields: Game.fields){
			for(Field f: fields){
				f.update(d);
			}
		}
	}
}
