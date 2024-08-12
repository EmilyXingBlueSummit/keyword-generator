package bss;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.creole.ANNIEConstants;
import gate.creole.Plugin;
import gate.creole.ResourceReference;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

public class GateTest {
    private SerialAnalyserController pipeline;

    public GateTest(String gateHome, String gappFilePath) throws GateException, IOException {
        // Initialize GATE
        Gate.setGateHome(new File(gateHome));
        Gate.init();

        // Load the processing pipeline
        pipeline = (SerialAnalyserController) PersistenceManager.loadObjectFromFile(new File(gappFilePath));
    }

    public void processDocument(Document doc) throws GateException {
        Corpus corpus = Factory.newCorpus("Corpus");
        corpus.add(doc);
        pipeline.setCorpus(corpus);
        pipeline.execute();
        corpus.clear();
    }

    public List<String> generateKeywords(String productDescription, List<String> searchTerms) throws GateException {
        // Create and process the product description document
        Document productDoc = Factory.newDocument(productDescription);
        processDocument(productDoc);

        // Process each search term and compute relevance scores
        List<SearchTerm> searchTermList = new ArrayList<>();
        for (String term : searchTerms) {
            Document termDoc = Factory.newDocument(term);
            processDocument(termDoc);

            double score = computeRelevanceScore(productDoc, termDoc);
            searchTermList.add(new SearchTerm(term, score));

            Factory.deleteResource(termDoc);
        }

        Factory.deleteResource(productDoc);

        // Sort search terms by relevance score
        searchTermList.sort(Comparator.comparingDouble(SearchTerm::getScore).reversed());

        // Extract sorted terms
        List<String> sortedTerms = new ArrayList<>();
        for (SearchTerm st : searchTermList) {
            sortedTerms.add(st.getTerm());
        }

        return sortedTerms;
    }

    private double computeRelevanceScore(Document productDoc, Document termDoc) {
        // Implement a method to compute the relevance score between the product description and a search term
        // For simplicity, this example uses a basic keyword frequency approach

        String productText = productDoc.getContent().toString();
        String termText = termDoc.getContent().toString();

        int count = 0;
        String[] words = termText.split("\\s+");
        for (String word : words) {
            if (productText.contains(word)) {
                count++;
            }
        }

        return (double) count / words.length;
    }

    private static class SearchTerm {
        private final String term;
        private final double score;

        public SearchTerm(String term, double score) {
            this.term = term;
            this.score = score;
        }

        public String getTerm() {
            return term;
        }

        public double getScore() {
            return score;
        }
    }

    public static void main(String[] args) throws GateException, IOException, URISyntaxException {
        // initialise the GATE library 
        Gate.init(); 
        
        // load the ANNIE plugin 
        Plugin anniePlugin = new Plugin.Maven( 
                "uk.ac.gate.plugins", "annie", gate.Main.version); 
        Gate.getCreoleRegister().registerPlugin(anniePlugin); 
        
        // load ANNIE application from inside the plugin 
        SerialAnalyserController controller = (SerialAnalyserController) 
            PersistenceManager.loadObjectFromUrl(new ResourceReference( 
            anniePlugin, "resources/" + ANNIEConstants.DEFAULT_FILE) 
                .toURL());
            // try {
        //     String gateHome = "/path/to/gate/home";
        //     String gappFilePath = "/path/to/application.xgapp";

        //     GateTest generator = new GateTest(gateHome, gappFilePath);

        //     String productDescription = "This is an example product description.";
        //     List<String> searchTerms = List.of("example", "product", "description", "sample", "item");

        //     List<String> rankedTerms = generator.generateKeywords(productDescription, searchTerms);

        //     System.out.println("Ranked search terms:");
        //     for (String term : rankedTerms) {
        //         System.out.println(term);
        //     }
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }
}
