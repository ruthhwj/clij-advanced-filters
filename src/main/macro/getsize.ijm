// CLIJ example macro: getsize.ijm
//
// This macro shows how to get the dimensions 
// of an image on the GPU
//
// Author: Robert Haase
//         September 2019
// ---------------------------------------------


// Get test data
run("Blobs (25K)");
input = getTitle();

// Init GPU
run("CLIJ Macro Extensions", "cl_device=");
Ext.CLIJ_clear();

// push data to GPU
Ext.CLIJ_push(input);

Ext.CLIJx_getSize(input);
width = getResult("Width", nResults() - 1);
height = getResult("Height", nResults() - 1);

IJ.log("Width: " + width);
IJ.log("Height: " + height);
