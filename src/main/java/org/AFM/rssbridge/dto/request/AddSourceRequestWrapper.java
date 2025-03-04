package org.AFM.rssbridge.dto.request;

import lombok.Data;

@Data
public class AddSourceRequestWrapper {
    private AddSourceRequest request;
    private String iin;
}