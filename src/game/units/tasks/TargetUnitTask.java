package game.units.tasks;

import java.util.Optional;

import org.lwjgl.util.vector.Vector3f;

import game.units.Unit;
import util.pathfinding.Path;
import util.pathfinding.Pathfinder;

public class TargetUnitTask extends PathfindingTask {
	
	protected Unit target;
	
	public TargetUnitTask(Unit target, Path path) {
		super(path);
		this.target = target;
	}
	
	@Override
	public void update(float d) {
		super.update(d);
	}

	@Override
	public boolean isFinished() {
		Optional<Path> destToTarget = Pathfinder.getPath(this.path.getDest(), this.target.getField());
		
		return this.path == null || !destToTarget.isPresent() || destToTarget.get().getFields().size() > 3 || super.isFinished();
	}
}
