package com.jonwohl;

import java.util.ArrayList;
import java.util.List;

import processing.core.*;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class SelectionWindow {
	public boolean display = false;

	private ArrayList<Vertex> vertices;
	
	private String[] corners;

	private PApplet parent;

	SelectionWindow(Attention attn) {
		this.parent = attn.parent;

		this.registerParentMethods();

		vertices = new ArrayList<Vertex>();
		
		corners = this.parent.loadStrings("corners.txt");

		if (corners != null) {
			// top left
			List<Integer> tl = this.parseCoords(corners[0]);
			vertices.add(new Vertex(this.parent, tl.get(0), tl.get(1)));
			// top right
			List<Integer> tr = this.parseCoords(corners[1]);
			vertices.add(new Vertex(this.parent, tr.get(0), tr.get(1)));
			// bottom right
			List<Integer> br = this.parseCoords(corners[2]);
			vertices.add(new Vertex(this.parent, br.get(0), br.get(1)));
			// bottom left
			List<Integer> bl = this.parseCoords(corners[3]);
			vertices.add(new Vertex(this.parent, bl.get(0), bl.get(1)));
		} else {
			// top left
			vertices.add(new Vertex(this.parent, 0, 0));
			// top right
			vertices.add(new Vertex(this.parent, attn.inputW, 0));
			// bottom right
			vertices.add(new Vertex(this.parent, attn.inputW, attn.inputH));
			// bottom left
			vertices.add(new Vertex(this.parent, 0, attn.inputH));
		}

	}

	/**
	 * Register hooks to be called by PApplet
	 */
	private void registerParentMethods() {
		// parent.registerMethod("pre", this);
		parent.registerMethod("draw", this);
		parent.registerMethod("mouseEvent", this);
//		parent.registerMethod("keyEvent", this);
		// parent.registerMethod("dispose", this);
	}
	
	private List<Integer> parseCoords(String coords) {
		String[] tokens = coords.split(",");
		List<Integer> coordsList = new ArrayList<Integer>();
		for (int i = 0; i < tokens.length; i++) {
			System.out.println(tokens[i]);
			Double db = Double.parseDouble(tokens[i]);
		    coordsList.add(db.intValue());
		}
		return coordsList;
	}

	/**
	 * Handle mouse events
	 * 
	 * @param event
	 */
	public void mouseEvent(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();

		switch (event.getAction()) {
		case MouseEvent.PRESS:
			this.mousePressed();
			break;
		case MouseEvent.RELEASE:
			this.mouseReleased();
			break;
		case MouseEvent.CLICK:
			// do something for mouse clicked
			break;
		case MouseEvent.DRAG:
			this.mouseDragged();
			break;
		case MouseEvent.MOVE:
			// umm... forgot
			break;
		}
	}

	/**
	 * If display enabled, draw selection window with handles for vertices
	 */
	public void draw() {
		if (!display) {
			return;
		}
		parent.stroke(200, 200, 255);
		parent.fill(100);
		parent.strokeWeight(2);

		int size = vertices.size();

		// draw lines
		for (int i = 0; i < size; i++) {
			Vertex a;
			Vertex b;
			a = vertices.get(i);
			if (i < vertices.size() - 1) {
				b = vertices.get(i + 1);
			} else {
				b = vertices.get(0);
			}

			parent.line(a.x(), a.y(), b.x(), b.y());
		}

		// draw vertices
		for (int i = 0; i < size; i++) {
			vertices.get(i).draw();
		}
	}

	/**
	 * Get the four vertices as points
	 * 
	 * @return
	 */
	public ArrayList<PVector> getPoints() {
		ArrayList<PVector> points = new ArrayList<PVector>();
		for (int i = 0; i < vertices.size(); i++) {
			points.add(vertices.get(i).point);
		}

		return points;
	}
	
	public String[] getPointsAsStringArray() {
		String points = "";
		for (int i = 0; i < vertices.size(); i++) {
			points += vertices.get(i).x() + "," + vertices.get(i).y() + " ";
		}
		points.trim();
		return points.split(" ");
	}

	/**
	 * Pass mousePressed events to vertices
	 */
	private void mousePressed() {
		for (int i = 0; i < vertices.size(); i++) {
			vertices.get(i).mousePressed();
		}
	}

	/**
	 * Pass mouseReleased events to vertices
	 */
	private void mouseReleased() {
		for (int i = 0; i < vertices.size(); i++) {
			vertices.get(i).mouseReleased();
		}
		this.parent.saveStrings("corners.txt", this.getPointsAsStringArray());
	}

	/**
	 * Pass mouseDragged events to vertices
	 */
	private void mouseDragged() {
		for (int i = 0; i < vertices.size(); i++) {
			vertices.get(i).mouseDragged();
		}
	}

}
