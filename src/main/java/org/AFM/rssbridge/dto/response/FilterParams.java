package org.AFM.rssbridge.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class FilterParams {
    private List<String> source_names;
    private List<String> source_types;
    private List<String> tags;
}
