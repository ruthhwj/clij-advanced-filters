package net.haesleinhuepf.clijx.clij1wrappers;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.kernels.Kernels;
// this is generated code. See src/test/java/net/haesleinhuepf/clijx/codegenerator for details
public class CLIJ1CountNonZeroPixelsLocally{
   
    /**
     * 
     */
    public boolean countNonZeroPixelsLocally(CLIJ clij, ClearCLImage arg1, ClearCLImage arg2, double arg3, double arg4) {
        return Kernels.countNonZeroPixelsLocally(clij, arg1, arg2, new Double (arg3).intValue(), new Double (arg4).intValue());
    }

}
