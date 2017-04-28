package game.units;

import game.Field;

public class UnitFarmer extends Unit {

	public UnitFarmer(Alliance alliance, Field startingField) {
		super(alliance, startingField);
		this.health = 500;
		this.damage = 0;
	}
	
	@Override
	public void update(float d) {
		super.update(d);
	}
	
	@Override
	public String getModelName() {
		return "unit2";
	}

}
