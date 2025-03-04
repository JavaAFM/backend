package org.AFM.rssbridge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String access;
    private String refresh;
    private String fio;
    private String iin;
    private String role;
}
