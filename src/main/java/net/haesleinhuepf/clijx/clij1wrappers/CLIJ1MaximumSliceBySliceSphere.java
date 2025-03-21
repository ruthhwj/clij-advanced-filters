package net.haesleinhuepf.clijx.clij1wrappers;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.kernels.Kernels;
// this is generated code. See src/test/java/net/haesleinhuepf/clijx/codegenerator for details
public class CLIJ1MaximumSliceBySliceSphere{
   
    /**
     * Computes the local maximum of a pixels ellipsoidal 2D neighborhood in an image stack 
     * slice by slice. The ellipses size is specified by its half-width and half-height (radius).
     * 
     * This filter is applied slice by slice in 2D.
     */
    public boolean maximumSliceBySliceSphere(CLIJ clij, ClearCLBuffer source, ClearCLBuffer destination, double radiusX, double radiusY) {
        return Kernels.maximumSliceBySliceSphere(clij, source, destination, new Double (radiusX).intValue(), new Double (radiusY).intValue());
    }

}
