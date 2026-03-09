/** A utility class defining a point object
 *  and relevant sorting functions to be used
 *  for the Closest Pair algorithms
 * 
 *  Programming assignment for
 *  CSI403 Algorithms and Data Structures
 *  University at Albany - SUNY
 *  
 * Instructions: Only change the runnningTimeComparison method
 * All other methods will not be considered when testing your 
 * program.
 */

package closestpair;

import java.util.Arrays;
import java.util.Comparator;

public class Point{
	
	/** 
	 * public members for coordinates
	 */
	public final Double x, y;
	
	/** 
	 * Constructor
	 */
	public Point(Double _x, Double _y) {
		x = _x;
		y = _y;
	}
	
	/** 
	 * Computes the Euclidean distance to another point o
	 */
	public Double dist(Point o) {
		return Math.sqrt(Math.pow(x-o.x, 2) + Math.pow(y-o.y, 2));
	}
	
	/** 
	 * Prints a string representation of a point
	 */
	public String toString() {
		return x.toString() + ":" + y.toString();
	}
	
	/** 
	 * Sorts an array of points by increasing x 
	 */
	public static Point[] sortByX(Point[] pts) {
		Arrays.sort( pts, new Comparator<Point>() {
		       public int compare(Point a, Point b) {
		         int res = Double.compare(a.x, b.x);
		         if ( res == 0 ) {
		           // both X are equal -> compare Y too
		           res = Double.compare(a.y, b.y);
		           if (res == 0) {
		        	   res = ((Integer)a.hashCode()).compareTo(b.hashCode());
		           }
		         } 
		         return res;
		      }
		});
		Point[] ptsX = new Point[pts.length];
		for(int i=0; i< pts.length; i++) ptsX[i] = pts[i];
		return ptsX;
	}
	
	/** 
	 * Sorts an array of points by increasing y 
	 */
	public static Point[] sortByY(Point[] pts) {
		Arrays.sort( pts, new Comparator<Point>() {
		       public int compare(Point a, Point b) {
		         int res = Double.compare(a.y, b.y);
		         if ( res == 0 ) {
		           // both Y are equal -> compare X too
		           res = Double.compare(a.x, b.x);
		           if (res == 0) {
		        	   res = ((Integer)a.hashCode()).compareTo(b.hashCode());
		           }
		         } 
		         return res;
		      }
		});
		Point[] ptsY = new Point[pts.length];
		for(int i=0; i< pts.length; i++) ptsY[i] = pts[i];
		return ptsY;
	}
	
	
}
