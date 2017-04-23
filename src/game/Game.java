package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.geosearchef.matella.entities.Entity;
import de.geosearchef.matella.models.Model;
import de.geosearchef.matella.models.ModelLoader;
import game.units.Alliance;
import game.units.Unit;
import game.units.UnitMelee;
import util.OpenSimplexNoise;

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

	public static void initUnits() {
		for (int i = 0; i < 500; i++) {
			units.add(new UnitMelee(Alliance.LIGHT, fields[FIELDS_X / 4][FIELDS_Y / 2]));
		}
		// units.add(new UnitMelee(Alliance.LIGHT, fields[FIELDS_X / 4][FIELDS_Y
		// / 2]));
		//
		// units.add(new UnitMelee(Alliance.DARK, fields[3 * FIELDS_X /
		// 4][FIELDS_Y / 2]));
		// units.add(new UnitMelee(Alliance.DARK, fields[3 * FIELDS_X /
		// 4][FIELDS_Y / 2]));
	}

	public static void initFieldEntities() {

		Model fieldModel = ModelLoader.loadModel("field", rendering.Renderer.loader, null);
		// Generate map field entities
		OpenSimplexNoise noise = new OpenSimplexNoise(System.currentTimeMillis());
		for (Field[] fields : Game.fields) {
			for (Field field : fields) {
				float fieldHeight = (float) noise.eval(field.getPosX() / 4f, field.getPosY() / 4f) * 0.4f;
				Entity fieldEntity = new Entity(fieldModel, new Vector3f(field.getPosX(), fieldHeight, field.getPosY()), new Vector3f(0f, 0f, 0f), new Vector3f(1f, 1f, 1f), false);
				fieldEntity.setName(field.getRawPosX() + "," + field.getRawPosY());
				rendering.Renderer.fieldEntities.add(fieldEntity);

				field.setEntity(fieldEntity);
				field.setHeight(fieldHeight);
			}
		}

		Map<Field, Float> newHeights = new HashMap<Field, Float>();
		for (Field[] fields : Game.fields) {
			for (Field field : fields) {
				Stream.of(field.getNeighbors()).filter(n -> Vector2f.sub(new Vector2f(field.getRawPosX(), field.getRawPosY()), new Vector2f(n.getRawPosX(), n.getRawPosY()), null).lengthSquared() > 9).mapToDouble(n -> n.getHeight()).average().ifPresent(avg -> newHeights.put(field, field.getHeight() * 0.68f + (float) avg * 0.32f));
			}
		}
		newHeights.entrySet().forEach(entry -> entry.getKey().setHeight(entry.getValue()));

		for (Field[] fields : Game.fields) {
			for (Field field : fields) {

				if (field.getHeight() < -0.165f)
					field.setType(FieldType.WATER);
			}
		}

		for (Field[] fields : Game.fields) {
			for (Field field : fields) {
				if (field.getType() != FieldType.WATER) {

					waterSandSearch: if (field.getHeight() < -0.05) {
						for (Field f : field.getNeighbors()) {
							if (f.getType() == FieldType.WATER || f.getType() == FieldType.SAND) {
								field.setType(FieldType.SAND);
								break waterSandSearch;
							}
						}
					}

				}
			}
		}

		for (Field[] fields : Game.fields) {
			for (Field field : fields) {

				if (field.getType() != FieldType.WATER) {

					waterSandSearch: if (field.getHeight() < -0.05) {
						for (Field f : field.getNeighbors()) {
							if (f.getType() == FieldType.WATER) {
								field.setType(FieldType.SAND);
								break waterSandSearch;
							}
						}
						field.setType(FieldType.GROUND);
					}

				}

				field.getEntity().setColor(field.getType().getColor());
			}
		}

	}
}
