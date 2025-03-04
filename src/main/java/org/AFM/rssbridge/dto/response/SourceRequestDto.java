package org.AFM.rssbridge.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SourceRequestDto {
    private Long id;
    private String reason;
    private String link;
    private String name;
    private LocalDateTime publishedDate;
    private String status;
    private String iin;
}
