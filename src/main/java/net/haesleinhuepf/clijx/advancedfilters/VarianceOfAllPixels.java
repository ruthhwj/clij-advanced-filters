package net.haesleinhuepf.clijx.advancedfilters;

import ij.measure.ResultsTable;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.AbstractCLIJPlugin;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import org.scijava.plugin.Plugin;

import java.util.HashMap;

/**
 * Author: @haesleinhuepf
 *         September 2019
 */
@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_varianceOfAllPixels")
public class VarianceOfAllPixels extends AbstractCLIJPlugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation {

    @Override
    public boolean executeCL() {
        double variance = 0;

        Object[] args = openCLBufferArgs();
        ClearCLBuffer buffer1 = (ClearCLBuffer)( args[0]);

        variance = varianceOfAllPixels(clij, buffer1);
        releaseBuffers(args);


        ResultsTable table = ResultsTable.getResultsTable();
        table.incrementCounter();
        table.addValue("Variance", variance);
        table.show("Results");
        return true;
    }

    public static double varianceOfAllPixels(CLIJ clij, ClearCLBuffer buffer1) {
        double meanIntensity = clij.op().sumPixels(buffer1) / (buffer1.getWidth() * buffer1.getHeight() * buffer1.getDepth());
        return varianceOfAllPixels(clij, buffer1, new Float(meanIntensity));
    }

    public static double varianceOfAllPixels(CLIJ clij, ClearCLBuffer buffer1, Float meanIntensity) {
        ClearCLBuffer clReducedImage = buffer1;
        float sum = 0;
        if (buffer1.getDimension() == 3) {
            clReducedImage = clij.createCLBuffer(new long[]{buffer1.getWidth(), buffer1.getHeight()}, NativeTypeEnum.Float);

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("src", buffer1);
            parameters.put("dst", clReducedImage);
            parameters.put("mean_intensity", meanIntensity);
            clij.execute(VarianceOfMaskedPixels.class, "varianceProject.cl", "squared_sum_project_3d_2d", parameters);


            RandomAccessibleInterval rai = clij.convert(clReducedImage, RandomAccessibleInterval.class);
            Cursor cursor = Views.iterable(rai).cursor();
            while (cursor.hasNext()) {
                sum += ((RealType) cursor.next()).getRealFloat();
            }

            clReducedImage.close();
        } else {
            RandomAccessibleInterval rai = clij.convert(clReducedImage, RandomAccessibleInterval.class);
            Cursor cursor = Views.iterable(rai).cursor();
            while (cursor.hasNext()) {
                sum += Math.pow(((RealType) cursor.next()).getRealFloat() - meanIntensity, 2);
            }
        }
        return sum / (buffer1.getWidth() * buffer1.getHeight() * buffer1.getDepth());
    }

    @Override
    public String getParameterHelpText() {
        return "Image source";
    }

    @Override
    public String getDescription() {
        return "Determines the variance of all pixels in an image. The value will be stored in a new row of ImageJs\n" +
                "Results table in the column 'Variance'.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

}
