package game.units.tasks;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import game.Field;
import game.Game;
import game.units.Alliance;
import game.units.Unit;
import game.units.UnitMelee;
import game.units.UnitRanged;
import lombok.Data;
import util.pathfinding.Path;
import util.pathfinding.Pathfinder;

public class AICalculation {
	
	public static Task calculate(Unit unit) {
		
		List<Unit> enemyUnits = null;
		
		synchronized (unit) {
			enemyUnits = Game.units.stream()
					.filter(u -> u.getAlliance() != unit.getAlliance())
					.collect(Collectors.toList());
			Collections.sort(enemyUnits, (Unit u1, Unit u2) ->new Float(Vector3f.sub(u1.getPosition(), unit.getPosition(), null).lengthSquared()).compareTo(Vector3f.sub(u2.getPosition(), unit.getPosition(), null).lengthSquared()));
		}
		
		
		if(unit instanceof UnitMelee) {
			for(Unit target : enemyUnits) {
				Optional<Path> path = Pathfinder.getPath(unit.getField(), target.getField());
				if(path.isPresent()) {
					return new TargetUnitTask(target, path.get());
				}
			}
		}
		
		
		
		//ELSE
		Optional<Path> path = Pathfinder.getPath(unit.getField(), Game.fields[(int)(Math.random() * (Game.FIELDS_X - 1))][(int)(Math.random() * (Game.FIELDS_Y - 1))]);
		
		if(!path.isPresent())
			return null;
		
		return new PathfindingTask(path.get());
	}
	
	
	public static TeamComposition getTeamComposition(Alliance alliance) {
		return new TeamComposition(1.0f, 0.0f);
	}
	
	
	@Data
	public static class TeamComposition {
		private final float melee;
		private final float ranged;
		
		public Class<?> getUnitClass() {
			float rand = (float) Math.random();
			
			if(rand < melee)
				return UnitMelee.class;
			rand -= melee;
			
			if(rand < ranged)
				return UnitRanged.class;
			rand -= ranged;
			
			return null;
		}
	}
}
