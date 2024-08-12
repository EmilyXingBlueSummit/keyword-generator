package bss.GUI;

/**
 * A template class for creating new panel types.
 * <p>
 * This class extends {@code JPanelHelper} and is intended as a base for other panel implementations.
 * It currently does not add any functionality beyond what is provided by {@code JPanelHelper}.
 * </p>
 */
public class PanelTemplate extends JPanelHelper {
    static PanelTemplate instance = null;

    private PanelTemplate() {
        super();
    }

    @Override
    protected void initComponents() {

    }

    public static PanelTemplate getInstance() {
        if (PanelTemplate.instance == null) {
            PanelTemplate.instance = new PanelTemplate();
        }
        return PanelTemplate.instance;
    }
}
