package org.AFM.rssbridge.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.AFM.rssbridge.news.model.News;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
public class TagRequest {
    @Nullable
    private String tag;

    @JsonProperty("is_specific")
    private boolean is_specific;
}

