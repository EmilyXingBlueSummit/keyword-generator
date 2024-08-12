package bss.Keywords;

import java.util.ArrayList;
import java.util.HashMap;

import edu.stanford.nlp.ling.CoreLabel;

/**
 * Represents a product with a description and additional attributes.
 */
public class Product extends Keyword {

    /** The unique identifier for the product. */
    String id;

    /**
     * Constructs a Product with the given ID and description.
     * 
     * @param id The unique identifier for the product.
     * @param description The product description.
     */
    public Product(String id, String description) {
        super(description);
        this.id = id;
    }

    /**
     * Copy constructor to create a new Product from an existing one.
     * 
     * @param product The Product to copy.
     */
    public Product(Product product) {
        super(product);
        this.id = product.id;
    }

    /**
     * Tokenizes the product description and processes the tokens.
     */
    @Override
    public void tokenize() {
        this.tokens = new ArrayList<>();

        ArrayList<CoreLabel> annotatedTokens = Tokenizer.createAnnotatedTokens(this.query);
        this.tokens = Tokenizer.extractTokensWithLemmas(annotatedTokens);

        if (this.tokens.contains("blue") && this.tokens.contains("summit")) {
            this.tokens.remove("blue");
            this.tokens.remove("summit");
        }

        this.tokens = Categories.replaceSynonyms(this.tokens);
        // HashMap<Category, HashMap<ArrayList<String>, String>> possibleMatches = Tokenizer.identifyPossibleMatches(this.tokens);
        // HashMap<String, ArrayList<ArrayList<String>>> matchesPerToken = new LinkedHashMap<>(); // keys = tokens, values = list of possible token groupings that can form a dict entry
        // // initialize list for each token
        // for (String t : tokens) {
        //     matchesPerToken.put(t, new ArrayList<>());
        // }
        // for (Category c : possibleMatches.keySet()) {
        //     for (ArrayList<String> matchingTokens : possibleMatches.get(c).keySet()) {
        //         for (String mt : matchingTokens) {
        //             matchesPerToken.get(mt).add(matchingTokens);
        //         }
        //     }
        // }

        // // check for ambiguous matches
        // for (Category c : possibleMatches.keySet()) {
        //     if (possibleMatches.get(c).isEmpty()) {
        //         continue;
        //     }
        //     for (ArrayList<String> matchingTokens : possibleMatches.get(c).keySet()) { // check all possible matches
        //         boolean validMatch = true; // true if all tokens are still present
        //         boolean isAmbiguous = true; // true if any token is part of only one possible match
        //         for (String mt : matchingTokens) {
        //             if (!tokens.contains(mt) || !matchesPerToken.get(mt).isEmpty()) {
        //                 validMatch = false;
        //                 break;
        //             }
        //             if (matchesPerToken.get(mt).size() <= 1) {
        //                 isAmbiguous = false;
        //             }
        //             else {
        //                 // if token is part of multiple possible matches, check if possible matches are all synonyms
        //                 ArrayList<String> matchList = new ArrayList<>();
        //                 for (ArrayList<String> match : matchesPerToken.get(mt)) {
        //                     String matchStr = possibleMatches.get(c).get(match);
        //                     if (!matchList.isEmpty() && !KeywordDictionary.getInstance().containsSynonym(matchList, matchStr, c)) {
        //                         break;
        //                     }
        //                     matchList.add(matchStr);
        //                 }
        //                 if (matchList.size() == matchesPerToken.get(mt).size()) { // token is unambiguous if all possible matches are synonyms
        //                     isAmbiguous = false;
        //                 }
        //             }
        //         }
        //         if (validMatch && !isAmbiguous) { // replace tokens with match if not ambiguous
        //             for (String mt : matchingTokens) {
        //                 tokens.remove(mt);
        //                 if (!tokens.contains(mt)) {
        //                     matchesPerToken.remove(mt);
        //                 }
        //             }
        //             String newToken = possibleMatches.get(c).get(matchingTokens);
        //             if (!tokens.contains(newToken)) {
        //                 tokens.add(newToken);
        //             }
        //             if (!matchesPerToken.containsKey(newToken)) {
        //                 matchesPerToken.put(newToken, new ArrayList<>());
        //             }
        //         }
        //     }
        // }

        // for (String t : new ArrayList<>(tokens)) {
        //     if (oldTokens.contains(t) && tokens.contains(t) && matchesPerToken.containsKey(t)) {
        //         ArrayList<String> closestMatch = new ArrayList<>();
        //         double shortestDistance = 100;
        //         for (ArrayList<String> match : matchesPerToken.get(t)) {
        //             int tPos = oldTokens.indexOf(t);
        //             boolean validMatch = true;
        //             double avgDistance = 0;
        //             for (String mt : match) {
        //                 if (!oldTokens.contains(mt) || !tokens.contains(mt)) {
        //                     validMatch = false;
        //                     break;
        //                 }
        //                 else {
        //                     avgDistance += Math.abs(oldTokens.indexOf(mt) - tPos);
        //                 }
        //             }
        //             avgDistance /= match.size();
        //             if (avgDistance < shortestDistance && validMatch) {
        //                 closestMatch = match;
        //                 shortestDistance = avgDistance;
        //             }
        //         }
        //         if (!closestMatch.isEmpty()) {
        //             for (String mt : closestMatch) {
        //                 tokens.remove(mt);
        //                 matchesPerToken.remove(mt);
        //             }
        //             for (Category c : possibleMatches.keySet()) {
        //                 if (possibleMatches.get(c).containsKey(closestMatch) && !tokens.contains(possibleMatches.get(c).get(closestMatch))) {
        //                     tokens.add(possibleMatches.get(c).get(closestMatch));
        //                 }
        //             }
        //         }
        //     }
        // }

        this.tokens = Tokenizer.removeExtraneousTokens(annotatedTokens, this.tokens);

        this.categorizeTokens();
    }

    /**
     * Replaces unambiguous matches in the list of possible matches with a single token.
     * 
     * @param possibleMatches The map of possible matches for different categories.
     * @param matchesPerToken The map of possible token groupings for each token.
     * @param c The category to process.
     */
    private void replaceUnambiguousMatches(HashMap<Categories, HashMap<ArrayList<String>, String>> possibleMatches, HashMap<String, ArrayList<ArrayList<String>>> matchesPerToken, Categories c) {
        if (possibleMatches.get(c).isEmpty()) {
            return;
        }
        for (ArrayList<String> matchingTokens : possibleMatches.get(c).keySet()) { // check all possible matches
            boolean validMatch = true; // true if all tokens are still present
            boolean isAmbiguous = true; // true if any token is part of only one possible match
            for (String mt : matchingTokens) {
                if (!tokens.contains(mt) || !matchesPerToken.get(mt).isEmpty()) {
                    validMatch = false;
                    break;
                }
                if (matchesPerToken.get(mt).size() <= 1) {
                    isAmbiguous = false;
                }
                else {
                    // if token is part of multiple possible matches, check if possible matches are all synonyms
                    ArrayList<String> matchList = new ArrayList<>();
                    for (ArrayList<String> match : matchesPerToken.get(mt)) {
                        String matchStr = possibleMatches.get(c).get(match);
                        if (!matchList.isEmpty() && !c.containsSynonym(matchList, matchStr)) {
                            break;
                        }
                        matchList.add(matchStr);
                    }
                    if (matchList.size() == matchesPerToken.get(mt).size()) { // token is unambiguous if all possible matches are synonyms
                        isAmbiguous = false;
                    }
                }
            }
            if (validMatch && !isAmbiguous) { // replace tokens with match if not ambiguous
                for (String mt : matchingTokens) {
                    tokens.remove(mt);
                    if (!tokens.contains(mt)) {
                        matchesPerToken.remove(mt);
                    }
                }
                String newToken = possibleMatches.get(c).get(matchingTokens);
                if (!tokens.contains(newToken)) {
                    tokens.add(newToken);
                }
                if (!matchesPerToken.containsKey(newToken)) {
                    matchesPerToken.put(newToken, new ArrayList<>());
                }
            }
        }
    }

}
