package net.haesleinhuepf.clijx.matrix;


import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.AbstractCLIJPlugin;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import org.scijava.plugin.Plugin;

import java.util.HashMap;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_transposeYZ")
public class TransposeYZ extends AbstractCLIJPlugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation {

    @Override
    public String getParameterHelpText() {
        return "Image input, Image destination";
    }

    @Override
    public boolean executeCL() {

        Object[] args = openCLBufferArgs();
        boolean result = transposeYZ(clij, (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]));
        releaseBuffers(args);
        return result;
    }

    public static boolean transposeYZ(CLIJ clij, ClearCLBuffer input, ClearCLBuffer output) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("src", input);
        parameters.put("dst", output);
        return clij.execute(TransposeYZ.class, "transpose.cl", "transpose_yz", parameters);
    }

    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input) {
        return clij.create(new long[]{input.getWidth(), input.getDepth(), input.getHeight()}, input.getNativeType());
    }

    @Override
    public String getDescription() {
        return "Transpose Y and Z in an image.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "3D";
    }
}
