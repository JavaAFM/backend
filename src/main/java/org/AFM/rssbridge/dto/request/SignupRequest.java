package org.AFM.rssbridge.dto.request;

import lombok.Data;

@Data
public class SignupRequest {
    private String iin;
    private String password;
    private String name;
    private String surname;
    private String fathername;
}
