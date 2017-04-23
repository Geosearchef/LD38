package game.units.tasks;

import org.lwjgl.util.vector.Vector3f;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import util.pathfinding.Path;

@RequiredArgsConstructor
public class PathfindingTask extends Task {
	
	protected final Path path;
	
	@Override
	public void update(float d) {
		if(this.getUnit().getField() == path.current() && path.hasNext()) {
			this.getUnit().move(path.next());
		}
	}

	@Override
	public boolean isFinished() {
		if(this.getUnit().getField() == path.getDest()) {
			this.getUnit().setVelocity(new Vector3f(0f, 0f, 0f));
			return true;
		} else {
			return false;
		}
	}
	
}
