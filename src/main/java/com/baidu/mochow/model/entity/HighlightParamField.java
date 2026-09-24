package com.baidu.mochow.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HighlightParamField {
    private Integer fragmentSize;
    private Integer numberOfFragments;

    public HighlightParamField(Integer fragmentSize, Integer numberOfFragments) {
        this.fragmentSize = fragmentSize;
        this.numberOfFragments = numberOfFragments;
    }
}
