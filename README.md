# Morphometrix Genie

## Build

### Perquisites
The following software must be installed on the target system to
build the software:
* JDK Version 1.8
* Maven

Run `mvn clean install`, this creates a target directory
containing the class and jar files.

## Installation
Navigate to the current release on github and download the Package.zip file.
Copy this file to your imagej-directory/plugins folder and extract it there. Now restart ImageJ.
If you use a GNU/Linux distribution make sure you use the portable version of
ImageJ. The packaged version cases problems when installing additional plugins.

## Commands
In your ImageJ, there should now be some new menu
entries at Analyze->Measure.

### Start Measure
This command starts the measuring process. You select a folder
to process, and the plugin will open each .tiff file in that folder.
The files will be sorted by the feature name and shuffled by the specimen name.
The following regular expression is used to find feature and specimen
in the filename: "([a-z0-9]*)_(\d{3})x_(\d*)\.tif"
You can now use the line tool to set a measurement in the picture.
After you are happy with your work you can close the image window.
The plugin will automatically open the next one.
In the end the "Generate Measure Table" command will be run automatically.

### End Measure
Should you want to stop the process you can run the End Measure command.
After that no new images will be opened.

### Generate Measure Table
This command goes over all images in the specified folder and generates
a table witch all features and individuals.

## Extended Line Tool
In the toolbar there should be a new tool now. The icon is a simple
circle. This tool has the same effect as the normal line tool,
but it also displays a perpendicular helper line. 