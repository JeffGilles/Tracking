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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import structure.Node;
import structure.Tree;


/**
 *
 * @author Jean-Francois Gilles
 */
public class ScoringFunction {
    //image settings
    int width, height, nSlices;
    //counting
    double birthRate, deathRate, priorC;
    //motion
    double motionVariance, priorM;
    
    double priorDS;
    
    private KdTreeC[] kd;
    
    int[] nbObjWithTime;
    Tree tree;
//    ArrayList <ObjectDescriptor> treeList;
    ArrayList <ObjectDescriptor> treeList = Population.getListObjects();
//    ArrayList <Integer> linkedNext = new ArrayList<>();
    
    /**
     * Set size of the images
     * @param width
     * @param height
     * @param nSlices 
     */
    public void setImagesParameters(int width, int height, int nSlices){
        this.width=width;
        this.height=height;
        this.nSlices=nSlices;
    }
    
    /**
     * Set parameters for the counting probas
     * @param newBirthRate
     * @param newDeathRate 
     * @param prior 
     */
    public void setCountingParameters(double newBirthRate, double newDeathRate, double prior){
        birthRate=newBirthRate;
        deathRate=newDeathRate;
        priorC=prior;
    }
    
    /**
     * Set motion parameters
     * @param variance 
     * @param prior 
     */
    public void setMotionParameters(double variance, double prior){
        motionVariance=variance;
        priorM=prior;
    }
    
    /**
     * 
     * @param percentDist value in [0,1], which will be multiply by closest dist with current neighbor in t
     */
    public void setSplitParameters(double percentDist){
        priorDS=percentDist;
    }
    
    /**
     * Geometric Distribution with p=birth/death
     * p should come from training dataset using maximum likelihood estimation
     * @param number 
     * @return  
     */
    public double geometricDistribution(int number){
        double p = birthRate/deathRate;
        return (p*Math.pow((1.0-p), number));
    }
    
    public double posteriorCountCell(int number){
        return (priorC*geometricDistribution(number-2));
    }
    
    /**
     * Probability density at a candidate location xt+1 in image t+1
     * @param xt previous location (t)
     * @param xt1 next location (t+1)
     * @return 
     */
    public double gaussianFunction(double xt, double xt1){
        return ((1.0/(2*motionVariance*Math.PI ))*Math.exp(Math.pow(xt1-xt, 2)/(2*motionVariance)));
    }
    
    public double gaussianMin(double xt, double xt1){
        return ((1.0/Math.pow(2*motionVariance*Math.PI, 0.5))*Math.exp(-Math.pow(xt1-xt, 2)/(2*motionVariance)));
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
    
    public double euclidan3D(double[] pt, double[] pt1){
        return Math.sqrt((Math.pow(pt1[0]-pt[0], 2) + Math.pow(pt1[1]-pt[1], 2)) + Math.pow(pt1[2]-pt[2], 2));
    }
    
    /**
     * Probability density at a candidate location pt1 in image t+1 
     * @param pt point time t, xyz(t)
     * @param pt1 point time t+1, xyz(t)
     * @return 
     */
    public double gaussianFunction3D(double[] pt, double[] pt1){
        double euclidianDist = Math.sqrt((Math.pow(pt1[0]-pt[0], 2) + Math.pow(pt1[1]-pt[1], 2)) + Math.pow(pt1[2]-pt[2], 2));
        return 1.0/(2*motionVariance*Math.PI)*(Math.exp(Math.pow(euclidianDist, 2)/(2.0*motionVariance)));
    }
    
    /**
     * Distribution of detections from other objects (uniform)
     * object is in the image
     * @return 
     */
    public double distributionDetection(){
        return 1.0/(width*height*nSlices);
    }
    
    /**
     * 
     * Based on Magnusson et al. april 2015
     * @param pt previous location (t)
     * @param pt1 next location (t+1)
     * @return 
     */
    public double posteriorProbaMobility (double[] pt, double[] pt1){
        return (priorM*gaussianFunction3D(pt, pt1)) / (priorM*gaussianFunction3D(pt, pt1)+(1.0-priorM)*(distributionDetection()));
    }
    
    /**
     * Return a prior proba relative to the proximity of the edge
     * @param pt point to analyse
     * @return 
     */
    public double priorProbaOut(double[] pt){
        double prior=Math.max((Math.abs(pt[0]-(width/2))/(width/2)),(Math.abs(pt[1]-(height/2))/(height/2))); //prior increase when position is near one edge
        if(nSlices>20){
            prior=Math.max(prior, (Math.abs(pt[2]-(nSlices/2))/(nSlices/2)));
        }
        return (prior*prior); //I add a non linear variable to enhance the value when near edges and reduce it when in center
    }
    
    public double posteriorProbaOut(double[] pt){
        double integ = 0;
//        double euclidianDist = 0.0;
//        int count=0;
//        for(int z=1; z<=nSlices; z++){
//            for(int y=0; y<height; y++){
//                for(int x=0; x<width;x++){
//                    double[] pt1 = {x,y,z};
//Math.pow( Math.pow(pt[0]-0, 2) , 0.5)
//        double euclidianDist = Math.pow( (Math.pow(pt1[0]-pt[0], 2) + Math.pow(pt1[1]-pt[1], 2)), 0.5);
        double y0 = gaussianMin(pt[1], 0);
        double yh = gaussianMin(pt[1], height);
        double x0 = gaussianMin(pt[0], 0);
        double xw = gaussianMin(pt[0], width);
        double inte = (1.0/(2*motionVariance))+(xw-x0)*(1.0/(2*motionVariance))*(yh-y0);
        double integX = (1.0/(2*motionVariance))*(gaussianFunction(pt[0],width)-gaussianFunction(pt[0],0))/gaussianFunction(0,width);
                    double integX2 = (1.0/(2*motionVariance))*Math.min(gaussianFunction(pt[0],width), gaussianFunction(pt[0],0));
                    double integY = (1.0/(2*motionVariance))*(gaussianFunction(pt[1],height)-gaussianFunction(pt[1],0));
                    double integY2 = (1.0/(2*motionVariance))*Math.min(gaussianFunction(pt[1],height), gaussianFunction(pt[1],0));
                    integ = Math.min(integX,integY);
                    //euclidianDist = euclidianDist+ Math.pow(Math.pow( (Math.pow(pt1[0]-pt[0], 2) + Math.pow(pt1[1]-pt[1], 2)) + Math.pow(pt1[2]-pt[2], 2), 0.5), 2);
//                    count++;
//                }
//            }
//        }
        IJ.log("x0="+x0+" xw="+xw+" inte="+inte+" post="+(0.2*(1-inte))+" else="+(0.1*(1-integ)));
//        IJ.log("intX="+integX+" intY="+integY+" x0="+gaussianFunction(pt[0],0)+" xw="+gaussianFunction(pt[0],width)+" integX2="+integX2+" integY2="+integY2);
//        IJ.log("priorO="+priorProbaOut(pt, width, height, nSlices)+" integ="+integ);
        return (0.1*(1-integ));
    }
    
    /**
     * 
     * @return 
     */
    public List<ArrayList> scoreMobility (){
        Population.setPopulations();
        List<ArrayList> links = Population.emptyAllObjectsLinking();
        List<ArrayList> listObj = Population.allObjectsLocalization();
        for(int t=0; t<listObj.size()-1; t++){//time
            ArrayList<ArrayList> link1 = (ArrayList) links.get(t);
            for(int i=0; i<listObj.get(t).size(); i++){//objects in time0
                ArrayList<Double> link = (ArrayList) link1.get(i);
                for(int j=0; j<listObj.get(t+1).size(); j++){//objects in time1
                    //if object goes out or desappear
                    double[] o = (double[]) listObj.get(t).get(i);//0
                    double[] o1 =(double[]) listObj.get(t+1).get(j);//1
//                    double euclidianDist = Math.pow( (Math.pow(o1[0]-o[0], 2) + Math.pow(o1[1]-o[1], 2) + Math.pow(o1[2]-o[2], 2)), 0.5);
//                    double gauss = ScoringFunction.gaussianFunction3D(o, o1, variance);
                    
                    double probaM = posteriorProbaMobility(o, o1);
                    double logProbaM = Math.log(1)-Math.log(probaM);
                    //show only bestProba
//                    if(!(String.valueOf(logProba).equals("NaN")) && (logProba!=0.0)){
//                        IJ.log(""+t+" ob1="+(i+1)+" ob2="+(j+1)+" distance "+euclidianDist+"  f="+gauss+"  p="+proba+"  deltaG="+logProba);

//                        IJ.log(""+t+" ob1="+(i+1)+" ob2="+(j+1)+"  p="+probaM+"  deltaG="+logProbaM);
                        
//                    }
                    //Ne faut-il pas récupéter directement le meilleur dès le calcul...
//                    if(i==17){
                        //testOut
                        double probaO = posteriorProbaOut(o);
                        double logProbaO = Math.log(1)-Math.log(probaO);
//                        IJ.log(""+t+" ob1="+(i+1)+" ob2="+(j+1)+" p="+probaM+"  pO="+probaO+"  deltaGO="+logProbaO);
//                    }
                    link.set(j, logProbaM);
//                    double value = link.set(j, logProba);
//                    IJ.log(""+value);
                }
                link1.set(i, link);
            }
            links.set(t, link1);
        }
        return links;
    }
    
    public List<ArrayList> scoring (){
        Population.setPopulations();
        nbObjWithTime = Population.getNbObjInTime();
        List<ArrayList> links = Population.emptyAllObjectsLinking();
        List<ArrayList> listObj=Population.allObjectsLocalization();
        int counter=0;
        for(int t=0; t<listObj.size()-1; t++){//time
            ArrayList<ArrayList> link1 = (ArrayList) links.get(t);
            for(int i=0; i<listObj.get(t).size(); i++){//objects in time0
                ArrayList<double[]> link = (ArrayList) link1.get(i);
                int findMotion=0;
                int objMotion=0;
                for(int j=0; j<listObj.get(t+1).size()+1; j++){//Test link with objects in time1
                    double[] res = new double [2];
                    double[] o = (double[]) listObj.get(t).get(i);//0
                    if(j<listObj.get(t+1).size()){
                        double[] o1 =(double[]) listObj.get(t+1).get(j);//1
                        //Counting
                        double probaC = posteriorCountCell(counter);
                        double logC = Math.log(probaC+1)-Math.log(probaC);
                        //Mobility
                        double probaM = posteriorProbaMobility(o, o1);
                        double logProbaM = Math.log(1.0)-Math.log(probaM);
                        res[0]=logProbaM;
                        res[1]=1.0;//=M
                        //IJ.log(""+t+" ob1="+(i+1)+" ob2="+(j+1)+" p="+probaM+"  logM="+logProbaM+" C="+probaC+" logC="+logC);
                        if(logProbaM>0 ){
                            if(findMotion==1 || findMotion==2 ){//there is another object in proximity in t+1
//                                //Should be a Mitosis, so test:
                                if(split(t, i, Arrays.copyOf(o, o.length-1), 0.7)){//test Distance Neighborhood
                                    res[1]=2.0;//=S
                                    //change previous entree
                                    double[] prev = link.get(objMotion);
                                    prev[1]=2.0;
                                    link.set(objMotion, prev);
                                    IJ.log("split "+t+" ob1="+(i+1)+" ob2="+(objMotion+1));
                                    IJ.log("split "+t+" ob1="+(i+1)+" ob2="+(j+1));
                                    findMotion=2;
                                }
                            }
                            else{
                                findMotion=1;
                                objMotion = j;
//                                IJ.log(""+t+" ob1="+(i+1)+" ob2="+(j+1)+" p="+probaM+"  logM="+logProbaM+" C="+probaC+" logC="+logC);
                            }
                        }
                        link.set(j, res);
                    }
                    if(j==listObj.get(t+1).size() && findMotion==0){//out or death case, none of the other object resolve the link
                        IJ.log(""+t+" ob1="+(i+1)+" "+j+" goes Out");
                        res[0]=0.1;//arbitrary value
                        res[1]=3.0;//=Out
                        link.set(j, res);
                    }
                }
                link1.set(i, link);
                if(t==0){
                    counter++;
                }
            }
//            IJ.log("nb="+listObj.get(t).size());
//            IJ.log("nblink1="+link1.size());
            links.set(t, link1);
        }
        return links;
    }
    
    public List<ArrayList> scoring2 (){//with merge
        Population.setPopulations();
        nbObjWithTime = Population.getNbObjInTime();
        List<ArrayList> links = Population.emptyAllObjectsLinking();
        List<ArrayList> listObj = Population.allObjectsLocalization();
        int counter=0;
        for(int t=0; t<listObj.size()-1; t++){//time
            ArrayList<ArrayList> link1 = (ArrayList) links.get(t);
            for(int i=0; i<listObj.get(t).size(); i++){//objects in time0
                ArrayList<double[]> link = (ArrayList) link1.get(i);
                int findMotion=0;
                int objMotion=0;
                double[] o = (double[]) listObj.get(t).get(i);//0
                int[] closestsT1 = closest(Arrays.copyOf(o, o.length-1), t+1);//obj closest in next t (t1)
                for(int j=0; j<listObj.get(t+1).size()+1; j++){//Test link with objects in time1
                    double[] res = new double [2];
                    if(j==closestsT1[0] || j==closestsT1[1] || j==closestsT1[2]){
                        double[] o1 =(double[]) listObj.get(t+1).get(j);//1
                        double minD = Population.minDistance(t, i);

                        double probaM = posteriorProbaMobility(o, o1);
                        if(probaM>0 ){
                            res[0]=probaM;
                            res[1]=1.0;//=M
                            if(findMotion==1 || findMotion==2 ){//there is another object in proximity in t+1
                                //Should be a Mitosis, so test:
                                if(split(t, i, Arrays.copyOf(o, o.length-1), priorDS)){//test Distance Neighborhood
                                    //here case if 2 next obj are closer than closest in same t
                                    res[1]=2.0;//=S
                                    //change previous entree
                                    double[] prev = link.get(objMotion);
                                    prev[1]=2.0;
                                    link.set(objMotion, prev);
//                                    IJ.log("split "+t+" ob1="+(i+1)+" ob2="+(objMotion+1));
//                                    IJ.log("split "+t+" ob1="+(i+1)+" ob2="+(j+1));
                                    findMotion=2;
                                }

                            }
                            else{   //motion
                                findMotion=1;
                                objMotion = j;
//                                IJ.log(""+t+" ob1="+(i+1)+" ob2="+(j+1)+" p="+probaM+"  pM="+probaM);
                            }

                            //test Merge
                            int prev = merge(t+1, j, Arrays.copyOf(o1, o1.length-1), 0.4);
                            if(prev!=-1 && prev!=i){
                                IJ.log("WWWW");
                                res[1]=3.0;//=Out
                            }
                        }
                        link.set(j, res);
                    }
                    //if j isn't close and detected, values=0
                    else if((j!=closestsT1[0] || j!=closestsT1[1] || j!=closestsT1[2]) && j<listObj.get(t+1).size()){
                        link.set(j, res);
                    }
                    if(j==listObj.get(t+1).size() && findMotion==0){//Out or death case, none of the other object resolve the link
                        res[0]=0.1;//arbitrary value
                        res[1]=3.0;//=Out
                        link.set(j, res);
                    }
                }
                link1.set(i, link);
                if(t==0){
                    counter++;
                }
            }
//            IJ.log("nb="+listObj.get(t).size());
//            IJ.log("nblink1="+link1.size());
            links.set(t, link1);
        }
        return links;
    }

    
    public List<ArrayList> processInputs(List<ArrayList> inputs)
        throws InterruptedException, ExecutionException {
        
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(threads);
        
//        List<ArrayList> linksOutput = Population.emptyAllObjectsLinking();
        List<Future<ArrayList>> futures = new ArrayList<>();//output
        for(final ArrayList input : inputs){//time
            Callable<ArrayList> callable = () -> {//output
                ArrayList output = new ArrayList();
                // process your input here and compute the output
                for (Object input1 : input) {
                    //objects in time0
                    ArrayList<double[]> out = new ArrayList<>();
                    int findMotion=0;
                    int objMotion=0;
                    double[] o = (double[]) input1;//0
                    int[] closestsT1 = closest(Arrays.copyOf(o, o.length-1), inputs.indexOf(input)+1);//obj closest in next t (t1)
                    for(int j=0; j<inputs.get(inputs.indexOf(input)+1).size()+1; j++){//Test link with objects in time1
                        double[] res = new double [2];
                        if(j==closestsT1[0] || j==closestsT1[1] || j==closestsT1[2]){
                            double[] o1 =(double[]) inputs.get(inputs.indexOf(input)+1).get(j);//1
                            double minD = Population.minDistance(inputs.indexOf(input), input.indexOf(input1));
                            double probaM = posteriorProbaMobility(o, o1);
                            if(probaM>0 ){
                                res[0]=probaM;
                                res[1]=1.0;//=M
                                if(findMotion==1 || findMotion==2 ){//there is another object in proximity in t+1
                                    //Should be a Mitosis, so test:
                                    if(split(inputs.indexOf(input), input.indexOf(input1), Arrays.copyOf(o, o.length-1), priorDS)){//test Distance Neighborhood
                                        //here case if 2 next obj are closer than closest in same t
                                        res[1]=2.0;//=S
                                        //change previous entree
                                        double[] prev = out.get(objMotion);
                                        prev[1]=2.0;
                                        out.set(objMotion, prev);
    //                                    IJ.log("split "+t+" ob1="+(i+1)+" ob2="+(objMotion+1));
    //                                    IJ.log("split "+t+" ob1="+(i+1)+" ob2="+(j+1));
                                        findMotion=2;
                                    }
                                }
                                else{   //motion
                                    findMotion=1;
                                    objMotion = j;
    //                                IJ.log(""+t+" ob1="+(i+1)+" ob2="+(j+1)+" p="+probaM+"  pM="+probaM);
                                }

                                //test Merge
                                int prev = merge(inputs.indexOf(input)+1, j, Arrays.copyOf(o1, o1.length-1), 0.4);
                                if(prev!=-1 && prev!=input.indexOf(input1)){
                                    IJ.log("WWWW");
                                    res[1]=3.0;//=Out
                                }
                            }
                            out.add(j, res);
                        }
                        //if j isn't close and detected, values=0
                        else if((j!=closestsT1[0] || j!=closestsT1[1] || j!=closestsT1[2]) && j<inputs.get(inputs.indexOf(input)+1).size()){
                            out.add(j, res);
                        }
                    }
                
               }
                
                return output;
            } //output
            ;
            futures.add(service.submit(callable));
        }
        service.shutdown();
        List<ArrayList> outputs = new ArrayList<>();
        for (Future<ArrayList> future : futures) {
            outputs.add(future.get());
        }
        return outputs;
        
    }
    
    public List<ArrayList> scoringThreads () throws InterruptedException, ExecutionException{//with merge
        Population.setPopulations();
        nbObjWithTime = Population.getNbObjInTime();
        List<ArrayList> listInput = Population.allObjectsLocalization();
        
//        for (int i=0; i<nbObjWithTime.length; i++){
//            
//        }
        
        
        
        
        List<ArrayList> linksOutput = processInputs(listInput);
        return linksOutput;
    }
    
    /**
     * 
     * @return 
     */
    public  List<ArrayList> bestScore1 (){
        List<ArrayList> linksMob = scoreMobility();
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
                IJ.log("bestS "+t+" : obj1="+(i)+"  obj2="+(old[0])+"  value="+old[1]);
            }
            bestLinks.set(t, bestLink1);
        }
        return bestLinks;
    }
    
    public  List<ArrayList> bestScore2 () throws InterruptedException, ExecutionException{
//        List<ArrayList> linksMob = scoring2();
        List<ArrayList> linksMob = scoringThreads();
        List<ArrayList> bestLinks= Population.emptyBestLinking();
        for(int t=0; t<nbObjWithTime.length-1; t++){//time
            ArrayList<double[]> bestLink1 = bestLinks.get(t);
            for(int i=0; i<nbObjWithTime[t]; i++){//objects in time0
                ArrayList<double[]> link = (ArrayList) linksMob.get(t).get(i);
                ArrayList <Integer> linkedNext = new ArrayList<>();
                double[] old = Arrays.copyOf(bestLink1.get(i), 4);//add one parameter
                for(int j=0; j<nbObjWithTime[t+1]+1; j++){//objects in time1
                    double[] l = new double [4];
                    l[0] = (double)j;//nb obj
                    l[1] = link.get(j)[0];//value
                    l[2] = link.get(j)[1];//=getState, 1=motion,2=split,3=out
                    if(l[2]==1 && l[1]>old[1]){
                        old[0]=l[0];
                        old[1]=l[1];
                        old[2]=l[2];
                    }
                    if(l[2]==2){//Split //here, only split in 2, not special case (3 or more)
                        //IJ.log("values: l0:"+l[0]+" l2:"+l[2]+" old0:"+old[0]);
                        old[3]=old[0];
                        old[0]=l[0]; 
                        old[1]=l[1];
                        old[2]=l[2];
                        
                        //IJ.log("bS "+t+" : obj1="+(i+1)+"  obj2="+(old[0]+1)+"  value="+old[1]+"  state="+old[2]+" "+old[3]);
                    }
                    if(l[2]==3){//out or dead
                        old[0]=l[0];
                        old[1]=l[1];
                        old[2]=l[2];
                    }
                    //merged due to segmentation
                    
                }
                bestLink1.set(i, old);//argMax and max
                IJ.log("bestS "+t+" : obj1="+(i+1)+"  obj2="+(old[0]+1)+"  value="+old[1]+"  state="+old[2]+" "+((int)old[3]+1));
            }
//            IJ.log("bestS "+t+" : obj1="+(i+1)+"  obj2="+(old[0]+1)+"  value="+old[1]+"  state="+old[2]+" "+((int)old[3]+1));
            bestLinks.set(t, bestLink1);
        }
        return bestLinks;
    }
    
    public void backTrack () throws InterruptedException, ExecutionException{
        List<ArrayList> bestScore = bestScore2();
        IJ.log("bestScoreSize="+bestScore.size());
        tree = new Tree();
        tree.setRoot(new Node("root"));
        
        ArrayList<ObjectDescriptor> arDesc = treeList;
        ArrayList<ObjectDescriptor> toDelete = new ArrayList<>();
        IJ.log("sizeBefore :" +arDesc.size());
        //Classify in the tree/forest
        int cellCounter = 0;
        while(arDesc.isEmpty()==false){
            ObjectDescriptor des = arDesc.get(0);
//            if(arDesc.size()==1 ){
//                IJ.log("...0: "+arDesc.get(0).getT()+" "+arDesc.get(0).getN());
//            }
            if(arDesc.size()>0){//if there are other object in list
                int t0 = des.getT();
                int n0 = des.getN();
                int previousT=t0;
                int previousN=n0;
                //scan time points
                for(int t=t0;t<bestScore.size();t++){
                    //scan all results in time t
                    for(int i=0; i<bestScore.get(t).size(); i++){
                        //retrieve position of the object t0-n0 in the results
                        if((t==previousT) && (i==previousN)){
                            //get Best link
                            double[] track = (double[]) bestScore.get(t).get(i);
                            //retrieve position of the best linked object in arDesc
                            int splitting=0;//try si split save change ardescJ, splitting++ and save change for the other 
                            
                            if(track[2]==2){
//                                for(int j=0; j<arDesc.size(); j++){
                                int j=0;
                                while(splitting!=2 && j<arDesc.size()){
                                    
                                    if(splitting==1){//take the second
                                        if(arDesc.get(j).getT()==(previousT+1) && arDesc.get(j).getN()==track[0] && arDesc.get(j)!=des){
                                            //point2
                                            arDesc.get(j).setPt(previousT);
                                            arDesc.get(j).setPn(previousN);
                                            previousN=(int)track[0];
                                            toDelete.add(arDesc.get(j));
//                                            IJ.log("split"+" obA="+previousN+"-> objB="+(int)track[0]+" t"+(t+1));
                                            previousT=previousT+1;
                                            splitting++;
                                            break;
                                        }
                                    }
                                    if(splitting==0){
                                        if(arDesc.get(j).getT()==(previousT+1) && arDesc.get(j).getN()==track[3] && arDesc.get(j)!=des){
                                            //point1
                                            arDesc.get(j).setPt(previousT);
                                            arDesc.get(j).setPn(previousN);
                                            toDelete.add(arDesc.get(j));
                                            splitting++;
//                                            IJ.log("split"+" obA="+previousN+"-> objB="+(int)track[3]+" t"+(t+1)+" 0");
                                            j=-1;
                                        }
                                    }
                                    j++;
                                }
                            }
                            else if (track[2]!=2){
                                for(int j=0; j<arDesc.size(); j++){
                                    if(arDesc.get(j).getT()==(previousT+1) && arDesc.get(j).getN()==track[0] && arDesc.get(j)!=des){

//                                        IJ.log("t"+previousT+" obA="+previousN+"-> objB="+(int)track[0]+" t"+(t+1));
                                        //add parent in objDescritor
                                        arDesc.get(j).setPt(previousT);
                                        arDesc.get(j).setPn(previousN);
                                        previousN=(int)track[0];
                                        toDelete.add(arDesc.get(j));
                                        previousT=previousT+1;
                                        break;
                                    }
                                }
                                
                            }
                        }
//                        IJ.log("obA="+n1+" t"+t0+" objB="+n1+" t"+t1+"  "+t);
                    }
                }
                //--------------------------------------------------------//
                //Add to tree
                Node a = new Node(des);
                tree.root.addChild(a);
                if(toDelete.isEmpty()==false){
                    
                    //les enfants doivent etre enfants d'enfants
                    // 0
                    ObjectDescriptor obj = toDelete.get(0);
                    
                    int index = arDesc.indexOf(obj);
                    ObjectDescriptor desChild = arDesc.get(index);
                    Node b = new Node(desChild);
                    a.addChildAt(0, b);
//                    a.addChild(b, cellCounter);
//                    cellCounter++;
                    if(toDelete.size()>1){
                        // + de 0
                        while(toDelete.size()!=1){
                            ObjectDescriptor obj2 = toDelete.get(1);
                            int index2 = arDesc.indexOf(obj2);
                            desChild = arDesc.get(index2);
                            Node c = new Node(desChild);
                            ObjectDescriptor oc = (ObjectDescriptor) c.getData();
//                            IJ.log("oc="+oc.getT()+" "+oc.getN()+" Poc="+oc.getPt()+" "+oc.getPn());
                            
                            if( oc.getPn()!=desChild.getN() && oc.getPt()!=desChild.getT()){
//                            if(oc.getPn()!=desChild.getN() && oc.getPt()!=desChild.getT()){
                                //searching the parent of multiple children
                                int pn = oc.getPn();
                                int pt = oc.getPt();
                                for(int g=1; g<a.getNumberOfChildren();g++){
                                    ObjectDescriptor og = (ObjectDescriptor) a.getChildren().get(g);
                                    if(og.getN()==pn && og.getT()==pt){
// -------------------------                   
                                        Node nd= tree.findNode(b, og);
//                                        Node nod = (Node) a.children.get(g);
                                        nd.addChildAt(1, c);
//                                        a.children.get(g).addChild(c, cellCounter);
//                                        cellCounter++;
                                        arDesc.remove(index2);
                                        toDelete.remove(1);
                                        b=c;
                                        break;
                                    }
                                }
                            }
//                            if(oc.getPn()==desChild.getN() && oc.getPt()==desChild.getT()){
//                            if( oc.getPn()==desChild.getN() && oc.getPt()==desChild.getT() ){
                                b.addChildAt(0, c);
//                                cellCounter++;
                                arDesc.remove(index2);
                                toDelete.remove(1);
                                b=c;
//                            }
//                            else if(oc.getPn()==0){
//                                    b.addChild(c, 0);
//                                    arDesc.remove(index2);
//                                    toDelete.remove(1);
//                                    b=c;
//                            }
                        }
                        
                        
                    }
                        arDesc.remove(index);
                        toDelete.remove(0);
                }
                arDesc.remove(des);
                cellCounter++;
            }
            else{
                Node a = new Node(des);
                tree.root.addChildAt(cellCounter, a);
                arDesc.remove(0);
                cellCounter++;
            }
            IJ.log("treeSizeIn "+tree.numberOfNodesInTree(tree.root));
        }
        
//        return tree;
    }
    
    public Tree getTree(){
        return tree;
    }
    
    private int[] closest (double[] coord, int t){
//        ArrayList<double[]> coords = Population.convertToArray(Population.getListObjectsTime(t));
        KdTreeC kd = Population.getKDtreeAtTime(t);
        kd.setScale2(1, 1,1);
//        kd.addMultiple(coords, 3);
        KdTreeC.KDPoint[] pt = kd.getNearestNeighbors(coord, 3);
        int[] objC = new int[3];
        for(int i=0; i<pt.length; i++){
//            ObjectDescriptor ob = (ObjectDescriptor) pt[0].obj;
            objC[i]=(int) pt[0].obj;
//            objC[i]=ob.getN();
        }
        return objC;
    }
    
    /**
     * If more than 2 objects in t+1 are closer than the closest in t, there should be a split
     * @param t time point of the object to analyse
     * @param nbObj number of the object in time t
     * @param coord of the object to analyse
     * @param percentageDistOK limite of the distance with its closest neighbor in its own time Point
     * @return 
     */
    private boolean split (int t, int nbObj, double[] coord, double percentageDistOK){
        boolean result = false;
//        ArrayList<ObjectDescriptor> list = Population.getListObjectsTime(t+1); //timePoint
//        ArrayList<double[]> coords = Population.convertToArray(list);
        double maxDistance = Population.minDistance(t, nbObj)*percentageDistOK;
//        KdTreeC kd1 = new KdTreeC(3, 64);
        KdTreeC kd = Population.getKDtreeAtTime(t+1);
        kd.setScale2(1, 1,1);
//        kd.addMultiple(coords, 3);
        KdTreeC.KDPoint[] pt = kd.getNearestNeighbors(coord, 2);
        //double euclidianDist = Math.sqrt((Math.pow(coord[0]-pt[1].pnt[0], 2) + Math.pow(coord[1]-pt[1].pnt[1], 2)) + Math.pow(coord[2]-pt[1].pnt[2], 2));
        //IJ.log("dist1="+maxDistance+" dist2="+kd.distance(coord, pt[1].pnt)+" eucl"+euclidianDist);
        if(kd.distance(coord, pt[1].pnt)<maxDistance){  //2nd closest
            result = true;
        }
        return result;
    }
    
    /**
     * If more than 2 objects in t-1 are closer than the closest in t, there should be a merge
*    * @param t time point of the object to analyse
     * @param nbObj number of the object in time t
     * @param coord of the object to analyse
     * @return -1 if no link, else the biggest
     */
    private int merge (int t, int nbObj, double[] coord, double percentageDistOK){
        int result = -1;
        ArrayList<ObjectDescriptor> list = Population.getListObjectsAtTime(t-1); //timePoint
//        ArrayList<double[]> coords = Population.convertToArray(list);
        double maxDistance = Population.minDistance(t, nbObj)*percentageDistOK;
        KdTreeC kd = Population.getKDtreeAtTime(t-1);
        kd.setScale2(1, 1,1);
//        kd.addMultiple(coords, 3);
        KdTreeC.KDPoint[] pt = kd.getNearestNeighbors(coord, 2);
        //double euclidianDist = Math.sqrt((Math.pow(coord[0]-pt[1].pnt[0], 2) + Math.pow(coord[1]-pt[1].pnt[1], 2)) + Math.pow(coord[2]-pt[1].pnt[2], 2));
        //IJ.log("dist1="+maxDistance+" dist2="+kd.distance(coord, pt[1].pnt)+" eucl"+euclidianDist);
        if(kd.distance(coord, pt[1].pnt)<maxDistance){  //2nd closest
            ObjectDescriptor a1 = list.get((int)pt[0].obj);
            ObjectDescriptor a2 = list.get((int)pt[1].obj);
            if(a1.getV()>a2.getV())
                return a1.getN();
            else
                return a2.getN();
        }
        return result;
    }
    
}
