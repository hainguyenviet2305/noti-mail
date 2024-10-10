package org.prox.abmobemail.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.prox.abmobemail.entity.AppInfo;
import org.prox.abmobemail.repository.AppInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    @Autowired
    private AppInfoRepository appInfoRepository;

    // Phương thức import dữ liệu từ file Excel
    public List<AppInfo> importExcel(MultipartFile file) throws IOException {
        List<AppInfo> appInfoList = new ArrayList<>();

        // Đọc file Excel từ InputStream
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);  // Lấy sheet đầu tiên

            for (Row row : sheet) {
                if (row.getRowNum() == 0) { // Bỏ qua hàng tiêu đề
                    continue;
                }

                // Lấy các giá trị từ các cột tương ứng
                String appId = getCellValue(row.getCell(0));            // Cột 0: appId
                String appName = getCellValue(row.getCell(1));          // Cột 1: appName
                String po = getCellValue(row.getCell(2));               // Cột 2: po
                String marketing = getCellValue(row.getCell(4));        // Cột 4: marketing
                String leaderMarketing = getCellValue(row.getCell(6));  // Cột 6: leaderMarketing
                String leaderPo = getCellValue(row.getCell(8));         // Cột 8: leaderPo

                // Kiểm tra nếu ít nhất một trong các giá trị không rỗng thì thêm vào danh sách
                if (!appId.isEmpty() || !appName.isEmpty() || !po.isEmpty() ||
                        !marketing.isEmpty() || !leaderMarketing.isEmpty() || !leaderPo.isEmpty()) {

                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppId(appId);
                    appInfo.setAppName(appName);
                    appInfo.setPo(po);
                    appInfo.setMarketing(marketing);
                    appInfo.setLeaderMarketing(leaderMarketing);
                    appInfo.setLeaderPo(leaderPo);

                    appInfoList.add(appInfo);  // Thêm vào danh sách
                }
            }

            // Lưu tất cả bản ghi vào database
            appInfoRepository.saveAll(appInfoList);
        }

        return appInfoList;  // Trả về danh sách các bản ghi đã được thêm
    }

    // Phương thức lấy giá trị của một ô trong Excel
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();  // Nếu là chuỗi
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();  // Nếu là ngày tháng
                } else {
                    // Kiểm tra nếu là số nguyên thì loại bỏ phần thập phân
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);  // Chuyển thành số nguyên
                    } else {
                        return String.valueOf(numericValue);  // Nếu là số thực, giữ nguyên
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());  // Nếu là giá trị boolean
            case FORMULA:
                return cell.getCellFormula();  // Nếu là công thức
            default:
                return "";  // Trường hợp mặc định
        }
    }
}
