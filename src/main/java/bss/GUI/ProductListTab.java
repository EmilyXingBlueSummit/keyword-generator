package bss.GUI;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JButton;

import bss.Data.DataAccess;
import bss.Keywords.Keyword;
import bss.Keywords.KeywordDictionary;
import bss.Keywords.Tokenizer;
import bss.Keywords.Categories;

/**
 * A tab in the {@code AdvancedSettingsFrame} that displays a list of all products with detailed
 * information, including how their tokens have been categorized.
 */
public class ProductListTab extends JPanelHelper {

    /**
     * Singleton instance of {@code ProductListTab}.
     */
    static ProductListTab instance = null;

    /**
     * Panel used to display product information.
     */
    JPanelHelper p;

    /**
     * Button to trigger the retokenization of all products.
     */
    JButton retokenizeButton;

    private ProductListTab() {
        super();
    }

    /**
     * Updates the list of products, including their tokens and categorized tokens, in the display panel.
     */
    public void updateList() {
        p.removeAll();
        p.addLabel("Product ID", 0, 0, 1, 1);
        p.addLabel("Tokens", 1, 0, 1, 1);
        p.addLabel("Categorized Tokens", 2, 0, 1, 1);
        p.addLabel("Description", 10, 0, 1, 1);

        LinkedHashMap<String, Keyword> masterList = DataAccess.getAmazonList();

        int n = 1;
        for (String productId : masterList.keySet()) {
            Keyword kw = masterList.get(productId);

            JPanelHelper productIdPanel = new JPanelHelper();
            productIdPanel.addLabelLeft(productId, 0, 0, 1, 1);
            JPanelHelper descriptionPanel = new JPanelHelper();
            descriptionPanel.addLabelLeft(kw.getKeyword(), 0, 0, 1, 1);

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

            p.addPanel(productIdPanel, 0, n, 1, 1);
            p.addPanel(tokenPanel, 1, n, 1, 1);
            p.setGBCFill(tokenPanel, GridBagConstraints.HORIZONTAL);
            p.setGBCAnchor(tokenPanel, GridBagConstraints.NORTHEAST);

            i = 2;
            for (JPanelHelper ctPanel : categorizedTokensPanels) {
                p.addPanel(ctPanel, i, n, 1, 1);
                p.setGBCFill(ctPanel, GridBagConstraints.HORIZONTAL);
                p.setGBCAnchor(ctPanel, GridBagConstraints.NORTHEAST);
                i++;
            }

            p.addPanel(descriptionPanel, i, n, 1, 1);
            n++;
        }
        //p.addPanel(new JPanelHelper(), GridBagConstraints.LAST_LINE_END, GridBagConstraints.LAST_LINE_END, 1, 1);
        revalidate();
    }

    @Override
    protected void initComponents() {
        this.retokenizeButton = addButton("Retokenize", 0, 0, 1, 1);
        setGBCAnchor(this.retokenizeButton, GridBagConstraints.LINE_END);
        this.retokenizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                LinkedHashMap<String, Keyword> amazonList = DataAccess.getAmazonList();
                for (Keyword kw : amazonList.values()) {
                    kw.tokenize();
                    kw.computeTF();
                }
                System.out.println("done");
                DataAccess.writeToJson("amazonTitles", amazonList);
                // KeywordDictionary.getInstance().updateDict();
                Tokenizer.retokenizeAllKeywords();
                updateList();
                setCursor(Cursor.getDefaultCursor());
            }
        });

        this.p = new JPanelHelper();
        p.addLabel(" ", 0, 0, 1, 1);
        updateList();
        addScrollPane(this.p, 0, 1, 1, 1);
    }

    /**
     * Gets the singleton instance of {@code ProductListTab}.
     *
     * @return the singleton instance of {@code ProductListTab}
     */
    public static ProductListTab getInstance() {
        if (ProductListTab.instance == null) {
            ProductListTab.instance = new ProductListTab();
        }
        return ProductListTab.instance;
    }
}