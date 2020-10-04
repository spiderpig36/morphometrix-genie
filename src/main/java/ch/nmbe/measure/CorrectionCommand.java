package ch.nmbe.measure;

import net.imagej.ImageJ;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.Arrays;

@Plugin(type = Command.class, headless = true,
        menuPath = "Analyze>Batch Measure>Correction")
public class CorrectionCommand implements Command {
    @Parameter(style = "files,extensions:tiff",  label = "Files to correct", description = "Select all files you want to open again")
    private File[] filesToCorrect;

    @Parameter
    private MeasureService measureService;

    @Override
    public void run() {
        measureService.setFiles(Arrays.asList(filesToCorrect));
        measureService.startMeasureBatch();
        measureService.nextImage();
    }

    /** Tests the command. */
    public static void main(final String... args) throws Exception {
        // Launch ImageJ for test run.
        final ImageJ ij = new ImageJ();
        ij.launch(args);
        ij.command().run(CorrectionCommand.class, true);
    }
}
