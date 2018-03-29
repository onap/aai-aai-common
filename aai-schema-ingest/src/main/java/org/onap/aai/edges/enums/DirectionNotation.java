package org.onap.aai.edges.enums;

public enum DirectionNotation { 
	DIRECTION("${direction}"), 
	OPPOSITE("!${direction}");

	private final String val;
	
	DirectionNotation(String dir) {
		this.val = dir;
	}
	
	public static DirectionNotation getValue(String val) {
		if (DIRECTION.toString().equals(val)) {
			return DIRECTION;
		} else if (OPPOSITE.toString().equals(val)) {
			return OPPOSITE;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public String toString() {
		return this.val;
	}
}