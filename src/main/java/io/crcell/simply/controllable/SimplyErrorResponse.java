package io.crcell.simply.controllable;

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
