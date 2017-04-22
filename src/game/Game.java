package game;

public class Game {

	public final static int FIELDS_X = 32;
	public final static int FIELDS_Y = 18;

	public static Field[][] fields = new Field[FIELDS_X][FIELDS_Y];

	public static void init() {
		// init Fields
		for (int x = 0; x < FIELDS_X; x++) {
			for (int y = 0; y < FIELDS_Y; y++) {
				fields[x][y] = new Field(x, y, FieldType.GRASS);
			}
		}
		// calculate Neighbors
		for (int x = 0; x < FIELDS_X; x++) {
			for (int y = 0; y < FIELDS_Y; y++) {
				fields[x][y].calculateNeighbors(fields);
			}
		}

	}
}
