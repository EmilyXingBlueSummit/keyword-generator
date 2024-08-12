package bss;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import bss.Data.DataAccess;
import bss.GUI.MainPanel;
import bss.KeywordGeneration.KeywordGenerator;
import bss.Keywords.Categories;
import bss.Keywords.Entry;

/**
 * Entry point for the application, sets up the main application window
 */
public class Main {
    public static void main(String[] args) {

        DataAccess.getInstance();

        JFrame frame = new JFrame("Keyword Generator");
        CardLayout cardLayout = new CardLayout();
        frame.setLayout(cardLayout);

        MainPanel mainPanel = MainPanel.getInstance();
        frame.add(mainPanel);
 
        frame.setSize(800, 650);
        //frame.setPreferredSize(new Dimension(800, 600));
        //frame.setMaximumSize(new Dimension(2000, 2000));
        frame.setMinimumSize(new Dimension(500, 500));
 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.pack();
        frame.setVisible(true);

        KeywordGenerator.getInstance();

        // System.out.println(DataAccess.getTestData());
        
        // LinkedHashMap<Category, ListOrderedMap<String, ArrayList<String>>> dict = new LinkedHashMap (KeywordDictionary.getInstance().getDict());
        // for (Category c : dict.keySet()) {
        //     ListOrderedMap<String, ArrayList<String>> dictc = new ListOrderedMap<>();
        //     dictc.putAll(dict.get(c));
        //     for (String entry : dictc.keyList()) {
                
        //         if (c != Category.PRODUCT_CATEGORY) {
        //             String entryLemma = Tokenizer.replaceLemmas(entry);
        //             if (!entryLemma.equals(entry)) {
        //                 KeywordDictionary.getInstance().replaceEntry(c, entry, entryLemma);
        //                 System.out.println(entry + "\t" + entryLemma);
        //             }
            
        //             for (String s : dictc.get(entry)) {
        //                 String sLemma = Tokenizer.replaceLemmas(s);
        //                 if (!sLemma.equals(s)) {
        //                     KeywordDictionary.getInstance().replaceSynonym(c, entryLemma, s, sLemma);
        //                     System.out.println(s + "\t" + sLemma);
        //                 }
        //             }
        //         }
        //         else {
        //             for (String s : dictc.get(entry)) {
        //                 String sLemma = Tokenizer.replaceLemmas(s);
        //                 if (!sLemma.equals(s)) {
        //                     KeywordDictionary.getInstance().replaceSynonym(c, entry, s, sLemma);
        //                     System.out.println(s + "\t" + sLemma);
        //                 }
        //             }
        //         }
        //     }
        // }

        //KeywordGenerator.test();

        //ArrayList<Keyword> kwlist = (ArrayList<Keyword>) DataAccess.readFromJson("testlist", new TypeToken<ArrayList<Keyword>>(){}.getType());
        //for (Keyword kw : kwlist) {
        //    kw.parseKeyword();
        //}
        //DataAccess.writeToJson("testlist", kwlist);
    }
}

// jpackage --input C:\Users\EmilyXing\Documents\KeywordGenerator\keywordgenerator --name KeywordGeneratorTest --main-jar keywordgenerator.jar --main-class Main --type msi
// jpackage --input C:\Users\EmilyXing\Documents\KeywordGenerator\keywordgenerator\target --name KeywordGeneratorTest --main-jar keywordgenerator-1.0-SNAPSHOT-jar-with-dependencies.jar --main-class Main --runtime-image custom-runtime --resource-dir src/main/resources --type msi

// mvn clean compile assembly:single
// https://adoptium.net/

// jlink --add-modules ALL-MODULE-PATH --output build/runtime

// mvn clean package
// java -jar target\keywordgenerator-1.0-SNAPSHOT.jar

// java -jar C:\Users\EmilyXing\Documents\KeywordGenerator\keywordgenerator\target\keywordgenerator-1.0-SNAPSHOT-jar-with-dependencies.jar

// mvn javadoc:javadoc

