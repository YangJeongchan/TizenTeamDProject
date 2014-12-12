/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import org.bytedeco.javacpp.Loader;
import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import org.bytedeco.javacpp.opencv_contrib;
import static org.bytedeco.javacpp.opencv_contrib.*;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import org.bytedeco.javacpp.opencv_imgproc;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvThreshold;
import org.bytedeco.javacpp.opencv_objdetect;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;

/**
 *
 * @author JHS-Home
 */
@Stateless
public class FaceManager implements FaceManagerRemote {

    private FaceRecognizer faceRecognizer;

    private final static String trainingDir = "C://JoinFile/";
    private final static String testDir = "C://LoginFile/";

    public FaceManager() {
        Loader.load(opencv_core.class);
        Loader.load(opencv_contrib.class);
        Loader.load(opencv_objdetect.class);
        Loader.load(opencv_imgproc.class);
        MakeModel();
    }

    private void MakeModel() {

        File root = new File(trainingDir);

        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png") || name.endsWith(".jpeg");
            }
        };

        File[] imageFiles = root.listFiles(imgFilter);

        MatVector images = new MatVector(imageFiles.length);

        Mat labels = new Mat(imageFiles.length, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.getIntBuffer();

        int counter = 0;

        for (File image : imageFiles) {
            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
            Size size = new Size(92, 112);
            Mat gray = img;
           // opencv_imgproc.cvtColor(img, gray, CV_BGR2GRAY);

            Mat dst = new Mat(size);

            opencv_imgproc.resize(gray, dst, size);
            
            int label = Integer.parseInt(image.getName().split("\\-")[0]);

            images.put(counter, dst);

            labelsBuf.put(counter, label);

            counter++;
        }

        faceRecognizer = createEigenFaceRecognizer();
        faceRecognizer.train(images, labels);
    }
 
    @Override
    public int predict(String fileName) {
         MakeModel();
        //fileName = "1-1.pgm";
        int i = 0 ; 
         while(i > 10000)
         {
             i++;
         }
        Mat src = imread(fileName);
        Size size = new Size(112, 92);
        Mat gray = new Mat(112,92,CV_8UC1);
        opencv_imgproc.cvtColor(src, gray, CV_BGR2GRAY);
        
        Mat dst = new Mat(size);
        
        opencv_imgproc.resize(gray, dst, size);
        
        int predictedLabel = faceRecognizer.predict(dst);

        System.out.println("Predicted label: " + predictedLabel);
        return predictedLabel;
    }

    @Override
    public void makeCompositePic(String fileName) throws IOException {
        String classifierName = "C://fileset/haarcascade_frontalface_alt.xml";
        String l_e = "C://fileset/haarcascade_mcs_lefteye.xml";
        String r_e = "C://fileset/haarcascade_mcs_righteye.xml";
        String mouth = "C://fileset/haarcascade_mcs_mouth.xml";
        String nose = "C://fileset/haarcascade_mcs_nose.xml";
        CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(cvLoad(classifierName));
        CvHaarClassifierCascade l_classifier = new CvHaarClassifierCascade(cvLoad(l_e));
        CvHaarClassifierCascade r_classifier = new CvHaarClassifierCascade(cvLoad(r_e));
        CvHaarClassifierCascade m_classifier = new CvHaarClassifierCascade(cvLoad(mouth));
        CvHaarClassifierCascade n_classifier = new CvHaarClassifierCascade(cvLoad(nose));

        int x_s[] = new int[4];
        int y_s[] = new int[4];
        int w_s[] = new int[4];
        int h_s[] = new int[4];

        if (classifier.isNull()) {
            System.err.println("Error loading classifier file \""
                    + classifierName + "\".");
            //System.exit(1);
        }

        IplImage img = cvLoadImage(fileName); // 얼굴 사진

        IplImage grabbedImage = img;
        int width = grabbedImage.width();
        int height = grabbedImage.height();
        IplImage grayImage = IplImage.create(width, height, IPL_DEPTH_8U, 1);

        int face_x = 0;
        int face_y = 0;
        int m_h = 0;
        int m_w = 0;
        int m_x = 0;
        int m_y = 0;
        int e_h = 0;
        int e_w = 0;
        int e_x = 0;
        int e_y = 0;
        CvMemStorage storage = CvMemStorage.create();
        cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);

        CvSeq faces = cvHaarDetectObjects(grayImage, classifier, storage, 1.1,
                3, CV_HAAR_DO_CANNY_PRUNING);

        int total = faces.total();

        for (int i = 0; i < total; i++) {
            CvRect r = new CvRect(cvGetSeqElem(faces, i));
            int x = r.x(), y = r.y(), w = r.width(), h = r.height();

            face_x = x + (w / 2);
            face_y = y + (h / 2);
            x_s[2] = x;
            y_s[2] = y;
            w_s[2] = w;
            h_s[2] = h;
            cvRectangle(grabbedImage, cvPoint(x, y), cvPoint(x + w, y + h),
                    CvScalar.RED, 1, CV_AA, 0);
        }
        System.out.println(face_x);

        faces = cvHaarDetectObjects(grayImage, l_classifier, storage, 1.1, 3,
                CV_HAAR_DO_CANNY_PRUNING);
        total = faces.total();
        for (int i = 0; i < total; i++) {
            CvRect r = new CvRect(cvGetSeqElem(faces, i));
            int x = r.x(), y = r.y(), w = r.width(), h = r.height();
            if (x <= face_x && y < face_y) {

                h_s[0] = h;
                w_s[0] = w;
                x_s[0] = x;
                y_s[0] = y;
                cvRectangle(grabbedImage, cvPoint(x, y), cvPoint(x + w, y + h),
                        CvScalar.RED, 1, CV_AA, 0);
            }
        }

        faces = cvHaarDetectObjects(grayImage, r_classifier, storage, 1.1, 3,
                CV_HAAR_DO_CANNY_PRUNING);
        total = faces.total();
        for (int i = 0; i < total; i++) {
            CvRect r = new CvRect(cvGetSeqElem(faces, i));
            int x = r.x(), y = r.y(), w = r.width(), h = r.height();
            if (x >= face_x) {
                System.out.println("가로:" + w + "세로:" + h);
                h_s[3] = h;
                w_s[3] = w;
                x_s[3] = x;
                y_s[3] = y;
                cvRectangle(grabbedImage, cvPoint(x, y), cvPoint(x + w, y + h),
                        CvScalar.GREEN, 1, CV_AA, 0);
            }
        }

        faces = cvHaarDetectObjects(grayImage, m_classifier, storage, 1.1, 3,
                CV_HAAR_DO_CANNY_PRUNING);
        total = faces.total();
        for (int i = 0; i < total; i++) {
            CvRect r = new CvRect(cvGetSeqElem(faces, i));
            int x = r.x(), y = r.y(), w = r.width(), h = r.height();
            if (y > face_y) {
                System.out.println("가로:" + x + "세로:" + h);
                h_s[1] = h;
                w_s[1] = w;
                x_s[1] = x;
                y_s[1] = y;
                cvRectangle(grabbedImage, cvPoint(x, y), cvPoint(x + w, y + h),
                        CvScalar.BLUE, 1, CV_AA, 0);
            }
        }

        cvThreshold(grayImage, grayImage, 64, 255, CV_THRESH_BINARY);

        BufferedImage[] images = new BufferedImage[4];
        BufferedImage image = ImageIO.read(new File(fileName));
        String fin[] = new String[]{"C://fileset/ande.png", "C://fileset/mouse.png", "C://fileset/daji.png",
            "C://fileset/rupi.png"};
        BufferedImage combined = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = combined.getGraphics();
        g.drawImage(image, 0, 0, null);
        for (int i = 0; i < 4; i++) {
            System.out.println(fin[i] + "" + x_s[i] + "" + y_s[i] + "" + w_s[i]
                    + "" + h_s[i]);
            images[i] = ImageIO.read(new File(fin[i]));
            g.drawImage(images[i], x_s[i], y_s[i], w_s[i], h_s[i], null);
        }
        String[] ssplit = fileName.split("\\.");
        String combineFileName =  ssplit[0];
        File comFile = new File(combineFileName + "combined.jpg");
        ImageIO.write(combined, "JPG", comFile); // 저장

        cvClearMemStorage(storage);
    }
}
