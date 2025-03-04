package org.AFM.rssbridge.controller;

import lombok.AllArgsConstructor;
import org.AFM.rssbridge.dto.response.SourceDto;
import org.AFM.rssbridge.news.service.SourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class SourceController {
    private final SourceService sourceService;

    @GetMapping("/allSources")
    public ResponseEntity<List<SourceDto>> getAllSources(){
        return ResponseEntity.ok(sourceService.getAllSources());
    }
}
