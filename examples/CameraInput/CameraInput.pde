import com.jonwohl.*;
import processing.video.*;

import gab.opencv.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;

import org.opencv.core.Mat;
import org.opencv.core.CvType;

Capture cam;
PImage out;
Attention attention;

/**

Press 's' to show the input image and selection window. 

With the selection window showing, drag the vertices to adjust
the focus region.

*/

void setup() {
  size(600, 600);
  cam = new Capture(this, 320, 240);
  cam.start();
  
  // instatiate passing an initial input image
  attention = new Attention(this, cam);
  
  // or instantiate by manually setting input h/w
  // attention = new Attention(this, 320, 240);
  
}

void draw() {
  // set bg to black
  background(0);
  
  if (cam.available()) { 
    // Reads the new frame
    cam.read();
  }
  
  // warp the selected region on the input image (cam) to an output image of width x height
  // i.e. 'focus' the 'attention' on cam
  out = attention.focus(cam, width, height);
  
  // display the resulting image
  image(out, 0, 0);
}
