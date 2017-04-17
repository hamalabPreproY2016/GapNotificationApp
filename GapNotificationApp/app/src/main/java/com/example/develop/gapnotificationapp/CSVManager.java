package com.example.develop.gapnotificationapp;

import com.example.develop.gapnotificationapp.experiment.SensorStruct;
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
import java.util.Objects;

/**
 * Created by develop on 2017/04/17.
 */

public class CSVManager {
    File mFile;

    public CSVManager(File file) {
        mFile = file;
    }

    public void csvWrite(List<SensorStruct.origin> list) throws IOException {
        FileOutputStream output = null;
        OutputStreamWriter oWriter = null;
        CSVWriter csvWriter = null;
        try {
            output = new FileOutputStream(mFile);
            oWriter = new OutputStreamWriter(output);
            csvWriter = new CSVWriter(oWriter);
            CSVWriter finalCsvWriter = csvWriter;
            list.forEach(s -> {
                finalCsvWriter.writeNext(s.toCSVStrings());
            });
            csvWriter.close();
            oWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (csvWriter != null) {
                csvWriter.close();
            }
            if (oWriter != null) {
                oWriter.close();
            }
            if (output != null) {
                output.close();
            }
        }
    }

    public List csvRead(ConvertFromStringsListener listener) {
        FileReader reader = null;
        CSVReader csvReader = null;

        List retList = null;

        try {
            reader = new FileReader(mFile);
            csvReader = new CSVReader(reader);
            List<String[]> entries = csvReader.readAll();
            List finalRetList =  new ArrayList();
            entries.forEach(s -> {
                if (listener != null) {
                    finalRetList.add(listener.convertToObjectFromStrings(s));
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

    public interface WriteCSVDelegate {
        public String[] writeLineStrings();
    }

    public interface ReadCSVDelegate {
        public Object convertToPojoObject(String[] strings);
    }

}
