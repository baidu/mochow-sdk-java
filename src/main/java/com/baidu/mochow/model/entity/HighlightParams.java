package com.baidu.mochow.model.entity;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HighlightParams {
    private Map<String, HighlightParamField> fields;
    private List<String> preTags;
    private List<String> postTags;
    private Integer fragmentSize;
    private Integer numberOfFragments;
}
