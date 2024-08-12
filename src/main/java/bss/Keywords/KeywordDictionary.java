package bss.Keywords;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import bss.Data.DataAccess;
import edu.stanford.nlp.util.StringUtils;

public class KeywordDictionary {
    // private static KeywordDictionary instance = null;
    LinkedHashMap<Categories, ListOrderedMap<String, ArrayList<String>>> dict;
    ArrayList<String> unclassified;
    ArrayList<String> filler;
    ArrayList<String> entryList;
    LinkedHashMap<String, ArrayList<String>> other;
    ArrayList<String> words;

    private KeywordDictionary() {

    }

    public LinkedHashMap<Categories, ListOrderedMap<String, ArrayList<String>>> getDict() {
        return this.dict;
    }

    public ArrayList<String> getUnclassified() {
        return this.unclassified;
    }

    public ListOrderedMap<String, ArrayList<String>> getCategory(Categories category) {
        return this.dict.get(category);
    }

    public List<String> getCategoryList(Categories category) {
        return (List<String>) (Object) Arrays.asList(this.dict.get(category).keySet().toArray());
    }

    public ArrayList<String> getAllEntries() {
        ArrayList<String> entryList = new ArrayList<>();
        for (Categories c : dict.keySet()) {
            for (String entry : dict.get(c).keySet()) {
                for (String s : dict.get(c).get(entry)) {
                    if (!entryList.contains(s)) {
                        entryList.add(s);
                    }
                }
            }
        }
        return entryList;
    }

    public void retrieveWordList() {
        this.words = (ArrayList<String>) DataAccess.readFromJson("wordList", new TypeToken<ArrayList<String>>(){}.getType());
    }

    public void addEntry(Categories c, String entry) {
        ArrayList<String> arr = new ArrayList<>();
        if (c == Categories.QUANTITY && StringUtils.isNumeric(entry)) {
            arr.add(entry);
            arr.add(entry + " count");
            arr.add(entry + " set");
            arr.add(entry + " pack");
        }
        else if (c != Categories.PRODUCT_CATEGORY){
            arr.add(entry);
        }
        
        this.dict.get(c).put(entry, arr);
        if (this.unclassified.contains(entry)) {
            this.unclassified.remove(entry);
        }
        updateDict();
    }

    public void replaceEntry(Categories c, String oldEntry, String newEntry) {
        ArrayList<String> arr = this.dict.get(c).get(oldEntry);
        if (arr.contains(newEntry)) {
            arr.remove(newEntry);
        }
        if (c != Categories.PRODUCT_CATEGORY) {
            arr.set(0, newEntry);
        }
        this.dict.get(c).put(this.dict.get(c).indexOf(oldEntry), newEntry, arr);
        this.dict.get(c).remove(oldEntry);
        updateDict();
    }
    
    public void removeEntry(Categories c, String entry) {
        this.dict.get(c).remove(entry);
        updateDict();
    }

    public void addSynonym(Categories c, String entry, String s) {
        // s = Tokenizer.replaceLemmas(s);
        this.dict.get(c).get(entry).add(s);
        if (this.unclassified.contains(s)) {
            this.unclassified.remove(s);
        }
        updateDict();
    }

    public void addSynonym(Categories c, String entry, String s, int pos) {
        this.dict.get(c).get(entry).add(pos, s);
        if (this.unclassified.contains(s)) {
            this.unclassified.remove(s);
        }
        updateDict();
    }

    public void replaceSynonym(Categories c, String entry, String oldString, String newString) {
        this.dict.get(c).get(entry).set(this.dict.get(c).get(entry).indexOf(oldString), newString);
        if (this.unclassified.contains(newString)) {
            this.unclassified.remove(newString);
        }
        updateDict();
    }

    public void removeSynonym(Categories c, String entry, String s) {
        this.dict.get(c).get(entry).remove(s);
        updateDict();
    }

    public void addFiller(String s) {
        if (!this.filler.contains(s)) {
            this.filler.add(s);
            updateDict();
        }
    }

    public void addOther(String type, String s) {
        this.other.get(type).add(s);
        if (this.unclassified.contains(s)) {
            this.unclassified.remove(s);
        }
        updateDict();
    }

    public void updateDict() {
        try (FileWriter fw = new FileWriter(DataAccess.getPath("dictionary.json"))) {
            this.words = null;
            this.entryList = null;
            GsonBuilder gsonBuilder = new GsonBuilder();
            String jsonstr = gsonBuilder.setPrettyPrinting().create().toJson(KeywordDictionary.getInstance());
            fw.write(jsonstr);
            retrieveWordList();
            this.entryList = getAllEntries();
            
        } catch (IOException ex) {
            System.out.println("cannot access " + DataAccess.getPath("dictionary.json"));
        } 
    }

    public List<String> replaceSynonyms(List<String> tokens) {
        LinkedHashMap<String, ArrayList<ArrayList<String>>> matchesPerToken = new LinkedHashMap<>(); // keys = tokens, values = list of possible token groupings that can form a dict entry
        LinkedHashMap<Categories, LinkedHashMap<ArrayList<String>, String>> possibleMatches = new LinkedHashMap<>(); // keys = categories, values = token groupings/corresponding dict entries
        ArrayList<String> oldTokens = new ArrayList<>(tokens);

        // initialize list for each token
        for (String t : tokens) {
            matchesPerToken.put(t, new ArrayList<>());
        }

        // check for token groupings that can form a dict entry
        for (Categories c : this.dict.keySet()) {
            if (c == Categories.PRODUCT_CATEGORY) { // skip product category
                continue;
            }

            possibleMatches.put(c, new LinkedHashMap<>()); // initialize list for each category

            ListOrderedMap<String, ArrayList<String>> entryMap = this.dict.get(c); // map of all entries in a category
            for (String entry : entryMap.keySet()) {
                ArrayList<String> synonymList = entryMap.get(entry); // list of all synonyms for an entry
                for (String s : synonymList) {
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
                        possibleMatches.get(c).put(matchingTokens, entry); // add possible match
                        if (c == Categories.PRODUCT_TYPE) {
                            for (String mt : matchingTokens) {
                                tokens.remove(mt);
                                if (!tokens.contains(mt)) {
                                    matchesPerToken.remove(mt);
                                }
                            }
                            if (!tokens.contains(entry)) {
                                tokens.add(entry);
                            }
                            matchesPerToken.put(entry, new ArrayList<>());
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
                    if (!tokens.contains(mt) || !matchesPerToken.get(mt).isEmpty() || (c == Categories.COLOR && getCategoryList(c).contains(mt) && !possibleMatches.get(c).get(matchingTokens).equals(mt))) {
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
                            if (!matchList.isEmpty() && !containsSynonym(matchList, matchStr, c)) {
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
                        if (!oldTokens.contains(mt) || !tokens.contains(mt) || (getCategoryList(Categories.COLOR).contains(mt) && !possibleMatches.get(Categories.COLOR).isEmpty() && possibleMatches.get(Categories.COLOR).containsKey(match) && !possibleMatches.get(Categories.COLOR).get(match).equals(mt))) {
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

    public void retokenizeAllKeywords() {
        ArrayList<SearchTerm> kwList = DataAccess.getSearchTermList("testData");
        // for (LinkedHashMap<String, Keyword> productMap : DataAccess.getMasterList().values()) {
        //     for (Keyword kw : productMap.values()) {
        //         kwList.add(kw);
        //     }
        // }

        for (SearchTerm kw : kwList) {
            kw.tokenize();
            kw.computeTF();
        }

        DataAccess.writeToJson("testData", kwList);
    }

    public boolean isSynonym(String s1, String s2, Categories category) {
        for (ArrayList<String> entry : this.dict.get(category).values()) {
            if (entry.contains(s1) && entry.contains(s2)) {
                return true;
            }
        }
        return false;
    }

    // checks if any item in a list is a synonym for a certain entry
    public boolean containsSynonym(List<String> strList, String match, Categories category) {
        for (String s : strList) {
            if (isSynonym(s, match, category)) {
                return true;
            }
        }
        return false;
    }

    public String getProductCategory(String productType) {
        for (String s : this.dict.get(Categories.PRODUCT_CATEGORY).keySet()) {
            if (this.dict.get(Categories.PRODUCT_CATEGORY).get(s).contains(productType)) {
                return s;
            }
        }
        return null;
    }

    public Categories checkTokenCategory(String token) {
        for (Categories c : this.dict.keySet()) {
            if (c == Categories.PRODUCT_CATEGORY) {
                continue;
            }
            for (ArrayList<String> entry : this.dict.get(c).values()) {
                if (entry.contains(token)) {
                    return c;
                }
            }
        }
        return Categories.OTHER;
    }

    public void checkTokens(Keyword kw) {
        for (String token : kw.getCategorizedTokens().get(Categories.OTHER)) {
            if (!this.unclassified.contains(token)) {
                this.unclassified.add(token);
            }
        }
    }

    // public static KeywordDictionary getInstance() {
    //     if (KeywordDictionary.instance == null) {
    //         KeywordDictionary.instance = DataAccess.getDictionary();
    //         // for (Category c : KeywordDictionary.instance.dict.keySet()) {
    //         //     for (String entry : KeywordDictionary.instance.dict.get(c).keySet()) {
    //         //         c.addEntry(entry);
    //         //         for (String s : KeywordDictionary.instance.dict.get(c).get(entry)) {
    //         //             c.addSynonym(entry, s);
    //         //         }
    //         //     }
    //         //     c.updateFile();
    //         // }

    //         //KeywordDictionary.instance.unclassified = new ArrayList<>();
    //         KeywordDictionary.instance.retrieveWordList();

    //         ArrayList<String> arr = new ArrayList<>();
    //         for (String s : KeywordDictionary.instance.unclassified) {
    //             if (!KeywordDictionary.instance.words.contains(s)) {
    //                 arr.add(s);
    //             }
    //         }

    //         KeywordDictionary.instance.entryList = KeywordDictionary.instance.getAllEntries();
    //         //System.out.println(arr);
            
    //     }
    //     return KeywordDictionary.instance;
    // }
}
