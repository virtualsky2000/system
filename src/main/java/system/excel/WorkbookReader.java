package system.excel;

import java.io.File;
import java.util.List;
import java.util.Map;

import system.utils.FileUtils;

public class WorkbookReader extends AbstractWorkbookReader {

    public static WorkbookReader load(String fileName) {
        return load(FileUtils.getFile(fileName), null, null, 0);
    }

    public static WorkbookReader load(String fileName, int userMode) {
        return load(FileUtils.getFile(fileName), null, null, userMode);
    }

    public static WorkbookReader load(String fileName, List<String> lstSheetName) {
        return load(FileUtils.getFile(fileName), lstSheetName, null, 0);
    }

    public static WorkbookReader load(String fileName, List<String> lstSheetName, int userMode) {
        return load(FileUtils.getFile(fileName), lstSheetName, null, userMode);
    }

    public static WorkbookReader load(String fileName, Map<String, List<String>> mapRange) {
        return load(FileUtils.getFile(fileName), null, mapRange, 0);
    }

    public static WorkbookReader load(String fileName, Map<String, List<String>> mapRange, int userMode) {
        return load(FileUtils.getFile(fileName), null, mapRange, userMode);
    }

    public static WorkbookReader load(File file) {
        return load(file, null, null, 0);
    }

    public static WorkbookReader load(File file, int userMode) {
        return load(file, null, null, userMode);
    }

    public static WorkbookReader load(File file, List<String> lstSheetName) {
        return load(file, lstSheetName, null, 0);
    }

    public static WorkbookReader load(File file, List<String> lstSheetName, int userMode) {
        return load(file, lstSheetName, null, userMode);
    }

    public static WorkbookReader load(File file, Map<String, List<String>> mapRange) {
        return load(file, null, mapRange, 0);
    }

    public static WorkbookReader load(File file, Map<String, List<String>> mapRange, int userMode) {
        return load(file, null, mapRange, userMode);
    }

    public static WorkbookReader load(File file, List<String> lstSheetName,
            Map<String, List<String>> mapRange, int userMode) {
        WorkbookReader reader = new WorkbookReader(file, lstSheetName, mapRange, userMode);
        reader.load();

        return reader;
    }

    protected WorkbookReader(File file, List<String> lstSheetName, Map<String, List<String>> mapRange, int userMode) {
        super();
        this.file = file;
        this.lstSheetName = lstSheetName;
        this.mapRange = mapRange;
        this.userMode = userMode;
    }

    public void load() {
        if (getType() == 1) {
            // excel2007以降
            workbook = XSSFWorkbookReader.load(file, lstSheetName, mapRange, userMode).workbook;
        } else {
            // excel2003
            workbook = HSSFWorkbookReader.load(file, lstSheetName, mapRange, userMode).workbook;
        }
    }

}
