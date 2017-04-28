package game.units.tasks;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.geosearchef.matella.profiling.CPUProfiler;
import game.Field;
import game.Game;
import game.units.Alliance;
import game.units.Unit;
import game.units.UnitFarmer;
import game.units.UnitMelee;
import game.units.UnitRanged;
import lombok.Data;
import util.pathfinding.Path;
import util.pathfinding.Pathfinder;

public class AICalculation {
	
	public static Task calculate(Unit unit) {
		
		List<Unit> enemyUnits = null;
		List<Unit> allyUnits = new LinkedList<Unit>();
		CPUProfiler.setPROFILING_ENABLED(true);
		CPUProfiler.start();
		synchronized (Game.units) {
			allyUnits.addAll(Game.units);
			
			enemyUnits = Game.units.stream()
					.filter(u -> u.getAlliance() != unit.getAlliance())
					.collect(Collectors.toList());
			Collections.sort(enemyUnits, (Unit u1, Unit u2) -> new Float(u1.distance(unit)).compareTo(u2.distance(unit)));
			
			allyUnits.removeAll(enemyUnits);
			allyUnits.remove(unit);
			Collections.sort(allyUnits, (Unit u1, Unit u2) -> new Float(u1.distance(unit)).compareTo(u2.distance(unit)));
		}
		CPUProfiler.dump("collect units");
		
		if(enemyUnits.isEmpty()) {
			if(Game.gameFinish == 0)
				Game.gameFinish = System.currentTimeMillis();
			return null;
		}
		
		List<Unit> enemyFarmers = enemyUnits.stream().filter(u -> ! (u instanceof UnitFarmer)).collect(Collectors.toList());
		
		
		//TODO: marriage task: WaitingForMarriageTask?
		float needToMate = 0.25f;
		if(allyUnits.size() < 20) needToMate += 0.4f;
		if(allyUnits.size() >= 2) needToMate += Math.max(0f, 1f - Math.max(1f, allyUnits.get(0).distance(unit) / 10f)) / 5f;
		if((float)allyUnits.size() / (float)enemyUnits.size() < 0.75) needToMate += 0.15f;
		if(allyUnits.size() > 100) needToMate -= 100f;
		if(Math.random() < needToMate) {
			for(Unit ally : allyUnits) {
				try {
					if(ally.getTask() == null || !(ally.getTask() instanceof MarriageTask)) {
						MarriageTask allyTask = new MarriageTask(Pathfinder.getPath(unit.getField(), ally.getField()).get(), unit);
						allyTask.setUnit(ally);
						MarriageTask ownTask = new MarriageTask(Pathfinder.getPath(unit.getField(), ally.getField()).get(), ally);
						ally.setTask(allyTask);
						ally.setVelocity(new Vector3f(0f, 0f, 0f));
						return ownTask;
					}
				} catch(NoSuchElementException e) {
					System.out.println("No path to ally found for marrying.");
				}
			}
		}
		CPUProfiler.dump("marriage task");
		
		//Flee if enemy force nearby is bigger then own force, attention, real distance via neighbours
		List<Unit> targetList = Math.random() > 0.75 ? enemyFarmers : enemyUnits;
		if(enemyUnits.get(0).distance(unit) < 3)
			targetList = enemyUnits;
		
		
		if(unit instanceof UnitMelee) {
			if(enemyUnits.get(0).getField() == unit.getField())
				return new MeleeAttackTask(enemyUnits.get(0));
			
			for(Unit target : enemyUnits) {
				Optional<Path> path = Pathfinder.getPath(unit.getField(), target.getField());
				if(path.isPresent()) {
					CPUProfiler.dump("melee target pathfinder");
					return new TargetUnitTask(target, path.get());
				}
			}
		}
		
		//TODO: optimise
		if(unit instanceof UnitRanged) {
			if(enemyUnits.get(0).getField() == unit.getField() || Stream.of(enemyUnits.get(0).getField().getNeighbors()).anyMatch(f -> unit.getField() == f) || Stream.of(enemyUnits.get(0).getField().getNeighbors()).flatMap(f -> Stream.of(f.getNeighbors())).anyMatch(f -> unit.getField() == f))
				return new RangedAttackTask(enemyUnits.get(0));
			
			for(Unit target : enemyUnits) {
				Optional<Path> path = Pathfinder.getPath(unit.getField(), target.getField());
				if(path.isPresent()) {
					CPUProfiler.dump("ranged target pathfinder");
					path.get().removeDest();//ranged, can skip two fields
					path.get().removeDest();
					return new TargetUnitTask(target, path.get());
				}
			}
		}
		
		if(unit instanceof UnitFarmer) {
			if(unit.getField().isHarvestable()) {
				unit.getField().harvest(unit.getAlliance());
				return null;
			}
			
			List<Field> closedList = new LinkedList<Field>();
			List<Field> openList = new LinkedList<Field>();
			openList.add(unit.getField());
			
			while(! openList.isEmpty()) {
				Field current = openList.remove(0);
				closedList.add(current);
				
				if(current.isHarvestable()) {
					Optional<Path> path = Pathfinder.getPath(unit.getField(), current);
					if(path.isPresent()) {
						return new PathfindingTask(path.get());
//						return null;
					}
				}
				
				Stream.of(current.getNeighbors())
				.filter(f -> !closedList.contains(f) && !openList.contains(f))
				.forEach(f -> openList.add(f));
			}
		}
		
		//ELSE
		Optional<Path> path = Pathfinder.getPath(unit.getField(), Game.fields[(int)(Math.random() * (Game.FIELDS_X - 1))][(int)(Math.random() * (Game.FIELDS_Y - 1))]);
		
		if(!path.isPresent())
			return null;
		
		return new PathfindingTask(path.get());
	}
	
	
	public static TeamComposition getTeamComposition(Alliance alliance) {
		int farmer = (int) Game.units.stream().filter(u -> u instanceof UnitFarmer && u.getAlliance() == alliance).count();
		int units = (int) Game.units.stream().filter(u -> u.getAlliance() == alliance).count();
		if(farmer < 2)
			return new TeamComposition(0.0f, 0.0f, 1.0f);
		else if(farmer < units / 4)
			return new TeamComposition(0.2f, 0.1f, 0.7f);
		else
			return new TeamComposition(0.6f, 0.3f, 0.1f);
	}
	
	
	@Data
	public static class TeamComposition {
		private final float melee;
		private final float ranged;
		private final float farmer;
		
		public Class<?> getUnitClass() {
			float rand = (float) Math.random();
			
			if(rand < melee)
				return UnitMelee.class;
			rand -= melee;
			
			if(rand < ranged)
				return UnitRanged.class;
			rand -= ranged;
			
			if(rand < farmer)
				return UnitFarmer.class;
			rand -= farmer;
			
			return null;
		}
	}
}
