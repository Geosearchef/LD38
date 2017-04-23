package game.units.tasks;

import java.util.Collections;
import java.util.LinkedList;
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
		List<Unit> allyUnits = new LinkedList<Unit>();
		
		synchronized (Game.units) {
			allyUnits.addAll(Game.units);
			
			enemyUnits = Game.units.stream()
					.filter(u -> u.getAlliance() != unit.getAlliance())
					.collect(Collectors.toList());
			Collections.sort(enemyUnits, (Unit u1, Unit u2) -> new Float(Vector3f.sub(u1.getPosition(), unit.getPosition(), null).lengthSquared()).compareTo(Vector3f.sub(u2.getPosition(), unit.getPosition(), null).lengthSquared()));//TODO: not working, use pathfinder for repeat!!
			
			allyUnits.removeAll(enemyUnits);
			allyUnits.remove(unit);
			Collections.sort(allyUnits, (Unit u1, Unit u2) -> new Float(Vector3f.sub(u1.getPosition(), unit.getPosition(), null).lengthSquared()).compareTo(Vector3f.sub(u2.getPosition(), unit.getPosition(), null).lengthSquared()));//TODO: not working, use pathfinder for repeat!!
		}
		
		if(enemyUnits.isEmpty())
			return null;
		
		
		float needToMate = 0.25f;
		if(allyUnits.size() < 20) needToMate += 0.2f;
		if(allyUnits.size() >= 2) needToMate += Math.max(0f, 1f - Math.max(1f, Vector3f.sub(allyUnits.get(0).getPosition(), unit.getPosition(), null).length() / 10f)) / 5f;
		if((float)allyUnits.size() / (float)enemyUnits.size() < 0.75) needToMate += 0.15f;
		if(Math.random() < needToMate) {//TODO: not working, use pathfinder for repeat!!   METHOD FOR DISTANCE BETWEEN UNITS USING PATHFINDER
			for(Unit ally : allyUnits) {
				if(ally.getTask() == null) {
					ally.setAiCalculation(null);
					MarriageTask allyTask = new MarriageTask(Pathfinder.getPath(unit.getField(), ally.getField()).get(), unit);
					allyTask.setUnit(ally);
					MarriageTask ownTask = new MarriageTask(Pathfinder.getPath(unit.getField(), ally.getField()).get(), ally);
					ally.setTask(allyTask);
					ally.setAiCalculation(null);
					return ownTask;
				}
			}
		}
		
		//Flee if enemy force nearby is bigger then own force, attention, real distance via neightbours
		
		
		if(unit instanceof UnitMelee) {
			if(enemyUnits.get(0).getField() == unit.getField())
				return new MeleeAttackTask(enemyUnits.get(0));
			
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
