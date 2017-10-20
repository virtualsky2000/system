package system.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.HSSFUserException;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.CellRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;

import system.exception.ApplicationException;
import system.utils.FileUtils;

public class HSSFWorkbookReader extends AbstractWorkbookReader {

    private SSTRecord sstRecord;

    private HSSFSheet curSheet;

    private HSSFRow curRow;

    private int curSheetIndex;

    private FormulaRecord stringFormulaRecord;

    private short previousSid;

    private String curSheetName;

    private List<String> sheetNames = new ArrayList<>();

    private boolean ignoreSheet = false;

    public static HSSFWorkbookReader load(String fileName) {
        return load(FileUtils.getFile(fileName), null, null, 0);
    }

    public static HSSFWorkbookReader load(String fileName, int userMode) {
        return load(FileUtils.getFile(fileName), null, null, userMode);
    }

    public static HSSFWorkbookReader load(String fileName, List<String> lstSheetName) {
        return load(FileUtils.getFile(fileName), lstSheetName, null, 0);
    }

    public static HSSFWorkbookReader load(String fileName, List<String> lstSheetName, int userMode) {
        return load(FileUtils.getFile(fileName), lstSheetName, null, userMode);
    }

    public static HSSFWorkbookReader load(String fileName, Map<String, List<String>> mapRange) {
        return load(FileUtils.getFile(fileName), null, mapRange, 0);
    }

    public static HSSFWorkbookReader load(String fileName, Map<String, List<String>> mapRange, int userMode) {
        return load(FileUtils.getFile(fileName), null, mapRange, userMode);
    }

    public static HSSFWorkbookReader load(File file) {
        return load(file, null, null, 0);
    }

    public static HSSFWorkbookReader load(File file, int userMode) {
        return load(file, null, null, userMode);
    }

    public static HSSFWorkbookReader load(File file, List<String> lstSheetName) {
        return load(file, lstSheetName, null, 0);
    }

    public static HSSFWorkbookReader load(File file, List<String> lstSheetName, int userMode) {
        return load(file, lstSheetName, null, userMode);
    }

    public static HSSFWorkbookReader load(File file, Map<String, List<String>> mapRange) {
        return load(file, null, mapRange, 0);
    }

    public static HSSFWorkbookReader load(File file, Map<String, List<String>> mapRange, int userMode) {
        return load(file, null, mapRange, userMode);
    }

    public static HSSFWorkbookReader load(File file, List<String> lstSheetName,
            Map<String, List<String>> mapRange, int userMode) {
        HSSFWorkbookReader reader = new HSSFWorkbookReader(file, lstSheetName, mapRange, userMode);
        reader.load();

        return reader;
    }

    protected HSSFWorkbookReader(File file, List<String> lstSheetName,
            Map<String, List<String>> mapRange, int userMode) {
        super(file, lstSheetName, mapRange, userMode);
    }

    public void load() {
        try {
            log.debug("load workbook start.");
            if (userMode == 0) {
                NPOIFSFileSystem fs = new NPOIFSFileSystem(file, true);
                workbook = new HSSFWorkbook(fs);
            } else {
                workbook = new HSSFWorkbook();
                HSSFRequest request = new HSSFRequest();

                addListener(request);

                HSSFEventFactory factory = new HSSFEventFactory();
                POIFSFileSystem poifs = new POIFSFileSystem(file, true);
                factory.abortableProcessWorkbookEvents(request, poifs);
            }
            log.debug("load workbook end.");
        } catch (HSSFUserException | IOException e) {
            throw new ApplicationException(e);
        }
    }

    protected void addListener(HSSFRequest request) {
        request.addListener(record -> processBOFRecord((BOFRecord) record), BOFRecord.sid);
        request.addListener(record -> processEOFRecord((EOFRecord) record), EOFRecord.sid);
        request.addListener(record -> processBoundSheetRecord((BoundSheetRecord) record), BoundSheetRecord.sid);
        request.addListener(record -> processSSTRecord((SSTRecord) record), SSTRecord.sid);

        request.addListener(record -> processRowRecord((RowRecord) record), RowRecord.sid);
        request.addListener(record -> processLabelSSTRecord((LabelSSTRecord) record), LabelSSTRecord.sid);
        request.addListener(record -> processNumberRecord((NumberRecord) record), NumberRecord.sid);
        request.addListener(record -> processFormulaRecord((FormulaRecord) record), FormulaRecord.sid);
        request.addListener(record -> processStringRecord((StringRecord) record), StringRecord.sid);

        // request.addListenerForAllRecords(new AbortableHSSFListener() {
        //
        // @Override
        // public short abortableProcessRecord(Record paramRecord) throws
        // HSSFUserException {
        // return HSSFWorkbookReader.this.abortableProcessRecord(paramRecord);
        // }
        //
        // });
    }

    protected short processBOFRecord(BOFRecord record) {
        int type = record.getType();
        if (type == BOFRecord.TYPE_WORKBOOK) {
            curSheetIndex = -1;
            log.debug("in processBOFRecord");
        } else if (type == BOFRecord.TYPE_WORKSHEET) {
            curSheetIndex++;
            curSheetName = sheetNames.get(curSheetIndex);

            if ((lstSheetName == null) || (lstSheetName != null && lstSheetName.contains(curSheetName))) {
                curSheet = (HSSFSheet) workbook.createSheet(curSheetName);
                log.debug("in processBOFRecord {}", curSheetName);
            } else if (lstSheetName != null && !lstSheetName.contains(curSheetName)) {
                curSheetName = null;
                ignoreSheet = true;
            }
        }
        return 0;
    }

    protected short processEOFRecord(EOFRecord record) {
        if (ignoreSheet) {
            return 0;
        }

        if (curSheetName != null) {
            log.debug("in processEOFRecord {}", curSheetName);
            sheetCount--;
            curSheet = null;
            curSheetName = null;
        } else {
            log.debug("in processEOFRecord");
        }

        if (lstSheetName != null && sheetCount == 0) {
            return -1;
        }
        return 0;
    }

    protected short processBoundSheetRecord(BoundSheetRecord record) {
        log.debug("in processBoundSheetRecord");
        sheetNames.add(record.getSheetname());
        return 0;
    }

    protected short processSSTRecord(SSTRecord record) {
        log.debug("in processSSTRecord");
        sstRecord = record;
        return 0;
    }

    protected short processRowRecord(RowRecord record) {
        if (ignoreSheet) {
            return 0;
        }

        if (mapSheetRange != null) {
            lstCurSheetRange = mapSheetRange.get(curSheetName);
            if (!inRow(lstCurSheetRange, record)) {
                return 0;
            }
        }

        log.debug("in processRowRecord {}", record.getRowNumber());
        curSheet.createRow(record.getRowNumber());
        return 0;
    }

    protected short processLabelSSTRecord(LabelSSTRecord record) {
        if (ignoreSheet || lstCurSheetRange == null || !inCell(lstCurSheetRange, record)) {
            return 0;
        }

        log.debug("in processLabelSSTRecord");
        HSSFCell cell = newCell(record);
        cell.setCellType(CellType.STRING);
        cell.setCellValue(sstRecord.getString(record.getSSTIndex()).getString());
        return 0;
    }

    protected short processNumberRecord(NumberRecord record) {
        if (ignoreSheet || lstCurSheetRange == null || !inCell(lstCurSheetRange, record)) {
            return 0;
        }

        log.debug("in processNumberRecord");
        HSSFCell cell = newCell(record);

        switch (record.getXFIndex()) {
        case 65:
            cell.setCellType(CellType.STRING);
            cell.setCellValue(WorkbookUtils.getDay((int) record.getValue(), "3"));
            break;
        case 66:
        case 67:
            cell.setCellType(CellType.STRING);
            cell.setCellValue(WorkbookUtils.getSec((int) (86400 * record.getValue())));
            break;
        default:
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(record.getValue());
        }
        return 0;
    }

    protected short processFormulaRecord(FormulaRecord record) {
        if (ignoreSheet || lstCurSheetRange == null || !inCell(lstCurSheetRange, record)) {
            return 0;
        }

        log.debug("in processFormulaRecord");
        if (record.hasCachedResultString()) {
            // The String itself should be the next record
            stringFormulaRecord = record;
        } else {
            // addTextCell(record, formatListener.formatNumberDateCell(formula));
        }
        return 0;
    }

    protected short processStringRecord(StringRecord record) {
        if (ignoreSheet || lstCurSheetRange == null
                || (previousSid == FormulaRecord.sid && !inCell(lstCurSheetRange, stringFormulaRecord))) {
            return 0;
        }

        log.debug("in processStringRecord");
        if (previousSid == FormulaRecord.sid) {
            // Cached string value of a string formula
            HSSFCell cell = newCell(stringFormulaRecord);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(record.getString());
        } else {
            // Some other string not associated with a cell, skip
        }
        return 0;
    }

    protected short abortableProcessRecord(Record record) throws HSSFUserException {
        if (curSheetName != null && lstSheetName != null && !lstSheetName.contains(curSheetName)
                && record.getSid() != BOFRecord.sid) {
            return 0;
        }

        short ret = 0;
        short sid = record.getSid();

        switch (sid) {
        case BOFRecord.sid:
            // start of workbook, worksheet etc. records
            ret = processBOFRecord((BOFRecord) record);
            break;
        case EOFRecord.sid:
            // end of workbook, worksheet etc. records
            ret = processEOFRecord((EOFRecord) record);
            break;
        case BoundSheetRecord.sid:
            // Worksheet index record
            ret = processBoundSheetRecord((BoundSheetRecord) record);
            break;
        case SSTRecord.sid:
            // holds all the strings for LabelSSTRecords
            ret = processSSTRecord((SSTRecord) record);
            break;
        case RowRecord.sid:
            ret = processRowRecord((RowRecord) record);
            break;
        case LabelSSTRecord.sid:
            // Ref. a string in the shared string table
            ret = processLabelSSTRecord((LabelSSTRecord) record);
            break;
        case NumberRecord.sid:
            // Contains a numeric cell value
            ret = processNumberRecord((NumberRecord) record);
            break;
        case FormulaRecord.sid:
            // Cell value from a formula
            ret = processFormulaRecord((FormulaRecord) record);
            break;
        case StringRecord.sid:
            processStringRecord((StringRecord) record);
            break;
        default:
            // log.debug("sid=" + sid);
            // log.debug(record);
            break;
        }

        return ret;
    }

    private HSSFCell newCell(CellRecord record) {
        int rowIndex = record.getRow();
        curRow = curSheet.getRow(rowIndex);
        if (curRow == null) {
            curRow = curSheet.createRow(rowIndex);
        }

        return curRow.createCell(record.getColumn());
    }

}
