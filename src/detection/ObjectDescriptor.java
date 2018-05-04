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
     * volume
     */
    private int v;
    /**
     * parent time if split
     */
    private int pt;
    /**
     * parent number if split
     */
    private int pn;
    
    
/**
 * ObjectDescriptor
 * @param timePoint
 * @param number
 * @param center
 * @param volume (pixels)
 * @param parentTime
 * @param parentNumber
 */
    public ObjectDescriptor (int timePoint, int number, double[] center, int volume, int parentTime, int parentNumber){
        t = timePoint;
        n = number;
        x = center[0];
        y = center[1];
        z = center[2];
        v = volume;
        pt = parentTime;
        pn = parentNumber;
    }
    
    /**
     * Set the value of t
     * @param newVar 
     */
    public void setT(int newVar) {
        t = newVar;
    }
    
        /**
     * Set the value of n
     * @param newVar 
     */
    public void setN(int newVar) {
        n = newVar;
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
     * Set the value of v (volume in pixels)
     * @param newVar 
     */
    public void setV(int newVar) {
        v = newVar;
    }
    
    /**
     * Set the value time of pt (object parent)
     * @param newVar 
     */
    public void setPt(int newVar) {
        pt = newVar;
    }
    
    /**
     * Set the values of p (object parent), [0]=time,[1]=number of object
     * @param newVar 
     */
    public void setPn(int newVar) {
        pn = newVar;
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
     * Get the value of v (volume in pixels)
     * @return the value of v, 
     */
    public int getV() {
        return v;
    }
    
    /**
     * Get the value of parent time
     * @return the value of Parent time, 
     */
    public int getPt() {
        return pt;
    }
    
    /**
     * Get the value of parent number
     * @return the value of Parent number, 
     */
    public int getPn() {
        return pn;
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
        double[] center = new double[5];
        center[0] = this.x;
        center[1] = this.y;
        center[2] = this.z;
        center[3] = this.t;
        center[4] = this.n;
        return center;
    }
}
