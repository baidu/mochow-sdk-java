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
public class PersistentBitmapIndex extends IndexField {
    public PersistentBitmapIndex(String indexName, String field) {
        setIndexName(indexName);
        setField(field);
        setIndexType(IndexType.PERSISTENT_BITMAP);
    }
}
