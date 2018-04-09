/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package functions;

import detection.ObjectDescriptor;
import detection.Population;
import ij.IJ;
import java.util.ArrayList;
import java.util.List;
import structure.Node;
import structure.Tree;


/**
 *
 * @author Jean-Francois Gilles
 */
public class ScoringFunction {
    int count;
    float motion;
    float split;
    float apoptosis;
    float out;
    float in;
    
    Tree tree;
    ArrayList <ObjectDescriptor> treeList = new ArrayList();
//    ArrayList <ObjectDescriptor> objToDelete = new ArrayList();
    
    public void ScoringFunction(){
        float g=motion+split+apoptosis+out+in;
    }
    
    /**
     * Probability density at a candidate location xt+1 in image t+1
     * @param xt previous location (t)
     * @param xt1 next location (t+1)
     * @param variance of the cell displacement, need to be learned
     * @return 
     */
    public float gaussianFunction(double xt, double xt1, double variance){
        return (float) ((1/(2*variance*Math.PI ))*Math.exp(Math.pow(xt1-xt, 2)/(2*variance)));
    }
    
    /**
     * Return the mean of the table
     * @param values
     * @return 
     */
    public double mean(double[] values){
        double sum = 0;
        for(int i=0; i<values.length; i++){
            sum += values[i];
        }
        return sum/values.length;
    }
    
    /**
     * Probability density at a candidate location pt1 in image t+1 
     * @param pt point time t, xyz(t)
     * @param pt1 point time t+1, xyz(t)
     * @param variance in 3D, need to be learned
     * @return 
     */
    public static double gaussianFunction3D(double[] pt, double[] pt1, double variance){
        double euclidianDist = Math.pow( 0.125*((Math.pow(pt1[0]-pt[0], 2) + Math.pow(pt1[1]-pt[1], 2)) + Math.pow(pt1[2]-pt[2], 2)), 0.5);
        return 1/(2*variance*Math.PI)*(Math.exp(Math.pow(euclidianDist, 2)/(2*variance)));
    }
    
    /**
     * distribution of dectections from other objects
     * @param width of the image
     * @param height of the image
     * @param nSlices of the image
     * @return 
     */
    public static double distributionDetection(int width, int height, int nSlices){
        return 1/(width*height*nSlices);
    }
    
    /**
     * 
     * @param pt previous location (t)
     * @param pt1 next location (t+1)
     * @param variance of the cell displacement, need to be learned
     * @param prior probability (t), indepentent, estimated from detection pairs in training data
     * @param area of the image
     * @return 
     */
    public static double posteriorProba (double[] pt, double[] pt1, double variance, double prior, double area){
        return (prior*gaussianFunction3D(pt, pt1, variance)) / (prior*gaussianFunction3D(pt, pt1, variance)+(1-prior)*(1/area));
    }
    
    
    public static List<ArrayList> scoreMobility (double variance, double prior, double area){
        Population.returnPopulations();
        List<ArrayList> links = Population.emptyAllObjectsLinking();
        List<ArrayList> listObj=Population.allObjectsLocalization();
        for(int t=0; t<listObj.size()-1; t++){//time
            ArrayList<ArrayList> link1 = (ArrayList) links.get(t);
            for(int i=0; i<listObj.get(t).size(); i++){//objects in time0
                ArrayList<Double> link = (ArrayList) link1.get(i);
                for(int j=0; j<listObj.get(t+1).size(); j++){//objects in time1
                    double[] o = (double[]) listObj.get(t).get(i);//0
                    double[] o1 =(double[]) listObj.get(t+1).get(j);//1
//                    double euclidianDist = Math.pow( (Math.pow(o1[0]-o[0], 2) + Math.pow(o1[1]-o[1], 2) + Math.pow(o1[2]-o[2], 2)), 0.5);
//                    double gauss = ScoringFunction.gaussianFunction3D(o, o1, variance);
                    
                    double proba = (ScoringFunction.posteriorProba(o, o1, variance, prior, area));
                    double logProba = Math.log(1)-Math.log(proba);
                    //show only bestProba
//                    if(!(String.valueOf(logProba).equals("NaN")) && (logProba!=0.0)){
//                        IJ.log(""+t+" ob1="+(i+1)+" ob2="+(j+1)+" distance "+euclidianDist+"  f="+gauss+"  p="+proba+"  deltaG="+logProba);

//                        IJ.log(""+t+" ob1="+(i+1)+" ob2="+(j+1)+"  p="+proba+"  deltaG="+logProba);
                        
//                    }
                    //Ne faut-il pas récupéter directement le meilleur dès le calcul...
                    
                    link.set(j, logProba);
//                    double value = link.set(j, logProba);
//                    IJ.log(""+value);
                }
                link1.set(i, link);
            }
            links.set(t, link1);
        }
        return links;
    }
    
//    public static List<ArrayList> bestScoreM (double variance, double prior, double area){
//        Population.returnPopulations();
//        List<ArrayList> listObj=Population.allObjectsLocalization();
//        List<ArrayList> listLinks= Population.emptyBestLinking();
//        for(int t=0; t<listObj.size()-1; t++){//time
//            ArrayList<double[]> arrayLink = listLinks.get(t);
//            for(int i=0; i<listObj.get(t).size(); i++){//objects in time0
//                double[] old = (double[]) arrayLink.get(i);
//                for(int j=0; j<listObj.get(t+1).size(); j++){//objects in time1
//                    double[] l = new double [2];
//                    double[] o = (double[]) listObj.get(t).get(i);//0
//                    double[] o1 =(double[]) listObj.get(t+1).get(j);//1
//                    double proba = (ScoringFunction.posteriorProba(o, o1, variance, prior, area));
//                    double logProba = Math.log(1)-Math.log(proba);
//                    
//                    l[0]=(double)j;
//                    l[1]=logProba;
//                    if(l[1]>old[1]){
//                        old[0]=l[0];
//                        old[1]=l[1];
//                    }
//                }
//                arrayLink.set(i, old);
////                IJ.log("best"+t+": obj1="+(i+1)+"  obj2="+(old[0]+1)+"  value="+old[1]);
//            }
//            listLinks.set(t, arrayLink);
//        }
//        return listLinks;
//    }
    
    /**
     * 
     * @param variance
     * @param prior
     * @param area
     * @return 
     */
    public static List<ArrayList> bestScore (double variance, double prior, double area){
        List<ArrayList> linksMob = scoreMobility(variance, prior, area);
        List<ArrayList> bestLinks= Population.emptyBestLinking();
        for(int t=0; t<linksMob.size(); t++){//time
            ArrayList<double[]> bestLink1 = bestLinks.get(t);
            for(int i=0; i<linksMob.get(t).size(); i++){//objects in time0
                ArrayList<Double> link = (ArrayList) linksMob.get(t).get(i);
//                ArrayList<double[]> bestLink2 = bestLink1.get(i);
                double[] old = (double[]) bestLink1.get(i);
                for(int j=0; j<linksMob.get(t).size(); j++){//objects in time1
                    double[] l = new double [3];
                    l[0] = (double)j;//nb obj
                    l[1] = link.get(j);//value
                    l[2] = 1;//1=mobility
                    if(l[1]>old[1]){
                        old[0]=l[0];
                        old[1]=l[1];
                    }
                }
                bestLink1.set(i, old);//argMax and max
//                IJ.log("bestS "+t+" : obj1="+(i)+"  obj2="+(old[0])+"  value="+old[1]);
            }
            bestLinks.set(t, bestLink1);
        }
        return bestLinks;
    }
    
    
    public static Tree backTrack (double variance, double prior, double area){
        List<ArrayList> bestScore = bestScore(variance, prior, area);
        Tree tree = new Tree(bestScore.size());
        tree.addRoot("root");
        
        ArrayList<ObjectDescriptor> arDesc = Population.returnListObjects();
        ArrayList<ObjectDescriptor> toDelete = new ArrayList<>();
        //Classify in the tree/forest
        int cellCounter = 0;
        while(arDesc.isEmpty()==false){
            ObjectDescriptor des = arDesc.get(0);
            if(arDesc.size()>1){//if there are other object in list
                int t0 = des.getT();
                int n0 = des.getN();
                int previousT=t0;
                int previousN=n0;
                for(int t=t0;t<bestScore.size();t++){//scan time points
                    
                    for(int i=0; i<bestScore.get(t).size(); i++){//scan all results in time t
                        
                        if((t==previousT) && (i==previousN)){//retrieve position of the object t0-n0 in the results
                            
                            double[] track = (double[]) bestScore.get(t).get(i);//get Best link
                            for(int j=0; j<arDesc.size(); j++){//retrieve position of the best linked object in arDesc
                                if(arDesc.get(j).getT()==(previousT+1) && arDesc.get(j).getN()==track[0] && arDesc.get(j)!=des){
//                                    if(track[2]==2){ //if motosis, add position in an array to retrieve the children, think about tree
                                            //should use something to retrieve parent in indexesToDelete...
//                                    }
                                    IJ.log("obA="+previousN+" t"+previousT+" objB="+(int)track[0]+" t"+t);
//                                    t1=t;
                                    previousN=(int)track[0];
                                    toDelete.add(arDesc.get(j));
//                                    objToDelete.add(cellCounter);
                                    previousT=previousT+1;
                                    cellCounter++;
                                    break;
                                }
                            }
                        }
//                        IJ.log("obA="+n1+" t"+t0+" objB="+n1+" t"+t1+"  "+t);
                    }
                }
                //Add to tree
                Node a = new Node(des);
                tree.root.addChild(a,des.getT());
                if(toDelete.isEmpty()==false){
                    //les enfants doivent etre enfants d'enfants
                    // 0
                    ObjectDescriptor obj = toDelete.get(0);
                    int index = arDesc.indexOf(obj);
                    ObjectDescriptor desChild = arDesc.get(index);
                    Node b = new Node(desChild);
                    a.addChild(b, 0);
//                    a.addChild(b, desChild.getT());
                    if(toDelete.size()>1){
                        // + de 0
                        while(toDelete.size()!=1){
                            ObjectDescriptor obj2 = toDelete.get(1);
                            int index2 = arDesc.indexOf(obj2);
                            desChild = arDesc.get(index2);
                            Node c = new Node(desChild);
                            b.addChild(c, 0);
//                            b.addChild(c, desChild.getT());
                            arDesc.remove(index2);
                            toDelete.remove(1);
                            b=c;
                        }
                        arDesc.remove(index);
                        toDelete.remove(0);
                        
                    }
                }
                arDesc.remove(des);
            }
            else{
                Node a = new Node(des);
                tree.root.addChild(a, des.getT());
                arDesc.remove(0);
            }
        }
        
        
        
//        for(int t=0; t<bestScore.size()-1; t++){//time
//            ArrayList<double[]> arrayLink = bestScore.get(t);
//            for(int i=0; i<bestScore.get(t).size(); i++){//objects in time0
//                double[] val = (double[]) arrayLink.get(i);
//                if(t==0){
//                    if(val[2]==1){
//                        Node a = new Node(val);
//                        tree.root.addChild(a, t);
//                    }
//                }
//            }
//        }
        return tree;
    }
    
    
}
