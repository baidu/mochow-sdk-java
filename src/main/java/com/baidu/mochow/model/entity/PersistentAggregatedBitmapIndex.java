package com.baidu.mochow.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.baidu.mochow.model.enums.IndexType;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PersistentAggregatedBitmapIndex extends IndexField {
    private long fanoutBits;
    private long maxDepth;

    public PersistentAggregatedBitmapIndex(
            String indexName, String field, long fanoutBits, long maxDepth) {
        setIndexName(indexName);
        setField(field);
        setIndexType(IndexType.PERSISTENT_AGGREGATED_BITMAP);
        this.fanoutBits = fanoutBits;
        this.maxDepth = maxDepth;
    }
}
