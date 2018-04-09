package detection;


import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import java.util.List;
import java.util.ArrayList;
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
            IJ.log("i"+i+" name="+im.getShortTitle()+" f="+im.getNFrames()+" z="+im.getNSlices() + " popsize="+allPopulations[i].getNbObjects());
        }
        return allPopulations;
    }
    
    /**
     * Get populations from each image opened
     */
    public static void returnPopulations(){
        int[] listID = getImagesList();
        allPopulations= new Objects3DPopulation[listID.length];
        for(int i=0 ; i < listID.length ; i++){
            ImagePlus im=WindowManager.getImage(listID[i]);
            allPopulations[i] = getPopulation(im);
            IJ.log("i"+i+" name="+im.getShortTitle()+" f="+im.getNFrames()+" z="+im.getNSlices() + " popsize="+allPopulations[i].getNbObjects());
        }
    }
    
        /**
     * Get populations as ArrayList of ObjectDescriptors from each image opened
     * @return 
     */
    public static ArrayList <ObjectDescriptor> returnListObjects(){
        int[] listID = getImagesList();
//        allPopulations= new Objects3DPopulation[listID.length];
        ArrayList <ObjectDescriptor> treeList = new ArrayList<>();
        for(int t=0 ; t< listID.length ; t++){
            ImagePlus im=WindowManager.getImage(listID[t]);
            Objects3DPopulation popu = getPopulation(im);
            for(int o=0; o<popu.getNbObjects(); o++){
                Object3D ob = popu.getObject(o);
                ObjectDescriptor obd = new ObjectDescriptor(t, o, ob.getCenterAsArray(), 0);
                treeList.add(obd);
            }
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
        IJ.log("listObj-Size= "+listObj.size());
        return listObj;
    }
    
    
    public static List<ArrayList> emptyAllObjectsLinking(){
        List<ArrayList> listEmpty= new ArrayList();
        for(int t=0; t<(allPopulations.length-1); t++){
            ArrayList<ArrayList> arrayLink1 = new ArrayList<>();
            for(int i=0; i<allPopulations[t].getNbObjects(); i++){
                ArrayList<Double> arrayLink2 = new ArrayList<>();
                for(int j=0; j<allPopulations[t+1].getNbObjects(); j++){
                    arrayLink2.add((double)0);
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
                    double[] values = new double [2] ;
                    values[0]=(double)i;//index
                    values[1]=(double)0;
//                }
                arrayLink1.add(values);
            }
            listEmpty.add(arrayLink1);
        }
        return listEmpty;
    }
}
