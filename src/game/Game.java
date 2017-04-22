package game;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import de.geosearchef.matella.logging.Log;
import de.geosearchef.matella.renderEngine.Light;
import de.geosearchef.matella.worlds.World;
import rendering.Renderer;

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
	}
}
