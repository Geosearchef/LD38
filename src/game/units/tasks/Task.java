package game.units.tasks;

import game.units.Unit;
import lombok.Data;
import lombok.Setter;

@Data
public abstract class Task {
	
	private Unit unit;
	
	public abstract void update(float d);
	
	public abstract boolean isFinished();
}
