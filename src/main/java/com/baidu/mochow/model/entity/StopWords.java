package com.baidu.mochow.model.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StopWords {
    private String mode;
    private List<String> words;

    public StopWords(String mode) {
        this.mode = mode;
    }
}
