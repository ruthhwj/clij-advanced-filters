package net.haesleinhuepf.clijx.clij1wrappers;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.kernels.Kernels;
// this is generated code. See src/test/java/net/haesleinhuepf/clijx/codegenerator for details
public class CLIJ1SubtractImages{
   
    /**
     * Subtracts one image X from another image Y pixel wise.
     * 
     * <pre>f(x, y) = x - y</pre>
     */
    public boolean subtractImages(CLIJ clij, ClearCLImage subtrahend, ClearCLImage minuend, ClearCLImage destination) {
        return Kernels.subtractImages(clij, subtrahend, minuend, destination);
    }

}
