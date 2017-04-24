package game.units.tasks;

import org.lwjgl.util.vector.Vector3f;

import game.Field;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import util.pathfinding.Path;

@RequiredArgsConstructor
public class PathfindingTask extends Task {
	
	protected final Path path;
	protected Field last;
	
	@Override
	public void update(float d) {
		if(this.getUnit().getField() == path.current() && path.hasNext()) {
			last = this.getUnit().getField();
			this.getUnit().move(path.next());
		}
	}

	@Override
	public boolean isFinished() {
		if((this.getUnit().getField() != last && this.getUnit().getField() != path.current()) || this.getUnit().getField() == path.getDest() || super.isFinished() || !this.path.isStillValid()) {
			this.getUnit().setVelocity(new Vector3f(0f, 0f, 0f));
			return true;
		} else {
			return false;
		}
	}
	
}
