package game.units;

import game.Field;

public class UnitMelee extends Unit {

	public UnitMelee(Alliance alliance, Field startingField) {
		super(alliance, startingField);
		this.health = 200;
		this.damage = 50;
    }

	@Override
	public void update(float d) {
		super.update(d);
	}

	@Override
	public String getModelName() {
		return "unit0";
	}

}
