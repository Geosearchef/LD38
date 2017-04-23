package game.units.tasks;

import java.util.Optional;

import game.Field;
import game.Game;
import game.units.Unit;
import util.pathfinding.Path;
import util.pathfinding.Pathfinder;

public class AICalculation {
	
	public static Task calculate(Unit unit) {
		Optional<Path> path = Pathfinder.getPath(unit.getField(), Game.fields[(int)(Math.random() * 30)][(int)(Math.random() * 30)]);
		
		if(!path.isPresent())
			return null;
		
		return new PathfindingTask(path.get());
	}
	
}
