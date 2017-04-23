package game;

import java.util.ArrayList;
import java.util.List;

import game.units.Alliance;
import game.units.Unit;
import game.units.UnitMelee;

public class Game {

    public final static int FIELDS_X = 48;
    public final static int FIELDS_Y = 48;
    public final static float MAP_SIZE_X = Game.FIELDS_X * Field.DIMENSIONS.x;
    public final static float MAP_SIZE_Y = Game.FIELDS_Y * Field.DIMENSIONS.y * 0.75f;

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

    }

    public static void initUnits() {
	units.add(new UnitMelee(Alliance.LIGHT, fields[FIELDS_X / 4][FIELDS_Y / 2]));
	units.add(new UnitMelee(Alliance.LIGHT, fields[FIELDS_X / 4][FIELDS_Y / 2]));

	units.add(new UnitMelee(Alliance.DARK, fields[3 * FIELDS_X / 4][FIELDS_Y / 2]));
	units.add(new UnitMelee(Alliance.DARK, fields[3 * FIELDS_X / 4][FIELDS_Y / 2]));
    }
}
