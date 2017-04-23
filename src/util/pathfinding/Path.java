package util.pathfinding;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import game.Field;
import lombok.Getter;

public class Path {
	/*
	 * Represents a path, includes start and destination field
	 */
	
	private @Getter List<Field> fields;
	private int currentIndex = 0;
	
	public Path(List<Field> fields) {
		this.fields = fields;
	}
	
	public Field next() {
		if(currentIndex >= fields.size() - 1)
			throw new IndexOutOfBoundsException("No elements left in this path");
		return fields.get(++currentIndex);
	}
	
	public Field current() {
		return fields.get(currentIndex);
	}
	
	public boolean hasNext() {
		return this.currentIndex < fields.size() - 1;
	}
	
	public Field getSrc() {
		return fields.get(0);
	}
	
	public Field getDest() {
		return fields.get(fields.size() - 1);
	}
	
	/**
	 * @return only true, if all remaining fields (not containing the current one) in this path are still passable
	 */
	public boolean isStillValid() {
		for(int i = currentIndex + 1;i < fields.size();i++) {
			if(! fields.get(i).isPassable())
				return false;
		}
		return true;
	}
	
	public Path reverse() {
		LinkedList<Field> newList = new LinkedList<Field>(this.fields);
		Collections.reverse(newList);
		return new Path(newList);
	}
}
