package net.haesleinhuepf.clijx.advancedfilters;

import ij.ImagePlus;
import net.haesleinhuepf.clij.CLIJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.clearcl.ClearCLImage;
import net.haesleinhuepf.clij.clearcl.ClearCLKernel;
import net.haesleinhuepf.clij.clearcl.interfaces.ClearCLImageInterface;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.kernels.Kernels;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clijx.CLIJx;
import net.haesleinhuepf.clijx.utilities.AbstractCLIJxPlugin;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;
import org.scijava.plugin.Plugin;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;

import static net.haesleinhuepf.clij.utilities.CLIJUtilities.assertDifferent;

/**
 * ConnectedComponentsLabeling
 * <p>
 * <p>
 * <p>
 * Author: @haesleinhuepf
 * 06 2019
 */
@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_connectedComponentsLabeling")
public class ConnectedComponentsLabeling extends AbstractCLIJxPlugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation {

    @Override
    public boolean executeCL() {
        Object[] args = openCLBufferArgs();
        ClearCLBuffer input = (ClearCLBuffer) args[0];
        ClearCLBuffer output = (ClearCLBuffer) args[1];

        boolean result = connectedComponentsLabeling(getCLIJx(), input, output);
        releaseBuffers(args);
        return result;
    }

    public static boolean connectedComponentsLabeling(CLIJx clijx, ClearCLBuffer input, ClearCLBuffer output) {
        //ClearCLImage temp1 = clij.create(output.getDimensions(), CLIJUtilities.nativeToChannelType(output.getNativeType()));
        //ClearCLImage temp2 = clij.create(output.getDimensions(), CLIJUtilities.nativeToChannelType(output.getNativeType()));

        ClearCLBuffer temp1 = clijx.create(output.getDimensions());
        ClearCLBuffer temp2 = clijx.create(output.getDimensions());
        ClearCLBuffer temp3 = clijx.create(output.getDimensions());

        ClearCLBuffer flag = clijx.create(new long[]{1,1,1}, NativeTypeEnum.Byte);
        ByteBuffer aByteBufferWithAZero = ByteBuffer.allocate(1);
        aByteBufferWithAZero.put((byte)0);
        flag.readFrom(aByteBufferWithAZero, true);

        setNonZeroPixelsToPixelIndex(clijx.getClij(), input, temp1);

        clijx.set(temp2, 0f);


        final int[] iterationCount = {0};
        int flagValue = 1;

        ClearCLKernel flipkernel = null;
        ClearCLKernel flopkernel = null;

        while (flagValue > 0) {
            if (iterationCount[0] % 2 == 0) {
                NonzeroMinimumBox.nonzeroMinimumBox(clijx, temp1, flag, temp2, null).close();
                /*if (flipkernel == null) {
                    flipkernel = NonzeroMinimumDiamond.nonzeroMinimumDiamond(clijx, temp1, flag, temp2, flipkernel);
                } else {
                    flipkernel.run(true);
                }*/
            } else {
                NonzeroMinimumBox.nonzeroMinimumBox(clijx, temp2, flag, temp1, null).close();
                /*
                if (flopkernel == null) {
                    flopkernel = NonzeroMinimumDiamond.nonzeroMinimumDiamond(clijx, temp2, flag, temp1, flopkernel);
                } else {
                    flopkernel.run(true);
                }*/

            }

            ImagePlus flagImp = clijx.pull(flag);
            flagValue = flagImp.getProcessor().get(0,0);
            flag.readFrom(aByteBufferWithAZero, true);
            iterationCount[0] = iterationCount[0] + 1;
        }

        if (iterationCount[0] % 2 == 0) {
            copyInternal(clijx.getClij(), temp1, temp3, temp1.getDimension(), temp3.getDimension());
        } else {
            copyInternal(clijx.getClij(), temp2, temp3, temp2.getDimension(), temp3.getDimension());
        }
        if (flipkernel != null) {
            flipkernel.close();
        }
        if (flopkernel != null) {
            flopkernel.close();
        }


        shiftIntensitiesToCloseGaps(clijx, temp3, output);

        clijx.release(temp1);
        clijx.release(temp2);
        clijx.release(temp3);
        clijx.release(flag);

        return true;
    }

    public static boolean shiftIntensitiesToCloseGaps(CLIJx clijx, ClearCLImageInterface input, ClearCLImageInterface output) {
        int maximum = 0;
        if (input instanceof ClearCLImage) {
            maximum = (int) clijx.maximumOfAllPixels((ClearCLImage) input);
        } else {
            maximum = (int) clijx.maximumOfAllPixels((ClearCLBuffer) input);
        }
        final float[] allNewIndices = new float[maximum + 1];

        float[] count = {1};
        long number_of_pixels = (int) (input.getWidth() * input.getHeight() * input.getDepth());
        if (number_of_pixels < Integer.MAX_VALUE && input.getNativeType() == NativeTypeEnum.Float) {
            float[] slice = new float[(int) number_of_pixels];
            FloatBuffer buffer = FloatBuffer.wrap(slice);

            input.writeTo(buffer, true);
            for (int i = 0; i < slice.length; i++) {
                int key = (int) slice[i];
                if (key > 0 && allNewIndices[key] == 0) {
                    allNewIndices[key] = count[0];
                    count[0] = count[0] + 1;
                }
            }
        } else { // that's slower but more generic:
            RandomAccessibleInterval rai = clijx.convert(input, RandomAccessibleInterval.class);
            Cursor<RealType> cursor = Views.iterable(rai).cursor();

            while (cursor.hasNext()) {
                int key = new Float(cursor.next().getRealFloat()).intValue();
                if (key > 0 && allNewIndices[key] == 0) {
                    allNewIndices[key] = count[0];
                    count[0] = count[0] + 1;
                }
            }
        }

        ClearCLBuffer keyValueMap = clijx.create(new long[]{allNewIndices.length, 1, 1}, NativeTypeEnum.Float);
        keyValueMap.readFrom(FloatBuffer.wrap(allNewIndices), true);

        ReplaceIntensities.replaceIntensities(clijx, input, keyValueMap, output);

        keyValueMap.close();
        return true;
    }

    public static boolean setNonZeroPixelsToPixelIndex(CLIJ clij, ClearCLImageInterface src, ClearCLImageInterface dst) {
        HashMap<String, Object> parameters = new HashMap<>();

        parameters.clear();
        parameters.put("src", src);
        parameters.put("dst", dst);
        return clij.execute(ConnectedComponentsLabeling.class, "cca.cl", "set_nonzero_pixels_to_pixelindex", parameters);
    }
/*
    public static boolean nonzeroMinimumBox(CLIJ clij, ClearCLBuffer src, ClearCLBuffer flag, ClearCLBuffer dst, int radiusX, int radiusY, int radiusZ) {
        return executeSeparableKernel(clij, src, flag, dst, "cca.cl", "min_sep_image" + src.getDimension() + "d", radiusToKernelSize(radiusX), radiusToKernelSize(radiusY), radiusToKernelSize(radiusZ), radiusX, radiusY, radiusZ, src.getDimension());
    }

    private static boolean executeSeparableKernel(CLIJ clij, ClearCLBuffer src, ClearCLBuffer flag, ClearCLBuffer dst, String clFilename, String kernelname, int kernelSizeX, int kernelSizeY, int kernelSizeZ, float blurSigmaX, float blurSigmaY, float blurSigmaZ, long dimensions) {
        CLIJUtilities.assertDifferent(src, dst);

        int[] n = new int[]{kernelSizeX, kernelSizeY, kernelSizeZ};
        float[] blurSigma = new float[]{blurSigmaX, blurSigmaY, blurSigmaZ};

        ClearCLBuffer temp = clij.createCLBuffer((ClearCLBuffer) src);

        HashMap<String, Object> parameters = new HashMap<>();

        if (blurSigma[0] > 0) {
            parameters.clear();
            parameters.put("N", n[0]);
            parameters.put("s", blurSigma[0]);
            parameters.put("dim", 0);
            parameters.put("src", src);
            if (dimensions == 2) {
                parameters.put("dst", temp);
            } else {
                parameters.put("dst", dst);
            }
            parameters.put("flag_dst", flag);
            clij.execute(ConnectedComponentsLabeling.class, clFilename, kernelname, dst.getDimensions(), parameters);
        } else {
            if (dimensions == 2) {
                copyInternal(clij, src, temp, 2, 2);
            } else {
                copyInternal(clij, src, dst, 3, 3);
            }
        }

        if (blurSigma[1] > 0) {
            parameters.clear();
            parameters.put("N", n[1]);
            parameters.put("s", blurSigma[1]);
            parameters.put("dim", 1);
            if (dimensions == 2) {
                parameters.put("src", temp);
                parameters.put("dst", dst);
            } else {
                parameters.put("src", dst);
                parameters.put("dst", temp);
            }
            parameters.put("flag_dst", flag);
            clij.execute(ConnectedComponentsLabeling.class, clFilename, kernelname, dst.getDimensions(),parameters);
        } else {
            if (dimensions == 2) {
                copyInternal(clij, temp, dst, 2, 2);
            } else {
                copyInternal(clij, dst, temp, 3, 3);
            }
        }

        if (dimensions == 3) {
            if (blurSigma[2] > 0) {
                parameters.clear();
                parameters.put("N", n[2]);
                parameters.put("s", blurSigma[2]);
                parameters.put("dim", 2);
                parameters.put("src", temp);
                parameters.put("dst", dst);
                parameters.put("flag_dst", flag);
                clij.execute(ConnectedComponentsLabeling.class, clFilename, kernelname, dst.getDimensions(), parameters);
            } else {
                copyInternal(clij, temp, dst,3, 3);
            }
        }

        temp.close();

        return true;
    }*/

    static boolean copyInternal(CLIJ clij, Object src, Object dst, long srcNumberOfDimensions, long dstNumberOfDimensions) {
        assertDifferent(src, dst);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("src", src);
        parameters.put("dst", dst);
        if (!checkDimensions(srcNumberOfDimensions, dstNumberOfDimensions)) {
            throw new IllegalArgumentException("Error: number of dimensions don't match! (copy)");
        }
        clij.execute(Kernels.class, "duplication.cl", "copy_" + srcNumberOfDimensions + "d", parameters);
        return true;
    }


    private static boolean checkDimensions(long... numberOfDimensions) {
        for (int i = 0; i < numberOfDimensions.length - 1; i++) {
            if (!(numberOfDimensions[i] == numberOfDimensions[i + 1])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getParameterHelpText() {
        return "Image binary_input, Image labeling_destination";
    }

    @Override
    public String getDescription() {
        return "Performs connected components analysis to a binary image and generates a label map.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input) {
        return clij.create(input.getDimensions(), NativeTypeEnum.Float);
    }
}
