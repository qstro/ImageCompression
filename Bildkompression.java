//Copyright (c) 2012-2022, Quirin Strotzer
//All rights reserved.

//This source code is licensed under the AGPL-3.0 license license found in the
//LICENSE file in the root directory of this source tree. 

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator; 
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 *
 * Die Klasse Bildkompression enthält dem Hauptteil des Bildkonverters. 
 * Es muss mittels Datei öffnen Monolog eine Bilddatei( ist durch Filter festgelegt) ausgewählt werden.
 * Weiterhin müssen ein Ausgabename und das Ausgabeformat ausgewählt werden. Falls das Dateiformat JPEG ausgewählt wurde,
 * kann mittels Schieberegler die Qualität des auszugebenden Bildes eingestellt werden. Per Druck auf "Kompressionsvorgang Starten" 
 * wird das Bild komprimiert und im Verzeichnis des Programms unter angegebenem Namen gespeichert.
 * 
 *Quellen:
 *http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
 *http://www.coderanch.com/t/526224/java/java/Image-Compression
 *http://docs.oracle.com/javase/tutorial/2d/images/saveimage.html
 *http://docs.oracle.com/javase/tutorial/uiswing/examples/components/
 *
 * @version 1.0 vom 06.09.2012
 * @author Quirin Strotzer
 */

public class Bildkompression extends JFrame {
    // Anfang Attribute
    String[] formats = {"JPEG" , "PNG", "GIF", "TIF", "BMP"};
    private JLabel lb_bildoeffnen = new JLabel();
    private JTextField tf_nameoeffnen = new JTextField();
    private JLabel lb_kstufe = new JLabel();
    private JSlider sl_stufe = new JSlider();
    private JLabel lb_aformat = new JLabel();
    private JComboBox jComboBox1 = new JComboBox(formats);
    private JLabel lb_background = new JLabel();
    private JLabel lb_speichernals = new JLabel();
    private JTextField tf_namespeichern = new JTextField();
    private JButton bt_start = new JButton();
    private JLabel lb_status = new JLabel();
    private JComboBox jComboBox2 = new JComboBox(formats);
    private JButton bt_auswaehlen = new JButton();
    private int count = 0;
    JLabel lb_anzeige1 = new JLabel();
    JLabel lb1 = new JLabel();
    Container cp = getContentPane();

    public String eingang;
    public String typausgabe;

    private JFileChooser fc;
    private File input;
    private String collectionDir = "c:\\Paint Collection";
    // Ende Attribute

    public Bildkompression(String title) {
        // Frame-Initialisierung
        super(title);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        int frameWidth = 800; 
        int frameHeight = 600;
        setSize(frameWidth, frameHeight);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        setLocation(x, y);
        setResizable(false);
        cp.setLayout(null);

        //Anfang Komponenten

        cp.add(tf_nameoeffnen);
        lb_kstufe.setBounds(364, 16, 103, 25);
        lb_kstufe.setText("Qualität:");
        lb_kstufe.setToolTipText("Qualität des komprimierten Bildes");
        lb_kstufe.setFont(new Font("Calibri", Font.BOLD, 12));
        cp.add(lb_kstufe);

        sl_stufe.setBounds(296, 48, 201, 49);
        sl_stufe.setMinorTickSpacing(10);
        sl_stufe.setMajorTickSpacing(50);
        sl_stufe.setMinimum(1);
        sl_stufe.setPaintTicks(true);
        sl_stufe.setPaintLabels(true);
        sl_stufe.setToolTipText("<HTML><BODY>Je niedriger die Qualität, desto stärker die Kompression <BR>-Nur bei der JPEG-Kompression einsetzbar!</BODY></HTML>");
        sl_stufe.setEnabled(true);
        cp.add(sl_stufe);

        jComboBox2.setBounds(207, 60, 74, 37);
        jComboBox2.setMaximumRowCount(5);
        jComboBox2.setToolTipText("Ausgabeformat wählen");
        jComboBox2.setToolTipText("Format, in welchem die Datei gespeichert werden soll, wählen");
        jComboBox2.setSelectedIndex(-1);
        jComboBox2.addItemListener(
            new ItemListener() {
                public void itemStateChanged( ItemEvent e )
                {
                    if (jComboBox2.getSelectedIndex() != 0)
                        sl_stufe.setEnabled(false); 

                    else sl_stufe.setEnabled(true);
                }

            });
        cp.add(jComboBox2);

        cp.add(lb_background);
        lb_speichernals.setBounds(10, 60, 70, 37);
        lb_speichernals.setText("Speichern als:");
        lb_speichernals.setFont(new Font("Calibri", Font.BOLD, 12));
        cp.add(lb_speichernals);

        tf_namespeichern.setBounds(84, 60, 117, 37);
        tf_namespeichern.setToolTipText("Name des zu speichernden Bildes ohne Endung eingeben");
        tf_namespeichern.setBackground(new Color(0xEEEEEE));
        cp.add(tf_namespeichern);

        bt_start.setBounds(512, 60, 273, 37);
        bt_start.setText("Kompressionsvorgang starten");
        bt_start.setMargin(new Insets(2, 2, 2, 2));
        bt_start.addActionListener(new 
            ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    bt_start_ActionPerformed(evt);
                }
            });
        bt_start.setFont(new 
            Font("Calibri", Font.BOLD, 12));
        cp.add(bt_start);

        lb_status.setBounds(512, 16, 273, 37);
        lb_status.setText(" Bild auswählen...");
        lb_status.setFont(new Font("Calibri", Font.BOLD, 12));
        lb_status.setBackground(Color.WHITE);
        lb_status.setOpaque(true);
        cp.add(lb_status);

        lb1.setBounds(8, 120, 780, 445);
        lb1.setText("");
        cp.add(lb1);

        bt_auswaehlen.setBounds(8, 16, 273, 37);
        bt_auswaehlen.setText("Datei auswählen");
        bt_auswaehlen.setMargin(new Insets(2, 2, 2, 2));
        bt_auswaehlen.addActionListener(new 
            ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    bt_auswaehlen_ActionPerformed(evt);
                }
            });
        cp.add(bt_auswaehlen);
        bt_auswaehlen.setToolTipText("zu komprimierendes Bild auswählen");

        // Ende Komponenten

        setVisible(true);
    } // end of public Bildkompression

    // Anfang Methoden

    public void jComboBox2_ActionPerformed(ActionEvent evt) {
    } // end of jComboBox1_ActionPerformed
    
    /**
     * 
     * Wenn Der "Datei auswählen" Button betätigt wird, öffnet sich ein Auswahl-Monolog, welcher nur 
     * die Auswahl von Bildern zulässt.
     * 
     */
    public void bt_auswaehlen_ActionPerformed(ActionEvent evt) {
        //Monolog um Bild zu öffnen
        fc = new JFileChooser();
        fc.setCurrentDirectory(new File(collectionDir));
        //Es können nur Bilddateien ausgewählt werden
        fc.setFileFilter(new ImageFilter());
        fc.setAcceptAllFileFilterUsed(false);
        //Fenster hinzufügen und anzeigen
        fc.setAccessory(new ImagePreview(fc));
        int returnVal = fc.showDialog(this, "Auswählen");
        //Eingabe bearbeiten
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            input = fc.getSelectedFile();
            lb_status.setText("<HTML><BODY>&nbsp " + input + "<BR>&nbsp ausgewählt <HTML><BODY>");
            eingang =  input.getPath();
            draw(eingang);
        }
    } // end of bt_auswaehlen_ActionPerformed
    
    /**
     * Der Kompressionsvorgang wird gestartet. Die Aufgabe wird an ein Objekt der Klasse Kompressor weitergeleitet, 
     * nachdem überprüft wurde, ob alle Felder ausgefüllt worden sind.
     * 
     */
    public void bt_start_ActionPerformed(ActionEvent evt) {
        String anzeigen = null;
        // Es muss ein Bild ausgewählt sein
        if(input!= null)
        {
            // Es muss ein Name eingegeben worden sein
            if(!tf_namespeichern.getText().equals(""))
            {
                // Es muss ein Ausgabeformat ausgewählt worden sein
                if(jComboBox2.getSelectedIndex() != -1)
                {
                    // Ausgabeformat
                    if(jComboBox2.getSelectedIndex() == 0)
                        typausgabe = ".jpg";
                    if(jComboBox2.getSelectedIndex() == 1)
                        typausgabe = ".png";
                    if(jComboBox2.getSelectedIndex() == 2)
                        typausgabe = ".gif";
                    if(jComboBox2.getSelectedIndex() == 3)
                        typausgabe = ".tif";
                    if(jComboBox2.getSelectedIndex() == 4)
                        typausgabe = ".bmp";

                    // Kompressionsstufe
                    float stufex = (float) sl_stufe.getValue();
                    // Auftrag wird weitergeleitet
                    Kompressor kompressor = new Kompressor(eingang ,stufex, tf_namespeichern.getText() + typausgabe, typausgabe); 
                    try{
                        anzeigen = kompressor.main();
                    }
                    catch (Exception e) {}

                    // Ausgegebenes Bild anzeigen
                    draw(anzeigen);
                    // Status-Label aktualisieren
                    lb_status.setText("<HTML><BODY>&nbsp Das Bild wurde komprimiert und unter dem Namen <BR>&nbsp "+ tf_namespeichern.getText() + typausgabe +" gespeichert. </BODY></HTML>");
                }

                // Wenn kein Ausgabeformat ausgewählt
                else {lb_status.setText(" Wähle ein Ausgabeformat aus!");}}
            // Wenn kein Ausgabename eingegeben
            else {lb_status.setText(" Fülle 'Speichern als' aus!");}}
        // Wenn kein Bild ausgewählt
        else {lb_status.setText(" Datei zum konvertieren auswählen!"); }
        repaint();
    } 
    // end of bt_start_ActionPerformed
    
    /**
     * Mit dieser Methode wird das komprimierte Bild angezeigt.
     */
    public void draw(String name) {
        // Anzeige des Bildes
        ImageIcon imageIcon = new ImageIcon(name); // Bild als ImageIcon laden
        Image image = imageIcon.getImage();
        Image newimg = image.getScaledInstance(780, 445,  java.awt.Image.SCALE_SMOOTH); // Größe anpassen 
        imageIcon = new ImageIcon(newimg);
        lb1.setIcon(imageIcon);
        repaint();
    }
    
    public static void main(String[] args) {
        new Bildkompression("Bildkonverter");
    } // end of main

    // Ende Methoden

} // end of class Bildkompression

/**
 * Aufbau des Datei Öffnen-Monologs und Erstellen des Filters, welcher nur die Auswahl von Bilddateien zulässt.
 */
class ImageFilter extends FileFilter
{
    //Der Filter akzeptiert nur bmp, gif, jpg, tiff und png Dateien.
    public boolean accept(final File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);
        if (extension != null) {
            if (extension.equals(Utils.tiff) ||
            extension.equals(Utils.tif) ||
            extension.equals(Utils.gif) ||
            extension.equals(Utils.jpeg) ||
            extension.equals(Utils.jpg) ||
            extension.equals(Utils.bmp) ||
            extension.equals(Utils.png)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    //Beschreibung des Filters
    public String getDescription() {
        return "Bilddateien";
    }
}
//Erweiterung des Datei-Monologes
class ImagePreview extends JComponent
implements PropertyChangeListener {
    Image image = null;
    File file = null;
    public ImagePreview(final JFileChooser fc) {
        setPreferredSize(new Dimension(100, 50));
        fc.addPropertyChangeListener(this);
    }

    public void propertyChange(final PropertyChangeEvent e) {
        boolean update = false;
        String prop = e.getPropertyName();
        //Kein Bild anzeigen, wenn Verzeichnis geändert
        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
            file = null;
            update = true;
            //Finde heraus, welche Datei ausgewählt wurde
        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            file = (File) e.getNewValue();
            update = true;
        }
        //Preview aktualisieren
        if (update) {
            image = null;
            if (isShowing()) {
                repaint();
            }
        }
    }

    protected void paintComponent(final Graphics g) {
        if( image == null && file != null ) {
            //Bild laden
            try{ image = ImageIO.read(new File(file.getPath())); }catch(IOException ex){}
            if (image != null) {
                if (image.getWidth(null) > 90) {
                    image = image.getScaledInstance(90, -1, Image.SCALE_DEFAULT);
                }
            }
        }
        if (image != null) {
            int x = getWidth()/2 - image.getWidth(null)/2;
            int y = getHeight()/2 - image.getHeight(null)/2;
            if (y < 0) {
                y = 0;
            }
            if (x < 5) {
                x = 5;
            }
            g.drawImage(image, x, y, null);
        }
    }
}
class Utils {
    
    // Es können nur Dateien mit diesen Endungen angezeigt und ausgewählt werden
    public final static String jpeg = "jpeg";
    public final static String jpg = "jpg";
    public final static String bmp = "bmp";
    public final static String gif = "gif";
    public final static String tiff = "tiff";
    public final static String tif = "tif";
    public final static String png = "png";

    //Dateiendung herausfinden
    public static String getExtension(final File f) { 
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}

