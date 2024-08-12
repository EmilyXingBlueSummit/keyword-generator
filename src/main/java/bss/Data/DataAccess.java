package bss.Data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.ImageIcon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import bss.Keywords.EntryList;
import bss.Keywords.Keyword;
import bss.Keywords.KeywordDictionary;
import bss.Keywords.Product;
import bss.Keywords.SearchTerm;

/**
 * Provides methods for reading from and writing to resource files.
 */
public class DataAccess {
    /** Singleton instance */
    static DataAccess instance = null;

    /** Path to the directory containing resource files */
    //static String filePath = "src/main/resources/";
    static String filePath = "classes/";

    private DataAccess() {
    }
    
    /**
     * Returns the full path to a file in the resource directory.
     * 
     * @param fileName the name of the file
     * @return the full path to the file
     */
    public static String getPath(String fileName) {
        return DataAccess.filePath + fileName;
    };

    /**
     * Returns an {@link InputStream} for reading a resource file.
     * 
     * @param fileName the name of the resource file (without extension)
     * @return the InputStream for the resource file
     */
    private InputStream getInputStream(String fileName) {
        return getClass().getResourceAsStream(DataAccess.filePath + fileName + ".json");
    }

    /**
     * Reads data from a JSON file and deserializes it into an object of the specified type.
     * 
     * @param fileName the name of the JSON file (without extension)
     * @param type the type of the object to deserialize
     * @return the deserialized object, or {@code null} if the file is not found
     */
    public static Object readFromJson(String fileName, Type type) {
        //InputStream in = instance.getInputStream(fileName);
        //BufferedReader fr = new BufferedReader(new InputStreamReader(in));
        try {
            FileReader fr = new FileReader(DataAccess.getPath(fileName) + ".json");
            Gson gson = new Gson();
            Object val = gson.fromJson(new JsonReader(fr), type);
            return val;
        } catch (FileNotFoundException e) {
            System.out.println("can't read file " + DataAccess.getPath(fileName) + ".json");
            return null;
        }
    }

    /**
     * Serializes an object to JSON and writes it to a file.
     * 
     * @param fileName the name of the file (without extension) to write to
     * @param obj the object to serialize and write
     */
    public static void writeToJson(String fileName, Object obj) {
        try (FileWriter fw = new FileWriter(DataAccess.getPath(fileName) + ".json")) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            String jsonStr = gsonBuilder.setPrettyPrinting().create().toJson(obj);
            fw.write(jsonStr);
        } catch (IOException ex) {
            System.out.println("can't write to file " + fileName);
        }
    }

    /**
     * Retrieves an {@link ImageIcon} from the resource directory.
     * 
     * @param fileName the name of the image file
     * @return the ImageIcon, or {@code null} if the image cannot be accessed
     */
    public static ImageIcon getImageIcon(String fileName) {
        try {
            return new ImageIcon(DataAccess.getPath("images/" + fileName));
        } catch (Exception e) {
            System.out.println("can't access image " + fileName);
            return null;
        }
    }

    /**
     * Reads test data from a JSON file and returns it as an {@link ArrayList} of {@link SearchTerm}.
     * 
     * @return the list of test data
     */
    public static ArrayList<SearchTerm> getTestData() {
        return (ArrayList<SearchTerm>) DataAccess.readFromJson("testData", new TypeToken<ArrayList<SearchTerm>>(){}.getType());
    }

    /**
     * Reads search terms from a JSON file and returns them as an {@link ArrayList} of {@link SearchTerm}.
     * 
     * @param fileName the name of the JSON file (without extension)
     * @return the list of search terms
     */
    public static ArrayList<SearchTerm> getSearchTermList(String fileName) {
        return (ArrayList<SearchTerm>) DataAccess.readFromJson(fileName, new TypeToken<ArrayList<SearchTerm>>(){}.getType());
    }

    /**
     * Reads the master list from a JSON file and returns it as a {@link LinkedHashMap}.
     * 
     * @return the master list
     */
    public static LinkedHashMap<String, LinkedHashMap<String, Keyword>> getMasterList() {
        return (LinkedHashMap<String, LinkedHashMap<String, Keyword>>) DataAccess.readFromJson("masterList", new TypeToken<LinkedHashMap<String, LinkedHashMap<String, Product>>>(){}.getType());
    }

    /**
     * Reads the Amazon list from a JSON file and returns it as a {@link LinkedHashMap}.
     * 
     * @return the Amazon list
     */
    public static LinkedHashMap<String, Keyword> getAmazonList() {
        return (LinkedHashMap<String, Keyword>) DataAccess.readFromJson("amazonTitles", new TypeToken<LinkedHashMap<String, Product>>(){}.getType());
    }

    /**
     * Reads the keyword dictionary from a JSON file and returns it as a {@link KeywordDictionary}.
     * 
     * @return the keyword dictionary, or {@code null} if the file cannot be accessed
     */
    public static KeywordDictionary getDictionary() {
        try {
            Gson gson = new Gson();
            FileReader fr = new FileReader(DataAccess.getPath("dictionary") + ".json");
            return gson.fromJson(new JsonReader(fr), KeywordDictionary.class);
        } catch (FileNotFoundException ex) {
            System.out.println("cannot access " + DataAccess.getPath("dictionary") + ".json");
            return null;
        } 
    }

    /**
     * Returns the singleton instance of the DataAccess class.
     * <p>
     * Initializes the instance if it does not already exist.
     * </p>
     * 
     * @return the singleton instance of DataAccess
     */
    public static DataAccess getInstance() {
        if (DataAccess.instance == null) {
            DataAccess.instance = new DataAccess();

            try {
                FileReader fr = new FileReader(DataAccess.getPath("dataAccessTest") + ".json");
                Gson gson = new Gson();
                Object val = gson.fromJson(new JsonReader(fr), new TypeToken<String>(){}.getType());
                System.out.println(val);
            } catch (FileNotFoundException e) {
                DataAccess.filePath = "src/main/resources/";
            }
        }
        return DataAccess.instance;
    }
}