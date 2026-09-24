package com.baidu.mochow.model.entity;

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
public class DecayFunction {
    private String name;
    private String type;
    private String fieldName;
    private Number origin;
    private Number offset;
    private Number scale;
    private Number decayRate;
    private Number weight;
}
