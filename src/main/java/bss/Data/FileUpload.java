package bss.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import bss.Keywords.SearchTerm;

/**
 * Enum for handling different types of file uploads and processing.
 * <p>
 * This enum provides different implementations for reading files based on their type and structure.
 * </p>
 */
public enum FileUpload {

    /** No file type specified. */
    NONE(" ", new String[]{}) {
        @Override
        public boolean read(File filePath) {
            return false;
        }
    },

    /** Handles CSV files containing keyword search terms. */
    KEYWORD_SEARCH_TERM("csv", new String[]{"keyword_text", "campaign_name", "query", "keyword_match_type", "ad_group_name", "clicks__sum", "cost__sum", "attributed_sales_14_day__sum", "attributed_conversions_14_day__sum", "impressions__sum"}) {
        @Override
        public boolean read(File filePath) {
            if (!checkFileType(filePath)) {
                return false;
            }

            try {    
                List<List<String>> records = readCsv(filePath);
                System.out.println(records.get(0));
                ArrayList<SearchTerm> searchTerms = new ArrayList<>();
                List<String> colNames = records.get(0);
                if (!checkColumnNames(colNames)) {
                    return false;
                }
                ArrayList<String> campaignNames = new ArrayList<>();
                int i = 0;
                for (List<String> record : records) {
                    if (record == colNames) {
                        continue;
                    }
                    if (!record.get(colNames.indexOf("campaign_name")).toLowerCase().contains("auto") && !record.get(colNames.indexOf("keyword_text")).equals("(_targeting_auto_)")) {
                        searchTerms.add(new SearchTerm(record.get(colNames.indexOf("keyword_text")), record.get(colNames.indexOf("campaign_name")), record.get(colNames.indexOf("query")), record.get(colNames.indexOf("keyword_match_type")), record.get(colNames.indexOf("ad_group_name")), Integer.parseInt(record.get(colNames.indexOf("clicks__sum"))), Double.parseDouble(record.get(colNames.indexOf("cost__sum"))), Double.parseDouble(record.get(colNames.indexOf("attributed_sales_14_day__sum"))), Integer.parseInt(record.get(colNames.indexOf("attributed_conversions_14_day__sum"))), Integer.parseInt(record.get(colNames.indexOf("impressions__sum")))));
                        System.out.println(record.get(colNames.indexOf("query")));
                        if (record.get(colNames.indexOf("campaign_name")).charAt(0) != '_') {
                            if (!campaignNames.contains(record.get(colNames.indexOf("campaign_name")))) {
                                campaignNames.add(record.get(colNames.indexOf("campaign_name")));
                            }
                            i++;
                        }
                    }
                }

                DataAccess.writeToJson("testData", searchTerms);
                System.out.println("search terms upload done");
                System.out.println(campaignNames);
                System.out.println(records.size());
                System.out.println(i);

                return true;
            } catch (IOException ex) {
                // ex.printStackTrace();
                System.out.println("File upload IOException");
                return false;
            } catch (CsvValidationException e) {
                // e.printStackTrace();
                System.out.println("File upload CsvValidationException");
                return false;
            }
        }
    }, 

    /** Handles CSV files containing campaign information. */
    ALL_CAMPAIGNS("csv", new String[]{"campaign_name", "campaign_label_name"}) {
        @Override
        public boolean read(File filePath) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'read'");
        }

        @Override
        public List<List<String>> readCsv(File filePath) throws CsvValidationException, IOException {
            return super.readCsv(filePath);
        }
    };
    // MASTER_LIST("xlsx", new HashMap<>(){{""}});
    
    /** The type of file this enum instance handles (e.g., "csv", "xlsx"). */
    public final String fileType;

    // public final HashMap<String, String[]> sheets;

    /** The expected column names for this file type. */
    public final String[] columns;

    /**
     * Constructs a FileUpload enum instance.
     * 
     * @param fileType the type of file (e.g., "csv")
     * @param columns the expected column names
     */
    private FileUpload(String fileType, String[] columns) {
        this.fileType = fileType;
        // this.sheets = new HashMap<>();
        this.columns = columns;
    }

    // private FileUpload(String fileType, HashMap<String, String[]> sheets) {
    //     this.fileType = fileType;
    //     this.sheets = sheets;
    //     this.columns = new String[]{};
    // }

    /**
     * Reads the specified file.
     * <p>
     * This method must be implemented by each enum instance.
     * </p>
     * 
     * @param filePath the file to read
     * @return true if the file was successfully read, false otherwise
     */
    public abstract boolean read(File filePath);

    /**
     * Checks if the file type matches the expected type.
     * 
     * @param filePath the file to check
     * @return true if the file type matches, false otherwise
     */
    public boolean checkFileType(File filePath) {
        return FilenameUtils.getExtension(filePath.getAbsolutePath()).equals(this.fileType);
    }

    /**
     * Reads the content of a CSV file and returns it as a list of lists of strings.
     * 
     * @param filePath the file to read
     * @return a list of records, where each record is a list of strings
     * @throws CsvValidationException if there is an error validating the CSV
     * @throws IOException if there is an error reading the file
     */
    public List<List<String>> readCsv(File filePath) throws CsvValidationException, IOException {
        List<List<String>> records = new ArrayList<>();
        CSVParser parser = new CSVParserBuilder().withEscapeChar('\0').build();
        CSVReader csvReader = new CSVReaderBuilder(new FileReader(filePath.getAbsolutePath())).withCSVParser(parser).build();
        String[] values;
        while ((values = csvReader.readNext()) != null) {
            records.add(Arrays.asList(values));
        }
        return records;
    }

    /**
     * Checks if the column names in the file match the expected columns.
     * 
     * @param colNames the list of column names from the file
     * @return true if all expected columns are present, false otherwise
     */
    public boolean checkColumnNames(List<String> colNames) {
        for (String s : this.columns) {
            if (!colNames.contains(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts a cell value to a string.
     * 
     * @param cell the cell to convert
     * @return the cell value as a string, or null if the cell is empty
     */
    public String cellToString(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == null) {
            return null;
        }
        else switch (cell.getCellType()) {
            case NUMERIC:
                return Double.toString(cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue();
            case BLANK:
                return "";
            default:
                return null;
        }
    }

    /**
     * Reads a column from an XLSX sheet and returns the values as a list of strings.
     * 
     * @param sheet the sheet to read from
     * @param col the index of the column to read
     * @return a list of values in the specified column
     */
    public ArrayList<String> readColumn(XSSFSheet sheet, int col) {
        ArrayList<String> list = new ArrayList<>();
        
        for (Row row : sheet) {
            Cell cell = row.getCell(col);
            if (this.cellToString(cell) == null) {
                //break;
            }
            else {
                list.add(this.cellToString(cell));
            }
        }

        System.out.println("read column"); 
        
        return list;
    }

    /**
     * Reads an XLSX file and returns it as a workbook.
     * 
     * @param filePath the file to read
     * @return the workbook representing the XLSX file
     * @throws IOException if there is an error reading the file
     */
    public XSSFWorkbook readXLSX(File filePath) throws IOException {
        FileInputStream fis =  new FileInputStream(filePath);             
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        return wb;
    }



    // public void readMasterList(XSSFWorkbook wb) {
    //     XSSFSheet sheet = wb.getSheet("Products By Category");
    //     LinkedHashMap<String, String> partNumbers = new LinkedHashMap<>();

    //     for (Row row : sheet) {
    //         Cell cell1 = row.getCell(0);
    //         Cell cell2 = row.getCell(4);
    //         if (this.cellToString(cell1) == null) {
    //             break;
    //         }
    //         else {
    //             partNumbers.put(this.cellToString(cell1), this.cellToString(cell2));
    //         }
    //     }
    //     partNumbers.remove("Part Number");

    //     LinkedHashMap<String, LinkedHashMap<String, Keyword>> categories = new LinkedHashMap<>();
    //     LinkedHashMap<String, Keyword> category = new LinkedHashMap<>();
    //     for (String s : partNumbers.keySet()) {
    //         if (s.equals("")) {
                
    //         }
    //         else if (partNumbers.get(s).equals("")) {
    //             category = new LinkedHashMap<>();
    //             categories.put(s, category);
    //         }
    //         else {
    //             Keyword kw = new Product(s, partNumbers.get(s));
    //             kw.tokenize();
    //             kw.computeTF();
    //             KeywordDictionary.getInstance().checkTokens(kw);
    //             category.put(s, kw);
    //         }
    //     }
    //     //categories.put(category);
    //     //categories.get(0).remove("Part Number");

    //     //System.out.println(categories.get("Binders").get("BSS-92588-603").keyword);
    //     DataAccess.writeToJson("masterList", categories);
    //     //KeywordDictionary.getInstance().updateProductCategories(categories);
    //     KeywordDictionary.getInstance().updateDict();
    // }

    // public void readAmazonTitlesAndBrandNames(XSSFWorkbook wb) {
    //     XSSFSheet sheet = wb.getSheet("Amazon Titles");
    //     LinkedHashMap<String, Keyword> skus = new LinkedHashMap<>();

    //     for (Row row : sheet) {
    //         Cell cell1 = row.getCell(0);
    //         Cell cell2 = row.getCell(1);
    //         if (this.cellToString(cell1) == null) {
    //             break;
    //         }
    //         else if (!this.cellToString(cell1).equals("SKU")) {
    //             Keyword kw = new Product(this.cellToString(cell1), this.cellToString(cell2));
    //             kw.tokenize();
    //             kw.computeTF();
    //             KeywordDictionary.getInstance().checkTokens(kw);
    //             skus.put(this.cellToString(cell1), kw);
    //         }
    //     }

    //     DataAccess.writeToJson("amazonTitles", skus);
    //     KeywordDictionary.getInstance().updateDict();

    //     // XSSFSheet sheet2 = wb.getSheet("Brand Names");

    //     // for (Row row : sheet2) {
    //     //     Cell cell1 = row.getCell(0);
    //     //     if (this.cellToString(cell1) == null) {
    //     //         break;
    //     //     }
    //     //     else {
    //     //         KeywordDictionary.getInstance().addOther("brand", this.cellToString(cell1).toLowerCase());
    //     //     }
    //     // }
    // }

    // public void readtest(XSSFWorkbook wb) {
    //     XSSFSheet sheet = wb.getSheet("Filters");
    //     ArrayList<SearchTerm> kws = DataAccess.getSearchTermList("keywordList");
    //     ArrayList<String> s1 = this.readColumn(sheet, 0);
    //     ArrayList<String> s2 = this.readColumn(sheet, 21);
    //     ArrayList<String> s3 = this.readColumn(sheet, 20);
    //     //System.out.println(s3);
    //     for (int i = 1; i < s1.size(); i++) {
    //         //kws.get(i - 1).addCVR(Double.parseDouble(s3.get(i + 1)));
    //         //System.out.println(kws.get(i - 1).keyword);
    //         //System.out.println(kws.get(i - 1).getCVR());
    //         //kws.add(new Keyword(s1.get(i), Double.parseDouble(s2.get(i  + 1))));
    //     }
    //     DataAccess.writeToJson("keywordList", kws);
    // }

    // TODO: Implement the readMasterList and readAmazonTitlesAndBrandNames methods when needed.
}
