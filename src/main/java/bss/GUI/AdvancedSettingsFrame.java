package bss.GUI;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * A frame that provides advanced settings options for keyword generation.
 * <p>
 * This class presents a user interface with multiple tabs for configuring advanced settings related 
 * to keyword generation. It includes tabs for Dictionary, Product List, and List Info.
 * </p>
 */
public class AdvancedSettingsFrame extends JFrame {
    /**
     * Singleton instance of {@code AdvancedSettingsFrame}.
     */
    static AdvancedSettingsFrame instance = null;

    private AdvancedSettingsFrame() {
        super("Advanced Settings");
        CardLayout cardLayout = new CardLayout();
        setLayout(cardLayout);
 
        setSize(1200, 900);
        //setPreferredSize(new Dimension(800, 600));
        //setMaximumSize(new Dimension(2000, 2000));
        setMinimumSize(new Dimension(300, 300));

        initComponents();
 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //pack();
        setVisible(false);
    }

    private void initComponents() {
        JTabbedPane tabPane = new JTabbedPane();

        tabPane.addTab("Dictionary", DictionaryTab.getInstance());
        // tabPane.addTab("Unclassified Tokens", UnclassifiedTokensTab.getInstance());
        //tabPane.addTab("Token Search", TokenSearchTab.getInstance());
        tabPane.addTab("Product List", ProductListTab.getInstance());
        tabPane.addTab("List Info", new JScrollPane(ListInfoTab.getInstance()));
        add(tabPane);
    }

    /**
     * Gets the singleton instance of {@code AdvancedSettingsFrame}.
     *
     * @return the singleton instance of {@code AdvancedSettingsFrame}
     */
    public static AdvancedSettingsFrame getInstance() {
        if (AdvancedSettingsFrame.instance == null) {
            AdvancedSettingsFrame.instance = new AdvancedSettingsFrame();
        }
        return AdvancedSettingsFrame.instance;
    }
}
