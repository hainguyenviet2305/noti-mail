package org.prox.abmobemail.controller;

import lombok.AllArgsConstructor;
import org.prox.abmobemail.entity.AppInfo;
import org.prox.abmobemail.service.AppInfoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appInfos")
@AllArgsConstructor
@CrossOrigin("*")
public class AppInfoController {
    private AppInfoService appInfoService;

    @GetMapping
    public ResponseEntity<Page<AppInfo>> getAllAppInfos(Pageable pageable) {
        Page<AppInfo> appInfos = appInfoService.findAll(pageable);
        return new ResponseEntity<>(appInfos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AppInfo> createAppInfo(@RequestBody AppInfo appInfo) {
        AppInfo createdAppInfo = appInfoService.create(appInfo);
        return new ResponseEntity<>(createdAppInfo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppInfo> updateAppInfo(@PathVariable Long id, @RequestBody AppInfo appInfo) {
        AppInfo updatedAppInfo = appInfoService.update(id, appInfo);
        return new ResponseEntity<>(updatedAppInfo, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppInfo(@PathVariable Long id) {
        appInfoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
