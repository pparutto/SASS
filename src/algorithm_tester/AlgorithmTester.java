/*
 * Copyright (C) 2017 Laboratory of Experimental Biophysics
 * Ecole Polytechnique Federale de Lausanne
 *
 * Author: Marcel Stefko
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package algorithm_tester;

import algorithm_tester.analyzers.autolase.AutoLase;
import algorithm_tester.analyzers.quickpalm.QuickPalm;
import algorithm_tester.analyzers.spotcounter.SpotCounter;
import algorithm_tester.controllers.pid.PIDController;
import algorithm_tester.controllers.simple.SimpleController;
import algorithm_tester.generators.realtime.STORMsim;
import algorithm_tester.generators.tiff.TiffGenerator;
import com.sun.media.sound.RealTimeSequencerProvider;
import ij.process.ImageProcessor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author stefko
 */
public class AlgorithmTester {
    ArrayList<EvaluationAlgorithm> analyzers;
    ImageGenerator generator;
    FeedbackController controller;
    int image_count;
    
    /**
     * Main function which executes the testing procedure.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AlgorithmTester tester = new AlgorithmTester();
        tester.execute();
    }
    
    /**
     * Define the testing procedure in this method.
     */
    public void execute() {
        
        
        JFileChooser fc = new JFileChooser();
        int returnVal;
        
        //*
        // File chooser dialog for saving output csv
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        //set a default filename 
        fc.setSelectedFile(new File("tester_output.csv"));
        //Set an extension filter
        fc.setFileFilter(new FileNameExtensionFilter("CSV file","csv"));
        returnVal = fc.showSaveDialog(null);
        if  (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File csv_output = fc.getSelectedFile();
        
        /*/
        File tiff_file = new File("C:\\Users\\stefko\\Desktop\\sim400orig.tif");
        File csv_output = new File("C:\\Users\\stefko\\Desktop\\output.csv");
        //*/
        
        analyzers = new ArrayList<EvaluationAlgorithm>();
        
        // Instantiate all analyzers and add them to the list.
        AutoLase autolase = new AutoLase();
        HashMap<String, Integer> autolase_params = new HashMap<String, Integer>();
        autolase_params.put("threshold", 70);
        autolase_params.put("averaging", 30);
        autolase.setCustomParameters(autolase_params);
        
        SpotCounter spotcounter = new SpotCounter();
        HashMap<String, Integer> spotcounter_params = new HashMap<String, Integer>();
        spotcounter_params.put("noise-tolerance", 90);
        spotcounter_params.put("box-size", 5);
        spotcounter.setCustomParameters(spotcounter_params);
        
        QuickPalm quickpalm = new QuickPalm();
        
        
        addAnalyzer(autolase);
        addAnalyzer(spotcounter);
        addAnalyzer(quickpalm);
        /* Tiff generator
        // Analyze all images from the generator. End of analysis is marked
        // by a null pointer.
        fc.setFileFilter(new FileNameExtensionFilter("TIF file","tif"));
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        returnVal = fc.showOpenDialog(null);
        if  (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File tiff_file = fc.getSelectedFile();
        
        
        generator = new TiffGenerator(tiff_file);
        ImageProcessor ip = generator.getNextImage();
        image_count = 0;
        while (ip != null) {
            image_count++;
            for (EvaluationAlgorithm analyzer: analyzers)
                analyzer.processImage(ip);
            ip = generator.getNextImage();
        }
        
        /*/
        // Real time generator
        generator = new STORMsim();
        
        // Set up controller
        //controller = new SimpleController();
        controller = new PIDController();
        controller.setTarget(120.0);
        controller.setAnalyzer(spotcounter);
        controller.setGenerator(generator);
        
        
        
        
        
        ImageProcessor ip;
        for (image_count = 0; image_count < 1000; image_count++) {
            ip = generator.getNextImage();
            for (EvaluationAlgorithm analyzer: analyzers)
                analyzer.processImage(ip.duplicate());
            //System.out.println(image_count);
            controller.adjust();
        }
        
        fc.setFileFilter(new FileNameExtensionFilter("TIF file","tif"));
        fc.setSelectedFile(new File("gen_stack.tif"));
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        returnVal = fc.showSaveDialog(null);
        if  (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }
        generator.saveStack(fc.getSelectedFile());
        
        
        //*/
        
        // Save analysis results to a csv file.
        saveToCsv(csv_output);
        System.exit(0);
    }
    
    /**
     * Adds the analyzer to list of all analyzers to be evaluated.
     * @param analyzer initialized and configured EvaluationAlgorithm
     */
    public void addAnalyzer(EvaluationAlgorithm analyzer) {
        analyzers.add(analyzer);
    }
    
    /**
     * Saves the data generated by analyzers for each frame into a .csv file
     * @param file destination csv file
     */
    private void saveToCsv(File file) {
        // Open the file for writing
        PrintWriter writer;
        try {
            writer = new PrintWriter(file.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AlgorithmTester.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        
        HashMap<String, Integer> parameter_map;
        // Print header: analyzer settings
        writer.println("#Analyzer settings:");
        String parameters = "";
        for (EvaluationAlgorithm analyzer: analyzers) {
            parameter_map = analyzer.getCustomParameters();
            for (String key: parameter_map.keySet()) {
                parameters = parameters.
                    concat(analyzer.getName()).concat(".").concat(key).
                    concat(":").concat(parameter_map.get(key).toString()).
                    concat(",");
            }
        }
        writer.println(parameters);
        
        HashMap<String,Double> setting_map;
        writer.println("#Controller settings:");
        String settings = "";
        setting_map = controller.getSettings();
        for (String key: setting_map.keySet()) {
            settings = settings.concat(String.format(
                "%s:%f,",key,setting_map.get(key)));
        }
        writer.println(parameters);
        
        HashMap<String, Double> output_map;
        // Print header: Column description
        writer.println("#Columns:");
        String analyzer_names = "frame-id,true-signal,laser-power";
        for (EvaluationAlgorithm analyzer: analyzers) {
            output_map = analyzer.getOutputValues(1);
                for (String key: output_map.keySet()) {
                    analyzer_names = analyzer_names.concat(",").
                    concat(analyzer.getName()).concat(":").concat(key);
                }
        }
        writer.println(analyzer_names);
        
        // Print data - one line for each frame
        for (int i=1; i<=image_count; i++) {
            String s = String.format("%d,%5.2f,%5.2f",
                    i,generator.getTrueSignal(i),controller.getHistory(i));
            
            for (EvaluationAlgorithm analyzer: analyzers) {
                output_map = analyzer.getOutputValues(i);
                for (String key: output_map.keySet()) {
                    s = s.concat(String.format(",%f",output_map.get(key)));
                }
                
            }
            writer.println(s);
        }
        writer.close();
        
    }
}
