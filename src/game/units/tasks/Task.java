package game.units.tasks;

import game.units.Alliance;
import game.units.Unit;
import lombok.Data;
import lombok.Setter;

@Data
public abstract class Task {
	
	private long validUntil = System.currentTimeMillis() + 10000l;
	private Unit unit;
	
	public abstract void update(float d);
	
	public boolean isFinished() {
		return System.currentTimeMillis() > validUntil;
	}
}
