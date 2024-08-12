package bss.GUI;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.HashMap;

import bss.Keywords.KeywordMatch;

/**
 * A panel that displays a list of generated keywords and their associated data.
 */
public class KeywordListPanel extends JPanelHelper {

    /**
     * Singleton instance of {@code KeywordListPanel}.
     */
    static KeywordListPanel instance = null;

    /**
     * A map that stores the columns of the keyword list panel.
     * Each entry maps a column header to its corresponding {@code JPanelHelper}.
     */
    HashMap<String, JPanelHelper> columns;

    private KeywordListPanel() {
        super();
    }

    /**
     * Updates the displayed list of keywords with the given matches.
     *
     * @param matchedList the list of {@code KeywordMatch} objects to display
     */
    public void updateList(ArrayList<KeywordMatch> matchedList) {
        for (JPanelHelper column : this.columns.values()) {
            column.removeAll();
        }

        int i = 1;
        for (KeywordMatch kw : matchedList) {
            //if (kw.getScore() >= 0) {
                this.columns.get("% Match").addLabel(String.format("%.2f", kw.getScore() * 100) + "%", 0, i, 1, 1);
                this.columns.get("Orders").addLabel(String.format("%d", kw.getOrders()), 0, i, 1, 1);
                this.columns.get("CVR").addLabel(String.format("%.0f", kw.getCVR() * 100) + "%", 0, i, 1, 1);
                this.columns.get("Keyword").addLabel(kw.getKeyword(), 0, i, 1, 1);
                i++;
            //}
        }
        revalidate();
    }

    @Override
    protected void initComponents() {
        addLabel("% Match", 0, 0, 1, 1);
        addLabel("Orders", 1, 0, 1, 1);
        addLabel("CVR", 2, 0, 1, 1);
        addLabel("Keyword", 3, 0, 1, 1);

        this.columns = new HashMap<>();
        this.columns.put("% Match", addPanel(new JPanelHelper(), 0, 1, 1, GridBagConstraints.REMAINDER));
        this.columns.put("Orders", addPanel(new JPanelHelper(), 1, 1, 1, GridBagConstraints.REMAINDER));
        this.columns.put("CVR", addPanel(new JPanelHelper(), 2, 1, 1, GridBagConstraints.REMAINDER));
        this.columns.put("Keyword", addPanel(new JPanelHelper(), 3, 1, 1, GridBagConstraints.REMAINDER));
    }

    /**
     * Gets the singleton instance of {@code KeywordListPanel}.
     *
     * @return the singleton instance of {@code KeywordListPanel}
     */
    public static KeywordListPanel getInstance() {
        if (KeywordListPanel.instance == null) {
            KeywordListPanel.instance = new KeywordListPanel();
        }
        return KeywordListPanel.instance;
    }
}
