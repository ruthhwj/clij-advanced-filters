// CLIJ example macro: projections.ijm
//
// This macro shows how median and standard devation 
// projections can be done in the GPU.
//
// Author: Robert Haase
// June 2019
// ---------------------------------------------

run ("Close All");

// Get test data
//open("C:/structure/data/t1-head.tif");
run("T1 Head (2.4M, 16-bits)");
input = getTitle();

median_projected = "median_projected";
stddev_projected = "stddev_projected";

// Init GPU
run("CLIJ Macro Extensions", "cl_device=");
Ext.CLIJ_clear();

// push images to GPU
Ext.CLIJ_push(input);

// CleanUp ImageJ
close();

// median projection
Ext.CLIJx_medianZProjection(input, median_projected);

// standard deviation projection
Ext.CLIJx_standardDeviationZProjection(input, stddev_projected);

// Get results back from GPU
Ext.CLIJ_pull(median_projected);
Ext.CLIJ_pull(stddev_projected);


// Cleanup by the end
Ext.CLIJ_clear();
