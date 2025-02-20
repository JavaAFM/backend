package org.AFM.rssbridge.dto.request;

import lombok.Data;

@Data
public class NewsRequest {
    private FilterRequest filterRequest;
    private TagRequest tagRequest;
}
