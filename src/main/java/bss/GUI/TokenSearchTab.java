package bss.GUI;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JTextField;

import bss.Data.DataAccess;
import bss.Keywords.Keyword;
import bss.Keywords.SearchTerm;

/**
 * A tab in the {@code AdvancedSettingsFrame} that allows the user to search for tokens and displays 
 * products and search terms that contain the specified token.
 */
public class TokenSearchTab extends JPanelHelper {

    /**
     * Singleton instance of {@code TokenSearchTab}.
     */
    static TokenSearchTab instance = null;

    /**
     * Panel that displays the list of products containing the searched token.
     */
    JPanelHelper productColumn;

    /**
     * Panel that displays the list of search terms containing the searched token.
     */
    JPanelHelper searchTermColumn;

    private TokenSearchTab() {
        super();
    }

    @Override
    protected void initComponents() {
        this.productColumn = new JPanelHelper();
        setGBCFill(this.productColumn, GridBagConstraints.HORIZONTAL);
        setGBCAnchor(this.productColumn, GridBagConstraints.NORTHEAST);
        this.searchTermColumn = new JPanelHelper();
        setGBCFill(this.searchTermColumn, GridBagConstraints.HORIZONTAL);
        setGBCAnchor(this.searchTermColumn, GridBagConstraints.NORTHEAST);

        JTextField searchField = addTextField(100, 0, 0, 2, 1);
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productColumn.removeAll();
                searchTermColumn.removeAll();

                String s = searchField.getText();
                LinkedHashMap<String, Keyword> amazonList = DataAccess.getAmazonList();
                int j = 0;
                for (String productID : amazonList.keySet()) {
                    boolean hasLabel = false;

                    if (amazonList.get(productID).getTokens().contains(s)) {
                        productColumn.addLabelLeft(productID, 0, j, 1, 1);
                        productColumn.addLabelLeft(amazonList.get(productID).getKeyword(), 1, j, 1, 1);
                        j++;
                    }
                }

                ArrayList<SearchTerm> searchTermList = DataAccess.getSearchTermList("keywordList");
                j = 0;
                for (Keyword kw : searchTermList) {
                    if (kw.getTokens().contains(s)) {
                        searchTermColumn.addLabelLeft(kw.getKeyword(), 0, j, 1, 1);
                        j++;
                    }
                }

                productColumn.revalidate();
                searchTermColumn.revalidate();
                revalidate();
            }
        });
        addLabel("Product Descriptions", 0, 1, 1, 1);
        addLabel("User Searches", 1, 1, 1, 1);
        addScrollPane(this.productColumn, 0, 2, 1, 1);
        addScrollPane(this.searchTermColumn, 1, 2, 1, 1);
    }

    /**
     * Gets the singleton instance of {@code TokenSearchTab}.
     *
     * @return the singleton instance of {@code TokenSearchTab}
     */
    public static TokenSearchTab getInstance() {
        if (TokenSearchTab.instance == null) {
            TokenSearchTab.instance = new TokenSearchTab();
        }
        return TokenSearchTab.instance;
    }
}
