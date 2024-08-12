package bss.GUI;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.commons.collections4.map.ListOrderedMap;

import bss.GUI.JPanelHelper.ComponentType;
import bss.Keywords.KeywordDictionary;
import bss.Keywords.Categories;
import bss.Keywords.Entry;
import bss.Keywords.Tokenizer;

/**
 * A tab in the {@code AdvancedSettingsFrame} that displays and allows editing of the keyword dictionary.
 * <p>
 * This tab presents a user interface for managing keyword entries and their synonyms. Users can add,
 * edit, and remove entries and synonyms within various categories of the dictionary.
 * </p>
 */
public class DictionaryTab extends JTabbedPane {

    /**
     * Singleton instance of {@code DictionaryTab}.
     */
    static DictionaryTab instance = null;

    /**
     * Maps {@code JPanelHelper} instances to their corresponding {@code Categories}.
     */
    HashMap<JPanelHelper, Categories> dictPanels;

    /**
     * Maps {@code JPanelHelper} instances to a list of component rows, where each row contains a list of {@code JComponent} objects.
     */
    HashMap<JPanelHelper, ArrayList<ArrayList<JComponent>>> componentsByIndex;

    /**
     * Label indicating the currently selected item.
     */
    JLabel selectedLabel;
    // JTextField entryTextField;

    private DictionaryTab() {
        super();

        initComponents();
    }

    /**
     * Adds an editable label to the given panel.
     * <p>
     * The label can be clicked to convert it into a text field for editing.
     * </p>
     *
     * @param p the panel to which the label is added
     * @param s the text for the label
     * @param x the x position of the label
     * @param y the y position of the label
     * @return the created {@code JLabel}
     */
    protected JLabel addEditableLabel(JPanelHelper p, String s, int x, int y) {
        JLabel l = p.addLabel(s, x, y, 1, 1);
        l.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTextField t = p.addTextField(x, y, 1, 1);
                if (x == 0) {
                    t.setText(l.getText().substring(0, l.getText().length() - 1));
                }
                else {
                    t.setText(l.getText());
                }
                t.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!t.getText().equals(l.getText()) && !(t.getText() + ":").equals(l.getText())) {
                            GridBagConstraints tGBC = p.gridBagLayout.getConstraints(t);
                            ArrayList<JComponent> row = componentsByIndex.get(p).get(tGBC.gridy);
                            //p.addLabel(t.getText(), tGBC.gridx, tGBC.gridy, 1, 1);

                            String entryStr = ((JLabel) row.get(0)).getText();
                            entryStr = entryStr.substring(0, entryStr.length() - 1);
                            if ((dictPanels.get(p) == Categories.PRODUCT_CATEGORY && x == 0) || (dictPanels.get(p) != Categories.PRODUCT_CATEGORY && (x == 0 || x == 1))) {
                                // if (dictPanels.get(p) == Category.PRODUCT_CATEGORY) {
                                //     if (x == 0) {
                                //         if (!t.getText().equals("") && !t.getText().replaceAll(" ", "").equals("")) {
                                //             KeywordDictionary.getInstance().replaceEntry(dictPanels.get(p), entryStr, t.getText());
                                //             ((JLabel) row.get(0)).setText(t.getText() + ":");
                                //         }
                                //         else {
                                //             KeywordDictionary.getInstance().removeEntry(dictPanels.get(p), entryStr);
                                //             componentsByIndex.get(p).remove(tGBC.gridy);
                                //             for (int i = tGBC.gridy; i < componentsByIndex.get(p).size(); i++) {
                                //                 for (JComponent c : componentsByIndex.get(p).get(i)) {
                                //                     p.setGBCY(c, i);
                                //                 }
                                //             }
                                //             p.remove(l);
                                //         }
                                //     }
                                //     else {
                                //         if (!t.getText().equals("") && !t.getText().replaceAll(" ", "").equals("")) {
                                //             KeywordDictionary.getInstance().replaceSynonym(dictPanels.get(p), entryStr, l.getText(), t.getText());
                                //             ((JLabel) row.get(row.indexOf(l))).setText(t.getText());
                                //         }
                                //         else {
                                //             KeywordDictionary.getInstance().removeSynonym(dictPanels.get(p), entryStr, l.getText());
                                //             row.remove(l);
                                //             p.remove(l);
                                //         }
                                //     }
                                // }
                                if (!t.getText().equals("") && !t.getText().replaceAll(" ", "").equals("")) {
                                    dictPanels.get(p).replaceEntry(entryStr, t.getText());
                                    ((JLabel) row.get(0)).setText(t.getText() + ":");
                                    if (dictPanels.get(p) != Categories.PRODUCT_CATEGORY) {
                                        ((JLabel) row.get(1)).setText(t.getText());
                                    }
                                }
                                else {
                                    // KeywordDictionary.getInstance().removeEntry(dictPanels.get(p), entryStr);
                                    //         componentsByIndex.get(p).remove(tGBC.gridy);
                                    //         for (int i = tGBC.gridy; i < componentsByIndex.get(p).size(); i++) {
                                    //             for (JComponent c : componentsByIndex.get(p).get(i)) {
                                    //                 p.setGBCY(c, i);
                                    //             }
                                    //         }
                                    //         p.remove(l);

                                    p.remove(row.get(1));
                                    row.remove(1);
                                    if (dictPanels.get(p) != Categories.PRODUCT_CATEGORY && row.size() > 1) {
                                        dictPanels.get(p).replaceEntry(entryStr, ((JLabel) row.get(1)).getText());
                                    }
                                    else {
                                        dictPanels.get(p).removeEntry(entryStr);
                                        p.remove(row.get(0));
                                        componentsByIndex.get(p).remove(row);
                                    }
                                    p.remove(l);
                                }
                            }
                            else {
                                if (!t.getText().equals("") && !t.getText().replaceAll(" ", "").equals("")) {
                                    dictPanels.get(p).replaceSynonym(entryStr, l.getText(), t.getText());
                                    ((JLabel) row.get(row.indexOf(l))).setText(t.getText());
                                }
                                else {
                                    dictPanels.get(p).removeSynonym(entryStr, l.getText());
                                    row.remove(l);
                                    p.remove(l);
                                }
                            }
                        }
                        l.setVisible(true);
                        p.remove(t);
                        p.revalidate();
                    }
                });
                p.setGBCAnchor(t, GridBagConstraints.NORTHWEST);
                l.setVisible(false);
                p.revalidate();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                selectedLabel = l;
                // System.out.println("enter");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                selectedLabel = null;
                // System.out.println("exit");
            }
        });
        return l;
    }

    /**
     * Adds a text field for entering synonyms to the given panel.
     *
     * @param p the panel to which the text field is added
     * @param x the x position of the text field
     * @param y the y position of the text field
     * @return the created {@code JTextField}
     */
    private JTextField addSynonymTextField(JPanelHelper p, int x, int y) {
        JTextField t = p.addTextField(50, x, y, 1, 1);
        t.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!t.getText().equals("") && !t.getText().replaceAll(" ", "").equals("")) {
                    GridBagConstraints tGBC = p.gridBagLayout.getConstraints(t);
                    ArrayList<JComponent> row = componentsByIndex.get(p).get(tGBC.gridy);
                    row.add(tGBC.gridx, addEditableLabel(p, t.getText(), tGBC.gridx, tGBC.gridy));
                    dictPanels.get(p).addSynonym(((JLabel) row.get(0)).getText().substring(0, ((JLabel) row.get(0)).getText().length() - 1), t.getText());
                    t.setText("");
                    p.setGBCX(t, tGBC.gridx + 1);
                    p.revalidate();
                }
            }
        });
        p.setGBCAnchor(t, GridBagConstraints.NORTHWEST);
        return t;
    }

    /**
     * Initializes the components of the {@code DictionaryTab}.
     * <p>
     * Sets up the layout and adds all categories and their entries to the tab.
     * </p>
     */
    private void initComponents() {
        this.dictPanels = new HashMap<>();
        this.componentsByIndex = new HashMap<>();

        // ListOrderedMap<String, ArrayList<String>> d = KeywordDictionary.getInstance().getCategory(Category.PRODUCT_CATEGORY);
        // Object[][] o = new Object[d.keyList().size()][12];
        // for (int i = 0; i < d.size(); i++) {
        //     o[i][0] = d.get(i);
        //     for (int j = 0; j < d.getValue(i).size(); j++) {
        //         o[i][j + 1] = d.getValue(i).get(j);
        //     }
        // }
        // String[] col = {"category", "entries", "", "", "", "", "", "", "", "", "", ""};
        // JTable tableTest = new JTable(o, col);
        // tableTest.setDragEnabled(true);

        // addTab("table test", new JScrollPane(tableTest));

        for (Categories c : Categories.getValues()) {
            JPanelHelper p = new JPanelHelper();
            p.weightx.replace(JPanelHelper.ComponentType.TEXTFIELD, 0.0);
            //p.weighty.replace(JPanelHelper.ComponentType.TEXTFIELD, 1.0);
            p.fill.replace(JPanelHelper.ComponentType.TEXTFIELD, GridBagConstraints.BOTH);

            ArrayList<ArrayList<JComponent>> rows = new ArrayList<>();

            for (Entry entry : c.getEntries()) {
                ArrayList<JComponent> row = new ArrayList<>();
                row.add(addEditableLabel(p, entry.getValue() + ":", 0, rows.size()));

                for (String s : entry.getSynonyms()) {
                    row.add(addEditableLabel(p, s, row.size(), rows.size()));
                }
                row.add(addSynonymTextField(p, row.size(), rows.size()));
                rows.add(row);
            }

            JTextField entryTextField = p.addTextField(50, 0, rows.size(), 1, 1);
            JPanel spacer = p.addPanel(new JPanelHelper(), GridBagConstraints.LAST_LINE_END, rows.size() + 1, 1, 1);
            entryTextField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // System.out.println(entryTextField.getText());
                    if (!entryTextField.getText().equals("") && !entryTextField.getText().replaceAll(" ", "").equals("")) {
                        // System.out.println("what");
                        GridBagConstraints tGBC = p.gridBagLayout.getConstraints(entryTextField);
                        ArrayList<JComponent> newRow = new ArrayList<>();
                        
                        newRow.add(addEditableLabel(p, entryTextField.getText() + ":", 0, tGBC.gridy));
                        if (c != Categories.PRODUCT_CATEGORY) {
                            newRow.add(addEditableLabel(p, entryTextField.getText(), 1, tGBC.gridy));
                        }
                        newRow.add(addSynonymTextField(p, newRow.size(), tGBC.gridy));

                        c.addEntry(entryTextField.getText());
                        componentsByIndex.get(p).add(tGBC.gridy, newRow);
                        entryTextField.setText("");
                        p.setGBCY(entryTextField, tGBC.gridy + 1);
                        p.setGBCY(spacer, tGBC.gridy + 2);
                        p.revalidate();
                    }
                }
            });
            ArrayList<JComponent> row = new ArrayList<>();
            row.add(entryTextField);
            rows.add(row);

            p.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    System.out.println(selectedLabel);
                }
            });
            p.setFocusable(true);

            //p.setGBCAnchor(t, GridBagConstraints.NORTHWEST);
            //System.out.println(p.gridBagLayout.getConstraints(p.addPanel(new JPanelHelper(), GridBagConstraints.LAST_LINE_END, GridBagConstraints.LAST_LINE_END, 1, 1)).gridy);
            this.dictPanels.put(p, c);
            componentsByIndex.put(p, rows);
            //p.setBackground(new Color(0, 0, 0));
            addTab(c.name(), new JScrollPane(p));
        }
    }

    /**
     * Gets the singleton instance of {@code DictionaryTab}.
     *
     * @return the singleton instance of {@code DictionaryTab}
     */
    public static DictionaryTab getInstance() {
        if (DictionaryTab.instance == null) {
            DictionaryTab.instance = new DictionaryTab();
        }
        return DictionaryTab.instance;
    }
}

