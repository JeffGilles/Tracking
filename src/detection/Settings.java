package detection;


import ij.ImagePlus;
import ij.measure.Calibration;

/**
 *
 * @author Jean-Francois Gilles
 */
public class Settings {
    
    public ImagePlus imp;
    public int width;
    public int height;
    public int nslices;
    public int nframes;
    public double dx = 1.0D;  
    public double dy = 1.0D;
    public double dz = 1.0D;
    public double dt = 1.0D;
    public String units = null;
    public String timeUnit= null;
    
    
    /**
     * return properties from the image
     * @param imp 
     */
    public void setFrom(ImagePlus imp) {
        this.imp = imp;
        if (imp == null) { return;}

        width = imp.getWidth();
        height = imp.getHeight();
        nslices = imp.getNSlices();
        nframes = imp.getNFrames();
        Calibration cali = imp.getCalibration();
        dx = cali.pixelWidth;
        dy = cali.pixelHeight;
        dz = cali.pixelDepth;
        dt = cali.frameInterval;
        units = cali.getUnits();
        timeUnit = cali.getTimeUnit();

        if (dt == 0.0D) {
          dt = 1.0D;
        }

      }
}
