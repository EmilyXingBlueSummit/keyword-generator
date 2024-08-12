package bss.GUI;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bss.GUI.JPanelHelper.ComponentType;
import bss.KeywordGeneration.KeywordGenerator;
import bss.Keywords.Categories;

/**
 * A panel for adjusting parameters related to keyword generation.
 */
public class SettingsPanel extends JPanelHelper {
    /**
     * Singleton instance of {@code SettingsPanel}.
     */
    static SettingsPanel instance = null;

    /**
     * A map that stores sliders for different categories.
     * Each entry maps a {@code Categories} constant to its corresponding {@code JSlider}.
     */
    HashMap<Categories, JSlider> categorySliders;

    /**
     * A list of sliders for attributes.
     */
    ArrayList<JSlider> attributeSliders;

    private SettingsPanel() {
        super();
    }

    /**
     * Gets the map of category sliders.
     *
     * @return a map of {@code Categories} to {@code JSlider} objects
     */
    public HashMap<Categories, JSlider> getCategorySliders() {
        return this.categorySliders;
    }

    /**
     * Gets the list of attribute sliders.
     *
     * @return an {@code ArrayList} of {@code JSlider} objects
     */
    public ArrayList<JSlider> getAttributeSliders() {
        return this.attributeSliders;
    }

    @Override
    protected void initComponents() {
        setBorder(BorderFactory.createLoweredBevelBorder());
        //this.insets = new Insets(1, 5, 1, 5);

        JPanelHelper ordersPanel = new JPanelHelper();
        ordersPanel.insets = new Insets(2, 0, 2, 0);
        ordersPanel.addLabel("Min. Orders:", 0, 0, 1, 1).setFont(this.smallerFont);
        JTextField ordersTextField = ordersPanel.addTextField(30, 1, 0, 1, 1);
        ordersTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ordersTextField.getText().equals("")) {
                    try {
                        KeywordGenerator.minOrders = Integer.parseInt(ordersTextField.getText());
                    } catch (NumberFormatException nfe) {

                    }
                }
                else {
                    KeywordGenerator.minOrders = 0;
                }
            }
        });

        JPanelHelper cvrPanel = new JPanelHelper();
        cvrPanel.insets = new Insets(2, 0, 2, 0);
        cvrPanel.addLabel("Min. CVR:", 0, 0, 1, 1).setFont(this.smallerFont);
        JTextField cvrTextField = cvrPanel.addTextField(30, 1, 0, 1, 1);
        cvrTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ordersTextField.getText().equals("")) {
                    try {
                        KeywordGenerator.minCVR = Double.parseDouble(cvrTextField.getText());
                    } catch (NumberFormatException nfe) {

                    }
                }
                else {
                    KeywordGenerator.minCVR = 0;
                }
            }
        });
        cvrPanel.addLabel("%", 2, 0, 1, 1).setFont(this.smallerFont);

        addPanel(ordersPanel, 0, 0, 1, 1);
        addPanel(cvrPanel, 1, 0, 1, 1);

        JPanelHelper categorySliderPanel = new JPanelHelper();
        categorySliderPanel.insets = new Insets(2, 0, 2, 0);
        categorySliderPanel.weightx.replace(ComponentType.LABELRIGHT, 0.0);
        JLabel categoryHeader = categorySliderPanel.addLabel("Category Importance", 0, 0, 2, 1);
        categoryHeader.setHorizontalAlignment(SwingConstants.CENTER);

        JPanelHelper sliderLabelPanel = new JPanelHelper();
        sliderLabelPanel.weightx.replace(ComponentType.LABEL, 1.0);
        sliderLabelPanel.insets = new Insets(0, 0, 0, 0);
        sliderLabelPanel.addLabel("Least", 0, 0, 1, 1).setFont(this.smallestFont);
        sliderLabelPanel.addLabelRight("Most", 1, 0, 1, 1).setFont(this.smallestFont);
        categorySliderPanel.addPanel(sliderLabelPanel, 1, 1, 1, 1);
        categorySliderPanel.setGBCInsets(sliderLabelPanel, new Insets(0, 0, 0, 0));


        categorySliderPanel.addLabelRight("Product Type", 0, 2, 1, 1).setFont(this.smallerFont);
        categorySliderPanel.addLabelRight("Dimensions", 0, 3, 1, 1).setFont(this.smallerFont);
        categorySliderPanel.addLabelRight("Color", 0, 4, 1, 1).setFont(this.smallerFont);
        categorySliderPanel.addLabelRight("Quantity", 0, 5, 1, 1).setFont(this.smallerFont);
        categorySliderPanel.addLabelRight("Material", 0, 6, 1, 1).setFont(this.smallerFont);
        categorySliderPanel.addLabelRight("Attribute 1", 0, 7, 1, 1).setFont(this.smallerFont);
        categorySliderPanel.addLabelRight("Attribute 2", 0, 8, 1, 1).setFont(this.smallerFont);
        categorySliderPanel.addLabelRight("Attribute 3", 0, 9, 1, 1).setFont(this.smallerFont);

        this.categorySliders = new HashMap<>();
        this.categorySliders.put(Categories.PRODUCT_TYPE, categorySliderPanel.addSlider(1, 2, 1, 1));
        this.categorySliders.put(Categories.DIMENSIONS, categorySliderPanel.addSlider(1, 3, 1, 1));
        this.categorySliders.put(Categories.COLOR, categorySliderPanel.addSlider(1, 4, 1, 1));
        this.categorySliders.put(Categories.QUANTITY, categorySliderPanel.addSlider(1, 5, 1, 1));
        this.categorySliders.put(Categories.MATERIAL, categorySliderPanel.addSlider(1, 6, 1, 1));

        this.attributeSliders = new ArrayList<>();
        this.attributeSliders.add(categorySliderPanel.addSlider(1, 7, 1, 1));
        this.attributeSliders.add(categorySliderPanel.addSlider(1, 8, 1, 1));
        this.attributeSliders.add(categorySliderPanel.addSlider(1, 9, 1, 1));

        for (Categories c : this.categorySliders.keySet()) {
            this.categorySliders.get(c).addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    System.out.println(c);
                    System.out.println(categorySliders.get(c).getValue());
                }
            });
        }
        for (JSlider s : this.attributeSliders) {
            s.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {

                }
            });
        }

        setFixedComponentSize(categorySliderPanel, getPreferredSize().width, categorySliderPanel.getPreferredSize().height);
        addPanel(categorySliderPanel, 0, 1, 2, 9);

        JPanelHelper applyButtonsPanel = new JPanelHelper();
        JButton applyButton = applyButtonsPanel.addButton("Apply", 0, 0, 1, 1);
        applyButton.setFont(this.smallerFont);
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        JButton undoButton = applyButtonsPanel.addButton("Undo", 1, 0, 1, 1);
        undoButton.setFont(this.smallerFont);
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        JButton resetButton = applyButtonsPanel.addButton("Reset", 2, 0, 1, 1);
        resetButton.setFont(this.smallerFont);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Categories c : categorySliders.keySet()) {
                    categorySliders.get(c).setValue(50);
                }
                for (JSlider s : attributeSliders) {
                    s.setValue(50);
                }
            }
        });
        setFixedComponentSize(applyButtonsPanel, categorySliderPanel.getPreferredSize().width, applyButtonsPanel.getPreferredSize().height);
        //addPanel(applyButtonsPanel, 0, 10, 2, 1);
        //setGBCInsets(applyButtonsPanel, new Insets(0, 0, 0, 0));

        JButton advancedButton = addButton("Advanced", 0, 11, 2, 1);
        advancedButton.setFont(this.smallerFont);
        setGBCFill(advancedButton, GridBagConstraints.HORIZONTAL);
        advancedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdvancedSettingsFrame frame = AdvancedSettingsFrame.getInstance();
                frame.setVisible(true);
            }
            
        });
    }

    /**
     * Gets the singleton instance of {@code SettingsPanel}.
     *
     * @return the singleton instance of {@code SettingsPanel}
     */
    public static SettingsPanel getInstance() {
        if (SettingsPanel.instance == null) {
            SettingsPanel.instance = new SettingsPanel();
        }
        return SettingsPanel.instance;
    }
}
