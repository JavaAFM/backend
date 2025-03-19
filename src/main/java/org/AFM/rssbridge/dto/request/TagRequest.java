package org.AFM.rssbridge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class TagRequest {
    @Nullable
    private String tag;

    @JsonProperty("is_specific")
    private boolean is_specific;
    @Nullable
    private String tone;
}

