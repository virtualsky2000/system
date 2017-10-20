package system.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.record.CellRecord;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;

import system.exception.ApplicationException;
import system.logging.LogManager;
import system.logging.Logger;

public abstract class AbstractWorkbookReader {

    private static final Pattern pCell = Pattern.compile("^([A-Z]+)(\\d+)$");

    private static final Pattern pRange = Pattern.compile("^(([A-Z]+)(\\d+)):(([A-Z]+)(\\d+))$");

    protected Logger log;

    protected Workbook workbook;

    protected File file;

    protected int userMode;

    protected List<String> lstSheetName;

    protected Map<String, List<String>> mapRange;

    protected Map<String, List<int[]>> mapSheetRange;

    protected List<int[]> lstCurSheetRange;

    protected int sheetCount;

    private int type = -1;

    public Iterator<Sheet> sheetIterator() {
        return workbook.iterator();
    }

    public Sheet getSheet(String name) {
        return workbook.getSheet(name);
    }

    public Sheet getSheet(int index) {
        return workbook.getSheetAt(index);
    }

    public int findEndRow(Sheet sheet, int rowIndex, int colIndex) {
        while (true) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                break;
            }
            Cell cell = row.getCell(colIndex);
            if (cell == null) {
                break;
            }
            if (cell.getCellTypeEnum() == CellType.BLANK) {
                break;
            }
            rowIndex++;
        }

        return rowIndex - 1;
    }

    public int findEndRow(Sheet sheet, String cell) {
        Point p = getPoint(cell);
        return findEndRow(sheet, p.row, p.col);
    }

    public int findEndCol(Sheet sheet, int rowIndex, int colIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return rowIndex;
        }

        while (true) {
            Cell cell = row.getCell(colIndex);
            if (cell == null) {
                break;
            }
            colIndex++;
        }

        return colIndex;
    }

    public String getCellValue(Sheet sheet, int rowIndex, int colIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return null;
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            return null;
        }
        return cell.getStringCellValue();
    }

    public String getCellValue(Sheet sheet, String cell) {
        Point p = getPoint(cell);
        return getCellValue(sheet, p.row, p.col);
    }

    public Object[][] readData(Sheet sheet, int startRow, int startCol, int endRow, int endCol) {
        Object[][] data = new Object[endRow - startRow + 1][endCol - startCol + 1];
        int i, j;

        for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            i = rowIndex - startRow;
            if (row != null) {
                for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    j = colIndex - startCol;
                    if (cell != null) {
                        switch (cell.getCellTypeEnum()) {
                        case STRING:
                            data[i][j] = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            data[i][j] = cell.getNumericCellValue();
                            break;
                        case BOOLEAN:
                            data[i][j] = cell.getBooleanCellValue();
                            break;
                        case BLANK:
                            data[i][j] = "";
                            break;
                        case FORMULA:

                            break;
                        default:
                            break;
                        }
                    } else {
                        data[i][j] = null;
                    }
                }
            } else {
                for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                    j = colIndex - startCol;
                    data[i][j] = null;
                }
            }
        }

        return data;
    }

    public List<Object[]> readValue(Sheet sheet, int startRow, int startCol, int endRow, int endCol) {
        List<Object[]> lstData = new ArrayList<>();

        for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            Object[] rowData = new Object[endCol - startCol + 1];
            if (row != null) {
                for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    int i = colIndex - startCol;
                    if (cell != null) {
                        switch (cell.getCellTypeEnum()) {
                        case STRING:
                            rowData[i] = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            rowData[i] = cell.getNumericCellValue();
                            break;
                        case BOOLEAN:
                            rowData[i] = cell.getBooleanCellValue();
                            break;
                        case BLANK:
                            rowData[i] = "";
                            break;
                        default:
                            break;
                        }
                    } else {
                        rowData[i] = null;
                    }
                }
            } else {
                Arrays.fill(rowData, 0, rowData.length, null);
            }

            lstData.add(rowData);
        }

        return lstData;
    }

    private void getCellValue(Cell cell, String[] rowData, int index) {
        if (cell != null) {
            switch (cell.getCellTypeEnum()) {
            case STRING:
                rowData[index] = cell.getStringCellValue();
                break;
            case NUMERIC:
                rowData[index] = Double.toString(cell.getNumericCellValue());
                break;
            case BOOLEAN:
                rowData[index] = Boolean.toString(cell.getBooleanCellValue());
                break;
            case BLANK:
                rowData[index] = "";
                break;
            default:
                break;
            }
        } else {
            rowData[index] = null;
        }
    }

    public List<String[]> readTextValue(Sheet sheet, int startRow, int startCol, int endRow, int endCol) {
        List<String[]> lstData = new ArrayList<>();

        for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            String[] rowData = new String[endCol - startCol + 1];
            if (row != null) {
                for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    getCellValue(cell, rowData, colIndex - startCol);
                }
            } else {
                Arrays.fill(rowData, 0, rowData.length, null);
            }

            lstData.add(rowData);
        }

        return lstData;
    }

    public List<String[]> readTextValue(Sheet sheet, int startRow, int startCol, int endRow, int endCol, int[] cols) {
        List<String[]> lstData = new ArrayList<>();

        for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            String[] rowData = new String[cols.length];
            if (row != null) {
                for (int i = 0; i < cols.length; i++) {
                    int colIndex = cols[i];
                    Cell cell = row.getCell(colIndex);
                    getCellValue(cell, rowData, i);
                }
            } else {
                Arrays.fill(rowData, 0, rowData.length, null);
            }

            lstData.add(rowData);
        }

        return lstData;
    }

    public List<String[]> readTextValue(Sheet sheet, String range) {
        Rect r = getRect(range);
        return readTextValue(sheet, r.x.row, r.x.col, r.y.row, r.y.col);
    }

    public List<String[]> readTextValue(Sheet sheet, String range, String[] columns) {
        Rect r = getRect(range);
        int len = columns.length;
        int[] cols = new int[len];
        for (int i = 0; i < len; i++) {
            cols[i] = getColumnIndex(columns[i]);
        }

        return readTextValue(sheet, r.x.row, r.x.col, r.y.row, r.y.col, cols);
    }

    public static int getColumnIndex(String str) {
        int col = 0;
        for (int i = 0, len = str.length(); i < len; i++) {
            char c = str.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                col = col * 26 + c - 64;
            } else {
                break;
            }
        }

        return col - 1;
    }

    private class Point {
        int row;
        int col;
    }

    private class Rect {
        Point x;
        Point y;
    }

    private Point getPoint(String cell) {
        Matcher m = pCell.matcher(cell);
        if (m.find()) {
            Point p = new Point();
            p.row = Integer.parseInt(m.group(2)) - 1;
            p.col = getColumnIndex(m.group(1));
            return p;
        }

        throw new ApplicationException("invalid cell string:" + cell);
    }

    private Rect getRect(String range) {
        Matcher m = pRange.matcher(range);
        if (m.find()) {
            Rect r = new Rect();
            r.x = getPoint(m.group(1));
            r.y = getPoint(m.group(4));
            return r;
        }

        throw new ApplicationException("invalid range string:" + range);
    }

    private int getExcelType() {
        InputStream inp = null;
        try {
            inp = new FileInputStream(file);
            if (!inp.markSupported()) {
                inp = new PushbackInputStream(inp, 8);
            }

            if (DocumentFactoryHelper.hasOOXMLHeader(inp)) {
                return 1;
            }

            byte[] header8 = IOUtils.peekFirst8Bytes(inp);

            // Try to create
            if (NPOIFSFileSystem.hasPOIFSHeader(header8)) {
                try (NPOIFSFileSystem fs = new NPOIFSFileSystem(inp)) {
                    DirectoryNode root = fs.getRoot();

                    // Encrypted OOXML files go inside OLE2 containers, is this one?
                    if (root.hasEntry(Decryptor.DEFAULT_POIFS_ENTRY)) {
                        return 1;
                    }
                }
            }

            return 0;
        } catch (IOException e) {
            throw new ApplicationException(e);
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(inp);
        }
    }

    public int getType() {
        if (type == -1) {
            type = getExcelType();
        }
        return type;
    }

    protected void setRange(Map<String, List<String>> mapRange) {
        this.mapRange = mapRange;
        if (mapRange != null) {
            lstSheetName = new ArrayList<>();
            mapSheetRange = new HashMap<>();
            for (Entry<String, List<String>> entry : mapRange.entrySet()) {
                List<String> _lstRange = entry.getValue();
                List<int[]> lstRange = new ArrayList<>();
                for (String range : _lstRange) {
                    Rect rect = getRect(range);
                    int[] x = new int[] { rect.x.row, rect.x.col, rect.y.row, rect.y.col };
                    lstRange.add(x);
                }
                mapSheetRange.put(entry.getKey(), lstRange);
                lstSheetName.add(entry.getKey());
            }
        }
    }

    protected boolean inRow(List<int[]> lstRange, RowRecord record) {
        return inRow(lstRange, record.getRowNumber());
    }

    protected boolean inRow(List<int[]> lstRange, int row) {
        for (int[] x : lstRange) {
            if (x[0] <= row && row <= x[2]) {
                return true;
            }
        }
        return false;
    }

    protected boolean inCell(List<int[]> lstRange, CellRecord record) {
        return inCell(lstRange, record.getRow(), record.getColumn());
    }

    protected boolean inCell(List<int[]> lstRange, int row, int col) {
        for (int[] x : lstRange) {
            if (x[0] <= row && row <= x[2] && x[1] <= col && col <= x[3]) {
                return true;
            }
        }
        return false;
    }

    protected AbstractWorkbookReader() {
        if (log == null) {
            log = LogManager.getLogger(this.getClass());
        }
    }

    protected AbstractWorkbookReader(File file, List<String> lstSheetName,
            Map<String, List<String>> mapRange, int userMode) {
        this();
        this.file = file;
        this.userMode = userMode;
        this.lstSheetName = lstSheetName;
        setRange(mapRange);
        if (this.lstSheetName != null) {
            sheetCount = this.lstSheetName.size();
        }
    }

}
