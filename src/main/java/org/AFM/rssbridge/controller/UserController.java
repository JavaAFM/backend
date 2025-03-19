package org.AFM.rssbridge.controller;

import lombok.RequiredArgsConstructor;
import org.AFM.rssbridge.dto.response.FilterParams;
import org.AFM.rssbridge.news.model.NewsTag;
import org.AFM.rssbridge.user.model.User;
import org.AFM.rssbridge.news.service.NewsTagService;
import org.AFM.rssbridge.user.service.RSSUserDetailService;
import org.AFM.rssbridge.news.service.SourceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final RSSUserDetailService userDetailService;
    private final SourceService sourceService;
    private final NewsTagService newsTagService;

    @GetMapping("/allUsers")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userDetailService.getAllUsers(pageable));
    }

    @GetMapping("/filterParams")
    public ResponseEntity<FilterParams> filterParams(){
        FilterParams filterParams = new FilterParams();

        filterParams.setTags(newsTagService.getAll().stream().map(NewsTag::getTag).toList());
        filterParams.setSource_names(sourceService.allNames());
        filterParams.setSource_types(sourceService.allTypes());

        return ResponseEntity.ok(filterParams);
    }



}

