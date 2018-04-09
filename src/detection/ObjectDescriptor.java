package detection;



/**
 *
 * @author Jean-Francois Gilles
 */
public class ObjectDescriptor {
    
    /**
     * time point
     */
    private int t;
    /**
     * number of the object
     */
    private int n;
    /**
     * IsoBarycenter x
     */
    private double x;
    /**
     * IsoBarycenter y
     */
    private double y;
    /**
     * IsoBarycenter z
     */
    private double z;
    /**
     * hidden if more than 0
     */
    private int h;

    
    
/**
 * ObjectDescriptor
 * @param timePoint
 * @param number
 * @param center
 * @param hidden , it can be used if object disappear
 */
    public ObjectDescriptor (int timePoint, int number, double[] center, int hidden){
        t = timePoint;
        n = number;
        x = center[0];
        y = center[1];
        z = center[2];
        h = hidden;

    }
    
    /**
     * Set the value of t
     * @param newVar 
     */
    public void setT(int newVar) {
        t = newVar;
    }
    
    /**
     * Set the value of x (center)
     * @param newVar 
     */
    public void setX(double newVar) {
        x = newVar;
    }
    
    /**
     * Set the value of y (center)
     * @param newVar 
     */
    public void setY(double newVar) {
        y = newVar;
    }
    
    /**
     * Set the value of z (center)
     * @param newVar 
     */
    public void setZ(double newVar) {
        z = newVar;
    }
    
    /**
     * Set the value of h (object is hidden if more than 0)
     * @param newVar 
     */
    public void setH(int newVar) {
        h = newVar;
    }
    
    
    /**
     * Get the value of t, timePoint
     * @return the value of t
     */
    public int getT() {
        return t;
    }
    
    /**
     * Get the value of n, number of the object
     * @return the value of n, number of the object
     */
    public int getN() {
        return n;
    }
    
    /**
     * Get the value of x
     * @return the value of x
     */
    public double getX() {
        return x;
    }
    
    /**
     * Get the value of y
     * @return the value of y
     */
    public double getY() {
        return y;
    }
    
    /**
     * Get the value of z
     * @return the value of z
     */
    public double getZ() {
        return z;
    }
    
    /**
     * Get the value of h, hidden object if more than 0
     * @return the value of h, 
     */
    public int getH() {
        return h;
    }

    
//    /**
//     * Get the volume in pixels of the object
//     * @param obj
//     * @param timePoint
//     * @return 
//     */
//    public int getObjetVolume(Object3D obj, int timePoint){
//        return obj.getVolumePixels();
//    }
    
    /**
     * ObjectDescriptor as Array
     * @return 
     */
    public double[] asArray(){
        double[] center = new double[4];
        center[0] = this.x;
        center[1] = this.y;
        center[2] = this.z;
        center[3] = this.t;
        return center;
    }
}
