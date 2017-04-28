package game;

import java.util.HashMap;
import java.util.Map;

import game.units.Alliance;

public class ResourceManager {

	private static final int STARTING_FOOD = 200;
	private static final int STARTING_WALLS = 10;
	private static final int STARTING_FARMLAND = 5;
	
	private static final float WALLS_GAIN = 0.25f;
	private static final float FARMLAND_GAIN = 0.2f;

	public static Map<Alliance, Float> food;

	public static volatile float walls;
	public static volatile float farmland;

	public static void init() {
		food = new HashMap<>();
		food.put(Alliance.LIGHT, (float)STARTING_FOOD);
		food.put(Alliance.DARK, (float)STARTING_FOOD);

		walls = STARTING_WALLS;
		farmland = STARTING_FARMLAND;
	}
	
	public static void update(float d) {
		walls += WALLS_GAIN * d;
		farmland += FARMLAND_GAIN * d;
	}

	public static void changeFoodCount(Alliance alliance, float change) {
		food.put(alliance, food.get(alliance) + change);
	}
	
	public static int getRoundedFoodAmount(Alliance alliance) {
		return food.get(alliance).intValue();
	}
	
	public static boolean hasWalls(int x) {
		return x < walls;
	}
	
	public static boolean hasFarmland() {
		return farmland >= 1f;
	}
}
