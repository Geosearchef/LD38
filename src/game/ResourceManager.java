package game;

import java.util.HashMap;
import java.util.Map;

import game.units.Alliance;

public class ResourceManager {

	private static final int STARTING_FOOD = 200;
	private static final int STARTING_WALLS = 20;
	private static final int STARTING_FARMLAND = 20;

	public static Map<Alliance, Integer> food;

	public static int walls;
	public static int farmland;

	public static void init() {
		food = new HashMap<>();
		food.put(Alliance.LIGHT, STARTING_FOOD);
		food.put(Alliance.DARK, STARTING_FOOD);

		walls = STARTING_WALLS;
		farmland = STARTING_FARMLAND;
	}

	public static void changeFoodCount(Alliance alliance, int change) {
		food.put(alliance, food.get(alliance) + change);
	}
}
