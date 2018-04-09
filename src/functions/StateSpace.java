package functions;


import mcib3d.geom.Objects3DPopulation;

/**
 *
 * @author Jean-Francois Gilles
 */

public class StateSpace {
    
    int [][][][] stateSpace;
    
    public int [][][][] DrawSpaceDiagram(Objects3DPopulation[] allPop){
//        double[][][][] stateSpace=new double[allPop.length][allPop[allPop.length-1].getNbObjects()][][];
        stateSpace=null;
        //create empty spaceDiagram with 
        for(int t=0; t<allPop.length-1;t++){
            for(int i=0; i<allPop[t].getNbObjects(); i++){
                for(int j=1; j<allPop[t+1].getNbObjects(); j++){
                    stateSpace[t][i][j][0] = 0;//last table = object can 1:come later in the field, 2:leave the field or die
                }
            }
        }
        return stateSpace;
    }
        
}
