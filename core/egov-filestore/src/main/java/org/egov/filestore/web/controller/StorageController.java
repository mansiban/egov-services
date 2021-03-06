package org.egov.filestore.web.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.ArrayList;
import java.util.List;

import org.egov.filestore.domain.service.StorageService;
import org.egov.filestore.web.contract.File;
import org.egov.filestore.web.contract.GetFilesByTagResponse;
import org.egov.filestore.web.contract.ResponseFactory;
import org.egov.filestore.web.contract.StorageResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("v1/files")
public class StorageController {

    private StorageService storageService;
    private ResponseFactory responseFactory;

    public StorageController(StorageService storageService, ResponseFactory responseFactory) {
        this.storageService = storageService;
        this.responseFactory = responseFactory;
    }

    @GetMapping("/id")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@RequestParam(value = "tenantId", required = true) String tenantId,
            @RequestParam("fileStoreId") String fileStoreId) {
        if (tenantId != null && !tenantId.isEmpty()) {
            org.egov.filestore.domain.model.Resource resource = storageService.retrieve(fileStoreId, tenantId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFileName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, resource.getContentType()).body(resource.getResource());
        }
        return null;
    }

    @GetMapping(value = "/tag", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public GetFilesByTagResponse getUrlListByTag(@RequestParam(value = "tenantId", required = true) String tenantId,
            @RequestParam("tag") String tag) {
        if (tenantId != null && !tenantId.isEmpty()) {
            return responseFactory.getFilesByTagResponse(storageService.retrieveByTag(tag, tenantId));
        }
        return null;
    }

    @PostMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public StorageResponse storeFiles(@RequestParam("file") List<MultipartFile> files,
            @RequestParam(value = "tenantId", required = true) String tenantId,
            @RequestParam("module") String module,
            @RequestParam(value = "tag", required = false) String tag) {
        final List<String> fileStoreIds = storageService.save(files, module, tag, tenantId);
        return getStorageResponse(fileStoreIds,tenantId);
    }

    private StorageResponse getStorageResponse(List<String> fileStorageIds,String tenantId) {
        List<File> files = new ArrayList<>();
        for(String fileStorageId : fileStorageIds){
           File f = new File(fileStorageId,tenantId);
           files.add(f);
        }
        return new StorageResponse(files);
    }
}
