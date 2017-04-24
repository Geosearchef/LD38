package game;

import java.util.HashMap;
import java.util.Map;

import game.units.Alliance;

public class ResourceManager {

	private static final int STARTING_FOOD = 200;
	private static final int STARTING_WALLS = 10;
	private static final int STARTING_FARMLAND = 5;
	
	private static final float WALLS_GAIN = 0.2f;
	private static final float FARMLAND_GAIN = 0.1f;

	public static Map<Alliance, Integer> food;

	public static volatile float walls;
	public static volatile float farmland;

	public static void init() {
		food = new HashMap<>();
		food.put(Alliance.LIGHT, STARTING_FOOD);
		food.put(Alliance.DARK, STARTING_FOOD);

		walls = STARTING_WALLS;
		farmland = STARTING_FARMLAND;
	}
	
	public static void update(float d) {
		walls += WALLS_GAIN * d;
		farmland += FARMLAND_GAIN * d;
	}

	public static void changeFoodCount(Alliance alliance, int change) {
		food.put(alliance, food.get(alliance) + change);
	}
	
	public static boolean hasWalls(int x) {
		return x < walls;
	}
	
	public static boolean hasFarmland() {
		return farmland >= 1f;
	}
}
