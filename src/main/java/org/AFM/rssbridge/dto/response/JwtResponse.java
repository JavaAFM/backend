package org.AFM.rssbridge.dto.response;

import lombok.Data;

@Data
public class JwtResponse {
    private String access;
    private String refresh;
    private String fio;
    private String iin;
    private String role;
}
