package org.prox.abmobemail.controller;
import org.prox.abmobemail.entity.AppInfo;
import org.prox.abmobemail.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcelFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Vui lòng chọn một file!", HttpStatus.BAD_REQUEST);
        }

        try {
            List<AppInfo> appInfoList = excelService.importExcel(file);
            return new ResponseEntity<>(appInfoList, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Có lỗi khi đọc file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
