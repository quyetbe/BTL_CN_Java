package util;

import model.ThoiKhoaBieu;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Tiện ích xuất dữ liệu báo cáo và thời khóa biểu ra file CSV/Excel UTF-8.
 * Gắn tiền tố UTF-8 BOM (\uFEFF) giúp Microsoft Excel hiển thị tiếng Việt có dấu chuẩn 100%.
 */
public class ExportUtil {

    /**
     * Xuất toàn bộ nội dung của JTable ra file CSV/Excel.
     */
    public static boolean exportTableToCSV(JTable table, File file, String title) {
        try (OutputStream os = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(writer)) {

            // Ghi UTF-8 BOM để Excel tự động nhận diện UTF-8
            os.write(0xEF);
            os.write(0xBB);
            os.write(0xBF);

            if (!ValidationUtil.isNullOrEmpty(title)) {
                bw.write(escapeCSV(title));
                bw.newLine();
                bw.newLine();
            }

            TableModel model = table.getModel();
            int colCount = model.getColumnCount();
            int rowCount = model.getRowCount();

            // Ghi Header
            for (int col = 0; col < colCount; col++) {
                bw.write(escapeCSV(model.getColumnName(col)));
                if (col < colCount - 1) {
                    bw.write(",");
                }
            }
            bw.newLine();

            // Ghi Rows
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    Object val = model.getValueAt(row, col);
                    bw.write(escapeCSV(val != null ? val.toString() : ""));
                    if (col < colCount - 1) {
                        bw.write(",");
                    }
                }
                bw.newLine();
            }

            bw.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Lỗi xuất CSV từ JTable: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xuất danh sách thời khóa biểu ra file CSV/Excel chi tiết.
     */
    public static boolean exportTimetableToCSV(List<ThoiKhoaBieu> list, File file, String title) {
        try (OutputStream os = new FileOutputStream(file);
             Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(writer)) {

            // Ghi UTF-8 BOM
            os.write(0xEF);
            os.write(0xBB);
            os.write(0xBF);

            if (!ValidationUtil.isNullOrEmpty(title)) {
                bw.write(escapeCSV(title));
                bw.newLine();
                bw.newLine();
            }

            // Headers
            String[] headers = {
                    "STT", "Mã Môn", "Tên Môn Học", "Loại Môn", "Mã Lớp", "Tên Lớp", "Sĩ Số",
                    "Giảng Viên", "Phòng Học", "Loại Phòng", "Thứ", "Tiết Học", "Số Tiết",
                    "Tuần Học", "Học Kỳ", "Năm Học", "Ghi Chú"
            };

            for (int i = 0; i < headers.length; i++) {
                bw.write(escapeCSV(headers[i]));
                if (i < headers.length - 1) bw.write(",");
            }
            bw.newLine();

            int index = 1;
            for (ThoiKhoaBieu tkb : list) {
                bw.write(escapeCSV(String.valueOf(index++)) + ",");
                bw.write(escapeCSV(tkb.getMaMon()) + ",");
                bw.write(escapeCSV(tkb.getTenMon()) + ",");
                bw.write(escapeCSV(tkb.getLoaiMon()) + ",");
                bw.write(escapeCSV(tkb.getMaLop()) + ",");
                bw.write(escapeCSV(tkb.getTenLop()) + ",");
                bw.write(escapeCSV(String.valueOf(tkb.getSiSoLop())) + ",");
                bw.write(escapeCSV(tkb.getHoTenGv()) + ",");
                bw.write(escapeCSV(tkb.getMaPhong() + " - " + tkb.getTenPhong()) + ",");
                bw.write(escapeCSV(tkb.getLoaiPhong()) + ",");
                bw.write(escapeCSV(tkb.getThuText()) + ",");
                bw.write(escapeCSV("Tiết " + tkb.getTietBatDau() + "-" + tkb.getTietKetThuc()) + ",");
                bw.write(escapeCSV(String.valueOf(tkb.getSoTiet())) + ",");
                bw.write(escapeCSV("Tuần " + tkb.getTuanBatDau() + "-" + tkb.getTuanKetThuc()) + ",");
                bw.write(escapeCSV(tkb.getHocKy()) + ",");
                bw.write(escapeCSV(tkb.getNamHoc()) + ",");
                bw.write(escapeCSV(tkb.getGhiChu() != null ? tkb.getGhiChu() : ""));
                bw.newLine();
            }

            bw.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Lỗi xuất Timetable ra CSV: " + e.getMessage());
            return false;
        }
    }

    private static String escapeCSV(String value) {
        if (value == null) return "\"\"";
        String str = value.replace("\"", "\"\"");
        return "\"" + str + "\"";
    }
}
