package bss.GUI;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;

import bss.Keywords.KeywordDictionary;
import bss.Keywords.Categories;

/**
 * A tab in the {@code AdvancedSettingsFrame} that displays unclassified tokens and allows users
 * to categorize them.
 * <p>
 * This tab presents a list of tokens that have not yet been assigned to a category. Users can
 * select a category and entry for each token, as well as assign miscellaneous tags.
 * </p>
 */
public class UnclassifiedTokensTab extends JPanelHelper {

    /**
     * Singleton instance of {@code UnclassifiedTokensTab}.
     */
    static UnclassifiedTokensTab instance = null;

    /**
     * Panel that contains the list of unclassified tokens and associated controls.
     */
    JPanelHelper listPanel;

    /**
     * Maps {@code JComboBox} components for categories to their corresponding entry {@code JComboBox} components.
     */
    HashMap<JComboBox<Object>, JComboBox<Object>> categoryToEntryBoxes;

    /**
     * Maps {@code JComboBox} components for categories to their corresponding unclassified tokens.
     */
    HashMap<JComboBox<Object>, String> categoryTokenMap;

    /**
     * Maps {@code JComboBox} components for miscellaneous categories to their corresponding unclassified tokens.
     */
    HashMap<JComboBox<Object>, String> miscTokenMap;

    private UnclassifiedTokensTab() {
        super();
    }

    /**
     * Refreshes the list of unclassified tokens, updating the display and combo boxes.
     */
    public void refreshList() {
        listPanel.removeAll();
        this.categoryToEntryBoxes = new HashMap<>();
        this.categoryTokenMap = new HashMap<>();
        this.miscTokenMap = new HashMap<>();
        int n = 0;
        for (String s : KeywordDictionary.getInstance().getUnclassified()) {
            listPanel.addLabel(s, 0, n, 1, 1);

            JComboBox<Object> categoryBox = listPanel.addComboBox(Arrays.asList(KeywordDictionary.getInstance().getDict().keySet().toArray()), 1, n, 1, 1);
            JComboBox<Object> entryBox = listPanel.addComboBox(2, n, 1, 1);
            this.categoryToEntryBoxes.put(categoryBox, entryBox);
            this.categoryTokenMap.put(categoryBox, s);

            categoryBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JComboBox<Object> eb = categoryToEntryBoxes.get(categoryBox);
                    eb.removeAllItems();
                    eb.addItem("");
                    if (!categoryBox.getSelectedItem().equals("")) {
                        for (String s : KeywordDictionary.getInstance().getCategoryList((Categories) categoryBox.getSelectedItem())) {
                            eb.addItem(s);
                        }
                    }
                }
            });

            JComboBox<Object> miscBox = listPanel.addComboBox(Arrays.asList(new String[] {"filler", "irrelevant", "brand", "product id", "number", "missed", "overlap", "misspelled", "notable"}), 3, n, 1, 1);
            this.miscTokenMap.put(miscBox, s);
            n++;
        }
        listPanel.revalidate();
    }

    @Override
    protected void initComponents() {
        JButton enterButton = addButton("Enter", 1, 0, 1, 1);
        setGBCAnchor(enterButton, GridBagConstraints.LINE_END);
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JComboBox<Object> categoryBox : categoryToEntryBoxes.keySet()) {
                    if (!categoryBox.getSelectedItem().equals("")) {
                        JComboBox<Object> entryBox = categoryToEntryBoxes.get(categoryBox);
                        if (!entryBox.getSelectedItem().equals("")) {
                            KeywordDictionary.getInstance().addSynonym((Categories) categoryBox.getSelectedItem(), (String) entryBox.getSelectedItem(), categoryTokenMap.get(categoryBox));
                        }
                        else {
                            KeywordDictionary.getInstance().addEntry((Categories) categoryBox.getSelectedItem(), categoryTokenMap.get(categoryBox));
                        }
                    }
                }

                for (JComboBox<Object> miscBox : miscTokenMap.keySet()) {
                    if (!miscBox.getSelectedItem().equals("")) {
                        KeywordDictionary.getInstance().addOther((String) miscBox.getSelectedItem(), miscTokenMap.get(miscBox));
                    }
                }
                refreshList();
            }
        });

        this.listPanel = new JPanelHelper();
        addScrollPane(this.listPanel, 0, 1, 1, 1);
        refreshList();

        add(TokenSearchTab.getInstance(), 1, 1, 1, 1, 50, 50, GridBagConstraints.BOTH);
    }

    /**
     * Gets the singleton instance of {@code UnclassifiedTokensTab}.
     *
     * @return the singleton instance of {@code UnclassifiedTokensTab}
     */
    public static UnclassifiedTokensTab getInstance() {
        if (UnclassifiedTokensTab.instance == null) {
            UnclassifiedTokensTab.instance = new UnclassifiedTokensTab();
        }
        return UnclassifiedTokensTab.instance;
    }
}
