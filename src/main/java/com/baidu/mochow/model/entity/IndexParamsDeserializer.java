/*
 * Copyright 2024 Baidu, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.baidu.mochow.model.entity;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import com.baidu.mochow.model.enums.IndexType;
import com.baidu.mochow.util.JsonUtils;

public class IndexParamsDeserializer extends JsonDeserializer<IndexParams> {
    @Override
    public IndexParams deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JacksonException {
        String paramStr = p.getCodec().readTree(p).toString();
        Object o = p.getCurrentValue();
        if (o instanceof IndexField) {
            IndexField indexField = (IndexField) o;
            IndexParams params = null;
            if (indexField.isVectorIndex()) {
                // vector index
                switch (indexField.getIndexType()) {
                    case HNSW:
                        params = JsonUtils.fromJsonString(paramStr, HNSWParams.class);
                        break;
                    case HNSWPQ:
                        params = JsonUtils.fromJsonString(paramStr, HNSWPQParams.class);
                        break;
                    case HNSWSQ:
                        params = JsonUtils.fromJsonString(paramStr, HNSWSQParams.class);
                        break;
                    case PUCK:
                        params = JsonUtils.fromJsonString(paramStr, PUCKParams.class);
                        break;
                    case SPARSE_OPTIMIZED_FLAT:
                        params = null;
                        break;
                    case DISKANN:
                        params = JsonUtils.fromJsonString(paramStr, DiskANNParams.class);
                        break;
                    case IVF:
                        params = JsonUtils.fromJsonString(paramStr, IVFParams.class);
                        break;
                    case IVFSQ:
                        params = JsonUtils.fromJsonString(paramStr, IVFSQParams.class);
                        break;
                }
            } else if (indexField.getIndexType().equals(IndexType.INVERTED_INDEX)) {
                // inverted index
                params = JsonUtils.fromJsonString(paramStr, InvertedIndexParams.class);
            }
            return params;
        }
        return null;
    }
}
