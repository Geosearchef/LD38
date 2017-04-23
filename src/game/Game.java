package game;

import java.util.ArrayList;
import java.util.List;

import game.units.Unit;

public class Game {

	public final static int FIELDS_X = 48;
	public final static int FIELDS_Y = 48;

	public static Field[][] fields = new Field[FIELDS_X][FIELDS_Y];
	public static List<Unit> units = new ArrayList<Unit>();

	public static void init() {
		// init Fields
		for (int x = 0; x < FIELDS_X; x++) {
			for (int y = 0; y < FIELDS_Y; y++) {
				fields[x][y] = new Field(x, y, Math.random() > 0.3f ? FieldType.GRASS : FieldType.WATER);
			}
		}
		// calculate Neighbors
		for (int x = 0; x < FIELDS_X; x++) {
			for (int y = 0; y < FIELDS_Y; y++) {
				fields[x][y].calculateNeighbors(fields);
			}
		}

		// units.add(new Unit())

	}
}
