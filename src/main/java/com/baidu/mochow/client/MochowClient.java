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
package com.baidu.mochow.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.baidu.mochow.auth.SignOptions;
import com.baidu.mochow.http.Headers;
import com.baidu.mochow.http.HttpMethodName;
import com.baidu.mochow.http.handler.HttpResponseHandler;
import com.baidu.mochow.http.handler.MochowJsonResponseHandler;
import com.baidu.mochow.http.handler.MochowMetadataResponseHandler;
import com.baidu.mochow.http.handler.MochowErrorResponseHandler;
import com.baidu.mochow.internal.InternalRequest;
import com.baidu.mochow.internal.RestartableInputStream;
import com.baidu.mochow.util.DateUtils;
import com.baidu.mochow.util.HttpUtils;
import com.baidu.mochow.util.JsonUtils;
import com.baidu.mochow.exception.MochowClientException;

import com.baidu.mochow.model.AbstractMochowRequest;
import com.baidu.mochow.model.AbstractMochowResponse;
import com.baidu.mochow.model.AddFieldRequest;
import com.baidu.mochow.model.AliasTableRequest;
import com.baidu.mochow.model.CreateDatabaseRequest;
import com.baidu.mochow.model.CreateIndexRequest;
import com.baidu.mochow.model.CreateTableRequest;
import com.baidu.mochow.model.DeleteRequest;
import com.baidu.mochow.model.DescribeIndexRequest;
import com.baidu.mochow.model.DescribeIndexResponse;
import com.baidu.mochow.model.DescribeTableRequest;
import com.baidu.mochow.model.DescribeTableResponse;
import com.baidu.mochow.model.InsertRequest;
import com.baidu.mochow.model.InsertResponse;
import com.baidu.mochow.model.ListDatabaseResponse;
import com.baidu.mochow.model.ListTableRequest;
import com.baidu.mochow.model.ListTableResponse;
import com.baidu.mochow.model.MochowConstants;
import com.baidu.mochow.model.ModifyIndexRequest;
import com.baidu.mochow.model.QueryRequest;
import com.baidu.mochow.model.QueryResponse;
import com.baidu.mochow.model.RebuildIndexRequest;
import com.baidu.mochow.model.SearchRequest;
import com.baidu.mochow.model.SearchResponse;
import com.baidu.mochow.model.SelectRequest;
import com.baidu.mochow.model.SelectResponse;
import com.baidu.mochow.model.ShowTableStatsRequest;
import com.baidu.mochow.model.ShowTableStatsResponse;
import com.baidu.mochow.model.UnaliasTableRequest;
import com.baidu.mochow.model.UpdateRequest;
import com.baidu.mochow.model.UpsertRequest;
import com.baidu.mochow.model.BatchSearchRequest;
import com.baidu.mochow.model.BatchSearchResponse;
import com.baidu.mochow.model.UpsertResponse;

/**
 * Provides the client for accessing the Baidu VDB Service.
 */
public class MochowClient extends AbstractMochowClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MochowClient.class);

    private static final String DATABASE_PREFIX = "database";
    private static final String TABLE_PREFIX = "table";
    private static final String INDEX_PREFIX = "index";
    private static final String ROW_PREFIX = "row";

    private static final String CREATE = "create";
    private static final String LIST = "list";
    private static final String DESC = "desc";
    private static final String ADD_FIELD = "addField";
    private static final String ALIAS = "alias";
    private static final String UNALIAS = "unalias";
    private static final String STATS = "stats";
    private static final String MODIFY = "modify";
    private static final String REBUILD = "rebuild";
    private static final String INSERT = "insert";
    private static final String UPSERT = "upsert";
    private static final String DELETE = "delete";
    private static final String QUERY = "query";
    private static final String SEARCH = "search";
    private static final String BATCH_SEARCH = "batchSearch";
    private static final String UPDATE = "update";
    private static final String SELECT = "select";

    private static final HttpResponseHandler[] MOCHOW_HANDLERS = new HttpResponseHandler[]{
            new MochowMetadataResponseHandler(),
            new MochowErrorResponseHandler(),
            new MochowJsonResponseHandler()
    };

    public MochowClient() {
        this(new ClientConfiguration());
    }

    public MochowClient(ClientConfiguration config) {
        super(config, MOCHOW_HANDLERS);
    }

    public void createDatabase(String databaseName) throws MochowClientException {
        CreateDatabaseRequest createDatabaseRequest = new CreateDatabaseRequest(databaseName);
        InternalRequest internalRequest = this.createRequest(createDatabaseRequest, HttpMethodName.POST, DATABASE_PREFIX);
        internalRequest.addParameter(CREATE, "");
        fillPayload(internalRequest, createDatabaseRequest);
        this.invokeHttpClient(internalRequest, AbstractMochowResponse.class);
    }

    public void dropDatabase(String databaseName) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(
                new AbstractMochowRequest() {}, HttpMethodName.DELETE, DATABASE_PREFIX);
        internalRequest.addParameter("database", databaseName);
        this.invokeHttpClient(internalRequest, AbstractMochowResponse.class);
    }

    public ListDatabaseResponse listDatabase() throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(
                new AbstractMochowRequest() {}, HttpMethodName.POST, DATABASE_PREFIX);
        internalRequest.addParameter(LIST, "");
        return this.invokeHttpClient(internalRequest, ListDatabaseResponse.class);
    }

    public boolean hasDatabase(String databaseName) throws MochowClientException {
        ListDatabaseResponse listDatabaseResponse = this.listDatabase();
        for (String database : listDatabaseResponse.getDatabases()) {
            if (database.equals(databaseName)) {
                return true;
            }
        }
        return false;
    }

    public void createTable(CreateTableRequest request) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, TABLE_PREFIX);
        internalRequest.addParameter(CREATE, "");
        fillPayload(internalRequest, request);
        this.invokeHttpClient(internalRequest, AbstractMochowResponse.class);
    }

    public boolean hasTable(String databaseName, String tableName) throws MochowClientException {
        if (!this.hasDatabase(databaseName)) {
            return false;
        }
        ListTableResponse listTableResponse = this.listTable(databaseName);
        for (String table : listTableResponse.getTables()) {
            if (table.equals(tableName)) {
                return true;
            }
        }
        return false;
    }

    public void dropTable(String databaseName, String tableName) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(
                new AbstractMochowRequest() {}, HttpMethodName.DELETE, TABLE_PREFIX);
        internalRequest.addParameter("database", databaseName);
        internalRequest.addParameter("table", tableName);
        this.invokeHttpClient(internalRequest, AbstractMochowResponse.class);
    }

    public ListTableResponse listTable(String databaseName) throws MochowClientException {
        ListTableRequest listTableRequest = new ListTableRequest(databaseName);
        InternalRequest internalRequest = this.createRequest(listTableRequest, HttpMethodName.POST, TABLE_PREFIX);
        internalRequest.addParameter(LIST, "");
        fillPayload(internalRequest, listTableRequest);
        return this.invokeHttpClient(internalRequest, ListTableResponse.class);
    }

    public DescribeTableResponse describeTable(String databaseName, String tableName) throws MochowClientException {
        DescribeTableRequest request = new DescribeTableRequest(databaseName, tableName);
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, TABLE_PREFIX);
        internalRequest.addParameter(DESC, "");
        fillPayload(internalRequest, request);
        return this.invokeHttpClient(internalRequest, DescribeTableResponse.class);
    }

    public void addField(AddFieldRequest request) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, TABLE_PREFIX);
        internalRequest.addParameter(ADD_FIELD, "");
        fillPayload(internalRequest, request);
        this.invokeHttpClient(internalRequest, AbstractMochowResponse.class);
    }

    public void aliasTable(AliasTableRequest request) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, TABLE_PREFIX);
        internalRequest.addParameter(ALIAS, "");
        fillPayload(internalRequest, request);
        this.invokeHttpClient(internalRequest, AbstractMochowResponse.class);
    }

    public void unaliasTable(UnaliasTableRequest request) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, TABLE_PREFIX);
        internalRequest.addParameter(UNALIAS, "");
        fillPayload(internalRequest, request);
        this.invokeHttpClient(internalRequest, AbstractMochowResponse.class);
    }

    public ShowTableStatsResponse showTableStats(String databaseName, String tableName) throws MochowClientException {
        ShowTableStatsRequest request = new ShowTableStatsRequest(databaseName, tableName);
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, TABLE_PREFIX);
        internalRequest.addParameter(STATS, "");
        fillPayload(internalRequest, request);
        return this.invokeHttpClient(internalRequest, ShowTableStatsResponse.class);
    }

    public void createIndex(CreateIndexRequest request) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, INDEX_PREFIX);
        internalRequest.addParameter(CREATE, "");
        fillPayload(internalRequest, request);
        this.invokeHttpClient(internalRequest, AbstractMochowResponse.class);
    }

    public DescribeIndexResponse describeIndex(String databaseName, String tableName, String indexName) throws MochowClientException {
        DescribeIndexRequest request = new DescribeIndexRequest(databaseName, tableName, indexName);
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, INDEX_PREFIX);
        internalRequest.addParameter(DESC, "");
        fillPayload(internalRequest, request);
        return this.invokeHttpClient(internalRequest, DescribeIndexResponse.class);
    }

    public void modifyIndex(ModifyIndexRequest request) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, INDEX_PREFIX);
        internalRequest.addParameter(MODIFY, "");
        fillPayload(internalRequest, request);
        this.invokeHttpClient(internalRequest, AbstractMochowResponse.class);
    }

    public void dropIndex(String databaseName, String tableName, String indexName) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(
            new AbstractMochowRequest() {}, HttpMethodName.DELETE, INDEX_PREFIX);
        internalRequest.addParameter("database", databaseName);
        internalRequest.addParameter("table", tableName);
        internalRequest.addParameter("indexName", indexName);
        this.invokeHttpClient(internalRequest, AbstractMochowResponse.class);
    }

    public void rebuildIndex(String databaseName, String tableName, String indexName) throws MochowClientException {
        RebuildIndexRequest request = new RebuildIndexRequest(databaseName, tableName, indexName);
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, INDEX_PREFIX);
        internalRequest.addParameter(REBUILD, "");
        fillPayload(internalRequest, request);
        this.invokeHttpClient(internalRequest, AbstractMochowResponse.class);
    }

    public InsertResponse insert(InsertRequest request) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, ROW_PREFIX);
        internalRequest.addParameter(INSERT, "");
        fillPayload(internalRequest, request);
        return this.invokeHttpClient(internalRequest, InsertResponse.class);
    }

    public UpsertResponse upsert(UpsertRequest request) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, ROW_PREFIX);
        internalRequest.addParameter(UPSERT, "");
        fillPayload(internalRequest, request);
        return this.invokeHttpClient(internalRequest, UpsertResponse.class);
    }

    public void delete(DeleteRequest request) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, ROW_PREFIX);
        internalRequest.addParameter(DELETE, "");
        fillPayload(internalRequest, request);
        this.invokeHttpClient(internalRequest, AbstractMochowResponse.class);
    }

    public QueryResponse query(QueryRequest request) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, ROW_PREFIX);
        internalRequest.addParameter(QUERY, "");
        fillPayload(internalRequest, request);
        return this.invokeHttpClient(internalRequest, QueryResponse.class);
    }

    public SearchResponse search(SearchRequest request) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, ROW_PREFIX);
        internalRequest.addParameter(SEARCH, "");
        fillPayload(internalRequest, request);
        return this.invokeHttpClient(internalRequest, SearchResponse.class);
    }

    public BatchSearchResponse batchSearch(BatchSearchRequest request) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, ROW_PREFIX);
        internalRequest.addParameter(BATCH_SEARCH, "");
        fillPayload(internalRequest, request);
        return this.invokeHttpClient(internalRequest, BatchSearchResponse.class);
    }

    public void update(UpdateRequest request) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, ROW_PREFIX);
        internalRequest.addParameter(UPDATE, "");
        fillPayload(internalRequest, request);
        this.invokeHttpClient(internalRequest, AbstractMochowResponse.class);
    }

    public SelectResponse select(SelectRequest request) throws MochowClientException {
        InternalRequest internalRequest = this.createRequest(request, HttpMethodName.POST, ROW_PREFIX);
        internalRequest.addParameter(SELECT, "");
        fillPayload(internalRequest, request);
        return this.invokeHttpClient(internalRequest, SelectResponse.class);
    }

    /**
     * Creates and initializes a new request object for the specified resource.
     *
     * @param bceRequest The original BCE request created by the user.
     * @param httpMethod The HTTP method to use when sending the request.
     * @param pathVariables The optional variables used in the URI path.
     * @return A new request object populated with endpoint, resource path and specific parameters to send.
     */
    protected InternalRequest createRequest(AbstractMochowRequest bceRequest,
                                            HttpMethodName httpMethod,
                                            String...pathVariables) {
        List<String> pathComponents = new ArrayList<String>();
        pathComponents.add(URL_PREFIX);
        if (pathVariables != null) {
            pathComponents.addAll(Arrays.asList(pathVariables));
        }
        InternalRequest request = new InternalRequest(httpMethod, HttpUtils.appendUri(this.getEndpoint(),
                pathComponents.toArray(new String[pathComponents.size()])));

        // add header
        request.addHeader(Headers.CONTENT_TYPE, MochowConstants.CONTENT_TYPE_JSON);
        request.addHeader(Headers.DATE, DateUtils.formatAlternateIso8601Date(new Date()));

        SignOptions signOptions = new SignOptions();
        request.setSignOptions(signOptions);
        return request;
    }

    /**
     * The method to fill the internalRequest's content field with mochowRequest.
     *
     * @param internalRequest A request object, populated with endpoint, resource path, ready for callers to populate
     *                        any additional headers or parameters, and execute.
     * @param mochowRequest   The original request, as created by the user.
     */
    private void fillPayload(InternalRequest internalRequest, AbstractMochowRequest mochowRequest) {
        byte[] content = toJson(mochowRequest);
        internalRequest.addHeader(Headers.CONTENT_LENGTH, String.valueOf(content.length));
        internalRequest.addHeader(Headers.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        internalRequest.setContent(RestartableInputStream.wrap(content));
    }

    private byte[] toJson(AbstractMochowRequest bceRequest) {
        try {
            String jsonStr = JsonUtils.toJsonString(bceRequest);
            return jsonStr.getBytes(DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new MochowClientException("Fail to get UTF-8 bytes", e);
        } catch (IllegalStateException e) {
            throw new MochowClientException("Fail to convert request to json", e);
        }
    }
}