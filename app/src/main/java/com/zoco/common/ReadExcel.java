package com.zoco.common;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class ReadExcel {

    public static List<String> readExcel(Context context) {// (String filename)
        // {
        Workbook workbook = null;
        Sheet sheet = null;
        Cell cell = null;
        ArrayList<String> univ = new ArrayList<String>();
        String file = "university.xls"; // filename.trim();

        try {
            InputStream is = context.getResources().getAssets()
                    .open("university.xls");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {
                sheet = workbook.getSheet(0);

                if (sheet != null) {
                    int rowStart = 0;
                    int rowEnd = sheet.getColumn(1).length - 1;
                    int columnStart = 0;
                    int columnEnd = sheet.getRow(1).length - 1;
                    String text = "";

                    for (int row = rowStart; row < rowEnd; row++) {
                        for (int column = columnStart; column < columnEnd; column++) {
                            text = sheet.getCell(column, row).getContents();
                            univ.add(text);
                            Log.d("NARA", text);
                        }
                    }
                } else {
                    Log.d("NARA", "Sheet is null");
                }
            } else {
                Log.d("NARA", "Workbook is null");
            }
        } catch (Exception e) {
            Log.d("NARA", "Exception");

        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }

        return univ;
    }
}
