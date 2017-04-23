package game.units;

import game.Field;

public class UnitRanged extends Unit {

    public UnitRanged(Alliance alliance, Field startingField) {
	super(alliance, startingField);
	this.health = 100;
	this.damage = 25;
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