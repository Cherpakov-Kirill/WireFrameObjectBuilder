package nsu.graphics.thirdlab.windows;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import javax.swing.*;

import nsu.graphics.thirdlab.object.ObjectPanel;
import ru.nsu.cg.MainFrame;


public class WireframeObjectWindow extends MainFrame implements ComponentListener, TemplateWindowListener {
    private final ObjectPanel objectPanel;
    private final WireframeTemplateWindow templateWindow;

    private final String[] extensions;

    public WireframeObjectWindow() {
        super(800, 600, "Wireframe Object Builder");
        try {
            addSubMenu("File", KeyEvent.VK_F);
            addMenuItem("File/Open", "Open a file", KeyEvent.VK_O, "/Open.png", "openFile");
            addMenuItem("File/Save as", "Save your picture as file", KeyEvent.VK_S, "/Save.png", "saveFile");
            addMenuItem("File/Exit", "Exit application", KeyEvent.VK_X, "/Exit.png", "exit");

            addSubMenu("View", KeyEvent.VK_V);
            addMenuItem("View/Normalize", "Normalize object position", KeyEvent.VK_F, "/FitImage.png", "normalize");
            addMenuItem("View/Palette", "Palette", KeyEvent.VK_P, "/Palette.png", "chooseObjectColor");
            addMenuItem("View/Create template", "Change key-points position", KeyEvent.VK_F, "/Settings.png", "createTemplate");

            addSubMenu("Help", KeyEvent.VK_H);
            addMenuItem("Help/About...", "Shows program version and copyright information", KeyEvent.VK_A, "/About.png", "showAbout");
            addMenuItem("Help/Usage", "Shows program usage information", KeyEvent.VK_U, "/Usage.png", "showUsage");

            addToolBarButton("File/Open");
            addToolBarButton("File/Save as");
            addToolBarSeparator();
            addToolBarButton("View/Normalize");
            addToolBarButton("View/Palette");
            addToolBarButton("View/Create template");


            JScrollPane scrollPane = new JScrollPane();
            objectPanel = new ObjectPanel(scrollPane, 685, 395);
            scrollPane.setViewportView(objectPanel);
            templateWindow = new WireframeTemplateWindow(this);
            add(scrollPane);
            addComponentListener(this);
            setBackground(Color.WHITE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        extensions = new String[1];
        extensions[0] = "wob";
    }

    @Override
    public void setTemplate(List<Point> splinePoints, int N, int K, int m, int M) {
        objectPanel.setTemplate(splinePoints,N,K,m,M);
    }

    //File/Open - opens any image file
    public void openFile() {
        File file = getOpenFileName(extensions);
        if (file == null) return;
        templateWindow.openFile(file);
        System.out.println("Opened file " + file.getAbsolutePath());
    }

    //File/Save - saves image file
    public void saveFile() {
        File file = getSaveFileName(extensions);
        if (file == null) return;
        templateWindow.saveFile(file);
        System.out.println("Saving file to " + file.getAbsolutePath());
    }


    //View/Create template
    public void createTemplate() {
        templateWindow.setVisible(true);
    }

    //View/Fit to screen
    public void normalize() {
        objectPanel.normalizeObject();
    }

    //View/Palette
    public void chooseObjectColor() {
        Color color = JColorChooser.showDialog(this,
                "Choose color", Color.BLACK);
        if(color != null) objectPanel.setObjectColor(color);
    }

    //File/Exit - exits application
    public void exit() {
        System.exit(0);
    }

    //Help/About... - shows program version and copyright information
    public void showAbout() {
        JOptionPane.showMessageDialog(this, "Wireframe object builder App. ver. 1.0\nCopyright 2022 Cherpakov Kirill, FIT, group 19201\nProgram for building wireframe objects.", "About Wireframe object builder App", JOptionPane.INFORMATION_MESSAGE);
    }

    //Help/Usage - shows program usage information
    public void showUsage() {
        JOptionPane.showMessageDialog(this, "Open object-file to start working with it.\nPush on the Open File button or find the same menu item in the menu \"File\"", "Usage", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        WireframeObjectWindow mainFrame = new WireframeObjectWindow();
        mainFrame.setVisible(true);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        objectPanel.componentResized();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
