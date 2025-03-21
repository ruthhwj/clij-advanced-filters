package net.haesleinhuepf.clijx.painting;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.utilities.AbstractCLIJxPlugin;
import org.scijava.plugin.Plugin;

import java.util.HashMap;

/**
 * DrawLine
 * <p>
 * <p>
 * <p>
 * Author: @haesleinhuepf
 * 08 2019
 */
@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_drawTwoValueLine")
public class DrawTwoValueLine extends AbstractCLIJxPlugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation {

    @Override
    public String getParameterHelpText() {
        return "Image destination, Number x1, Number y1, Number z1, Number x2, Number y2, Number z2, Number thickness, Number value1, Number value2";
    }

    @Override
    public boolean executeCL() {
        ClearCLBuffer input = (ClearCLBuffer)args[0];
        Float x1 = asFloat(args[1]);
        Float y1 = asFloat(args[2]);
        Float z1 = asFloat(args[3]);
        Float x2 = asFloat(args[4]);
        Float y2 = asFloat(args[5]);
        Float z2 = asFloat(args[6]);
        Float thickness = asFloat(args[7]);
        Float value1 = asFloat(args[8]);
        Float value2 = asFloat(args[9]);

        return drawTwoValueLine(getCLIJx(), input, x1, y1, z1, x2, y2, z2, thickness, value1, value2);
    }

    public static boolean drawTwoValueLine(CLIJx clijx, ClearCLBuffer output, Float x1, Float y1, Float z1, Float x2, Float y2, Float z2, Float thickness, Float value1, Float value2) {

        long[] globalSizes;

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("value1", value1);
        parameters.put("value2", value2);

        parameters.put("x1", x1);
        parameters.put("y1", y1);
        parameters.put("x2", x2);
        parameters.put("y2", y2);
        if (output.getDimension() > 2) {
            parameters.put("z1", z1);
            parameters.put("z2", z2);
            globalSizes = new long[]{
                    (long)(Math.abs(x1 - x2) + 1 + thickness),
                    (long)(Math.abs(y1 - y2) + 1 + thickness),
                    (long)(Math.abs(z1 - z2) + 1 + thickness)
            };
        } else {
            globalSizes = new long[]{
                    (long)(Math.abs(x1 - x2) + 1 + thickness),
                    (long)(Math.abs(y1 - y2) + 1 + thickness)
            };
        }
        parameters.put("radius", new Float(thickness / 2));
        parameters.put("dst", output);


        //System.out.println("Draw line from " + x1 + "/" + y1 + "/" + z1 + " to "  + x2 + "/" + y2 + "/" + z2);
        clijx.execute(DrawTwoValueLine.class, "draw_two_value_line_" +output.getDimension() + "d_x.cl", "draw_two_value_line_" +output.getDimension() + "D", globalSizes, globalSizes, parameters);
        return true;
    }

    @Override
    public String getDescription() {
        return "Draws a line between two points with a given thickness. Pixels close to point 1 are set to value1. Pixels closer to point 2 are set to value2 All pixels other than on the line are untouched. Consider using clij.set(buffer, 0); in advance.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }
}
