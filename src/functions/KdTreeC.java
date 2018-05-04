
package functions;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is a KD Range Tree, for fast sorting and searching of K dimensional
 * data.
 *
 * @author Chase
 *
 */
public class KdTreeC {

    /**
     * KDPoint, for moving data around.
     *
     * @author Chase
     */
    public class KDPoint {
        
        public double[] pnt;
        public Object obj;
        public double distanceSq;
        
        private KDPoint(double[] p, Object o) {
            pnt = p;
            obj = o;
        }
    }
    private final int dimensions;
    private final int box_size;
    private KDNode root;
    private double[] scale;

    /**
     * Constructor with value for dimensions.
     *
     * @param dimensions - Number of dimensions
     */
    public KdTreeC(int dimensions) {
        this.dimensions = dimensions;
        this.box_size = 64;
        this.root = new KDNode(this);
        // scale ??
        double[] tmp = {1, 1, 1};
    }

    /**
     * Constructor with value for dimensions and box size.
     *
     * @param dimensions - Number of dimensions
     * @param box - Size of the boxes.
     */
    public KdTreeC(int dimensions, int box) {
        this.dimensions = dimensions;
        this.box_size = box;
        this.root = new KDNode(this);
    }
    
    public void setScale(double[] sc) {
        double[] tmp = {sc[0] * sc[0], sc[1] * sc[1], sc[2] * sc[2]};
        this.setScaleSq(tmp);
    }
    
    public void setScale2(double rx, double ry,double rz) {
        double[] tmp = {rx*rx, ry*ry, rz*rz};
        this.setScaleSq(tmp);
    }
    

    private void setScaleSq(double[] scale_) {
        scale = scale_;
    }

    /**
     * Add a coord and its associated value to the tree.
     *
     * @param coord - Coord to add
     * @param val - object to add
     */
    public void add(double[] coord, Object val) {
        root.add(new KDPoint(coord, val));
    }
    
    public void addMultiple(ArrayList<double[]> coords, int coordsDim){
        if(coords.get(0).length<=coordsDim)return;
        for(int i=0; i<coords.size(); i++){
//            double[] coord = new double[coordsDim];
//            for(int l=0; l<coordsDim; l++){
//                coord[l] = coords.get(i)[l];
//            }
            double[] coord = Arrays.copyOf(coords.get(i), coordsDim);
            root.add(new KDPoint(coord, i));
        }
    }

    /**
     * Returns all PointKD within a certain range defined by an upper and lower
     * KDPoint.
     *
     * @param low - lower bounds of area
     * @param high - upper bounds of area
     * @return - All KDPoint between low and high.
     */
    public KDPoint[] getRange(double[] low, double[] high) {
        return root.range(high, low);
    }
    
    /**
     * Gets the N nearest neighbors to the given coord.
     *
     * @param coord - Coord
     * @param num - Number of results
     * @return Array of KDPoint Objects, distances within the KDPoints are the square
     * of the actual distance between them and the coord
     */
    public KDPoint[] getNearestNeighbors(double[] coord, int num) {
        ShiftArray arr = new ShiftArray(num);
        root.nearestn(arr, coord);
        return arr.getArray();
    }
    
//    public int getNearestNeighbors(double[] coord, int num) {
//        ShiftArray arr = new ShiftArray(num);
//        root.nearestn(arr, coord);
//        return arr.getArray();
//    }

    /**
     * Compares arrays of double and returns the euclidean distance between
     * them. The distance is converted to unit.
     *
     * @param a - The first set of numbers
     * @param b - The second set of numbers
     * @return The distance squared root between <b>a</b> and <b>b</b>.
     */
    public double distance(double[] a, double[] b) {
        double total = 0;
        for (int i = 0; i < a.length; ++i) {
            total += (b[i] - a[i]) * (b[i] - a[i]) * scale[i];
        }
        return Math.sqrt(total);
    }

    /**
     * Compares arrays of double and returns the squared euclidean distance
     * between them. The distance is converted to unit.
     *
     * @param a - The first set of numbers
     * @param b - The second set of numbers
     * @return The distance squared between <b>a</b> and <b>b</b>.
     */
    public double distanceSq(double[] a, double[] b) {
        double total = 0;
        for (int i = 0; i < a.length; ++i) {
            total += (b[i] - a[i]) * (b[i] - a[i]) * scale[i];
        }
        return total;
    }

    //Internal tree node
    private class KDNode {
        
        private KdTreeC owner;
        private KDNode left, right;
        private double[] upper, lower;
        private KDPoint[] box;
        private int current, dim;
        private double slice;

        //note: we always start as a box
        private KDNode(KdTreeC own) {
            owner = own;
            upper = lower = null;
            left = right = null;
            box = new KDPoint[own.box_size];
            current = 0;
            dim = 0;
        }
        
        //when we create non-root nodes within this class
        //we use this one here
        private KDNode(KDNode node) {
            owner = node.owner;
            dim = node.dim + 1;
            box = new KDPoint[owner.box_size];
            if (dim + 1 > owner.dimensions) {
                dim = 0;
            }
            left = right = null;
            upper = lower = null;
            current = 0;
        }
        
        
        private void add(KDPoint m) {
            if (box == null) {
                //Branch
                if (m.pnt[dim] > slice) {
                    right.add(m);
                } else {
                    left.add(m);
                }
            } else {
                //Box
                if (current + 1 > owner.box_size) {
                    split(m);
                    return;
                }
                box[current++] = m;
            }
            extendBounds(m.pnt);
        }
        
        //nearest neighbor thing
        private void nearestn(ShiftArray arr, double[] data) {
            if (box == null) {
                //Branch
                if (data[dim] > slice) {
                    right.nearestn(arr, data);
                    if (left.current != 0) {
                        if (owner.distanceSq(left.nearestRect(data), data) < arr.getLongest()) {
                            left.nearestn(arr, data);
                        }
                    }
                } else {
                    left.nearestn(arr, data);
                    if (right.current != 0) {
                        if (owner.distanceSq(right.nearestRect(data), data) < arr.getLongest()) {
                            right.nearestn(arr, data);
                        }
                    }
                }
            } else {
                //Box
                for (int i = 0; i < current; i++) {
                    box[i].distanceSq = owner.distanceSq(box[i].pnt, data);
                    arr.add(box[i]);
                }
            }
        }
        
        //gets all KDPoints from within a range
        private KDPoint[] range(double[] upper, double[] lower) {
            if (box == null) {
                //Branch
                KDPoint[] tmp = new KDPoint[0];
                if (intersects(upper, lower, left.upper, left.lower)) {
                    KDPoint[] tmpl = left.range(upper, lower);
                    if (0 == tmp.length) tmp = tmpl;
                }
                if (intersects(upper, lower, right.upper, right.lower)) {
                    KDPoint[] tmpr = right.range(upper, lower);
                    if (0 == tmp.length) {
                        tmp = tmpr;
                    } else if (0 < tmpr.length) {
                        KDPoint[] tmp2 = new KDPoint[tmp.length + tmpr.length];
                        System.arraycopy(tmp, 0, tmp2, 0, tmp.length);
                        System.arraycopy(tmpr, 0, tmp2, tmp.length, tmpr.length);
                        tmp = tmp2;
                    }
                }
                return tmp;
            }
            //Box
            KDPoint[] tmp = new KDPoint[current];
            int n = 0;
            for (int i = 0; i < current; i++) {
                if (contains(upper, lower, box[i].pnt)) {
                    tmp[n++] = box[i];
                }
            }
            KDPoint[] tmp2 = new KDPoint[n];
            System.arraycopy(tmp, 0, tmp2, 0, n);
            return tmp2;
        }

        //These are helper functions from here down
        //check if this hyper rectangle contains a give hyper-point
        public boolean contains(double[] upper, double[] lower, double[] point) {
            if (current == 0) return false;
            for (int i = 0; i < point.length; ++i) {
                if (point[i] > upper[i] || point[i] < lower[i]) return false;
            }
            return true;
        }
        
        //checks if two hyper-rectangles intersect
        public boolean intersects(double[] up0, double[] low0, double[] up1, double[] low1) {
            for (int i = 0; i < up0.length; ++i) {
                if (up1[i] < low0[i] || low1[i] > up0[i]) return false;
            }
            return true;
        }
        
        //splits a box into a branch
        private void split(KDPoint m) {
            //split based on our bound data
            slice = (upper[dim] + lower[dim]) / 2.0;
            left = new KDNode(this);
            right = new KDNode(this);
            for (int i = 0; i < current; ++i) {
                if (box[i].pnt[dim] > slice) {
                    right.add(box[i]);
                } else {
                    left.add(box[i]);
                }
            }
            box = null;
            add(m);
        }
        
        //gets nearest point to data within this hyper rectangle
        private double[] nearestRect(double[] data) {
            double[] nearest = data.clone();
            for (int i = 0; i < data.length; ++i) {
                if (nearest[i] > upper[i]) {
                    nearest[i] = upper[i];
                }
                if (nearest[i] < lower[i]) {
                    nearest[i] = lower[i];
                }
            }
            return nearest;
        }
        
        //expands this hyper rectangle
        private void extendBounds(double[] data) {
            if (upper == null) {
                upper = Arrays.copyOf(data, owner.dimensions);
                lower = Arrays.copyOf(data, owner.dimensions);
                return;
            }
            for (int i = 0; i < data.length; ++i) {
                if (upper[i] < data[i]) {
                    upper[i] = data[i];
                }
                if (lower[i] > data[i]) {
                    lower[i] = data[i];
                }
            }
        }
    }
    
    //A simple shift array that shifts data up
    //as we add new ones to lower in the array.
    private class ShiftArray {
        
        private KDPoint[] items;
        private final int max;
        private int currentSize;
        
        private ShiftArray(int maximum) {
            max = maximum;
            currentSize = 0;
            items = new KDPoint[max];
        }
        
        @SuppressWarnings("empty-statement")
        private void add(KDPoint m) {
            int i;
            for (i = currentSize; i > 0 && items[i - 1].distanceSq > m.distanceSq; i--);
            if (i >= max) {
                return;
            }
            if (currentSize < max) {
                ++currentSize;
            }
            System.arraycopy(items, i, items, i + 1, currentSize - (i + 1));
            items[i] = m;
        }
        
        private double getLongest() {
            if (currentSize < max) {
                return Double.POSITIVE_INFINITY;
            }
            return items[max - 1].distanceSq;
        }
        
        private KDPoint[] getArray() {
            return items.clone();
        }
    }
}