package org.AFM.rssbridge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilterRequest {
    @Nullable
    private String source_name;
    @Nullable
    private String source_type;
    @Nullable
    private String title;
    @Nullable
    private List<String> tags;
    @Nullable
    private LocalDate from;
    @Nullable
    private LocalDate to;
    @Nullable
    private boolean neg;
}
