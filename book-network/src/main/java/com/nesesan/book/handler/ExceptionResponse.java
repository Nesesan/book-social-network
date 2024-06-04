package com.nesesan.book.handler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {

    private Integer businessErrorCode;
    private String businessErrorDescription;
    private String error;
    private Set<String> validationErrors;
    private Map<String,String> errors;

}
