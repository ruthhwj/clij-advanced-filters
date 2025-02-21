package net.haesleinhuepf.clijx.clij1wrappers;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.kernels.Kernels;
// this is generated code. See src/test/java/net/haesleinhuepf/clijx/codegenerator for details
public class CLIJ1MinimumZProjection{
   
    /**
     * Determines the minimum projection of an image along Z.
     */
    public boolean minimumZProjection(CLIJ clij, ClearCLImage source, ClearCLImage destination_sum) {
        return Kernels.minimumZProjection(clij, source, destination_sum);
    }

}
