package game.units;

import org.lwjgl.util.vector.Vector3f;

import game.Field;

public class UnitMelee extends Unit {

	public UnitMelee(Vector3f position, int alliance, Field startingField) {
		super(position, alliance, startingField);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(float d) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getModelName() {
		return "unit0";
	}

}
