package game;

import java.util.ArrayList;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import de.geosearchef.matella.entities.Entity;
import game.units.Alliance;
import game.units.Unit;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Field {

	public static final Vector2f DIMENSIONS = new Vector2f(1, (float) (2 / Math.sqrt(3)));

	public ArrayList<Unit> units = new ArrayList<Unit>();
	private @Getter final int rawPosX, rawPosY;
	private @NonNull @Getter FieldType type;
	private @Getter Field[] neighbors = new Field[6];
	private float posX, posY;
	private @Getter float height = 2f;// too heigh, error can be clearly seen in
	// game
	private @Getter @Setter Entity entity;

	void calculateNeighbors(Field[][] fields) {

		int rightShift = isLefter() ? 0 : 1;

		int x, y;

		// 0
		x = (rawPosX + rightShift) % Game.FIELDS_X;
		y = (rawPosY - 1);
		if (y < 0)
			y += Game.FIELDS_Y;
		neighbors[0] = fields[x][y];

		// 1
		x = (rawPosX + 1) % Game.FIELDS_X;
		y = (rawPosY);
		neighbors[1] = fields[x][y];

		// 2
		x = (rawPosX + rightShift) % Game.FIELDS_X;
		y = (rawPosY + 1) % Game.FIELDS_Y;
		neighbors[2] = fields[x][y];

		// 3
		x = (rawPosX - 1 + rightShift) % Game.FIELDS_X;
		y = (rawPosY + 1) % Game.FIELDS_Y;
		if (x < 0)
			x += Game.FIELDS_X;
		neighbors[3] = fields[x][y];

		// 4
		x = (rawPosX - 1);
		y = (rawPosY);
		if (x < 0)
			x += Game.FIELDS_X;
		neighbors[4] = fields[x][y];

		// 5
		x = (rawPosX - 1 + rightShift) % Game.FIELDS_X;
		y = (rawPosY - 1);
		if (x < 0)
			x += Game.FIELDS_X;
		if (y < 0)
			y += Game.FIELDS_Y;
		neighbors[5] = fields[x][y];

		this.posX = rawPosX + (isLefter() ? 0 : 0.5f);
		this.posY = rawPosY * DIMENSIONS.y * 0.75f;
	}

	public float getPosX() {
		if (this.entity == null) {
			return this.posX;
		} else {
			return this.entity.getPosition().x;
		}
	}

	public float getPosY() {
		if (this.entity == null) {
			return this.posY;
		} else {
			return this.entity.getPosition().z;
		}
	}

	public boolean isLefter() {
		return rawPosY % 2 == 0;
	}

	public boolean isPassable() {
		return this.type != FieldType.WALL && this.type != FieldType.WATER;
	}

	@Override
	public String toString() {
		return "[" + getRawPosX() + "|" + getRawPosY() + "]";
	}

	public synchronized void setHeight(float height) {
		this.height = height;
		if (this.entity != null)
			this.entity.getPosition().y = this.height + (this.type == FieldType.WALL ? 0.8f : 0f);
	}

	public void setType(FieldType type) {
		this.type = type;
		if (type == FieldType.FARMLAND) {
			plantingCycle = 0;
			growthState = GrowthState.GROWTH_STATE_0;
		}
		this.setHeight(this.getHeight());
		this.getEntity().setColor(type.getColor());
	}

	// Farmland
	private static final float GROWTH_STATE_DURATION = 1f;
	private static final int MAX_PLANTING_CYCLES = 3;
	private static final int FOOD_PER_HARVEST = 25;

	private enum GrowthState {
		FULLY_GROWN(0, new Vector4f(0.866f, 0.741f, 0.211f, 1f)), GROWTH_STATE_0(1, FieldType.FARMLAND.getColor()), GROWTH_STATE_1(2, (Vector4f) Vector4f.add(GROWTH_STATE_0.getColor(), Vector4f.add(GROWTH_STATE_0.getColor(), FULLY_GROWN.getColor(), null), null).scale(0.333f)), GROWTH_STATE_2(3, (Vector4f) Vector4f.add(FULLY_GROWN.getColor(), Vector4f.add(GROWTH_STATE_0.getColor(), FULLY_GROWN.getColor(), null), null).scale(0.333f));

		public final @Getter int index;
		public final @Getter Vector4f color;

		GrowthState(int index, Vector4f color) {
			this.index = index;
			this.color = color;
		}
	}

	private GrowthState growthState = null;
	private float timePlanted = 0;
	private int plantingCycle = 0;

	public void update(float d) {

		if (type == FieldType.FARMLAND) {

			if (growthState != null && growthState != GrowthState.FULLY_GROWN) {
				this.timePlanted += d;
				if (this.timePlanted > GROWTH_STATE_DURATION) {
					// System.out.println("Updating GROWTH!");
					int index=this.growthState.index + 1;
					if(index>=GrowthState.values().length){
						this.setGrowthState(GrowthState.FULLY_GROWN);
					}else{
						this.setGrowthState(GrowthState.values()[index]);
					}
					this.timePlanted = 0;
				}
			}

		}
	}

	public void plantWheat() {
		if (type == FieldType.FARMLAND) {
			plantingCycle++;
			this.setGrowthState(GrowthState.GROWTH_STATE_0);
			this.timePlanted = 0;
		}
	}

	public void harvest(Alliance alliance) {
		this.setGrowthState(null);
		if (plantingCycle == MAX_PLANTING_CYCLES)
			this.setType(FieldType.GRASS);
		game.ResourceManager.changeFoodCount(alliance, FOOD_PER_HARVEST);
	}

	private void setGrowthState(GrowthState growthState) {
		this.growthState = growthState;
		if (this.growthState == null)
			this.getEntity().setColor(GrowthState.GROWTH_STATE_0.getColor());
		else
			this.getEntity().setColor(this.growthState.getColor());
	}
	
	public boolean isHarvestable(){
		return this.growthState==GrowthState.FULLY_GROWN;
	}
}
