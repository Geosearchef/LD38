package game.units.tasks;

import java.lang.reflect.InvocationTargetException;

import org.lwjgl.util.vector.Vector3f;

import game.Field;
import game.Game;
import game.units.Alliance;
import game.units.Unit;
import lombok.Getter;
import update.Updater;
import util.pathfinding.Path;

public class MarriageTask extends PathfindingTask {
	
	protected Unit partner;
	protected @Getter long birthTime = 0;
	
	public MarriageTask(Path path, Unit partner) {
		super(path);
		this.partner = partner;
	}
	
	@Override
	public void update(float d) {
		if(partner.getField() == getUnit().getField() && !(partner.getTask() instanceof MarriageTask && ((MarriageTask)partner.getTask()).birthTime != 0)) {
			this.getUnit().setVelocity(new Vector3f(0f, 0f, 0f));
			birthTime = System.currentTimeMillis() + 2000l;
		}
	}
	
	@Override
	public boolean isFinished() {
		
		if(birthTime != 0) {
			if(System.currentTimeMillis() >= birthTime) {
				Updater.scheduleAfterUpdate(() -> {
					try {
						Game.units.add((Unit) AICalculation.getTeamComposition(getUnit().getAlliance()).getUnitClass().getConstructor(new Class[]{Alliance.class, Field.class}).newInstance(getUnit().getAlliance(), getUnit().getField()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				
				return true;
			} else {//ignores partner
				return false;
			}
		}
		
		if(!(partner.getTask() instanceof MarriageTask) || (partner.getTask() instanceof MarriageTask && ((MarriageTask)partner.getTask()).birthTime != 0) || !Game.units.contains(partner)) {
			this.getUnit().setVelocity(new Vector3f(0f, 0f, 0f));
			return true;
		} else {
			return false;
		}		
	}
}
