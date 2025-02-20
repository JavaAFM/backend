package org.AFM.rssbridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SourceDto {
    private String name;
    private String type;
    private String title;
    private String link;
}
