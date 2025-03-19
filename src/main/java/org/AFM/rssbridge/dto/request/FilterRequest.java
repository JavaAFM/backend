package org.AFM.rssbridge.dto.request;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.List;

@Data
public class FilterRequest {
    @Nullable
    private String source_name;
    @Nullable
    private LocalDate from;
    @Nullable
    private LocalDate to;
    @Nullable
    private boolean neg;
    @Nullable
    private boolean pos;
    private float similarity;
}
