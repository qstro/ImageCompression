//Copyright (c) 2012-2022, Quirin Strotzer
//All rights reserved.

//This source code is licensed under the AGPL-3.0 license license found in the
//LICENSE file in the root directory of this source tree. 

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.Locale;
import java.io.IOException;
import javax.imageio.ImageIO; 
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.ImageWriteParam;
import javax.imageio.plugins.bmp.BMPImageWriteParam;

/**
 *
 * Hier findet die eigentliche Kompression statt.
 * Je nachdem, welches Ausgabeformat ausgewählt wurde, wird eine andere spezielle Kompressionsroutine gestartet.
 * Das Komprimierte Bild wird im Verzeichnis des Programms gespeichert
 * 
 * @version 1.0 vom 06.09.2012
 * @author Quirin Strotzer
 */

public class Kompressor {

    static private String name; // Name der zu komprimierenden Datei
    static private float stufe; // eingestellte Qualität
    static private String name2; // Name der Ausgabedatei
    static private String typausgabe; // Ausgabeformat

    public Kompressor(String xname, float xstufe, String xname2,String typausgabex)
    {
        name = xname;
        stufe = xstufe;
        name2 = xname2;
        typausgabe = typausgabex;
    }

    public static String main() throws Exception {
        // Bild laden
        if (typausgabe.equals(".jpg")) {
            BufferedImage image = ImageIO.read(new File(name));
            Iterator iterator = ImageIO.getImageWritersBySuffix("jpeg");
            ImageWriter imageWriter = (ImageWriter) iterator.next();
            JPEGImageWriteParam imageWriteParam = new JPEGImageWriteParam(Locale
                    .getDefault());
            imageWriteParam.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
            // Kompressionsstufe
            imageWriteParam.setCompressionQuality(stufe/100);
            IIOImage iioImage = new IIOImage(image, null, null);
            imageWriter.setOutput(ImageIO.createImageOutputStream(new File(name2)));
            imageWriter.write(null, iioImage, imageWriteParam);
            return name2;
        }

        if (typausgabe.equals(".bmp"))
        {
            BufferedImage image = ImageIO.read(new File(name));
            Iterator<ImageWriter> i = ImageIO.getImageWritersByFormatName("bmp");  
            ImageWriter bmpWriter = i.next();  
            ImageWriteParam param = bmpWriter.getDefaultWriteParam();  
            param.setCompressionMode(BMPImageWriteParam.MODE_EXPLICIT);  
            param.setCompressionType("BI_BITFIELDS"); 
            param.setCompressionQuality(0.0f);  
            IIOImage iioImage = new IIOImage(image, null, null);
            bmpWriter.setOutput(ImageIO.createImageOutputStream(new File(name2)));
            bmpWriter.write(null, iioImage, param);
            return name;
        }

        if (typausgabe.equals(".png")) 
        {
            try {
                // retrieve image
                BufferedImage bi = ImageIO.read(new File(name));
                File outputfile = new File(name2);
                ImageIO.write(bi, "png", outputfile);
            } catch (IOException e) {

            }
            return name2;
        }

        if (typausgabe.equals(".gif")) 
        {
            try {
                // retrieve image
                BufferedImage bi = ImageIO.read(new File(name));
                File outputfile = new File(name2);
                ImageIO.write(bi, "gif", outputfile);
            } catch (IOException e) {

            }
            return name2;
        }
        
        if (typausgabe.equals(".tif"))
        {
            BufferedImage image = ImageIO.read(new File(name));
            Iterator iterator = ImageIO.getImageWritersBySuffix("jpeg");
            ImageWriter imageWriter = (ImageWriter) iterator.next();
            JPEGImageWriteParam imageWriteParam = new JPEGImageWriteParam(Locale
                    .getDefault());
            imageWriteParam.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
            // Kompressionsstufe
            IIOImage iioImage = new IIOImage(image, null, null);
            imageWriter.setOutput(ImageIO.createImageOutputStream(new File(name2)));
            imageWriter.write(null, iioImage, imageWriteParam);
            return name2;
        }
              else {
            return null;
        }
    }
}
