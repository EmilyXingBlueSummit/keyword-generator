package bss.GUI;

import java.awt.GridBagConstraints;
import java.util.ArrayList;

import bss.Keywords.Categories;
import bss.Keywords.KeywordMatch;

/**
 * A tab in {@code AdvancedSettings} that displays the generated keyword list from {@code KeywordListPanel}.
 * It provides detailed information on each keyword, including token categorization and TF (Term Frequency) values.
 */
public class ListInfoTab extends JPanelHelper {

    /**
     * Singleton instance of {@code ListInfoTab}.
     */
    static ListInfoTab instance = null;

    private ListInfoTab() {
        super();
    }

    /**
     * Updates the displayed list with keyword matches, including their scores, tokens, categorized tokens, 
     * and TF values.
     *
     * @param matchedList a list of {@code KeywordMatch} objects to display
     */
    public void updateList(ArrayList<KeywordMatch> matchedList) {
        if (matchedList.size() > 1000) {
            matchedList = new ArrayList<>(matchedList.subList(0, 1000));
        }
        
        removeAll();
        addLabel("% Match", 0, 0, 1, 1);
        addLabel("Tokens", 1, 0, 1, 1);
        addLabel("Categorized Tokens", 2, 0, 1, 1);
        addLabel("TF", 10, 0, 1, 1);
        addLabel("Keyword", 11, 0, 1, 1);


        int n = 1;
        for (KeywordMatch kw : matchedList) {
            JPanelHelper matchPanel = new JPanelHelper();
            matchPanel.addLabelLeft(String.format("%.2f", kw.getScore() * 100) + "%", 0, 0, 1, 1);
            JPanelHelper keywordPanel = new JPanelHelper();
            keywordPanel.addLabelLeft(kw.getKeyword(), 0, 0, 1, 1);

            JPanelHelper tokenPanel = new JPanelHelper();
            int j = 0;
            for (String token : kw.getTokens()) {
                tokenPanel.addLabelLeft(token, 0, j, 1, 1);
                j++;
            }

            ArrayList<JPanelHelper> categorizedTokensPanels = new ArrayList<>();
            int i = 0;
            for (Categories c : kw.getCategorizedTokens().keySet()) {
                JPanelHelper ctPanel = new JPanelHelper();
                ctPanel.addLabelLeft(c.name(), i, 0, 1, 1);
                j = 1;
                for (String token : kw.getCategorizedTokens().get(c)) {
                    ctPanel.addLabelLeft(token, i, j, 1, 1);
                    j++;
                }
                categorizedTokensPanels.add(ctPanel);
                i++;
            }

            j = 0;
            JPanelHelper tfPanel = new JPanelHelper();
            for (String token : kw.getTF().keySet()) {
                tfPanel.addLabelLeft(token, 0, j, 1, 1);
                tfPanel.addLabelRight(String.format("%.2f", kw.getTF().get(token)), 1, j, 1, 1);
                j++;
            }

            addPanel(matchPanel, 0, n, 1, 1);
            addPanel(tokenPanel, 1, n, 1, 1);
            setGBCFill(tokenPanel, GridBagConstraints.HORIZONTAL);
            setGBCAnchor(tokenPanel, GridBagConstraints.NORTHEAST);

            i = 2;
            for (JPanelHelper ctPanel : categorizedTokensPanels) {
                addPanel(ctPanel, i, n, 1, 1);
                setGBCFill(ctPanel, GridBagConstraints.HORIZONTAL);
                setGBCAnchor(ctPanel, GridBagConstraints.NORTHEAST);
                i++;
            }
            addPanel(tfPanel, 10, n, 1, 1);
            setGBCFill(tfPanel, GridBagConstraints.HORIZONTAL);
            setGBCAnchor(tfPanel, GridBagConstraints.NORTHEAST);
            addPanel(keywordPanel, 11, n, 1, 1);
            n++;
        }

        //addPanel(new JPanelHelper(), GridBagConstraints.LAST_LINE_END, GridBagConstraints.LAST_LINE_END, 1, 1);
        revalidate();
    }

    @Override
    protected void initComponents() {
        addLabel("% Match", 0, 0, 1, 1);
        addLabel("Tokens", 1, 0, 1, 1);
        addLabel("Categorized Tokens", 2, 0, 1, 1);
        addLabel("TF", 10, 0, 1, 1);
        addLabel("Keyword", 11, 0, 1, 1);

        addPanel(new JPanelHelper(), 0, 1, 1, GridBagConstraints.REMAINDER);
        addPanel(new JPanelHelper(), 1, 1, 1, GridBagConstraints.REMAINDER);
        addPanel(new JPanelHelper(), 2, 1, 1, GridBagConstraints.REMAINDER);
        addPanel(new JPanelHelper(), 3, 1, 1, GridBagConstraints.REMAINDER);
        addPanel(new JPanelHelper(), 4, 1, 1, GridBagConstraints.REMAINDER);
    }

    /**
     * Gets the singleton instance of {@code ListInfoTab}.
     *
     * @return the singleton instance of {@code ListInfoTab}
     */
    public static ListInfoTab getInstance() {
        if (ListInfoTab.instance == null) {
            ListInfoTab.instance = new ListInfoTab();
        }
        return ListInfoTab.instance;
    }
}
