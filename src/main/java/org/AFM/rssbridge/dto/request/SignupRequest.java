package org.AFM.rssbridge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    private String iin;
    private String password;
    private String name;
    private String surname;
    private String fathername;
}
