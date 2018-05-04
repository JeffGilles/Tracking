package detection;


import functions.KdTreeC;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import mcib3d.geom.Object3D;
import mcib3d.geom.Objects3DPopulation;
import mcib3d.image3d.ImageInt;


/**
 *
 * @author Jean-Francois Gilles
 */
public class Population {
    
    static ImagePlus plus;
    static Objects3DPopulation pop;
    static Objects3DPopulation[] allPopulations;
    static int[] nbObjTime;
    
    static ArrayList<KdTreeC> kd;
    static ArrayList <ObjectDescriptor> allDescriptors;
//    ArrayList <ObjectDescriptor> treeList = new ArrayList();
    
    static void setImageT(ImagePlus ima){
        plus=ima;
    }
    
    static void setPopulationT(Objects3DPopulation pop3d){
        pop=pop3d;
    }
    
    public static Objects3DPopulation getPopulation(ImagePlus im){
        Objects3DPopulation popu = new Objects3DPopulation (ImageInt.wrap(im));
        return popu;
    }
    
    /**
     * get list of the imageID
     * @return 
     */
    public static int[] getImagesList (){
        return WindowManager.getIDList();
    }
    
    /**
     * Get populations from each image opened
     * @return 
     */
    public static Objects3DPopulation[] getAllPopulation(){
        int[] listID = getImagesList();
        allPopulations= new Objects3DPopulation[listID.length];
        for(int i=0 ; i < listID.length ; i++){
            ImagePlus im=WindowManager.getImage(listID[i]);
            allPopulations[i] = getPopulation(im);
//            IJ.log("i"+i+" name="+im.getShortTitle()+" f="+im.getNFrames()+" z="+im.getNSlices() + " popsize="+allPopulations[i].getNbObjects());
        }
        return allPopulations;
    }
    
    /**
     * Return the number of objects in each timePoint
     * @return 
     */
    public static int[] getNbObjInTime(){
        return nbObjTime;
    }
    
    /**
     * Get populations from each image opened
     */
    public static void setPopulations(){
        int[] listID = getImagesList();
        allPopulations= new Objects3DPopulation[listID.length];
        nbObjTime = new int[listID.length];
//        kd = new KdTreeC[listID.length];
        kd = new ArrayList<>();
        allDescriptors = new ArrayList<>();
        for(int i=0 ; i < listID.length ; i++){
            ImagePlus im=WindowManager.getImage(listID[i]);
            allPopulations[i] = getPopulation(im);
            nbObjTime[i]=allPopulations[i].getNbObjects();
            IJ.log("i"+i+" name="+im.getShortTitle()+" f="+im.getNFrames()+" z="+im.getNSlices() + " popsize="+nbObjTime[i]);
            for(int o=0; o<allPopulations[i].getNbObjects(); o++){
                Object3D ob = allPopulations[i].getObject(o);
                ObjectDescriptor obd = new ObjectDescriptor(i, o, ob.getCenterAsArray(), ob.getVolumePixels(), 0, 0);
                allDescriptors.add(obd);
//                kd[i].add(Arrays.copyOf(ob.getCenterAsArray(), ob.getCenterAsArray().length-1), o);
            }
            KdTreeC k = new KdTreeC(3);
            ArrayList<double[]> coords = Population.convertToArray(getListObjectsAtTime(i));
            k.addMultiple(coords, 3);
            kd.add(k);
        }
//        returnListObjects();
    }
    
    /**
     * allDescriptors & all kd
     */
//    public static void returnListObjects(){
//        int[] listID = getImagesList();
////        allPopulations= new Objects3DPopulation[listID.length];
////        ArrayList <ObjectDescriptor> treeList = new ArrayList<>();
//        for(int t=0 ; t< listID.length ; t++){
//            ImagePlus im=WindowManager.getImage(listID[t]);
//            Objects3DPopulation popu = getPopulation(im);
//            kd[t].setScale2(1, 1, 1);
//            for(int o=0; o<popu.getNbObjects(); o++){
//                Object3D ob = popu.getObject(o);
//                ObjectDescriptor obd = new ObjectDescriptor(t, o, ob.getCenterAsArray(), ob.getVolumePixels(), 0, 0);
//                allDescriptors.add(obd);
//                kd[t].add(Arrays.copyOf(ob.getCenterAsArray(), ob.getCenterAsArray().length-1), o);
//            }
//        }
//    }
    
    /**
     * allDescriptors
     * @return
     */
    public static ArrayList <ObjectDescriptor> getListObjects(){
        return allDescriptors;
    }
    
    /**
     * Get list of ObjectDescriptor for the time point "time"
     * @param time point
     * @return 
     */
    public static ArrayList <ObjectDescriptor> getListObjectsAtTime(int time){
        ArrayList <ObjectDescriptor> thatTime = new ArrayList<>();
        for(int i=0 ; i< allDescriptors.size() ; i++){
            if(allDescriptors.get(i).getT()==time){
                thatTime.add(allDescriptors.get(i));
            }
        }
        return thatTime;
    }
    
    public static ArrayList <KdTreeC> getKDtrees(){
        return kd;
    }
    
    public static KdTreeC getKDtreeAtTime(int time){
        return kd.get(time);
    }
    
        /**
     * Get populations as ArrayList of ObjectDescriptors from each image opened
     * @param timePoint of the returned list of objects, begin with 0
     * @return 
     */
    public static ArrayList <ObjectDescriptor> returnListObjcts(int timePoint){
        int[] listID = getImagesList();
//        allPopulations= new Objects3DPopulation[listID.length];
        ArrayList <ObjectDescriptor> treeList = new ArrayList<>();
        
        ImagePlus im=WindowManager.getImage(listID[timePoint]);
        Objects3DPopulation popu = getPopulation(im);
        for(int o=0; o<popu.getNbObjects(); o++){
            Object3D ob = popu.getObject(o);
            ObjectDescriptor obd = new ObjectDescriptor(timePoint, o, ob.getCenterAsArray(), ob.getVolumePixels(), 0, 0);
            treeList.add(obd);
        }
        return treeList;
    }
    
    
    /**
     * Return an array with the position of the center of the object with the timePoint
     * @param pop population
     * @param nbObj Number of the object to return position
     * @param timePoint Frame where to find the object
     * @return 
     */
    public static ObjectProperties getObjectCenter(Objects3DPopulation pop, int nbObj, int timePoint){
        Object3D obj= pop.getObject(nbObj);
        ObjectProperties o = new ObjectProperties(obj, timePoint);
        return o;
    }
    
    
    public static List<ArrayList> allObjectsLocalization(){
        
        List<ArrayList> listObj= new ArrayList();
        for(int t=0; t<allPopulations.length; t++){
            ArrayList<double[]> arrayObjects = new ArrayList<>();
//            ArrayList<ObjectProperties> arrayObjects = new ArrayList<>();
            for(int j=0; j<allPopulations[t].getNbObjects(); j++){
                ObjectProperties ob = Population.getObjectCenter(allPopulations[t], j, t);
                double[] loca = ob.asArray();
                arrayObjects.add(loca);
            }
            listObj.add(arrayObjects);
        }
        return listObj;
    }
    
    
    public static List<ArrayList> emptyAllObjectsLinking(){
        List<ArrayList> listEmpty= new ArrayList();
        for(int t=0; t<(allPopulations.length-1); t++){
            ArrayList<ArrayList> arrayLink1 = new ArrayList<>();
            for(int i=0; i<allPopulations[t].getNbObjects(); i++){
                ArrayList<double[]> arrayLink2 = new ArrayList<>();
                for(int j=0; j<allPopulations[t+1].getNbObjects()+1; j++){//add one obj for state out/dead
                    double[] zero = new double[2];
                    arrayLink2.add(zero);
                }
                arrayLink1.add(arrayLink2);
            }
            listEmpty.add(arrayLink1);
        }
        return listEmpty;
    }
    
    public static List<ArrayList> emptyBestLinking(){
        List<ArrayList> listEmpty= new ArrayList();
        for(int t=0; t<(allPopulations.length-1); t++){
            ArrayList<double[]> arrayLink1 = new ArrayList<>();
            for(int i=0; i<allPopulations[t].getNbObjects(); i++){
//                ArrayList<Double> arrayLink2 = new ArrayList<>();
//                for(int j=0; j<allPopulations[t+1].getNbObjects(); j++){
                    double[] values = new double [3] ;
                    values[0]=(double)i;//index
                    values[1]=(double)0;
                    values[2]=(double)0;//state (M,S,O...)
//                }
                arrayLink1.add(values);
            }
            listEmpty.add(arrayLink1);
        }
        return listEmpty;
    }
    
    /**
     * Convert this ArrayList of ObjectDescriptor as array
     * @param list
     * @return 
     */
    public static ArrayList<double[]> convertToArray(ArrayList<ObjectDescriptor> list){
        ArrayList<double[]> coords = new ArrayList<>();
        for(int i = 0; i<list.size(); i++){
            coords.add(list.get(i).asArray());
        }
        return coords;
    }
    
    /**
     * Get min distance for object nbObj in its population
     * @param t timePoint
     * @param nbObj number of the object
     * @return distance closest
     */
    public static double minDistance (int t, int nbObj){
        Objects3DPopulation pop = allPopulations[t];
        Object3D obj = pop.getObject(nbObj);
        Object3D target = pop.closestCenter(obj, true);
        double[] coord=obj.getCenterAsArray();
        double[] pt = target.getCenterAsArray();
        double euclidianDist = Math.sqrt((Math.pow(coord[0]-pt[0], 2) + Math.pow(coord[1]-pt[1], 2)) + Math.pow(coord[2]-pt[2], 2));
//        IJ.log("t"+t+"obj"+nbObj+"distanceminDist="+obj.distPixelCenter(target.getCenterAsPoint())+" eucl"+euclidianDist+" obj"+target.getName());
        return euclidianDist;
    }
    
}
