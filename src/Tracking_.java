
import detection.ObjectDescriptor;
import detection.Settings;
import functions.ScoringFunction;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import structure.Node;
import structure.Tree;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jean-Francois Gilles
 */
public class Tracking_ implements PlugIn {
    ImagePlus imp=null;
    protected Settings settings;
    
    @Override
    public void run(String imagePath) {
        try {
            imp=WindowManager.getCurrentImage();
            settings = createSettings(imp);
            
//        Population.returnPopulations();
//        List<ArrayList> listObj=Population.allObjectsLocalization();
//        IJ.log("listObj-Size= "+listObj.size());
//        List<ArrayList> logProbas = ScoringFunction.computeMobility(20, 0.6, settings.width*settings.height*settings.nslices);
//        List<ArrayList> logProbas2 = ScoringFunction.bestScore(20, 0.6, settings.width*settings.height*settings.nslices);
//        List<ArrayList> logProbas3 = ScoringFunction.bestScoreM(20, 0.6, settings.width*settings.height*settings.nslices);

            ScoringFunction score = new ScoringFunction();
            score.setImagesParameters(settings.width, settings.height, settings.nslices);
            score.setCountingParameters(0.2, 0.8, 1);
            score.setMotionParameters(30, 0.5);
            score.setSplitParameters(0.6);

            
            score.backTrack();
            Tree forest = score.getTree();
            IJ.log("end ");
            IJ.log("end "+forest.root.getNumberOfChildren());
//        IJ.log("1: "+forest.root.children.size());
//        IJ.log("2: "+forest.root.children.get(0).children.size());
//        IJ.log("3: "+forest.root.children.get(0).children.get(0).children.size());
//        detection.ObjectDescriptor ob =(ObjectDescriptor)forest.root.children.get(0).children.get(0).children.get(0).info;
//        IJ.log(""+ob.getT()+" "+ob.getN());
        
//        for(int i=1; i<55; i++){
//            
//            detection.ObjectDescriptor ob =(ObjectDescriptor) forest.root.children.get(i).info;
//            IJ.log("test");
//            IJ.log("t"+ob.getT()+" n"+ob.getN()+" parent t"+ob.getPt()+" n"+ob.getPn());
//        }
//        IJ.log("number of nodes"+forest.numberOfNodesInTree());

//        Tree tree=new Tree();
//        Node a = new Node("a");
//        Node b = new Node("b");
//        Node c = new Node("c");
//        Node d = new Node("d");
//        Node e = new Node("e");
//        Node f = new Node("f");
//        tree.setRoot(new Node("root"));
//        tree.root.addChildAt(0,a);
//        tree.root.addChildAt(1,c);
//        tree.root.addChildAt(2, f);
//        a.addChildAt(0,b);
//        b.addChildAt(0, b);
//        b.addChildAt(1, e);
////        IJ.log(""+tree.numberOfNodesInTree(tree.root));
////        IJ.log("node size "+tree.root.getChildren().size()+" size="+tree.root.getNumberOfChildren());
////        IJ.log("node nbchildren "+tree.root.getNumberOfChildren());
//        IJ.log("node nbchildrenV2 a "+tree.getNumberOfDescendants(a));
//        IJ.log("in "+tree.find(a, "b"));
        } catch (InterruptedException | ExecutionException ex) {
            IJ.error("Problem", "Problem when computing distances");
            Logger.getLogger(Tracking_.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }
    
    protected Settings createSettings(ImagePlus imp){
        Settings settings = new Settings();
        settings.setFrom(imp);
        return settings;
    }
    
    
    
}
