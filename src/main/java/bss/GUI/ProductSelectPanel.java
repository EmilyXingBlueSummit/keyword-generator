package bss.GUI;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSlider;

import bss.KeywordGeneration.KeywordGenerator;
import bss.Keywords.Keyword;
import bss.Keywords.KeywordDictionary;
import bss.Keywords.Categories;
import bss.Keywords.KeywordMatch;
import bss.Keywords.SearchTerm;

/**
 * ProductSelectPanel is a panel that allows users to select product characteristics and generate keywords based on their selections.
 * It extends {@link JPanelHelper} and provides functionality for selecting attributes and generating keywords using the 
 * {@link KeywordGenerator}.
 */
public class ProductSelectPanel extends JPanelHelper {
    /** Singleton instance of the ProductSelectPanel */
    static ProductSelectPanel instance = null;

    /** Mapping of product categories to their corresponding combo boxes */
    HashMap<Categories, JComboBox<Object>> categoryComboBoxes;

    /** List of combo boxes for selecting additional attributes */
    ArrayList<JComboBox<Object>> attributeComboBoxes;

    /**
     * Constructs a ProductSelectPanel and initializes its components.
     */
    private ProductSelectPanel() {
        super();
    }

    /**
     * Gets the selected text from the category combo box for the given category.
     * 
     * @param c the category for which to get the selected text
     * @return the selected text from the combo box
     */
    private String getCategoryText(Categories c) {
        return (String) this.categoryComboBoxes.get(c).getSelectedItem();
    }

    /**
     * Sets the weights for category and attribute generators based on user selections and slider values.
     */
    private void setGeneratorWeights() {
        HashMap<Categories, Double> categoryWeights = new HashMap<>();
        HashMap<Categories, JSlider> sliders = SettingsPanel.getInstance().getCategorySliders();
        for (Categories c : sliders.keySet()) {
            if (!getCategoryText(c).equals("")) {
                categoryWeights.put(c, ((double) sliders.get(c).getValue()) / 100);
            }
        }
        KeywordGenerator.setCategoryWeights(categoryWeights);

        HashMap<String, Double> attributeWeights = new HashMap<>();
        for (int i = 0; i < attributeComboBoxes.size(); i++) {
            String str = (String) attributeComboBoxes.get(i).getSelectedItem();
            if (!str.equals("")) {
                attributeWeights.put(str, ((double) SettingsPanel.getInstance().getAttributeSliders().get(i).getValue()) / 100);
            }
        }
        KeywordGenerator.setAttributeWeights(attributeWeights);
    }

    @Override
    protected void initComponents() {
        addLabelRight("Product Type", 0, 0, 1, 1);
        addLabelRight("Dimensions", 0, 1, 1, 1);
        addLabelRight("Color", 0, 2, 1, 1);
        addLabelRight("Quantity", 0, 3, 1, 1);
        addLabelRight("Material", 0, 4, 1, 1);
        addLabelRight("Attribute 1", 0, 5, 1, 1);
        addLabelRight("Attribute 2", 0, 6, 1, 1);
        addLabelRight("Attribute 3", 0, 7, 1, 1);

        this.categoryComboBoxes = new HashMap<>();
        this.categoryComboBoxes.put(Categories.PRODUCT_TYPE, addComboBox(Categories.PRODUCT_TYPE.getEntries().toStr(), 1, 0, 1, 1));
        this.categoryComboBoxes.put(Categories.DIMENSIONS, addComboBox(Categories.DIMENSIONS.getEntries().toStr(), 1, 1, 1, 1));
        this.categoryComboBoxes.put(Categories.COLOR, addComboBox(Categories.COLOR.getEntries().toStr(), 1, 2, 1, 1));
        this.categoryComboBoxes.put(Categories.QUANTITY, addComboBox(Categories.QUANTITY.getEntries().toStr(), 1, 3, 1, 1));
        this.categoryComboBoxes.put(Categories.MATERIAL, addComboBox(Categories.MATERIAL.getEntries().toStr(), 1, 4, 1, 1));

        this.attributeComboBoxes = new ArrayList<>();
        this.attributeComboBoxes.add(addComboBox(Categories.ATTRIBUTES.getEntries().toStr(), 1, 5, 1, 1));
        this.attributeComboBoxes.add(addComboBox(Categories.ATTRIBUTES.getEntries().toStr(), 1, 6, 1, 1));
        this.attributeComboBoxes.add(addComboBox(Categories.ATTRIBUTES.getEntries().toStr(), 1, 7, 1, 1));

        int widestBox = 0;
        for (JComboBox<Object> box : this.categoryComboBoxes.values()) {
            if (box.getPreferredSize().width > widestBox) {
                widestBox = box.getPreferredSize().width;
            }
        }
        for (JComboBox<Object> box : this.attributeComboBoxes) {
            if (box.getPreferredSize().width > widestBox) {
                widestBox = box.getPreferredSize().width;
            }
        }
        for (JComboBox<Object> box : this.categoryComboBoxes.values()) {
            box.setPreferredSize(new Dimension(widestBox, 25));
            box.setMaximumSize(new Dimension(widestBox, 25));
            box.setMinimumSize(new Dimension(widestBox, 25));
        }
        for (JComboBox<Object> box : this.attributeComboBoxes) {
            box.setPreferredSize(new Dimension(widestBox, 25));
            box.setMaximumSize(new Dimension(widestBox, 25));
            box.setMinimumSize(new Dimension(widestBox, 25));
        }
        
        addAccordionPanel(SettingsPanel.getInstance(), 0, 10, 2, 1).setVisible(true);

        JButton generateButton = addButton("Generate", 1, 9, 1, 1);
        setGBCFill(generateButton, GridBagConstraints.HORIZONTAL);
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                setGeneratorWeights();
                ArrayList<String> attributeStrs = new ArrayList<>();
                for (int i = 0; i < attributeComboBoxes.size(); i++) {
                    String str = (String) attributeComboBoxes.get(i).getSelectedItem();
                    if (!str.equals("")) {
                        attributeStrs.add(str);
                    }
                }
                Keyword keyword = new SearchTerm(getCategoryText(Categories.PRODUCT_TYPE), getCategoryText(Categories.DIMENSIONS), getCategoryText(Categories.COLOR), getCategoryText(Categories.QUANTITY), getCategoryText(Categories.MATERIAL), attributeStrs);
                ArrayList<KeywordMatch> matchedList = KeywordGenerator.generate(keyword);
                if (matchedList.size() > 1000) {
                    matchedList = new ArrayList<>(matchedList.subList(0, 1000));
                }
                KeywordListPanel.getInstance().updateList(matchedList);
                ListInfoTab.getInstance().updateList(matchedList);
                // UnclassifiedTokensTab.getInstance().refreshList();
                //DataAccess.writeToJson("lastSearch", matchedList);
                setCursor(Cursor.getDefaultCursor());
            }
        });

        JButton settingsToggle = addAccordionToggle("settings.png", 0, 9, 1, 1);
        setGBCAnchor(settingsToggle, GridBagConstraints.LINE_END);
        settingsToggle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsPanel.getInstance().setVisible(!SettingsPanel.getInstance().isVisible());
            }
        });

        this.categoryComboBoxes.get(Categories.PRODUCT_TYPE).setSelectedItem("classification folder");
        this.categoryComboBoxes.get(Categories.DIMENSIONS).setSelectedItem("8.5 x 14");
        this.categoryComboBoxes.get(Categories.COLOR).setSelectedItem("blue");
        this.categoryComboBoxes.get(Categories.QUANTITY).setSelectedItem("10");
        this.categoryComboBoxes.get(Categories.MATERIAL).setSelectedItem("paper");
        this.attributeComboBoxes.get(0).setSelectedItem("2 divider");

        setGeneratorWeights();
    }

    /**
     * Gets the singleton instance of the ProductSelectPanel.
     * 
     * @return the singleton instance of ProductSelectPanel
     */
    public static ProductSelectPanel getInstance() {
        if (ProductSelectPanel.instance == null) {
            ProductSelectPanel.instance = new ProductSelectPanel();
        }
        return ProductSelectPanel.instance;
    }
}
