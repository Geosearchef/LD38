package game.units.tasks;

import game.Game;
import game.units.Unit;
import lombok.Data;

@Data
public class MeleeAttackTask extends Task {
	
	private final Unit target;
	
	@Override
	public void update(float d) {
		if(target.getField() == this.getUnit().getField()) {
			target.damage(this.getUnit().getDamage());
		}
	}
	
	@Override
	public boolean isFinished() {
		return super.isFinished() || target.isDead() || target.getField() != getUnit().getField();
	}
}
