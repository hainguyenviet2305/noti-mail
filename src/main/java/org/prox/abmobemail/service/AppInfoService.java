package org.prox.abmobemail.service;

import lombok.AllArgsConstructor;
import org.prox.abmobemail.entity.AppInfo;
import org.prox.abmobemail.exception.ResourceNotFoundException;
import org.prox.abmobemail.repository.AppInfoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AppInfoService {
    private AppInfoRepository appInfoRepository;

    public Page<AppInfo> findAll(Pageable pageable) {
        return appInfoRepository.findAll(pageable);
    }

    public AppInfo create(AppInfo appInfo) {
        return appInfoRepository.save(appInfo);
    }

    public AppInfo update(Long id, AppInfo appInfo) {
        Optional<AppInfo> existingAppInfo = appInfoRepository.findById(id);
        if (existingAppInfo.isPresent()) {
            AppInfo updatedAppInfo = existingAppInfo.get();
            updatedAppInfo.setAppId(appInfo.getAppId());
            updatedAppInfo.setAppName(appInfo.getAppName());
            updatedAppInfo.setPo(appInfo.getPo());
            updatedAppInfo.setMarketing(appInfo.getMarketing());
            updatedAppInfo.setLeaderMarketing(appInfo.getLeaderMarketing());
            updatedAppInfo.setLeaderPo(appInfo.getLeaderPo());
            return appInfoRepository.save(updatedAppInfo);
        } else {
            throw new ResourceNotFoundException("AppInfo not found with id " + id);
        }
    }

    public void delete(Long id) {
        appInfoRepository.deleteById(id);
    }
}
