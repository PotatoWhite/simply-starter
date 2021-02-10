package io.easywalk.simply;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SimplyErrorResponse {
    private String originalExceptionType;
    private String description;
}
