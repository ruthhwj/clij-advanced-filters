package net.haesleinhuepf.clijx.clij1wrappers;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.kernels.Kernels;
// this is generated code. See src/test/java/net/haesleinhuepf/clijx/codegenerator for details
public class CLIJ1DilateSphereSliceBySlice{
   
    /**
     * Computes a binary image with pixel values 0 and 1 containing the binary dilation of a given input image.
     * The dilation takes the von-Neumann-neighborhood (4 pixels in 2D and 6 pixels in 3d) into account.
     * The pixels in the input image with pixel value not equal to 0 will be interpreted as 1.
     * 
     * This filter is applied slice by slice in 2D.
     */
    public boolean dilateSphereSliceBySlice(CLIJ clij, ClearCLBuffer source, ClearCLBuffer destination) {
        return Kernels.dilateSphereSliceBySlice(clij, source, destination);
    }

}
