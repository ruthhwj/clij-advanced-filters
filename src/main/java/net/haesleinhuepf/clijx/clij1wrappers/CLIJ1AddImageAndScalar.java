package net.haesleinhuepf.clijx.clij1wrappers;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.kernels.Kernels;
// this is generated code. See src/test/java/net/haesleinhuepf/clijx/codegenerator for details
public class CLIJ1AddImageAndScalar{
   
    /**
     * Adds a scalar value s to all pixels x of a given image X.
     * 
     * <pre>f(x, s) = x + s</pre>
     */
    public boolean addImageAndScalar(CLIJ clij, ClearCLImage source, ClearCLImage destination, double scalar) {
        return Kernels.addImageAndScalar(clij, source, destination, new Double (scalar).floatValue());
    }

}
