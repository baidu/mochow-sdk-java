/*
 * Copyright 2025 Baidu, Inc.
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

package com.baidu.mochow.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mochow.model.entity.Vector;
import com.baidu.mochow.model.entity.VectorSearchConfig;
import com.baidu.mochow.model.entity.AdvancedOptions;

public class VectorSearchFields extends SearchCommonFields {
    public String vectorField;
    public Vector vector;
    public List<Vector> vectors;
    public String textField;
    public String text;
    public List<String> texts;
    public boolean hasDistanceNear;
    public float distanceNear;
    public boolean hasDistanceFar;
    public float distanceFar;
    public VectorSearchConfig config;
    public AdvancedOptions advancedOptions;
    public String iteratedIds = "";
    public boolean hasIteratedIds;

    public void fillSearchFields(Map<String, Object> fields) {
        boolean hasTextInput = textField != null || text != null || texts != null;
        boolean hasVectorInput = vector != null || vectors != null;
        if (hasTextInput && hasVectorInput) {
            throw new IllegalArgumentException(
                    "text and vector search inputs are mutually exclusive");
        }
        if (hasTextInput && (textField == null || text == null && texts == null)) {
            throw new IllegalArgumentException("textField and text/texts are required");
        }
        if (text != null && texts != null) {
            throw new IllegalArgumentException("text and texts are mutually exclusive");
        }
        // anns.params
        Map<String, Object> params = new HashMap<>();
        if (hasDistanceNear) {
            params.put("distanceNear", distanceNear);
        }
        if (hasDistanceFar) {
            params.put("distanceFar", distanceFar);
        }
        if (hasLimit) {
            params.put("limit", limit);
        }
        if (config != null) {
            config.toMap().forEach(params::put);
        }

        // anns
        Map<String, Object> anns = new HashMap<>();
        if (vectorField != null && !vectorField.isEmpty()) {
            anns.put("vectorField", vectorField);
        }
        if (textField != null) {
            anns.put("textField", textField);
        }
        if (text != null) {
            anns.put("text", text);
        }
        if (texts != null) {
            anns.put("texts", texts);
        }
        if (vector != null) {
            anns.put(vector.name(), vector.representation());
        }
        if (vectors != null && !vectors.isEmpty()) {
            List<Object> vecs = new ArrayList<>();
            for (Vector v : vectors) {
                if (v != null) {
                    vecs.add(v.representation());
                }
            }
            if (!vecs.isEmpty()) {
                anns.put(vectors.get(0).name(), vecs);
            }
        }
        if (filter != null) {
            anns.put("filter", filter);
        }
        if (!params.isEmpty()) {
            anns.put("params", params);
        }
        if (!anns.isEmpty()) {
            fields.put("anns", anns);
        }

        // iteratedIds
        if (hasIteratedIds) {
            fields.put("iteratedIds", iteratedIds);
        }

        // common fields
        for (Map.Entry<String, Object> entry : toMap().entrySet()) {
            if (entry.getKey().equals("filter") || entry.getKey().equals("limit")) { // in "anns"
                continue;
            }
            fields.put(entry.getKey(), entry.getValue());
        }

        if (advancedOptions != null) {
            fields.put("advancedOptions", advancedOptions.toMap());
        }
    }
}
