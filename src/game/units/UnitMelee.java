package game.units;

import game.Field;

public class UnitMelee extends Unit {

    public UnitMelee(Alliance alliance, Field startingField) {
	super(alliance, startingField);
	this.health = 200;
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
