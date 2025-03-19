package org.AFM.rssbridge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DocumentScore {
    private Long id;
    private double similarity_score;
    private String text;
    @JsonProperty("is_negative")
    private boolean is_negative;
    @JsonProperty("is_positive")
    private boolean is_positive;
    private double sentiment_score;
}
