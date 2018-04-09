package detection;


import mcib3d.geom.Object3D;

/**
 *
 * @author Jean-Francois Gilles
 */
public class ObjectProperties {
    
    /**
     * IsoBarycenter x
     */
    private double x=Double.NaN;
    /**
     * IsoBarycenter y
     */
    private double y=Double.NaN;
    /**
     * IsoBarycenter z
     */
    private double z=Double.NaN;
    /**
     * time point
     */
    private double t=Double.NaN;
    

    public ObjectProperties (Object3D obj, int timePoint){
        this.x = obj.getCenterX();
        this.y = obj.getCenterY();
        this.z = obj.getCenterZ();
        this.t = (double) timePoint;
    }
    
//    /**
//     * 
//     * @param obj
//     * @param timePoint
//     * @return 
//     */
//    public static double[] getObjectCenter(Object3D obj, int timePoint){
//        double[] center = new double[4];
//        center[0] = obj.getCenterX();
//        center[1] = obj.getCenterY();
//        center[2] = obj.getCenterZ();
//        center[3] = (double) timePoint;
//        return center;
//    }
//    
//    /**
//     * 
//     * @param obj
//     * @param timePoint 
//     */
//    public void setObjectCenter(Object3D obj, int timePoint){
//        x = obj.getCenterX();
//        y = obj.getCenterY();
//        z = obj.getCenterZ();
//        t = (double) timePoint;
//    }
    
    /**
     * Set the value of x
     * @param newVar 
     */
    public void setX(double newVar) {
        x = newVar;
    }
    
    /**
     * Set the value of y
     * @param newVar 
     */
    public void setY(double newVar) {
        y = newVar;
    }
    
    /**
     * Set the value of z
     * @param newVar 
     */
    public void setZ(double newVar) {
        z = newVar;
    }
    
    /**
     * Set the value of t
     * @param newVar 
     */
    public void setT(double newVar) {
        t = newVar;
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
     * Get the value of t
     * @return the value of t
     */
    public double getT() {
        return t;
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
     * ObjectProperties as Array
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
