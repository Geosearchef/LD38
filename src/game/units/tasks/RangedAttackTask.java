package game.units.tasks;

import java.util.stream.Stream;

import game.units.Unit;
import lombok.Data;

@Data
public class RangedAttackTask extends Task {
	
	private final Unit target;
	
	@Override
	public void update(float d) {
		//unit is on this field or on neighbour field or on neighbours neighbours field (i have no time for correct english)
		if(target.getField() == this.getUnit().getField() || Stream.of(target.getField().getNeighbors()).anyMatch(f -> this.getUnit().getField() == f) || Stream.of(target.getField().getNeighbors()).flatMap(f -> Stream.of(f.getNeighbors())).anyMatch(f -> this.getUnit().getField() == f)) {
			target.damage(this.getUnit().getDamage());
		}
	}
	
	@Override
	public boolean isFinished() {
		return super.isFinished() || target.isDead() || !(target.getField() == this.getUnit().getField() || Stream.of(target.getField().getNeighbors()).anyMatch(f -> this.getUnit().getField() == f) || Stream.of(target.getField().getNeighbors()).flatMap(f -> Stream.of(f.getNeighbors())).anyMatch(f -> this.getUnit().getField() == f));
	}
}
