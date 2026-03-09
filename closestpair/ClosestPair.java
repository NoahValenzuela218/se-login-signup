/** A main class for the Closest Pair algorithms
 *  Programming assignment for
 *  CSI403 Algorithms and Data Structures
 *  University at Albany - SUNY
 *  
 * Instructions: Implement methods: 
 * 1) getCPBruteForce()
 * 2) getCPDivideAndConquer()
 * As discussed in class and in the assignment part (a)
 */
package closestpair;
import java.util.HashSet;

public class ClosestPair {
	
	/** TODO: IMPLEMENT 
	 *  A brute force method for the closest pair
	 *  @returns an array of exactly the two closest points
	 *  IMPORTANT: DO NOT CHANGE THIS METHOD SIGNATURE,
	 *  ONLY THE BODY WILL BE CONSIDERED FOR GRADING
	 */
	public static Point[] getCPBruteForce (Point[] pts)  {
		//TODO: Implement this method for part (a) of the assignment 
		double minDistance = Double.MAX_VALUE;
    Point[] closestPoints = null;
    
    for (int i = 0; i < pts.length-1; i++) {
        for (int j = i + 1; j < pts.length; j++) {
		 double distance = pts[i].dist(pts[j]);
        	if (distance < minDistance) {
                minDistance = distance;
                closestPoints = new Point[] {pts[i], pts[j]};
            }
        }
    }
    return closestPoints;
	}
	
	/** A driver for the Divide-And-Conquer method for the closest pair
	 *  takes unsorted array of points, sorts them and invokes 
	 *  the recursive method you are required to implement
	 *  
	 *  @returns an array of exactly the two closest points
	 *  IMPORTANT: DO NOT CHANGE THIS METHOD
	 */
	public static Point[] getCPDivideAndConquer(Point[] pts) {
		Point[] ptsX = Point.sortByX(pts); 
		Point[] ptsY = Point.sortByY(pts);
		return getCPDivideAndConquer(ptsX, ptsY);
	}
	
	/** TODO: IMPLEMENT 
	 *  A Divide-And-Conquer method for the closest pair
	 *  takes as input the points sorted by increasing x
	 *  and y coordinates in ptsX and ptsY respectively
	 *  @returns an array of exactly the two closest points.
	 *  IMPORTANT: DO NOT CHANGE THIS METHOD SIGNATURE,
	 *  ONLY THE BODY WILL BE CONSIDERED FOR GRADING
	 */
	public static Point[] getCPDivideAndConquer(Point[] ptsX, Point[] ptsY) {
		int n = ptsX.length;
		
		// Base case: if less than 2 points, return null
		if (n < 2) {
			return null;
		}
		
		// Base case: if exactly 2 points, return them
		if (n == 2) {
			return new Point[] {ptsX[0], ptsX[1]};
		}
		
		// Base case: if 3 points, use brute force
		if (n == 3) {
			double d01 = ptsX[0].dist(ptsX[1]);
			double d02 = ptsX[0].dist(ptsX[2]);
			double d12 = ptsX[1].dist(ptsX[2]);
			
			if (d01 <= d02 && d01 <= d12) {
				return new Point[] {ptsX[0], ptsX[1]};
			} else if (d02 <= d01 && d02 <= d12) {
				return new Point[] {ptsX[0], ptsX[2]};
			} else {
				return new Point[] {ptsX[1], ptsX[2]};
			}
		}
		
		// DIVIDE: Find median and split points
		int mid = n / 2;
		Point medianPoint = ptsX[mid];
		
		// Split ptsX into left and right
		Point[] ptsXLeft = new Point[mid];
		Point[] ptsXRight = new Point[n - mid];
		for (int i = 0; i < mid; i++) {
			ptsXLeft[i] = ptsX[i];
		}
		for (int i = mid; i < n; i++) {
			ptsXRight[i - mid] = ptsX[i];
		}
		
		// Split ptsY into left and right based on median x-coordinate
		Point[] ptsYLeft = new Point[mid];
		Point[] ptsYRight = new Point[n - mid];
		int leftIdx = 0, rightIdx = 0;
		
		// Create a set of points in the left half for quick lookup
		HashSet<Point> leftSet = new HashSet<Point>();
		for (int i = 0; i < mid; i++) {
			leftSet.add(ptsXLeft[i]);
		}
		
		for (int i = 0; i < n; i++) {
			if (leftSet.contains(ptsY[i])) {
				ptsYLeft[leftIdx++] = ptsY[i];
			} else {
				ptsYRight[rightIdx++] = ptsY[i];
			}
		}
		
		// CONQUER: Recursively find closest pairs in left and right halves
		Point[] pairLeft = getCPDivideAndConquer(ptsXLeft, ptsYLeft);
		Point[] pairRight = getCPDivideAndConquer(ptsXRight, ptsYRight);
		
		// Find minimum distance and corresponding pair from left and right
		double dLeft = (pairLeft != null) ? pairLeft[0].dist(pairLeft[1]) : Double.MAX_VALUE;
		double dRight = (pairRight != null) ? pairRight[0].dist(pairRight[1]) : Double.MAX_VALUE;
		
		double d;
		Point[] closestPair;
		
		if (dLeft <= dRight) {
			d = dLeft;
			closestPair = pairLeft;
		} else {
			d = dRight;
			closestPair = pairRight;
		}
		
		// COMBINE: Check strip around median line for closer pairs
		// Build array of points within distance d from median
		Point[] strip = new Point[n];
		int stripSize = 0;
		
		for (int i = 0; i < n; i++) {
			if (Math.abs(ptsY[i].x - medianPoint.x) < d) {
				strip[stripSize++] = ptsY[i];
			}
		}
		
		// Check points in strip (already sorted by y coordinate)
		// Only need to check next 7 points for each point
		for (int i = 0; i < stripSize; i++) {
			for (int j = i + 1; j < Math.min(stripSize, i + 8); j++) {
				double dist = strip[i].dist(strip[j]);
				if (dist < d) {
					d = dist;
					closestPair = new Point[] {strip[i], strip[j]};
				}
			}
		}
		
		return closestPair;
	}
}
