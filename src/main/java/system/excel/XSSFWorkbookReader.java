package system.excel;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import system.exception.ApplicationException;
import system.utils.FileUtils;

public class XSSFWorkbookReader extends AbstractWorkbookReader {

    private SharedStringsTable sst;

    private XSSFSheet curSheet;

    private XSSFRow curRow;

    private XSSFCell curCell;

    private String curValue;

    private AttributesImpl curAttributes = new AttributesImpl();

    private int curRowIndex;

    private boolean loadRow = true;

    private boolean loadCell = true;

    public static XSSFWorkbookReader load(String fileName) {
        return load(FileUtils.getFile(fileName), null, null, 0);
    }

    public static XSSFWorkbookReader load(String fileName, int userMode) {
        return load(FileUtils.getFile(fileName), null, null, userMode);
    }

    public static XSSFWorkbookReader load(String fileName, List<String> lstSheetName) {
        return load(FileUtils.getFile(fileName), lstSheetName, null, 0);
    }

    public static XSSFWorkbookReader load(String fileName, List<String> lstSheetName, int userMode) {
        return load(FileUtils.getFile(fileName), lstSheetName, null, userMode);
    }

    public static XSSFWorkbookReader load(String fileName, Map<String, List<String>> mapRange) {
        return load(FileUtils.getFile(fileName), null, mapRange, 0);
    }

    public static XSSFWorkbookReader load(String fileName, Map<String, List<String>> mapRange, int userMode) {
        return load(FileUtils.getFile(fileName), null, mapRange, userMode);
    }

    public static XSSFWorkbookReader load(File file) {
        return load(file, null, null, 0);
    }

    public static XSSFWorkbookReader load(File file, int userMode) {
        return load(file, null, null, userMode);
    }

    public static XSSFWorkbookReader load(File file, List<String> lstSheetName) {
        return load(file, lstSheetName, null, 0);
    }

    public static XSSFWorkbookReader load(File file, List<String> lstSheetName, int userMode) {
        return load(file, lstSheetName, null, userMode);
    }

    public static XSSFWorkbookReader load(File file, Map<String, List<String>> mapRange) {
        return load(file, null, mapRange, 0);
    }

    public static XSSFWorkbookReader load(File file, Map<String, List<String>> mapRange, int userMode) {
        return load(file, null, mapRange, userMode);
    }

    public static XSSFWorkbookReader load(File file, List<String> lstSheetName,
            Map<String, List<String>> mapRange, int userMode) {
        XSSFWorkbookReader reader = new XSSFWorkbookReader(file, lstSheetName, mapRange, userMode);
        reader.load();

        return reader;
    }

    protected XSSFWorkbookReader(File file, List<String> lstSheetName,
            Map<String, List<String>> mapRange, int userMode) {
        super(file, lstSheetName, mapRange, userMode);
    }

    public void load() {
        try {
            log.debug("load workbook start.");
            OPCPackage pkg = OPCPackage.open(file, PackageAccess.READ);
            if (userMode == 0) {
                if (lstSheetName == null) {
                    workbook = new XSSFWorkbook(pkg);
                } else {
                    workbook = new XSSFWorkbook(pkg) {

                        @Override
                        public void parseSheet(Map<String, XSSFSheet> shIdMap, CTSheet ctSheet) {
                            if (sheetCount > 0 && XSSFWorkbookReader.this.lstSheetName.contains(ctSheet.getName())) {
                                log.debug("load Sheet start: {}", ctSheet.getName());
                                super.parseSheet(shIdMap, ctSheet);
                                log.debug("load Sheet end: {}", ctSheet.getName());
                                sheetCount--;
                            }
                        }

                    };
                }

            } else {
                workbook = new XSSFWorkbook();

                XSSFReader reader = new XSSFReader(pkg);
                sst = reader.getSharedStringsTable();
                SheetIterator sheetIterator = (SheetIterator) reader.getSheetsData();

                while (sheetIterator.hasNext()) {
                    InputStream is = sheetIterator.next();
                    String sheetName = sheetIterator.getSheetName();
                    if (lstSheetName == null || lstSheetName.contains(sheetName)) {
                        curSheet = (XSSFSheet) workbook.createSheet(sheetName);
                        log.debug("load Sheet start: {}", sheetName);
                        if (mapSheetRange != null) {
                            lstCurSheetRange = mapSheetRange.get(sheetName);
                        }
                        XMLReader parser = XMLReaderFactory.createXMLReader();
                        ContentHandler handler = new SheetHandler();
                        parser.setContentHandler(handler);
                        parser.parse(new InputSource(is));
                        is.close();
                        log.debug("load Sheet end: {}", sheetName);
                        if (lstSheetName != null) {
                            sheetCount--;
                            if (sheetCount == 0) {
                                break;
                            }
                        }
                    }
                }
            }

            pkg.revert();
            log.debug("load workbook end.");
        } catch (Exception e) {
            throw new ApplicationException("Excelファイル読込が失敗しました。", e);
        }
    }

    protected void startRow(Attributes attributes) {
        int rownum = Integer.parseInt(attributes.getValue("r")) - 1;
        if (lstCurSheetRange != null) {
            loadRow = inRow(lstCurSheetRange, rownum);
        }
        if (loadRow) {
            log.debug("load row : {}", rownum);
            curRow = curSheet.createRow(rownum);
            curRowIndex = rownum;
            curCell = null;
        }
    }

    protected void startCell(Attributes attributes) {
        int columnIndex = getColumnIndex(attributes.getValue("r"));
        if (lstCurSheetRange != null) {
            loadCell = inCell(lstCurSheetRange, curRowIndex, columnIndex);
        }
        if (loadCell) {
            log.debug("load cell : {}", columnIndex);
            curCell = curRow.createCell(columnIndex);
            curAttributes.clear();
            curAttributes.setAttributes(attributes);
        } else {
            curCell = null;
        }
    }

    protected void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        curValue = "";

        if (qName.equals("row")) {
            // row
            startRow(attributes);
        } else if (qName.equals("c")) {
            // cell
            startCell(attributes);
        }
    }

    protected void endElement(String uri, String localName, String qName) throws SAXException {
        if (curCell != null) {
            String t = curAttributes.getValue("t");
            switch (qName) {
            case "v":
                if (t != null) {
                    // log.info("in endElement... t=({})", t);
                    switch (t) {
                    case "s":
                        curCell.setCellType(CellType.STRING);
                        int index = Integer.parseInt(curValue);
                        curCell.setCellValue(sst.getEntryAt(index).getT());
                        // log.info(sst.getEntryAt(index).getT());
                        break;
                    case "str":
                        curCell.setCellType(CellType.STRING);
                        curCell.setCellValue(curValue);
                        break;
                    case "e":
                        // エラー
                        curCell.setCellType(CellType.ERROR);
                        curCell.setCellErrorValue(FormulaError.forString(curValue));
                        break;
                    case "b":
                        // Boolean
                        curCell.setCellType(CellType.BOOLEAN);
                        curCell.setCellValue("1".equals(curValue));
                        break;
                    default:
                        // log.error("in endElement... t=({})", t);
                        break;
                    }

                } else {
                    String s = curAttributes.getValue("s");
                    // log.info("in endElement... s=({}) v=({})", s, curValue);

                    if (s == null) {
                        curCell.setCellType(CellType.STRING);
                        curCell.setCellValue(curValue);
                    } else {

                        switch (s) {
                        case WorkbookUtils.NUMERIC:
                            curCell.setCellType(CellType.NUMERIC);
                            curCell.setCellValue(curValue);
                            break;
                        case WorkbookUtils.DATE_YYYYMD: // 日付(yyyy/m/d)
                        case WorkbookUtils.DATE_YYYYMMDD: // 日付(yyyy/mm/dd)
                        case "8": // 日付(yyyy年mm月dd日)
                            // 日付
                            curCell.setCellType(CellType.STRING);
                            curCell.setCellValue(WorkbookUtils.getDay(Integer.parseInt(curValue), s));
                            break;
                        case "4":
                            //
                            curCell.setCellType(CellType.STRING);

                            break;
                        case "5":
                            // 時刻 (hh:mm:ss)
                            curCell.setCellType(CellType.STRING);
                            int sec = (int) (86400 * Double.parseDouble(curValue));
                            curCell.setCellValue(WorkbookUtils.getSec(sec));
                            break;
                        case "9":
                            curCell.setCellType(CellType.STRING);
                            curCell.setCellValue(curValue);
                            break;
                        default:
                            curCell.setCellType(CellType.STRING);
                            curCell.setCellValue(curValue);
                            break;
                        }
                    }

                }
                break;
            case "f":
                if (curValue != null && curValue.length() > 0) {
                    curCell.setCellFormula(curValue);
                }
                break;
            default:
                //				log.debug("in endElement... qName=({})", qName);
                break;
            }
        }
    }

    protected void characters(char[] ch, int start, int length) throws SAXException {
        curValue += new String(ch, start, length);
    }

    private class SheetHandler extends DefaultHandler {

        private XSSFWorkbookReader self = XSSFWorkbookReader.this;

        private int level = 0;

        private boolean flag = false;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            level++;
            if (level == 2) {
                flag = "sheetData".equals(qName);
            }
            if (flag && loadRow && loadCell) {
                self.startElement(uri, localName, qName, attributes);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            level--;
            if (flag && loadRow && loadCell) {
                self.endElement(uri, localName, qName);
            }
            if (level == 2 && qName.equals("row")) {
                loadRow = true;
            } else if (level == 3 && qName.equals("c")) {
                loadCell = true;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (flag && loadRow && loadCell) {
                self.characters(ch, start, length);
            }
        }

    }

}
