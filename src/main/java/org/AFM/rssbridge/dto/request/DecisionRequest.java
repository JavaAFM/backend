package org.AFM.rssbridge.dto.request;

import lombok.Data;


@Data
public class DecisionRequest {
    private String status;
    private Long requestId;
    private String reason;
}

