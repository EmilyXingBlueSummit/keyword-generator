package bss.GUI;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * MainPanel is a singleton class that represents the main panel of the application GUI.
 * It extends {@link JPanelHelper} and sets up various components including labels, buttons, and panels.
 */
public class MainPanel extends JPanelHelper {
    private static MainPanel instance = null;

    /** Button to toggle the visibility of the ProductSelectPanel */
    JButton toggle;

    /**
     * Private constructor to prevent instantiation.
     */
    private MainPanel() {
        super();
    }

    /**
     * Initializes the components of the main panel. This method sets up the layout, adds labels,
     * scroll panes, buttons, and handles their action events.
     */
    @Override
    protected void initComponents() {
        //setMinimumSize(new Dimension(500, 500));

        addLabel("Keywords", 2, 0, 5, 1);
        //Font smallerFont = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
        addScrollPane(KeywordListPanel.getInstance(), 2, 1, 5, 10);

        JButton uploadButton = addButton("Upload", 6, 0, 1, 1);
        uploadButton.setFont(this.smallerFont);
        setGBCAnchor(uploadButton, GridBagConstraints.LINE_END);
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileUploadFrame.getInstance().setVisible(true);
                // FileUpload fileUpload = new FileUpload();
                // fileUpload.read();
            }
        });

        this.toggle = addAccordionToggle("left arrow.png", 0, 0, 1, 1);
        this.toggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProductSelectPanel.getInstance().setVisible(!ProductSelectPanel.getInstance().isVisible());
                if (ProductSelectPanel.getInstance().isVisible()) {
                    setButtonIcon(toggle, "left arrow.png");
                }
                else {
                    setButtonIcon(toggle, "right arrow.png");
                }
            }
        });

        //addButton("get last search", 1, 0, 1, 1).addActionListener(new ActionListener() {
        //    @Override
        //    public void actionPerformed(ActionEvent e) {
        //        ArrayList<KeywordMatch> matchedList = (ArrayList<KeywordMatch>) DataAccess.readFromJson("lastSearch", new TypeToken<ArrayList<KeywordMatch>>(){}.getType());
        //        updateList(matchedList);
        //    }
        //});

        //ProductSelectPanel.getInstance().setMinimumSize(ProductSelectPanel.getInstance().getPreferredSize());
        addAccordionPanel(ProductSelectPanel.getInstance(), 0, 1, 2, 9);
    }

    /**
     * Gets the singleton instance of the MainPanel class.
     * 
     * @return the singleton instance of MainPanel
     */
    public static MainPanel getInstance() {
        if (MainPanel.instance == null) {
            MainPanel.instance = new MainPanel();
        }
        return MainPanel.instance;
    }
}
