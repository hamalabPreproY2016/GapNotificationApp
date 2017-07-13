package com.example.develop.gapnotificationapp;

import android.util.Log;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by develop on 2017/04/17.
 */

public class CSVManager {
    File mFile;

    FileOutputStream output = null;
    OutputStreamWriter oWriter = null;
    CSVWriter csvWriter = null;

    public static File createCSVDirectory(File parentDir) {
        File csvDir = new File(parentDir, "csv");

        if (!csvDir.exists() || !csvDir.isDirectory()) {
            csvDir.mkdir();
        }

        return csvDir;
    }

    public File getCSVFile() {
        return  mFile;
    }

    public CSVManager(File file) {
        mFile = file;
    }

    public void csvWriteForLine(CSVLineParser obj) {
        try {
            output = new FileOutputStream(mFile, true);
            oWriter = new OutputStreamWriter(output);
            csvWriter = new CSVWriter(oWriter);

            csvWriter.writeNext(obj.parseCSVLine(this));
            Log.d("csvWrite", obj.parseCSVLine(this)[0]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (csvWriter != null) {
                    csvWriter.close();
                }
                if (oWriter != null) {
                    oWriter.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void csvWrite(List<? extends CSVLineParser> list) {

        CSVWriter finalCsvWriter = csvWriter;
        list.forEach(s -> {
            finalCsvWriter.writeNext(s.parseCSVLine(this));
        });
    }

    public List csvRead(ParseObjectFactory factory) {
        FileReader reader = null;
        CSVReader csvReader = null;

        List retList = null;

        try {
            reader = new FileReader(mFile);
            csvReader = new CSVReader(reader);
            List<String[]> entries = csvReader.readAll();
            List finalRetList =  new ArrayList();
            entries.forEach(s -> {
                if (factory != null) {
                    CSVLineParser newObject = factory.create();
                    newObject.setPropertyFromCSVLine(this, s);
                    finalRetList.add(newObject);
                } else {
                    finalRetList.add(s);
                }
            });
            retList = finalRetList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (csvReader != null) {
                    csvReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return retList;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            super.finalize();
        } finally {
            try {
                if (csvWriter != null) {
                    csvWriter.close();
                }
                if (oWriter != null) {
                    oWriter.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface ParseObjectFactory {
        CSVLineParser create();
    }

    public interface CSVLineParser {
        String[] parseCSVLine(CSVManager manager);

        void setPropertyFromCSVLine(CSVManager manager, String[] strings);
    }
}
