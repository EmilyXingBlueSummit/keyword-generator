package bss.GUI;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import bss.Data.DataAccess;

/**
 * JPanelHelper is a utility class that extends {@link JPanel} and provides convenient methods for
 * adding and managing components using a {@link GridBagLayout}. It includes methods for adding and configuring
 * various Swing components such as labels, buttons, text fields, and panels.
 */
public class JPanelHelper extends JPanel {

    /** Layout manager for this panel */
    GridBagLayout gridBagLayout;

    /** Insets for component margins */
    Insets insets;

    /** Mapping of component types to their weightx values in GridBagConstraints */
    HashMap<ComponentType, Double> weightx;

    /** Mapping of component types to their weighty values in GridBagConstraints */
    HashMap<ComponentType, Double> weighty;

    /** Mapping of component types to their fill values in GridBagConstraints */
    HashMap<ComponentType, Integer> fill;

    /** Mapping of component types to their anchor values in GridBagConstraints */
    HashMap<ComponentType, Integer> anchor;

    /** Default font used in the panel */
    Font font;

    /** Slightly smaller font used for some components */
    Font smallerFont;

    /** Even smaller font used for other components */
    Font smallestFont;

    /**
     * Enum for component types used to configure layout constraints.
     */
    public enum ComponentType {
        PANEL, ACCORDIONPANEL, LABEL, LABELLEFT, LABELRIGHT, TEXTFIELD, BUTTON, ACCORDIONTOGGLE, COMBOBOX, SLIDER, SCROLLPANE, TABBEDPANE
    }

    /**
     * Initializes JPanelHelper with a GridBagLayout and default constraints.
     */
    public JPanelHelper() {
        this.gridBagLayout = new GridBagLayout();
        setLayout(this.gridBagLayout);
        setDefaultGBC();
        JLabel l = addLabel("a", 0, 0, 1, 1);
        this.font = l.getFont();
        this.smallerFont = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
        this.smallestFont = new Font(font.getName(), font.getStyle(), font.getSize() - 2);
        remove(l);
        initComponents();
    }

    /**
     * Adds a component to the panel with specified layout constraints.
     * 
     * @param c the component to add
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @param weightx the weight in x direction
     * @param weighty the weight in y direction
     * @param fill how the component should fill its cell
     * @return the added component
     */
    protected JComponent add(JComponent c, int x, int y, int width, int height, double weightx, double weighty, int fill) {
        this.gridBagLayout.setConstraints(c, getComponentGBC(x, y, width, height, weightx, weighty, fill));
        add(c);
        return c;
    }

    /**
     * Adds a JPanelHelper instance to the panel with default layout constraints.
     * 
     * @param p the JPanelHelper to add
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the added JPanelHelper
     */
    protected JPanelHelper addPanel(JPanelHelper p, int x, int y, int width, int height) {
        this.gridBagLayout.setConstraints(p, getComponentGBC(ComponentType.PANEL, x, y, width, height));
        add(p);
        return p;
    }

    /**
     * Adds a JPanelHelper instance to the panel with custom layout constraints.
     * 
     * @param p the JPanelHelper to add
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @param weightx the weight in x direction
     * @param weighty the weight in y direction
     * @param fill how the component should fill its cell
     * @return the added JPanelHelper
     */
    protected JPanelHelper addPanel(JPanelHelper p, int x, int y, int width, int height, double weightx, double weighty, int fill) {
        this.gridBagLayout.setConstraints(p, getComponentGBC(x, y, width, height, weightx, weighty, fill));
        add(p);
        return p;
    }

    /**
     * Adds an accordion panel to the panel with default layout constraints.
     * 
     * @param p the JPanelHelper to add
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the added JPanelHelper
     */
    protected JPanelHelper addAccordionPanel(JPanelHelper p, int x, int y, int width, int height) {
        this.gridBagLayout.setConstraints(p, getComponentGBC(ComponentType.ACCORDIONPANEL, x, y, width, height));
        add(p);
        return p;
    }

    /**
     * Adds a JLabel to the panel with default layout constraints.
     * 
     * @param s the text of the label
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the added JLabel
     */
    protected JLabel addLabel(String s, int x, int y, int width, int height) {
        JLabel l = new JLabel(s);
        this.gridBagLayout.setConstraints(l, getComponentGBC(ComponentType.LABEL, x, y, width, height));
        add(l);
        return l;
    }

    /**
     * Adds a left-aligned JLabel to the panel with default layout constraints.
     * 
     * @param s the text of the label
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the added JLabel
     */
    protected JLabel addLabelLeft(String s, int x, int y, int width, int height) {
        JLabel l = new JLabel(s);
        GridBagConstraints gbc = getComponentGBC(ComponentType.LABELLEFT, x, y, width, height);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        this.gridBagLayout.setConstraints(l, gbc);
        add(l);
        return l;
    }

    /**
     * Adds a right-aligned JLabel to the panel with default layout constraints.
     * 
     * @param s the text of the label
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the added JLabel
     */
    protected JLabel addLabelRight(String s, int x, int y, int width, int height) {
        JLabel l = new JLabel(s);
        GridBagConstraints gbc = getComponentGBC(ComponentType.LABELRIGHT, x, y, width, height);
        gbc.anchor = GridBagConstraints.EAST;
        this.gridBagLayout.setConstraints(l, gbc);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        add(l);
        return l;
    }

    /**
     * Adds a JTextField to the panel with default layout constraints.
     * 
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the added JTextField
     */
    protected JTextField addTextField(int x, int y, int width, int height) {
        JTextField t = new JTextField();
        this.gridBagLayout.setConstraints(t, getComponentGBC(ComponentType.TEXTFIELD, x, y, width, height));
        add(t);
        return t;
    }

    /**
     * Adds a JTextField with a fixed width to the panel.
     * 
     * @param boxWidth the fixed width of the text field
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the added JTextField
     */
    protected JTextField addTextField(int boxWidth, int x, int y, int width, int height) {
        JTextField t = new JTextField();
        setFixedComponentSize(t, boxWidth, t.getPreferredSize().height);
        this.gridBagLayout.setConstraints(t, getComponentGBC(ComponentType.TEXTFIELD, x, y, width, height));
        add(t);
        return t;
    }

    /**
     * Adds a JButton to the panel with default layout constraints.
     * 
     * @param s the text of the button
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the added JButton
     */
    protected JButton addButton(String s, int x, int y, int width, int height) {
        JButton b = new JButton(s);
        this.gridBagLayout.setConstraints(b, getComponentGBC(ComponentType.BUTTON, x, y, width, height));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setFocusPainted(false);
        add(b);
        return b;
    }

    /**
     * Adds an accordion toggle button with an image icon to the panel.
     * 
     * @param imageFile the path to the image file
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the added JButton
     */
    protected JButton addAccordionToggle(String imageFile, int x, int y, int width, int height) {
        JButton b = new JButton();
        setButtonIcon(b, imageFile);
        this.gridBagLayout.setConstraints(b, getComponentGBC(ComponentType.ACCORDIONTOGGLE, x, y, width, height));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        add(b);
        return b;
    }

    /**
     * Sets an icon for a button.
     * 
     * @param b the button to set the icon for
     * @param imageFile the path to the image file
     */
    protected void setButtonIcon(JButton b, String imageFile) {
        ImageIcon icon = DataAccess.getImageIcon(imageFile);
        if (icon != null) {
            b.setIcon(icon);
            setFixedComponentSize(b, icon.getIconWidth(), icon.getIconHeight());
        }
    }

    /**
     * Adds a JComboBox with items to the panel.
     * 
     * @param items the list of items to add
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the added JComboBox
     */
    protected JComboBox<Object> addComboBox(Object items, int x, int y, int width, int height) {
        JComboBox<Object> c = new JComboBox<>();
        c.addItem("");
        for (Object item : (List<Object>) items) {
            c.addItem(item);
        }
        this.gridBagLayout.setConstraints(c, getComponentGBC(ComponentType.COMBOBOX, x, y, width, height));
        //c.setEditable(true);
        c.setMinimumSize(new Dimension(100, 25));
        add(c);
        return c;
    }

    /**
     * Adds an empty JComboBox to the panel with default layout constraints.
     * 
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the added JComboBox
     */
    protected JComboBox<Object> addComboBox(int x, int y, int width, int height) {
        JComboBox<Object> c = new JComboBox<>();
        c.addItem("");
        this.gridBagLayout.setConstraints(c, getComponentGBC(ComponentType.COMBOBOX, x, y, width, height));
        c.setPreferredSize(new Dimension(113, 25));
        c.setMinimumSize(new Dimension(113, 25));
        add(c);
        return c;
    }

    /**
     * Adds a JSlider to the panel.
     * 
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the added JSlider
     */
    protected JSlider addSlider(int x, int y, int width, int height) {
        JSlider s = new JSlider(0, 100);
        this.gridBagLayout.setConstraints(s, getComponentGBC(ComponentType.SLIDER, x, y, width, height));
        add(s);
        return s;
    }

    /**
     * Adds a JScrollPane containing a component to the panel.
     * 
     * @param c the component to add inside the JScrollPane
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the added JScrollPane
     */
    protected JScrollPane addScrollPane(JComponent c, int x, int y, int width, int height) {
        JScrollPane s = new JScrollPane(c);
        this.gridBagLayout.setConstraints(s, getComponentGBC(ComponentType.SCROLLPANE, x, y, width, height));
        add(s);
        return s;
    }

    /**
     * Adds a JTabbedPane to the panel.
     * 
     * @param t the JTabbedPane to add
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the added JTabbedPane
     */
    protected JTabbedPane addTabbedPane(JTabbedPane t, int x, int y, int width, int height) {
        this.gridBagLayout.setConstraints(t, getComponentGBC(ComponentType.TABBEDPANE, x, y, width, height));
        add(t);
        return t;
    }

    /**
     * Sets a fixed size for a component.
     * 
     * @param c the component to resize
     * @param width the width to set
     * @param height the height to set
     */
    public void setFixedComponentSize(JComponent c, int width, int height) {
        Dimension d = new Dimension(width, height);
        c.setPreferredSize(d);
        c.setMaximumSize(d);
        c.setMinimumSize(d);
    }

    /**
     * Gets GridBagConstraints for a component based on type and position.
     * 
     * @param componentType the type of the component
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @return the GridBagConstraints
     */
    private GridBagConstraints getComponentGBC(ComponentType componentType, int x, int y, int width, int height) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.insets = this.insets;
        gbc.weightx = this.weightx.get(componentType);
        gbc.weighty = this.weighty.get(componentType);
        gbc.fill = this.fill.get(componentType);
        return gbc;
    }

    /**
     * Gets GridBagConstraints for a component with custom weights and fill.
     * 
     * @param x the x position in the grid
     * @param y the y position in the grid
     * @param width the number of columns the component occupies
     * @param height the number of rows the component occupies
     * @param weightx the weight in x direction
     * @param weighty the weight in y direction
     * @param fill how the component should fill its cell
     * @return the GridBagConstraints
     */
    private GridBagConstraints getComponentGBC(int x, int y, int width, int height, double weightx, double weighty, int fill) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.insets = this.insets;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.fill = fill;
        return gbc;
    }

    /**
     * Sets the x position in GridBagConstraints for a component.
     * 
     * @param c the component
     * @param x the x position
     */
    protected void setGBCX(JComponent c, int x) {
        GridBagConstraints cGBC = gridBagLayout.getConstraints(c);
        cGBC.gridx = x;
        gridBagLayout.setConstraints(c, cGBC);
    }

    /**
     * Sets the y position in GridBagConstraints for a component.
     * 
     * @param c the component
     * @param y the y position
     */
    protected void setGBCY(JComponent c, int y) {
        GridBagConstraints cGBC = gridBagLayout.getConstraints(c);
        cGBC.gridy = y;
        gridBagLayout.setConstraints(c, cGBC);
    }

    /**
     * Sets both x and y positions in GridBagConstraints for a component.
     * 
     * @param c the component
     * @param x the x position
     * @param y the y position
     */
    protected void setGBCPos(JComponent c, int x, int y) {
        GridBagConstraints cGBC = gridBagLayout.getConstraints(c);
        cGBC.gridx = x;
        cGBC.gridy = y;
        gridBagLayout.setConstraints(c, cGBC);
    }

    /**
     * Sets the anchor in GridBagConstraints for a component.
     * 
     * @param c the component
     * @param anchor the anchor position
     */
    protected void setGBCAnchor(JComponent c, int anchor) {
        GridBagConstraints cGBC = gridBagLayout.getConstraints(c);
        cGBC.anchor = anchor;
        gridBagLayout.setConstraints(c, cGBC);
    }

    /**
     * Sets the weightx in GridBagConstraints for a component.
     * 
     * @param c the component
     * @param weightx the weight in x direction
     */
    protected void setGBCWeightX(JComponent c, double weightx) {
        GridBagConstraints cGBC = gridBagLayout.getConstraints(c);
        cGBC.weightx = weightx;
        gridBagLayout.setConstraints(c, cGBC);
    }

    /**
     * Sets the weighty in GridBagConstraints for a component.
     * 
     * @param c the component
     * @param weighty the weight in y direction
     */
    protected void setGBCWeightY(JComponent c, double weighty) {
        GridBagConstraints cGBC = gridBagLayout.getConstraints(c);
        cGBC.weighty = weighty;
        gridBagLayout.setConstraints(c, cGBC);
    }

    /**
     * Sets the fill in GridBagConstraints for a component.
     * 
     * @param c the component
     * @param fill how the component should fill its cell
     */
    protected void setGBCFill(JComponent c, int fill) {
        GridBagConstraints cGBC = gridBagLayout.getConstraints(c);
        cGBC.fill = fill;
        gridBagLayout.setConstraints(c, cGBC);
    }

    /**
     * Sets the insets in GridBagConstraints for a component.
     * 
     * @param c the component
     * @param insets the insets to set
     */
    protected void setGBCInsets(JComponent c, Insets insets) {
        GridBagConstraints cGBC = gridBagLayout.getConstraints(c);
        cGBC.insets = insets;
        gridBagLayout.setConstraints(c, cGBC);
    }

    /**
     * Sets default GridBagConstraints values for various component types.
     */
    protected void setDefaultGBC() {
        this.insets = new Insets(3, 5, 3, 5);

        this.weightx = new HashMap<>();
        this.weightx.put(ComponentType.PANEL, 1.0);
        this.weightx.put(ComponentType.ACCORDIONPANEL, 0.0);
        this.weightx.put(ComponentType.LABEL, 0.0);
        this.weightx.put(ComponentType.LABELLEFT, 1.0);
        this.weightx.put(ComponentType.LABELRIGHT, 1.0);
        this.weightx.put(ComponentType.TEXTFIELD, 1.0);
        this.weightx.put(ComponentType.BUTTON, 0.0);
        this.weightx.put(ComponentType.ACCORDIONTOGGLE, 0.0);
        this.weightx.put(ComponentType.COMBOBOX, 0.0);
        this.weightx.put(ComponentType.SLIDER, 1.0);
        this.weightx.put(ComponentType.SCROLLPANE, 50.0);
        this.weightx.put(ComponentType.TABBEDPANE, 50.0);
        
        this.weighty = new HashMap<>();
        this.weighty.put(ComponentType.PANEL, 1.0);
        this.weighty.put(ComponentType.ACCORDIONPANEL, 0.0);
        this.weighty.put(ComponentType.LABEL, 0.0);
        this.weighty.put(ComponentType.LABELLEFT, 1.0);
        this.weighty.put(ComponentType.LABELRIGHT, 0.0);
        this.weighty.put(ComponentType.TEXTFIELD, 0.0);
        this.weighty.put(ComponentType.BUTTON, 0.0);
        this.weighty.put(ComponentType.ACCORDIONTOGGLE, 0.0);
        this.weighty.put(ComponentType.COMBOBOX, 0.0);
        this.weighty.put(ComponentType.SLIDER, 0.0);
        this.weighty.put(ComponentType.SCROLLPANE, 50.0);
        this.weighty.put(ComponentType.TABBEDPANE, 50.0);
        
        int none = GridBagConstraints.NONE;
        int both = GridBagConstraints.BOTH;
        this.fill = new HashMap<>();
        this.fill.put(ComponentType.PANEL, both);
        this.fill.put(ComponentType.ACCORDIONPANEL, both);
        this.fill.put(ComponentType.LABEL, both);
        this.fill.put(ComponentType.LABELLEFT, none);
        this.fill.put(ComponentType.LABELRIGHT, both);
        this.fill.put(ComponentType.TEXTFIELD, none);
        this.fill.put(ComponentType.BUTTON, none);
        this.fill.put(ComponentType.ACCORDIONTOGGLE, none);
        this.fill.put(ComponentType.COMBOBOX, none);
        this.fill.put(ComponentType.SLIDER, both);
        this.fill.put(ComponentType.SCROLLPANE, both);
        this.fill.put(ComponentType.TABBEDPANE, both);
    }

    /**
     * Initializes components. Override in subclasses to add specific components.
     */
    protected void initComponents() {
        
    }
}

