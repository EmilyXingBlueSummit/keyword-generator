package bss.Keywords;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import bss.Data.DataAccess;
import edu.stanford.nlp.util.StringUtils;

/**
 * Enum representing various categories and terms related to products.
 * Acts as a dictionary for storing and managing terms and synonyms applicable to different product attributes.
 */
public enum Categories {

    /** Category for product categories. */
    PRODUCT_CATEGORY {
        @Override
        public Entry addEntry(String str) {
            if (!this.entries.contains(str)) {
                this.entries.add(str);
                return new Entry(str);
            }
            else {
                return null;
            }
        }

        @Override
        public void replaceEntry(String oldStr, String newStr) {
            if (this.entries.contains(oldStr)) {
                Entry entry = this.entries.get(oldStr);
                entry.str = newStr;
                if (entry.synonyms.contains(newStr)) {
                    entry.synonyms.remove(newStr);
                }
                // update dict
            }
        }
    }, 

    /** Category for product types. */
    PRODUCT_TYPE {
        @Override
        public String getProductCategory(String productType) {
            for (Entry entry : Categories.PRODUCT_CATEGORY.entries) {
                if (entry.synonyms.contains(productType)) {
                    return entry.str;
                }
            }
            return null;
        }
    }, 

    /** Category for product dimensions. */
    DIMENSIONS {

    }, 

    /** Category for product colors. */
    COLOR {

    }, 

    /** Category for product quantities. */
    QUANTITY {
        @Override
        public Entry addEntry(String str) {
            Entry entry = super.addEntry(str);
            if (entry != null && StringUtils.isNumeric(str)) {
                entry.synonyms.add(str + " count");
                entry.synonyms.add(str + " set");
                entry.synonyms.add(str + " pack");
            }
            return entry;
        }
    }, 

    /** Category for product materials. */
    MATERIAL {

    }, 

    /** Category for additional product attributes. */
    ATTRIBUTES {

    }, 

    /** Default category for unclassified terms. */
    OTHER {
        @Override
        protected void initializeCategory() {
        }
    };

    /** List of entries for this category. */
    final EntryList entries;
    // static final ArrayList<String> unclassified;

    /** Static initialization block to load dictionary entries. */
    static {

    }
    
    private Categories() {
        this.entries = new EntryList();
        initializeCategory();
    }

    /**
     * Initializes the category by loading entries from a JSON file.
     */
    protected void initializeCategory() {
        this.entries.addAll((EntryList) DataAccess.readFromJson("dictionary/" + name(), new TypeToken<EntryList>(){}.getType()));
    }

    /**
     * Gets the values of all categories except OTHER.
     * 
     * @return An array of all category values excluding OTHER.
     */
    public static Categories[] getValues() {
        Categories[] oldValues = Categories.values();
        Categories[] newValues = new Categories[oldValues.length - 1];
        System.arraycopy(oldValues, 0, newValues, 0, oldValues.length - 1);
        return newValues;
    }

    // public ArrayList<String> getSynonyms(String entry) {
    //     return this.entries.get(entry).synonyms;
    // }

    /**
     * Gets the list of entries for this category.
     * 
     * @return The list of entries.
     */
    public EntryList getEntries() {
        return this.entries;
    }

    /**
     * Gets a list of all dictionary entries across all categories.
     * 
     * @return A list of all entries.
     */
    public static ArrayList<String> getAllEntries() {
        ArrayList<String> entryList = new ArrayList<>();
        for (Categories c : Categories.values()) {
            for (Entry entry : c.entries) {
                entryList.add(entry.str);
            }
        }
        return entryList;
    }

    /**
     * Gets a list of all synonyms across all categories.
     * 
     * @return A list of all synonyms.
     */
    public static ArrayList<String> getAllSynonyms() {
        ArrayList<String> synonymList = new ArrayList<>();
        for (Categories c : Categories.values()) {
            for (Entry entry : c.entries) {
                synonymList.addAll(entry.synonyms);
            }
        }
        return synonymList;
    }

    /**
     * Gets the product category for a given product type.
     * 
     * @param productType The product type to match.
     * @return The product category if found, otherwise null.
     */
    public String getProductCategory(String productType) {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds a new entry to the category.
     * 
     * @param str The entry to add.
     * @return The added entry, or null if it already exists.
     */
    public Entry addEntry(String str) {
        if (!this.entries.contains(str)) {
            Entry entry = new Entry(str);
            entry.synonyms.add(str);
            this.entries.add(entry);
            return entry;
        }
        else {
            return null;
        }
        // update dict
    }

    /**
     * Replaces an existing entry with a new entry.
     * 
     * @param oldStr The old entry to replace.
     * @param newStr The new entry to use.
     */
    public void replaceEntry(String oldStr, String newStr) {
        if (this.entries.contains(oldStr)) {
            Entry entry = this.entries.get(oldStr);
            entry.str = newStr;
            if (entry.synonyms.contains(newStr)) {
                entry.synonyms.remove(newStr);
            }
            entry.synonyms.set(0, newStr);
            // update dict
        }
    }

    /**
     * Removes an entry from the category.
     * 
     * @param str The entry to remove.
     */
    public void removeEntry(String str) {
        this.entries.remove(str);
        // update dict
    }

    /**
     * Adds a synonym to an existing entry.
     * 
     * @param entry The entry to add a synonym to.
     * @param str The synonym to add.
     */
    public void addSynonym(String entry, String str) {
        ArrayList<String> synonyms = this.entries.get(entry).synonyms;
        if (!synonyms.contains(str)) {
            synonyms.add(str);
        }
        // update dict
    }

    /**
     * Adds a synonym to an existing entry at a specific position.
     * 
     * @param entry The entry to add a synonym to.
     * @param str The synonym to add.
     * @param pos The position to add the synonym at.
     */
    public void addSynonym(String entry, String str, int pos) {
        ArrayList<String> synonyms = this.entries.get(entry).synonyms;
        if (!synonyms.contains(str)) {
            synonyms.add(pos, str);
        }
        // update dict
    }

    // public void moveSynonym(String entry, String str, int pos) {
        
    // }

    /**
     * Replaces a synonym in an entry.
     * 
     * @param entry The entry to replace a synonym in.
     * @param oldStr The old synonym to replace.
     * @param newStr The new synonym to use.
     */
    public void replaceSynonym(String entry, String oldStr, String newStr) {
        ArrayList<String> synonyms = this.entries.get(entry).synonyms;
        if (synonyms.contains(oldStr)) {
            synonyms.set(synonyms.indexOf(oldStr), newStr);
        }
        // update dict
    }

    /**
     * Removes a synonym from an entry.
     * 
     * @param entry The entry to remove a synonym from.
     * @param str The synonym to remove.
     */
    public void removeSynonym(String entry, String str) {
        this.entries.get(entry).synonyms.remove(str);
        // update dict
    }

    /**
     * Checks if two strings are synonyms.
     * 
     * @param s1 The first string.
     * @param s2 The second string.
     * @return True if the strings are synonyms, otherwise false.
     */
    public boolean isSynonym(String s1, String s2) {
        for (Entry entry : this.entries) {
            if (entry.synonyms.contains(s1) && entry.synonyms.contains(s2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any item in a list is a synonym for a given entry.
     * 
     * @param strList The list of strings to check.
     * @param match The entry to match against.
     * @return True if any item is a synonym, otherwise false.
     */
    public boolean containsSynonym(List<String> strList, String match) {
        for (String s : strList) {
            if (isSynonym(s, match)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks the category of a given token.
     * 
     * @param token The token to check.
     * @return The category of the token.
     */
    public static Categories checkTokenCategory(String token) {
        for (Categories c : Categories.getValues()) {
            if (c == Categories.PRODUCT_CATEGORY) {
                continue;
            }
            for (Entry entry : c.entries) {
                if (entry.synonyms.contains(token)) {
                    return c;
                }
            }
        }
        return Categories.OTHER;
    }

    /**
     * Updates the dictionary file with the current entries.
     */
    public void updateFile() {
        String filePath = DataAccess.getPath("dictionary/" + name() + ".json");
        try (FileWriter fw = new FileWriter(filePath)) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            String jsonStr = gsonBuilder.setPrettyPrinting().create().toJson(this.entries);
            fw.write(jsonStr);
            
        } catch (IOException ex) {
            System.out.println("cannot access " + filePath);
        } 
    }

    // public abstract List<String> replaceSynonyms(List<String> tokens);



    /**
     * Replaces tokens with their corresponding dictionary entries.
     * 
     * @param tokens The list of tokens to process.
     * @return The updated list of tokens with replacements.
     */
    public static List<String> replaceSynonyms(List<String> tokens) {
        LinkedHashMap<String, ArrayList<ArrayList<String>>> matchesPerToken = new LinkedHashMap<>(); // keys = tokens, values = list of possible token groupings that can form a dict entry
        LinkedHashMap<Categories, LinkedHashMap<ArrayList<String>, String>> possibleMatches = new LinkedHashMap<>(); // keys = categories, values = token groupings/corresponding dict entries
        ArrayList<String> oldTokens = new ArrayList<>(tokens);

        // initialize list for each token
        for (String t : tokens) {
            matchesPerToken.put(t, new ArrayList<>());
        }

        // check for token groupings that can form a dict entry
        for (Categories c : Categories.getValues()) {
            if (c == Categories.PRODUCT_CATEGORY) { // skip product category
                continue;
            }

            possibleMatches.put(c, new LinkedHashMap<>()); // initialize list for each category

            for (Entry entry : c.entries) {
                for (String s : entry.synonyms) {
                    ArrayList<String> matchingTokens = new ArrayList<>(); // list of tokens matched within a dict entry
                    String s2 = s.replaceAll(" ", "");

                    // identify when s contains a token
                    for (String t : tokens) {
                        if (s.contains(t) || s2.contains(t)) {
                            matchingTokens.add(t);
                            if (s.contains(t)) {
                                s = s.replace(t, "");
                            }
                            s2 = s2.replace(t, "");
                        }
                    }

                    // if s contains no material that is not part of a token, tokens can be replaced with s
                    s = s.replaceAll(" ", "");
                    s = s.replaceAll("-", "");
                    s = s.replaceAll(",", "");
                    s = s.replaceAll("\"", "");
                    if (s.equals("") || s2.equals("")) {
                        possibleMatches.get(c).put(matchingTokens, entry.str); // add possible match
                        if (c == Categories.PRODUCT_TYPE) {
                            for (String mt : matchingTokens) {
                                tokens.remove(mt);
                                if (!tokens.contains(mt)) {
                                    matchesPerToken.remove(mt);
                                }
                            }
                            if (!tokens.contains(entry.str)) {
                                tokens.add(entry.str);
                            }
                            matchesPerToken.put(entry.str, new ArrayList<>());
                        }
                        else {
                            for (String mt : matchingTokens) {
                                matchesPerToken.get(mt).add(matchingTokens);
                            }
                        }
                    }
                }
            }
        }

        // check for ambiguous matches
        for (Categories c : possibleMatches.keySet()) {
            if (possibleMatches.get(c).isEmpty()) {
                continue;
            }
            for (ArrayList<String> matchingTokens : possibleMatches.get(c).keySet()) { // check all possible matches
                boolean validMatch = true; // true if all tokens are still present
                boolean isAmbiguous = true; // true if any token is part of only one possible match
                for (String mt : matchingTokens) {
                    if (!tokens.contains(mt) || !matchesPerToken.get(mt).isEmpty() || (c == Categories.COLOR && c.getEntries().contains(mt) && !possibleMatches.get(c).get(matchingTokens).equals(mt))) {
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

        for (String t : new ArrayList<>(tokens)) {
            if (oldTokens.contains(t) && tokens.contains(t) && matchesPerToken.containsKey(t)) {
                ArrayList<String> closestMatch = new ArrayList<>();
                double shortestDistance = 100;
                for (ArrayList<String> match : matchesPerToken.get(t)) {
                    int tPos = oldTokens.indexOf(t);
                    boolean validMatch = true;
                    double avgDistance = 0;
                    for (String mt : match) {
                        if (!oldTokens.contains(mt) || !tokens.contains(mt) || (Categories.COLOR.getEntries().contains(mt) && !possibleMatches.get(Categories.COLOR).isEmpty() && possibleMatches.get(Categories.COLOR).containsKey(match) && !possibleMatches.get(Categories.COLOR).get(match).equals(mt))) {
                            validMatch = false;
                            break;
                        }
                        else {
                            avgDistance += Math.abs(oldTokens.indexOf(mt) - tPos);
                        }
                    }
                    avgDistance /= match.size();
                    if (avgDistance < shortestDistance && validMatch) {
                        closestMatch = match;
                        shortestDistance = avgDistance;
                    }
                }
                if (!closestMatch.isEmpty()) {
                    for (String mt : closestMatch) {
                        tokens.remove(mt);
                        matchesPerToken.remove(mt);
                    }
                    for (Categories c : possibleMatches.keySet()) {
                        if (possibleMatches.get(c).containsKey(closestMatch) && !tokens.contains(possibleMatches.get(c).get(closestMatch))) {
                            tokens.add(possibleMatches.get(c).get(closestMatch));
                        }
                    }
                }
            }
        }

        return tokens;
    }

}
