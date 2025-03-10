package net.haesleinhuepf.clijx.advancedfilters;

import ij.measure.ResultsTable;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.AbstractCLIJPlugin;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import org.scijava.plugin.Plugin;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;

/**
 * Author: @haesleinhuepf
 *         September 2019
 */
@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_resultsTableToImage2D")
public class ResultsTableToImage2D extends AbstractCLIJPlugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation {

    @Override
    public boolean executeCL() {

        Object[] args = openCLBufferArgs();

        ClearCLBuffer buffer = (ClearCLBuffer)( args[0]);

        ResultsTable table = ResultsTable.getResultsTable();
        resultsTableToImage2D(clij, buffer, table);
        // table.show("Results");
        return true;
    }

    public static boolean resultsTableToImage2D(CLIJ clij, ClearCLBuffer buffer, ResultsTable table) {
        if (buffer.getNativeType() == NativeTypeEnum.UnsignedByte) {
            byte[] array = new byte[(int) (buffer.getWidth() * buffer.getHeight())];

            int rows = table.getCounter();
            int cols = table.getHeadings().length;
            int c = 0;
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++ ) {
                    array[c] = (byte) table.getValueAsDouble(x, y);
                    c++;
                }
            }
            ByteBuffer arrayBuffer = ByteBuffer.wrap(array);
            buffer.readFrom(arrayBuffer, true);
        } else if (buffer.getNativeType() == NativeTypeEnum.UnsignedShort) {
            char[] array = new char[(int) (buffer.getWidth() * buffer.getHeight())];

            int rows = table.getCounter();
            int cols = table.getHeadings().length;
            int c = 0;
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++ ) {
                    array[c] = (char) table.getValueAsDouble(x, y);
                    c++;
                }
            }
            CharBuffer arrayBuffer = CharBuffer.wrap(array);
            buffer.readFrom(arrayBuffer, true);
        } else {
            float[] array = new float[(int) (buffer.getWidth() * buffer.getHeight())];

            int rows = table.getCounter();
            int cols = table.getHeadings().length;
            int c = 0;
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++ ) {
                    array[c] = (byte) table.getValueAsDouble(x, y);
                    c++;
                }
            }
            FloatBuffer arrayBuffer = FloatBuffer.wrap(array);
            buffer.readFrom(arrayBuffer, true);

        }
        return true;
    }

    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input) {
        ResultsTable table = ResultsTable.getResultsTable();
        return clij.create(new long[]{table.getHeadings().length, table.getCounter()}, NativeTypeEnum.Float);
    }

    @Override
    public String getParameterHelpText() {
        return "Image destination";
    }

    @Override
    public String getDescription() {
        return "Converts a table to an image.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D";
    }

}
