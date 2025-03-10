package net.haesleinhuepf.clijx.clij1wrappers;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.kernels.Kernels;
// this is generated code. See src/test/java/net/haesleinhuepf/clijx/codegenerator for details
public class CLIJ1MinimumSphere{
   
    /**
     * Computes the local minimum of a pixels rectangular neighborhood. The rectangles size is specified by 
     * its half-width and half-height (radius).
     */
    public boolean minimumSphere(CLIJ clij, ClearCLImage source, ClearCLImage destination, double radiusX, double radiusY) {
        return Kernels.minimumSphere(clij, source, destination, new Double (radiusX).intValue(), new Double (radiusY).intValue());
    }

}
