package org.AFM.rssbridge.dto.request;

import lombok.Data;


@Data
public class AddSourceRequest {
    private String link;
    private String name;
    private String type = "telegram";
}

