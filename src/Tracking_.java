
import detection.Settings;
import functions.ScoringFunction;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;
import java.util.ArrayList;
import java.util.List;
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
        imp=WindowManager.getCurrentImage();
        
        settings = createSettings(imp);
        
//        Population.returnPopulations();
//        List<ArrayList> listObj=Population.allObjectsLocalization();
//        IJ.log("listObj-Size= "+listObj.size());
//        List<ArrayList> logProbas = ScoringFunction.computeMobility(20, 0.6, settings.width*settings.height*settings.nslices);
//        List<ArrayList> logProbas2 = ScoringFunction.bestScore(20, 0.6, settings.width*settings.height*settings.nslices);
//        List<ArrayList> logProbas3 = ScoringFunction.bestScoreM(20, 0.6, settings.width*settings.height*settings.nslices);
        
        Tree forest = ScoringFunction.backTrack(20, 0.6, settings.width*settings.height*settings.nslices);
        IJ.log("number of nodes"+forest.numberOfNodesInTree());
//        Tree tree=new Tree(3);
//        Node a = new Node("a");
//        Node b = new Node("b");
//        Node c = new Node("c");
//        tree.addRoot("root");
//        
//        tree.root.addChild(a,0);
//        a.addChild(b,0);
//        tree.root.addChild(c,1);
//        IJ.log(""+tree.numberOfNodesInTree(tree.root));

//        StateSpace st = new StateSpace();
//        st.DrawSpaceDiagram(list);
        
//        score.PosteriorProba(0, 0, 0, 0, 0)


    }
    
    protected Settings createSettings(ImagePlus imp){
        Settings settings = new Settings();
        settings.setFrom(imp);
        return settings;
    }
    
    
    
}
