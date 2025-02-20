package org.AFM.rssbridge.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ModelResponse {
    private List<DocumentScore> document_scores;
}
