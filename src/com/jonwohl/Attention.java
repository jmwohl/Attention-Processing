/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package com.jonwohl;

import java.util.ArrayList;

import processing.core.*;
import gab.opencv.*;

import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.core.Mat;
import org.opencv.core.CvType;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * This is a template class and can be used to start a new processing library or
 * tool. Make sure you rename this class as well as the name of the example
 * package 'template' to your own library or tool naming convention.
 * 
 * (the tag example followed by the name of an example included in folder
 * 'examples' will automatically include the example in the javadoc.)
 * 
 * @example Hello
 */

public class Attention implements PConstants {

	// parent is a reference to the parent sketch
	PApplet parent;

	// an opencv object
	OpenCV ocv;

	// input and output images
	PImage inputImg;
	PImage outputImg;

	// input and output dimensions
	public int inputW;
	public int inputH;
	public int outputW;
	public int outputH;

	// selection window
	private SelectionWindow window;
	private boolean display = false;

	public final static String VERSION = "##library.prettyVersion##";

	/**
	 * Basic constructor, uses parent dimensions
	 * 
	 * @param theParent
	 */
	public Attention(PApplet theParent) {
		parent = theParent;

		// setup default dimensions
		inputW = outputW = theParent.width;
		inputH = outputH = theParent.height;

		registerParentMethods();
		initialize();

		// print welcome to sys out
		welcome();
	}

	/**
	 * Basic constructor, passing in an initial input image
	 * 
	 * @param theParent
	 */
	public Attention(PApplet theParent, PImage inputImg) {
		parent = theParent;
		this.inputImg = inputImg;

		// setup default dimensions
		inputW = outputW = inputImg.width;
		inputH = outputH = inputImg.height;

		registerParentMethods();
		initialize();

		// print welcome to sys out
		welcome();
	}

	/**
	 * Basic constructor, input dimensions specified
	 * 
	 * @param theParent
	 */
	public Attention(PApplet theParent, int inputW, int inputH) {
		parent = theParent;

		// setup default dimensions
		this.inputW = this.outputW = inputW;
		this.inputH = this.outputH = inputH;

		registerParentMethods();
		initialize();

		// print welcome to sys out
		welcome();
	}

	/**
	 * Basic constructor, input/output dimensions specified
	 * 
	 * @param theParent
	 */
	public Attention(PApplet theParent, int inputW, int inputH, int outputW,
			int outputH) {
		parent = theParent;

		// setup default dimensions
		this.inputW = inputW;
		this.inputH = inputH;
		this.outputW = outputW;
		this.outputH = outputH;

		registerParentMethods();
		initialize();

		// print welcome to sys out
		welcome();
	}

	/**
	 * Setup selection window, opencv and input/output images.
	 */
	private void initialize() {
		this.window = new SelectionWindow(this);
		this.ocv = new OpenCV(parent, inputW, inputH);
		this.inputImg = parent.createImage(inputW, inputH, RGB);
		this.outputImg = parent.createImage(outputW, outputH, RGB);
	}

	/**
	 * Register hooks to be called by PApplet
	 */
	private void registerParentMethods() {
		// parent.registerMethod("pre", this);
		parent.registerMethod("draw", this);
		// parent.registerMethod("mouseEvent", this);
		parent.registerMethod("keyEvent", this);
		parent.registerMethod("dispose", this);
	}

	private void welcome() {
		System.out
				.println("##library.name## ##library.prettyVersion## by ##author##");
	}

	public void setOutputDimensions(int w, int h) {
		this.outputW = w;
		this.outputH = h;
	}

	private Mat getPerspectiveTransformation(ArrayList<PVector> inputPoints,
			int w, int h) {
		Point[] canonicalPoints = new Point[4];
		canonicalPoints[0] = new Point(0, 0);
		canonicalPoints[1] = new Point(w, 0);
		canonicalPoints[2] = new Point(w, h);
		canonicalPoints[3] = new Point(0, h);

		MatOfPoint2f canonicalMarker = new MatOfPoint2f();
		canonicalMarker.fromArray(canonicalPoints);

		Point[] points = new Point[4];
		for (int i = 0; i < 4; i++) {
			points[i] = new Point(inputPoints.get(i).x, inputPoints.get(i).y);
		}
		MatOfPoint2f marker = new MatOfPoint2f(points);
		return Imgproc.getPerspectiveTransform(marker, canonicalMarker);
	}

	private Mat warpPerspective(ArrayList<PVector> inputPoints, int w, int h) {
		Mat transform = getPerspectiveTransformation(inputPoints, w, h);
		Mat unWarpedMarker = new Mat(w, h, CvType.CV_8UC1);
		Imgproc.warpPerspective(ocv.getColor(), unWarpedMarker, transform,
				new Size(w, h));
		return unWarpedMarker;
	}

	public void draw() {
		if (!display) {
			return;
		}

		parent.image(inputImg, 0, 0);
	}

	/**
	 * Called from parent when processing sketch is closed - used for cleanup.
	 */
	public void dispose() {
		// Anything in here will be called automatically when
		// the parent sketch shuts down. For instance, this might
		// shut down a thread used by this library.
	}

	/**
	 * Return the output image that is the result of warping the selected input window
	 * to the output width/height.
	 * 
	 * @param input
	 * @return
	 */
	public PImage focus(PImage input) {
		this.inputImg = input;
		ocv.loadImage(this.inputImg);
		ocv.toPImage(warpPerspective(window.getPoints(), outputW, outputH),
				outputImg);
		return outputImg;
	}

	/**
	 * Return the output image that is the result of warping the selected input window
	 * to the output width/height, specifying a new output width/height.
	 * 
	 * @param input
	 * @param w
	 * @param h
	 * @return PImage
	 */
	public PImage focus(PImage input, int w, int h) {
		inputImg = input;
		if (inputW != inputImg.width || inputH != inputImg.height) {
			inputW = inputImg.width;
			inputH = inputImg.height;
			ocv = new OpenCV(parent, inputW, inputH);
		}
		if (outputW != w || outputH != h) {
			outputW = w;
			outputH = h;
			outputImg = parent.createImage(outputW, outputH, RGB);
		}

		return focus(input);
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
			// do something for the mouse being pressed
			break;
		case MouseEvent.RELEASE:
			// do something for mouse released
			break;
		case MouseEvent.CLICK:
			// do something for mouse clicked
			break;
		case MouseEvent.DRAG:
			// do something for mouse dragged
			break;
		case MouseEvent.MOVE:
			// umm... forgot
			break;
		}
	}

	/**
	 * Handle key events
	 * 
	 * @param event
	 */
	public void keyEvent(KeyEvent event) {
		int key = event.getKey();
		System.out.println("Key! " + key);

		switch (key) {
		case 's':
			// toggle display
			display = !display;
			this.window.display = display;
			break;
		}
	}

	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}
}
