package bss.GUI;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import com.toedter.calendar.JDateChooser;

import bss.Data.FileUpload;

/**
 * A frame that allows users to select a file to upload and configure upload parameters.
 * <p>
 * This class provides a user interface for selecting a report type, specifying a date range, 
 * and choosing the file to upload. The frame includes options for different report types and 
 * updates the expected file type and required columns based on the selection.
 * </p>
 */
public class FileUploadFrame extends JFrame {

    /**
     * Singleton instance of {@code FileUploadFrame}.
     */
    static FileUploadFrame instance = null;

    /**
     * The file upload configuration based on the selected report type.
     */
    FileUpload fileUpload = null;

    /**
     * ComboBox for selecting the report type.
     */
    JComboBox<Object> reportTypeBox;

    /**
     * Label displaying the expected file type.
     */
    JLabel fileTypeLabel;

    /**
     * List of required columns for the selected report type.
     */
    JList<String> requiredColumnsList;

    /**
     * Date chooser for selecting the start date of the report period.
     */
    JDateChooser startDateChooser;

    /**
     * Date chooser for selecting the end date of the report period.
     */
    JDateChooser endDateChooser;

    /**
     * ScrollPane containing the list of required columns.
     */
    JScrollPane requiredColumnsScrollPane;

    private FileUploadFrame() {
        super("Upload File");
        CardLayout cardLayout = new CardLayout();
        setLayout(cardLayout);
 
        setSize(400, 300);
        //setPreferredSize(new Dimension(800, 600));
        //setMaximumSize(new Dimension(2000, 2000));
        setMinimumSize(new Dimension(200, 200));

        initComponents();
 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //pack();
        setVisible(false);
    }

    private void initComponents() {
        JPanelHelper p = new JPanelHelper();
        p.addLabel("Select a report type: ", 0, 0, 1, 1);
        p.weightx.replace(JPanelHelper.ComponentType.COMBOBOX, 50.0);
        this.reportTypeBox = p.addComboBox(Arrays.asList(new String[] {"Keyword Search Term Report", "All Campaigns Report"}), 1, 0, 1, 1);
        p.setGBCFill(this.reportTypeBox, GridBagConstraints.HORIZONTAL);
        this.reportTypeBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension d = requiredColumnsScrollPane.getPreferredSize();
                switch ((String) reportTypeBox.getSelectedItem()) {
                    case "Keyword Search Term Report":
                        fileUpload = FileUpload.KEYWORD_SEARCH_TERM;
                        break;
                    case "All Campaigns Report":
                        fileUpload = FileUpload.ALL_CAMPAIGNS;
                        break;
                    default:
                        fileUpload = FileUpload.NONE;
                        break;
                }
                fileTypeLabel.setText(fileUpload.fileType);
                requiredColumnsList.setListData(fileUpload.columns);
                requiredColumnsScrollPane.setPreferredSize(d);
            }
        });

        JPanelHelper dateSelectPanel = new JPanelHelper();
        dateSelectPanel.insets = new Insets(2, 2, 2, 2);
        dateSelectPanel.addLabelRight("Start Date: ", 0, 0, 1, 1);
        this.startDateChooser = new JDateChooser();
        dateSelectPanel.add(this.startDateChooser, 1, 0, 1, 1, 50, 0, GridBagConstraints.BOTH);
        dateSelectPanel.addLabelRight("End Date: ", 0, 1, 1, 1);
        this.endDateChooser = new JDateChooser();
        dateSelectPanel.add(this.endDateChooser, 1, 1, 1, 1, 50, 0, GridBagConstraints.BOTH);

        p.addLabel("Select date range:", 0, 1, 1, 1);
        p.addPanel(dateSelectPanel, 1, 1, 1, 2);

        p.addLabel("Expected File Type: ", 0, 3, 1, 1);
        this.fileTypeLabel = p.addLabel(" ", 1, 3, 1, 1);

        p.addLabel("Required Columns: ", 0, 4, 1, 1);
        this.requiredColumnsList = new JList<>();
        this.requiredColumnsScrollPane = p.addScrollPane(this.requiredColumnsList, 1, 4, 1, 2);

        JButton uploadButton = p.addButton("Upload file", 0, 5, 1, 1);
        p.setGBCAnchor(uploadButton, GridBagConstraints.PAGE_END);
        p.setGBCInsets(uploadButton, new Insets(5, 5, 5, 5));
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileUpload != FileUpload.NONE) {
                    System.out.println("upload");
                    JFileChooser fileChooser = new JFileChooser();
                    int res = fileChooser.showOpenDialog(null);
                    if (res == JFileChooser.APPROVE_OPTION) {
                        fileUpload.read(new File(fileChooser.getSelectedFile().getAbsolutePath()));
                    }
                }
            }
        });

        add(p);
    }

    /**
     * Gets the singleton instance of {@code FileUploadFrame}.
     *
     * @return the singleton instance of {@code FileUploadFrame}
     */
    public static FileUploadFrame getInstance() {
        if (FileUploadFrame.instance == null) {
            FileUploadFrame.instance = new FileUploadFrame();
        }
        return FileUploadFrame.instance;
    }
}
