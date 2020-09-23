package ch.nmbe.measure;

import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.io.Opener;
import io.scif.services.DatasetIOService;
import net.imagej.ImageJ;
import net.imagej.roi.ROIService;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UserInterface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;

@Plugin(type = Command.class, headless = true,
        menuPath = "Analyze>Batch Measure>Generate Measure Table")
public class GenerateTableCommand implements Command {

    @Parameter
    private DatasetIOService ioService;

    @Parameter
    private ROIService roiService;

    @Parameter(style = "directory", label = "Image Folder", description = "Select the folder that contains the images you want to measure")
    private File imageFolder;

    @Parameter(choices = {"µm", "mm", "cm"}, style = "listBox", label = "Unit", description = "Unit of measurement")
    private String unit;

    @Parameter(label = "Distance", description = "Real distance in the specified unit", min = "1")
    private int distance;

    @Parameter(label = "Pixel", description = "Number of pixels for the specified distance", min = "1")
    private int pixel;

    @Override
    public void run() {
        Map<String, Map<String, Double>> table = new TreeMap<>();
        List<String> columnNames = new ArrayList<>();

        File[] files = imageFolder.listFiles(MeasureService.tifFilter);
        for (File file : files) {
            String featureName = "";
            String specimen = "";

            Matcher matcher = MeasureService.fileNamePattern.matcher(file.getName());
            boolean isMatching = matcher.matches();
            if (isMatching) {
                specimen = matcher.group(1);
                featureName = matcher.group(3);
            }

            if (!table.containsKey(specimen)) {
                table.put(specimen, new HashMap<>());
            }
            Map<String, Double> row = table.get(specimen);

            Opener opener = new Opener();
            ImagePlus imagePlus = opener.openTiff(file.getParent(), file.getName());

            Overlay overlay = imagePlus.getOverlay();
            for (Roi roi : overlay) {
                if (roi instanceof Line) {
                    Line line = (Line) roi;
                    if (line.getRawLength() > 0) {
                        if (!columnNames.contains(featureName)) {
                            columnNames.add(featureName);
                        }
                        row.put(featureName, line.getRawLength() / this.pixel * this.distance);
                    }
                }
            }
        }

        columnNames.sort(String::compareTo);

        String dateString = new SimpleDateFormat("dd-MM-yyyy_HHmm").format(new Date());
        File outputFile = new File(imageFolder.getPath() + "/measurements_" + dateString + ".csv");

        try {
            FileWriter writer = new FileWriter(outputFile, false);

            StringBuilder header = new StringBuilder("Specimen");
            for (String title : columnNames) {
                header.append(",").append(title).append(" ").append(this.unit);
            }
            header.append("\n");
            writer.write(header.toString());

            for (Map.Entry<String, Map<String, Double>> entry : table.entrySet()) {
                StringBuilder line = new StringBuilder(entry.getKey());
                Map<String, Double> column = entry.getValue();
                for (String title : columnNames) {
                    line.append(",");
                    if (!column.containsKey(title)) {
                        continue;
                    }
                    line.append(column.get(title));
                }
                line.append("\n");
                writer.write(line.toString());
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Tests the command. */
    public static void main(final String... args) throws Exception {
        // Launch ImageJ for test run.
        final ImageJ ij = new ImageJ();
        ij.launch(args);
        ij.command().run(GenerateTableCommand.class, true);
    }
}
