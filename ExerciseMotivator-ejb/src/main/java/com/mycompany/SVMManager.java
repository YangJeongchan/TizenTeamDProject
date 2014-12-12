/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 *
 * @author JHS-Home
 */
@Stateless
public class SVMManager implements SVMManagerLocal {

//   public static void testLinearKernel(String[] args) throws IOException, ClassNotFoundException {
//        String trainFileName = args[0];
//        String testFileName = args[1];
//        String outputFileName = args[2];
//
//        //Read training file
//        Instance[] trainingInstances = DataFileReader.readDataFile(trainFileName);
//
//        //Register kernel function
//        KernelManager.setCustomKernel(new LinearKernel());
//
//        //Setup parameters
//        svm_parameter param = new svm_parameter();
//
//        //Train the model
//        System.out.println("Training started...");
//        svm_model model = SVMTrainer.train(trainingInstances, param);
//        System.out.println("Training completed.");
//
//        //Save the trained model
//        //SVMTrainer.saveModel(model, "a1a.model");
//        //model = SVMPredictor.load_model("a1a.model");
//        //Read test file
//        Instance[] testingInstances = DataFileReader.readDataFile(testFileName);
//        //Predict results
//        double[] predictions = SVMPredictor.predict(testingInstances, model, true);
//        writeOutputs(outputFileName, predictions);
//        //SVMTrainer.doCrossValidation(trainingInstances, param, 10, true);
//        //SVMTrainer.doInOrderCrossValidation(trainingInstances, param, 10, true);
//    }
//
//    private static void writeOutputs(String outputFileName, double[] predictions) throws IOException {
//        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
//        for (double p : predictions) {
//            writer.write(String.format("%.0f\n", p));
//        }
//        writer.close();
//    }
//
//    private static void showUsage() {
//        System.out.println("Demo training-file testing-file output-file");
//    }
//
//    private static boolean checkArgument(String[] args) {
//        return args.length == 3;
//    }
//
//    /**
//     * @param args the command line arguments
//     */
//    public static void predict(String[] args) {
//        try {
//            // TODO code application logic here
//            String[] arg = new String[3];
//            arg[0] = "svmtrain.txt";
//            arg[1] = "svmtest.txt";
//            arg[2] = "output.txt";
////            arg[0] = "a1a.train";
////            arg[1] = "a1a.test";
////            arg[2] = "output.txt";
//            testLinearKernel(arg);
//        } catch (IOException ex) {
//            Logger.getLogger(SVMManager.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(SVMManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
