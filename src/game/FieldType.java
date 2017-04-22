package game;

import org.lwjgl.util.vector.Vector4f;

import lombok.Getter;

public enum FieldType {
	GROUND(new Vector4f()),
	GRASS(new Vector4f()),
	WATER(new Vector4f()),
	WALL(new Vector4f());
	
	private @Getter Vector4f color;
	private FieldType(Vector4f color) {
		this.color = color;
	}
}
