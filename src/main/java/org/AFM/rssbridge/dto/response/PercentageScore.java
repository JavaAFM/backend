package org.AFM.rssbridge.dto.response;

import lombok.Data;

@Data
public class PercentageScore {
    private double negative;
    private double positive;
    private long overall;
    private String tag;
}
