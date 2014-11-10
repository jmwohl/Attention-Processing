package com.jonwohl;

import processing.core.*;

public class Vertex {
	public PApplet parent;
	public PVector point;

	private boolean selected = false;
	private float radius = 10;
	private float hoveredRadius = 12;

	Vertex(PApplet parent, int x, int y) {
		this.parent = parent;
		point = new PVector(x, y);
	}

	public float x() {
		return point.x;
	}

	public float y() {
		return point.y;
	}

	void draw() {
		float finalRad = radius;
		int fillColor = parent.g.fillColor;
		if (isHovering()) {
			finalRad = hoveredRadius;
			parent.fill(255);
		}

		parent.ellipse(point.x, point.y, finalRad, finalRad);
		parent.fill(fillColor);
	}

	boolean isHovering() {
		float dist = PApplet.dist(parent.mouseX, parent.mouseY, point.x,
				point.y);
		return dist <= radius;
	}

	void mousePressed() {
		if (isHovering()) {
			selected = true;
		} else {
			selected = false;
		}
	}

	void mouseDragged() {
		// println("mouse dragged");
		if (selected) {
			point.x = parent.mouseX;
			point.y = parent.mouseY;
		}
	}

	void mouseReleased() {
		selected = false;
	}
}
