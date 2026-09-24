package com.baidu.mochow.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mochow.client.ClientConfiguration;
import com.baidu.mochow.client.MochowClient;
import com.baidu.mochow.exception.MochowClientException;
import com.baidu.mochow.exception.MochowServiceException;
import com.baidu.mochow.model.AddFieldRequest;
import com.baidu.mochow.model.BM25SearchRequest;
import com.baidu.mochow.model.CreateTableRequest;
import com.baidu.mochow.model.CreateIndexRequest;
import com.baidu.mochow.model.DeleteRequest;
import com.baidu.mochow.model.DescribeIndexResponse;
import com.baidu.mochow.model.DescribeTableResponse;
import com.baidu.mochow.model.HybridSearchRequest;
import com.baidu.mochow.model.MultiVectorSearchRequest;
import com.baidu.mochow.model.QueryRequest;
import com.baidu.mochow.model.QueryResponse;
import com.baidu.mochow.model.SearchRowResponse;
import com.baidu.mochow.model.SelectRequest;
import com.baidu.mochow.model.SelectResponse;
import com.baidu.mochow.model.ShowTableStatsResponse;
import com.baidu.mochow.model.UpdateRequest;
import com.baidu.mochow.model.UpsertRequest;
import com.baidu.mochow.model.UpsertResponse;
import com.baidu.mochow.model.VectorBatchSearchRequest;
import com.baidu.mochow.model.VectorRangeSearchRequest;
import com.baidu.mochow.model.VectorTopkSearchRequest;
import com.baidu.mochow.model.SingleVectorSearchRequestInterface;
import com.baidu.mochow.model.SearchIterator;
import com.baidu.mochow.model.SearchIteratorArgs;
import com.baidu.mochow.model.entity.BinaryVector;
import com.baidu.mochow.model.entity.DistanceRange;
import com.baidu.mochow.model.entity.DecayFunction;
import com.baidu.mochow.model.entity.HighlightParams;
import com.baidu.mochow.model.entity.HighlightParamField;
import com.baidu.mochow.model.entity.StopWords;
import com.baidu.mochow.model.entity.PersistentBitmapIndex;
import com.baidu.mochow.model.entity.PersistentAggregatedBitmapIndex;
import com.baidu.mochow.model.entity.Field;
import com.baidu.mochow.model.entity.FilteringIndexField;
import com.baidu.mochow.model.entity.FloatVector;
import com.baidu.mochow.model.entity.HNSWParams;
import com.baidu.mochow.model.entity.HNSWPQParams;
import com.baidu.mochow.model.entity.HNSWSQParams;
import com.baidu.mochow.model.entity.HNSWRABITQParams;
import com.baidu.mochow.model.entity.DiskANNParams;
import com.baidu.mochow.model.entity.DiskANNRaBitQParams;
import com.baidu.mochow.model.entity.IVFParams;
import com.baidu.mochow.model.entity.IVFPQParams;
import com.baidu.mochow.model.entity.IVFSQParams;
import com.baidu.mochow.model.entity.InvertedIndex;
import com.baidu.mochow.model.entity.InvertedIndexParams;
import com.baidu.mochow.model.entity.FilteringIndex;
import com.baidu.mochow.model.entity.PartitionParams;
import com.baidu.mochow.model.entity.RRFRank;
import com.baidu.mochow.model.entity.Row;
import com.baidu.mochow.model.entity.RowField;
import com.baidu.mochow.model.entity.Schema;
import com.baidu.mochow.model.entity.SecondaryIndex;
import com.baidu.mochow.model.entity.SparseFloatVector;
import com.baidu.mochow.model.entity.Vector;
import com.baidu.mochow.model.entity.VectorIndex;
import com.baidu.mochow.model.entity.VectorSearchConfig;
import com.baidu.mochow.model.entity.WeightedRank;
import com.baidu.mochow.model.entity.SearchResultRow;
import com.baidu.mochow.model.entity.ArrayUpdateOperations;
import com.baidu.mochow.model.enums.ElementType;
import com.baidu.mochow.model.enums.FieldType;
import com.baidu.mochow.model.enums.IndexState;
import com.baidu.mochow.model.enums.IndexStructureType;
import com.baidu.mochow.model.enums.IndexType;
import com.baidu.mochow.model.enums.InvertedIndexAnalyzer;
import com.baidu.mochow.model.enums.InvertedIndexParseMode;
import com.baidu.mochow.model.enums.MetricType;
import com.baidu.mochow.model.enums.PartitionType;
import com.baidu.mochow.model.enums.ReadConsistency;
import com.baidu.mochow.model.enums.TableState;
import com.baidu.mochow.util.JsonUtils;

public class MochowExample {
    private static final String DATABASE = "book";
    private static final String TABLE = "book_segments";
    private static final String TABLE_ALIAS = "book_segments_alias";
    private MochowClient mochowClient;
    private IndexType vectorIndexType;

    public MochowExample(ClientConfiguration clientConfiguration) {
        this(clientConfiguration, IndexType.HNSW);
    }

    public MochowExample(ClientConfiguration clientConfiguration, IndexType vectorIndexType) {
        this.mochowClient = new MochowClient(clientConfiguration);
        this.vectorIndexType = vectorIndexType;
    }

    public void example() {
        try {
            this.clearEnv();
            System.out.println("clear vdb environment success");

            this.createDatabaseAndTable();
            System.out.println("create database and table success");

            this.upsertData();
            System.out.println("upsert data success");

            this.showTableStats();
            System.out.println("show table stats success");

            this.changeTableSchema();
            System.out.println("change table schema success");

            this.queryData();
            System.out.println("query data success");

            this.selectData();
            System.out.println("select data success");

            this.searchData();
            System.out.println("search data success");

            this.bm25AdvancedSearchExample();
            System.out.println("bm25 advanced search(highlight/synonyms/decay) success");

            this.vectorIndexMembershipExample();
            System.out.println("vector index membership query success");

            this.searchIteratorExample();
            System.out.println("search iterator example success");

            this.bm25AndHybridSearchIteratorExample();
            System.out.println("bm25/hybrid search iterator example success");

            this.createAndDropInvertedIndexExample();
            System.out.println("create/drop inverted index example success");

            this.updateData();
            System.out.println("update data success");

            this.deleteData();
            System.out.println("delete data success");

            this.binaryVectorUsageExample();
            System.out.println("binary vector example success");

            this.sparseVectorUsageExample();
            System.out.println("sparse vector example success");

            this.truncationDimensionExample();
            System.out.println("truncation dimension example success");

            this.persistentBitmapIndexExample();
            System.out.println("persistent bitmap index example success");

            this.deleteAndDrop();
            System.out.println("delete and drop table success");
        } catch (MochowServiceException e) {
            System.out.printf("Fail to execute example due to service error: %s\n", e.getMessage());
        } catch (InterruptedException e) {
            System.out.printf("Fail to execute example due to interrupted error: %s\n", e.getMessage());
        }
    }

    public void clearEnv() throws MochowClientException, InterruptedException {
        if (!mochowClient.hasDatabase(DATABASE)) {
            return;
        }
        // drop table
        if (mochowClient.hasTable(DATABASE, TABLE)) {
            mochowClient.dropTable(DATABASE, TABLE);
            boolean tableDropped = false;
            do {
                Thread.sleep(3000);
                try {
                    mochowClient.describeTable(DATABASE, TABLE);
                } catch (MochowServiceException e) {
                    if (e.getStatusCode() == 404) {
                        System.out.println("drop table finished");
                        tableDropped = true;
                    }
                }
            } while (!tableDropped);
        }
        // drop database
        mochowClient.dropDatabase(DATABASE);
    }

    public void createDatabaseAndTable() throws MochowClientException, InterruptedException {
        // create database
        mochowClient.createDatabase(DATABASE);

        // create table
        Schema.Builder schemaBuilder = Schema.builder()
                .addField(
                        Field.builder()
                                .fieldName("id")
                                .fieldType(FieldType.STRING)
                                .primaryKey(true)
                                .partitionKey(true)
                                .autoIncrement(false)
                                .notNull(true).build())
                .addField(
                        Field.builder()
                                .fieldName("bookName")
                                .fieldType(FieldType.STRING)
                                .notNull(true).build())
                .addField(
                        Field.builder()
                                .fieldName("author")
                                .fieldType(FieldType.STRING).build())
                .addField(
                        Field.builder()
                                .fieldName("page")
                                .fieldType(FieldType.UINT32).build())
                .addField(
                        Field.builder()
                                .fieldName("segment")
                                .fieldType(FieldType.TEXT).build())
                .addField(
                        Field.builder()
                                .fieldName("category")
                                .fieldType(FieldType.UINT32).build())
                .addField(
                        Field.builder()
                                .fieldName("vector")
                                .fieldType(FieldType.FLOAT_VECTOR)
                                .dimension(4).build())
                .addField(
                        Field.builder()
                                .fieldName("arr_field")
                                .fieldType(FieldType.ARRAY)
                                .elementType(ElementType.STRING).build())
                .addField(
                        Field.builder()
                                .fieldName("json_field")
                                .fieldType(FieldType.JSON).build());

        // 根据索引类型创建不同的向量索引
        if (IndexType.HNSW.equals(vectorIndexType)) {
            schemaBuilder.addIndex(
                    VectorIndex.builder()
                            .indexName("vector_idx")
                            .indexType(vectorIndexType)
                            .fieldName("vector")
                            .params(new HNSWParams(32, 200))
                            .metricType(MetricType.L2)
                            .autoBuild(false).build());
        } else if (IndexType.HNSWPQ.equals(vectorIndexType)) {
            schemaBuilder.addIndex(
                    VectorIndex.builder()
                            .indexName("vector_idx")
                            .indexType(IndexType.HNSWPQ)
                            .fieldName("vector")
                            .params(new HNSWPQParams(32, 200, 4, 0.1f))
                            .metricType(MetricType.L2)
                            .autoBuild(false).build());
        } else if (IndexType.HNSWSQ.equals(vectorIndexType)) {
            schemaBuilder.addIndex(
                VectorIndex.builder()
                        .indexName("vector_idx")
                        .indexType(IndexType.HNSWSQ)
                        .fieldName("vector")
                        .params(new HNSWSQParams(16, 200, 8))
                        .metricType(MetricType.L2)
                        .autoBuild(false).build());
        } else if (IndexType.HNSWRABITQ.equals(vectorIndexType)) {
            schemaBuilder.addIndex(
                VectorIndex.builder()
                        .indexName("vector_idx")
                        .indexType(IndexType.HNSWRABITQ)
                        .fieldName("vector")
                        .params(new HNSWRABITQParams(32, 200))
                        .metricType(MetricType.L2)
                        .autoBuild(false).build());
        } else if (IndexType.DISKANN.equals(vectorIndexType)) {
            schemaBuilder.addIndex(
                VectorIndex.builder()
                        .indexName("vector_idx")
                        .indexType(vectorIndexType)
                        .fieldName("vector")
                        .params(new DiskANNParams(4,100, 64))
                        .metricType(MetricType.L2)
                        .autoBuild(false).build());
        } else if (IndexType.DISKANNRABITQ.equals(vectorIndexType)) {
            schemaBuilder.addIndex(
                VectorIndex.builder()
                        .indexName("vector_idx")
                        .indexType(vectorIndexType)
                        .fieldName("vector")
                        .params(new DiskANNRaBitQParams(64, 100))
                        .metricType(MetricType.L2)
                        .autoBuild(false).build());
        } else if (IndexType.IVF.equals(vectorIndexType)) {
            schemaBuilder.addIndex(
                VectorIndex.builder()
                        .indexName("vector_idx")
                        .indexType(IndexType.IVF)
                        .fieldName("vector")
                        .params(new IVFParams(100))
                        .metricType(MetricType.L2)
                        .autoBuild(false).build());
        } else if (IndexType.IVFPQ.equals(vectorIndexType)) {
            schemaBuilder.addIndex(
                VectorIndex.builder()
                        .indexName("vector_idx")
                        .indexType(IndexType.IVFPQ)
                        .fieldName("vector")
                        .params(new IVFPQParams(100, 4))
                        .metricType(MetricType.L2)
                        .autoBuild(false).build());
        } else if (IndexType.IVFSQ.equals(vectorIndexType)) {
            schemaBuilder.addIndex(
                VectorIndex.builder()
                        .indexName("vector_idx")
                        .indexType(IndexType.IVFSQ)
                        .fieldName("vector")
                        .params(new IVFSQParams(100, 8))
                        .metricType(MetricType.L2)
                        .autoBuild(false).build());
        } else {
            throw new IllegalArgumentException("Unsupported index type: " + vectorIndexType);
        }

        schemaBuilder.addIndex(new SecondaryIndex("book_name_idx", "bookName"))
                .addIndex(new InvertedIndex(
                              "book_segment_inverted_idx",
                              new String[]{"segment"},
                              new InvertedIndexParams(
                                  InvertedIndexAnalyzer.ENGLISH_ANALYZER,
                                  InvertedIndexParseMode.FINE_MODE,
                                  true,
                                  new StopWords("CUSTOM", Arrays.asList("the", "a", "of")))))
                .addIndex(new FilteringIndex(
                              "bookname_filtering_idx",
                              new String[]{"bookName"}))
                .addIndex(FilteringIndex.builder()
                            .name("category_filtering_idx")
                            .addField(new FilteringIndexField("category", IndexStructureType.BITMAP))
                            // AGGREGATED_BITMAP 适用于数值列的范围过滤
                            .addField(new FilteringIndexField("page", IndexStructureType.AGGREGATED_BITMAP)).build());

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .database(DATABASE)
                .table(TABLE)
                .replication(1)
                .partition(new PartitionParams(PartitionType.HASH, 1))
                .description("test")
                .ttl(60)
                .schema(schemaBuilder.build()).build();
        mochowClient.createTable(createTableRequest);

        // wait for create table finished
        boolean tableCreated = false;
        do {
            Thread.sleep(3000);
            DescribeTableResponse describeResponse = mochowClient.describeTable(DATABASE, TABLE);
            if (describeResponse.getTable().getState() == TableState.NORMAL) {
                tableCreated = true;
                break;
            }
            System.out.printf("Describe table response: %s\n", JsonUtils.toJsonString(describeResponse));
        } while (!tableCreated);
    }

    public void upsertData() throws MochowClientException {
        List<Row> rows = new ArrayList<Row>();
        rows.add(
                Row.builder()
                        .addField(new RowField("id", "0001"))
                        .addField(new RowField("vector", Arrays.asList(1, 0.21, 0.213, 0)))
                        .addField(new RowField("bookName", "西游记"))
                        .addField(new RowField("author", "吴承恩"))
                        .addField(new RowField("page", 21))
                        .addField(new RowField("arr_field", Arrays.asList()))
                        .addField(new RowField("json_field", new HashMap<>()))
                        .addField(new RowField("segment", "富贵功名，前缘分定，为人切莫欺心。")).build()
        );
        rows.add(
                Row.builder()
                        .addField(new RowField("id", "0002"))
                        .addField(new RowField("vector", Arrays.asList(2, 0.22, 0.213, 0)))
                        .addField(new RowField("bookName", "西游记"))
                        .addField(new RowField("author", "吴承恩"))
                        .addField(new RowField("page", 22))
                        .addField(new RowField("arr_field", Arrays.asList()))
                        .addField(new RowField("json_field", new HashMap<String, Integer>() {{
                            put("page", 22);
                        }}))
                        .addField(new RowField("segment", "正大光明，忠良善果弥深。些些狂妄天加谴，眼前不遇待时临。")).build()
        );
        rows.add(
                Row.builder()
                        .addField(new RowField("id", "0003"))
                        .addField(new RowField("vector", Arrays.asList(3, 0.23, 0.213, 0)))
                        .addField(new RowField("bookName", "三国演义"))
                        .addField(new RowField("author", "罗贯中"))
                        .addField(new RowField("page", 23))
                        .addField(new RowField("arr_field", Arrays.asList("吕布")))
                        .addField(new RowField("segment", "细作探知这个消息，飞报吕布。"
                                + " Xuande heard the news of Lubu at Xuzhou.")).build()
        );
        rows.add(
                Row.builder()
                        .addField(new RowField("id", "0004"))
                        .addField(new RowField("vector", Arrays.asList(4, 0.23, 0.213, 0)))
                        .addField(new RowField("bookName", "三国演义"))
                        .addField(new RowField("author", "罗贯中"))
                        .addField(new RowField("page", 24))
                        .addField(new RowField("arr_field", Arrays.asList("吕布", "陈宫", "刘玄德")))
                        .addField(new RowField("segment", "布大惊，与陈宫商议。宫曰：\"闻刘玄德新领徐州，可往投之。\" 布从其言，竟投徐州来。有人报知玄德。")).build()
        );
        rows.add(
                Row.builder()
                        .addField(new RowField("id", "0005"))
                        .addField(new RowField("vector", Arrays.asList(5, 0.23, 0.213, 0)))
                        .addField(new RowField("bookName", "三国演义"))
                        .addField(new RowField("author", "罗贯中"))
                        .addField(new RowField("page", 25))
                        .addField(new RowField("arr_field", Arrays.asList("玄德", "糜竺", "吕布")))
                        .addField(new RowField("segment", "玄德曰：\"布乃当今英勇之士，可出迎之。\"糜竺曰：\"吕布乃虎狼之徒，不可收留；收则伤人矣。")).build()
        );
        for (int i = 6; i <= 100; i++) {
            rows.add(
                    Row.builder()
                            .addField(new RowField("id", String.valueOf(i)))
                            .addField(new RowField("vector", Arrays.asList(0.2f + i * 0.01, 0.23, 0.213, 0)))
                            .addField(new RowField("bookName", "三国演义"))
                            .addField(new RowField("author", "罗贯中"))
                            .addField(new RowField("page", 26))
                            .addField(new RowField("arr_field", Arrays.asList("玄德", "糜竺", "吕布")))
                            .addField(new RowField("segment", 
                            "玄德曰：\"布乃当今英勇之士，可出迎之。\"糜竺曰：\"吕布乃虎狼之徒，不可收留；收则伤人矣。"
                            + " Xuande said Lubu was a brave man of the time.")).build()
            );
        }
        UpsertRequest upsertRequest = UpsertRequest.builder().database(DATABASE).table(TABLE).rows(rows).build();
        UpsertResponse upsertResponse = mochowClient.upsert(upsertRequest);
        System.out.printf("Upsert affected count:%d\n", upsertResponse.getAffectedCount());
    }

    public void showTableStats() throws MochowClientException {
        ShowTableStatsResponse showTableStatsResponse = mochowClient.showTableStats(DATABASE, TABLE);
        System.out.printf("Total row count: %d\n", showTableStatsResponse.getRowCount());
    }

    public void queryData() throws MochowClientException {
        QueryRequest queryRequest = QueryRequest.builder()
                .database(DATABASE)
                .table(TABLE)
                .retrieveVector(true)
                .addPrimaryKey("id", "0005")
                .readConsistency(ReadConsistency.STRONG)
                .projections(Arrays.asList("id", "bookName")).build();
        QueryResponse queryResponse = mochowClient.query(queryRequest);
        System.out.printf("Query result: %s\n", JsonUtils.toJsonString(queryResponse.getRow()));
    }

    public void selectData() throws MochowClientException {
        SelectRequest selectRequest = SelectRequest.builder()
                .database(DATABASE)
                .table(TABLE)
                .limit(30)
                .readConsistency(ReadConsistency.STRONG)
                .projections(Arrays.asList("id", "bookName")).build();
        while (true) {
            SelectResponse selectResponse = mochowClient.select(selectRequest);
            System.out.printf("Select result count: %d\n", selectResponse.getRows().size());
            if (!selectResponse.isTruncated()) {
                break;
            }
            selectRequest.setMarker(selectResponse.getNextMarker());
        }
    }

    public void searchData() throws MochowClientException, InterruptedException {
        // rebuild index
        mochowClient.rebuildIndex(DATABASE, TABLE, "vector_idx");

        // wait for index rebuild finished and search
        while (true) {
            Thread.sleep(3000);
            DescribeIndexResponse describeIndexResponse = mochowClient.describeIndex(DATABASE, TABLE, "vector_idx");
            if (describeIndexResponse.getIndex().getState() == IndexState.NORMAL) {
                System.out.println("Index rebuild finished");
                break;
            }
            System.out.printf("Index rebuild state: %s\n", describeIndexResponse.getIndex().getState());
        }

        this.topkSearch();
        this.rangeSearch();
        this.batchSearch();
        this.multiVectorSearch();
        this.bm25Search();
        this.hybridSearch();
    }

    public void topkSearch() throws MochowClientException, InterruptedException {
        FloatVector vector = new FloatVector(Arrays.asList(1F, 0.21F, 0.213F, 0F));
        VectorTopkSearchRequest.Builder searchRequestBuilder = VectorTopkSearchRequest.builder("vector", vector, 10)
                .filter("bookName='三国演义'");
        if (IndexType.HNSW.equals(this.vectorIndexType)) {
            searchRequestBuilder = searchRequestBuilder.config(VectorSearchConfig.builder().ef(200).pruning(true).build());
        } else if (IndexType.HNSWPQ.equals(this.vectorIndexType)) {
            searchRequestBuilder = searchRequestBuilder.config(VectorSearchConfig.builder().ef(200).build());
        } else if (IndexType.HNSWSQ.equals(this.vectorIndexType)) {
            searchRequestBuilder = searchRequestBuilder.config(VectorSearchConfig.builder().ef(200).build());
        } else if (IndexType.HNSWRABITQ.equals(this.vectorIndexType)) {
            searchRequestBuilder = searchRequestBuilder.config(VectorSearchConfig.builder().ef(200).build());
        } else if (IndexType.DISKANN.equals(this.vectorIndexType)) {
            searchRequestBuilder = searchRequestBuilder.config(VectorSearchConfig.builder().w(1).searchL(100).build());
        } else if (IndexType.DISKANNRABITQ.equals(this.vectorIndexType)) {
            searchRequestBuilder = searchRequestBuilder.config(VectorSearchConfig.builder().w(1).searchL(100).build());
        } else if (IndexType.IVF.equals(this.vectorIndexType) || IndexType.IVFPQ.equals(this.vectorIndexType)
                || IndexType.IVFSQ.equals(this.vectorIndexType)) {
            searchRequestBuilder = searchRequestBuilder.config(VectorSearchConfig.builder().nprobe(10).build());
        } else {
            throw new IllegalArgumentException("Unknown index type: " + this.vectorIndexType);
        }
        
        VectorTopkSearchRequest searchRequest = searchRequestBuilder.build();
        SearchRowResponse searchResponse = mochowClient.vectorSearch(DATABASE, TABLE, searchRequest);
        System.out.printf("TopkSearch result: %s\n", JsonUtils.toJsonString(searchResponse.getRows()));
    }

    public void rangeSearch() throws MochowClientException, InterruptedException {
        FloatVector vector = new FloatVector(Arrays.asList(1F, 0.21F, 0.213F, 0F));
        VectorRangeSearchRequest searchRequest =
            VectorRangeSearchRequest.builder("vector", vector, new DistanceRange(0, 20))
                .filter("bookName='三国演义'")
                .limit(15)
                .projections(Arrays.asList("id", "vector"))
                .config(VectorSearchConfig.builder().ef(200).build())
                .build();

        SearchRowResponse searchResponse = mochowClient.vectorSearch(DATABASE, TABLE, searchRequest);
        System.out.printf("RangeSearch result: %s\n", JsonUtils.toJsonString(searchResponse.getRows()));
    }

    public void multiVectorSearch() throws MochowClientException, InterruptedException {
        List<SingleVectorSearchRequestInterface> requests = new ArrayList<>();

        // in real world senario, you should use vectors in different columns
        requests.add(
            VectorTopkSearchRequest.builder(
                "vector", new FloatVector(Arrays.asList(1F, 0.21F, 0.213F, 0F)), 10
            )
            .config(VectorSearchConfig.builder().ef(200).build())
            .build()
        );

        requests.add(
            VectorTopkSearchRequest.builder(
                "vector", new FloatVector(Arrays.asList(1F, 0.21F, 0.213F, 0F)), 10
            )
            .config(VectorSearchConfig.builder().ef(200).build())
            .build()
        );

        MultiVectorSearchRequest searchRequest = MultiVectorSearchRequest.builder(requests)
            .rankPolicy(new RRFRank(60))
            .limit(10)
            .filter("bookName='三国演义'")
            .projections(Arrays.asList("id"))
            .build();

        SearchRowResponse searchResponse = mochowClient.vectorSearch(DATABASE, TABLE, searchRequest);
        System.out.printf("MultiVectorSearch result: %s\n", JsonUtils.toJsonString(searchResponse.getRows()));
    }

    public void batchSearch() throws MochowClientException, InterruptedException {
        List<Vector> vectors = Arrays.asList(
            new FloatVector(Arrays.asList(1F, 0.21F, 0.213F, 0F)),
            new FloatVector(Arrays.asList(1F, 0.32F, 0.513F, 0F))
        );
        VectorBatchSearchRequest searchRequest = VectorBatchSearchRequest.builder("vector", vectors)
            .filter("bookName='三国演义'")
            .limit(10)
            .config(VectorSearchConfig.builder().ef(200).build())
            .projections(Arrays.asList("id"))
            .build();

        SearchRowResponse searchResponse = mochowClient.vectorSearch(DATABASE, TABLE, searchRequest);
        System.out.printf("BatchSearch result: %s\n", JsonUtils.toJsonString(searchResponse.getBatchRows()));
    }

    public void bm25Search() throws MochowClientException, InterruptedException {
        BM25SearchRequest searchRequest = BM25SearchRequest.builder("book_segment_inverted_idx", "吕布")
            .filter("bookName='三国演义'")
            .limit(10)
            .projections(Arrays.asList("id", "segment"))
            .build();

        SearchRowResponse searchResponse = mochowClient.bm25Search(DATABASE, TABLE, searchRequest);
        System.out.printf("BM25Search result: %s\n", JsonUtils.toJsonString(searchResponse.getRows()));
    }

    public void hybridSearch() throws MochowClientException, InterruptedException {
        FloatVector vector = new FloatVector(Arrays.asList(1F, 0.21F, 0.213F, 0F));
        VectorTopkSearchRequest vectorRequest = VectorTopkSearchRequest.builder("vector", vector, 10)
            .config(VectorSearchConfig.builder().ef(200).build())
            .build();
        BM25SearchRequest bm25Request = BM25SearchRequest.builder("book_segment_inverted_idx", "吕布").build();
        HybridSearchRequest searchRequest = HybridSearchRequest.builder(vectorRequest, bm25Request, 0.4F, 0.6F)
            .filter("bookName='三国演义'")
            .limit(10)
            .projections(Arrays.asList("id"))
            .build();

        SearchRowResponse searchResponse = mochowClient.hybridSearch(DATABASE, TABLE, searchRequest);
        System.out.printf("HybridSearch result: %s\n", JsonUtils.toJsonString(searchResponse.getRows()));
    }

    public void changeTableSchema() throws MochowClientException {
        // add new field
        AddFieldRequest addFieldRequest = AddFieldRequest.builder()
                .database(DATABASE)
                .table(TABLE)
                .schema(
                        Schema.builder()
                                .addField(Field.builder().fieldName("publisher").fieldType(FieldType.STRING).build())
                                .addField(Field.builder().fieldName("synopsis").fieldType(FieldType.STRING).build())
                                .build()
                ).build();
        mochowClient.addField(addFieldRequest);

        // get new table schema
        DescribeTableResponse describeTableResponse = mochowClient.describeTable(DATABASE, TABLE);
        System.out.printf("New table schema: %s\n", JsonUtils.toJsonString(describeTableResponse.getTable()));
    }

    public void updateData() throws MochowClientException {
        UpdateRequest updateRequest = UpdateRequest.builder()
                .database(DATABASE)
                .table(TABLE)
                .addPrimaryKey("id", "0001")
                .addUpdate("bookName", "红楼梦")
                .addUpdate("author", "曹雪芹")
                .addUpdate("page", 21)
                .addUpdate("arr_field", new ArrayUpdateOperations.Append(Arrays.asList("林黛玉", "薛宝钗")))
                .addUpdate("segment", "满纸荒唐言，一把辛酸泪").build();
        mochowClient.update(updateRequest);
    }

    public void deleteData() throws MochowClientException {
        DeleteRequest deleteRequest = DeleteRequest.builder()
                .database(DATABASE)
                .table(TABLE)
                .addPrimaryKey("id", "0001")
                .build();
        mochowClient.delete(deleteRequest);

        ShowTableStatsResponse showTableStatsResponse = mochowClient.showTableStats(DATABASE, TABLE);
        System.out.printf("Total row count %d after deleted\n", showTableStatsResponse.getRowCount());
    }

    public void deleteAndDrop() throws MochowClientException, InterruptedException {
        // drop table
        mochowClient.dropTable(DATABASE, TABLE);
        boolean tableDropped = false;
        while (!tableDropped) {
            Thread.sleep(3000);
            try {
                mochowClient.describeTable(DATABASE, TABLE);
            } catch (MochowServiceException e) {
                if (e.getStatusCode() == 404) {
                    tableDropped = true;
                }
            }
        }

        // drop database
        mochowClient.dropDatabase(DATABASE);
    }

    // 将数字转换为二进制表示，返回0和1的列表
    private List<Integer> numToBinaryList(int num, int dimension) {
        String binaryStr = Integer.toBinaryString(num);
        List<Integer> binaryList = new ArrayList<>();
        for (char bit : binaryStr.toCharArray()) {
            binaryList.add(Character.getNumericValue(bit));
        }
        
        if (binaryList.size() > dimension) {
            binaryList = binaryList.subList(0, dimension);
        } else if (binaryList.size() < dimension) {
            List<Integer> tmp = new ArrayList<>();
            for (int i = 0; i < dimension - binaryList.size(); i++) {
                tmp.add(0);
            }
            tmp.addAll(binaryList);
            binaryList = tmp;
        }
        return binaryList;
    }

    public void binaryVectorUsageExample() throws MochowClientException, InterruptedException {
        // 1. 创建表
        String database = "test_binary_vec";
        String tableName = "test_binary_vec_tab";

        // 清理环境
        if (mochowClient.hasDatabase(database)) {
            if (mochowClient.hasTable(database, tableName)) {
                mochowClient.dropTable(database, tableName);
                boolean tableDropped = false;
                do {
                    Thread.sleep(3000);
                    try {
                        mochowClient.describeTable(database, tableName);
                    } catch (MochowServiceException e) {
                        if (e.getStatusCode() == 404) {
                            tableDropped = true;
                        }
                    }
                } while (!tableDropped);
            }
            Thread.sleep(10000);
            mochowClient.dropDatabase(database);
        }

        mochowClient.createDatabase(database);
        int vectorDimension = 128;
        
        Schema.Builder schemaBuilder = Schema.builder()
                .addField(Field.builder()
                        .fieldName("id")
                        .fieldType(FieldType.STRING)
                        .primaryKey(true)
                        .partitionKey(true)
                        .autoIncrement(false)
                        .notNull(true).build())
                .addField(Field.builder()
                        .fieldName("vector")
                        .fieldType(FieldType.BINARY_VECTOR)
                        .notNull(true)
                        .dimension(vectorDimension).build());

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .database(database)
                .table(tableName)
                .replication(1)
                .partition(new PartitionParams(PartitionType.HASH, 1))
                .description("test binary vector")
                .schema(schemaBuilder.build()).build();
        mochowClient.createTable(createTableRequest);

        // 等待表创建完成
        boolean tableCreated = false;
        do {
            Thread.sleep(2000);
            DescribeTableResponse describeResponse = mochowClient.describeTable(database, tableName);
            if (describeResponse.getTable().getState() == TableState.NORMAL) {
                tableCreated = true;
                break;
            }
        } while (!tableCreated);

        // 2. 插入数据到表
        List<Row> rows = new ArrayList<>();
        for (int num = 0; num < 50; num++) {
            List<Integer> binaryList = numToBinaryList(num, vectorDimension);
            // 将二进制列表转换为字节数组
            byte[] binaryBytes = new byte[vectorDimension / 8];
            for (int i = 0; i < binaryList.size(); i += 8) {
                byte b = 0;
                for (int j = 0; j < 8 && i + j < binaryList.size(); j++) {
                    b |= (binaryList.get(i + j) << (7 - j));
                }
                binaryBytes[i / 8] = b;
            }
            
            rows.add(Row.builder()
                    .addField(new RowField("id", String.valueOf(num)))
                    .addField(new RowField("vector", new BinaryVector(binaryBytes)))
                    .build());
        }
        
        UpsertRequest upsertRequest = UpsertRequest.builder()
                .database(database)
                .table(tableName)
                .rows(rows).build();
        mochowClient.upsert(upsertRequest);

        // 3. 搜索二进制向量
        List<Integer> targetBinaryList = numToBinaryList(123, vectorDimension);
        byte[] targetBytes = new byte[vectorDimension / 8];
        for (int i = 0; i < targetBinaryList.size(); i += 8) {
            byte b = 0;
            for (int j = 0; j < 8 && i + j < targetBinaryList.size(); j++) {
                b |= (targetBinaryList.get(i + j) << (7 - j));
            }
            targetBytes[i / 8] = b;
        }
        
        BinaryVector target = new BinaryVector(targetBytes);
        VectorTopkSearchRequest request = VectorTopkSearchRequest.builder("vector", target, 10).build();
        SearchRowResponse res = mochowClient.vectorSearch(database, tableName, request);
        System.out.printf("Binary vector search result: %s\n", JsonUtils.toJsonString(res.getRows()));

        // 清理
        mochowClient.dropTable(database, tableName);
        Thread.sleep(10000);
        mochowClient.dropDatabase(database);
    }

    public void sparseVectorUsageExample() throws MochowClientException, InterruptedException {
        // 1. 创建表
        String database = "test_sparse_vec";
        String tableName = "test_sparse_vec_tab";

        // 清理环境
        if (mochowClient.hasDatabase(database)) {
            if (mochowClient.hasTable(database, tableName)) {
                mochowClient.dropTable(database, tableName);
                boolean tableDropped = false;
                do {
                    Thread.sleep(3000);
                    try {
                        mochowClient.describeTable(database, tableName);
                    } catch (MochowServiceException e) {
                        if (e.getStatusCode() == 404) {
                            tableDropped = true;
                        }
                    }
                } while (!tableDropped);
            }
            Thread.sleep(10000);
            mochowClient.dropDatabase(database);
        }

        mochowClient.createDatabase(database);
        
        Schema.Builder schemaBuilder = Schema.builder()
                .addField(Field.builder()
                        .fieldName("id")
                        .fieldType(FieldType.STRING)
                        .primaryKey(true)
                        .partitionKey(true)
                        .autoIncrement(false)
                        .notNull(true).build())
                .addField(Field.builder()
                        .fieldName("vector")
                        .fieldType(FieldType.SPARSE_FLOAT_VECTOR)
                        .notNull(true).build());

        // 添加稀疏向量索引
        schemaBuilder.addIndex(VectorIndex.builder()
                .indexName("sparse_vector_idx")
                .indexType(IndexType.SPARSE_OPTIMIZED_FLAT)
                .fieldName("vector")
                .metricType(MetricType.IP)
                .build());

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .database(database)
                .table(tableName)
                .replication(1)
                .partition(new PartitionParams(PartitionType.HASH, 1))
                .description("test sparse vector")
                .schema(schemaBuilder.build()).build();
        mochowClient.createTable(createTableRequest);

        // 等待表创建完成
        boolean tableCreated = false;
        do {
            Thread.sleep(2000);
            DescribeTableResponse describeResponse = mochowClient.describeTable(database, tableName);
            if (describeResponse.getTable().getState() == TableState.NORMAL) {
                tableCreated = true;
                break;
            }
        } while (!tableCreated);

        // 2. 插入数据到表
        List<Row> rows = new ArrayList<>();
        for (int num = 0; num < 50; num++) {
            Map<String, Float> sparseVector = new HashMap<>();
            sparseVector.put("1", 0.56465f);
            sparseVector.put("100", 0.2366456f);
            sparseVector.put("10000", 0.543111f);
            
            rows.add(Row.builder()
                    .addField(new RowField("id", String.valueOf(num)))
                    .addField(new RowField("vector", new SparseFloatVector(sparseVector)))
                    .build());
        }
        
        UpsertRequest upsertRequest = UpsertRequest.builder()
                .database(database)
                .table(tableName)
                .rows(rows).build();
        mochowClient.upsert(upsertRequest);

        // 3. 搜索稀疏向量
        Map<String, Float> targetSparseVector = new HashMap<>();
        targetSparseVector.put("1", 0.56465f);
        targetSparseVector.put("100", 0.2366456f);
        targetSparseVector.put("10000", 0.543111f);
        
        SparseFloatVector target = new SparseFloatVector(targetSparseVector);
        VectorTopkSearchRequest request = VectorTopkSearchRequest.builder("vector", target, 10).build();
        SearchRowResponse res = mochowClient.vectorSearch(database, tableName, request);
        System.out.printf("Sparse vector search result: %s\n", JsonUtils.toJsonString(res.getRows()));

        // 清理
        mochowClient.dropTable(database, tableName);
        Thread.sleep(10000);
        mochowClient.dropDatabase(database);
    }

    public void searchIteratorExample() throws MochowClientException, InterruptedException {
        // 确保索引已重建
        mochowClient.rebuildIndex(DATABASE, TABLE, "vector_idx");

        // 等待索引重建完成
        while (true) {
            Thread.sleep(3000);
            DescribeIndexResponse describeIndexResponse = mochowClient.describeIndex(DATABASE, TABLE, "vector_idx");
            if (describeIndexResponse.getIndex().getState() == IndexState.NORMAL) {
                System.out.println("Index rebuild finished for search iterator");
                break;
            }
            System.out.printf("Index rebuild state: %s\n", describeIndexResponse.getIndex().getState());
        }

        VectorTopkSearchRequest topkRequest = VectorTopkSearchRequest.builder("vector", 
                new FloatVector(Arrays.asList(1F, 0.21F, 0.213F, 0F)), 100)
                .config(VectorSearchConfig.builder().ef(2000).build())
                .build();

        SearchIteratorArgs topkArgs = new SearchIteratorArgs();
        topkArgs.database = DATABASE;
        topkArgs.table = TABLE;
        topkArgs.request = topkRequest;
        topkArgs.batchSize = 100;
        topkArgs.totalSize = 1000;

        SearchIterator iterator1 = mochowClient.searchIterator(topkArgs);
        while (true) {
            List<SearchResultRow> rows = iterator1.next();
            if (rows == null || rows.isEmpty()) {
                break;
            }
        }
        iterator1.close();

        List<SingleVectorSearchRequestInterface> requests = new ArrayList<>();
        requests.add(
            VectorTopkSearchRequest.builder("vector", 
                new FloatVector(Arrays.asList(1F, 0.21F, 0.213F, 0F)), 100)
                .config(VectorSearchConfig.builder().ef(2000).build())
                .build()
        );
        requests.add(
            VectorTopkSearchRequest.builder("vector", 
                new FloatVector(Arrays.asList(1F, 0.21F, 0.213F, 0F)), 100)
                .config(VectorSearchConfig.builder().ef(2000).build())
                .build()
        );

        MultiVectorSearchRequest multiRequest = MultiVectorSearchRequest.builder(requests)
                .rankPolicy(new WeightedRank(Arrays.asList(1.0f, 1.0f)))
                .limit(100)
                .build();

        SearchIteratorArgs multiArgs = new SearchIteratorArgs();
        multiArgs.database = DATABASE;
        multiArgs.table = TABLE;
        multiArgs.request = multiRequest;
        multiArgs.batchSize = 100;
        multiArgs.totalSize = 1000;

        SearchIterator iterator2 = mochowClient.searchIterator(multiArgs);
        while (true) {
            List<SearchResultRow> rows = iterator2.next();
            if (rows == null || rows.isEmpty()) {
                break;
            }
        }
        iterator2.close();
    }

    // 全文检索高亮、请求级同义词以及衰变排名器
    public void bm25AdvancedSearchExample() throws MochowClientException {
        HighlightParams highlight = new HighlightParams();
        highlight.setPreTags(Arrays.asList("<em>"));
        highlight.setPostTags(Arrays.asList("</em>"));
        highlight.setFragmentSize(120);
        highlight.setNumberOfFragments(3);
        Map<String, HighlightParamField> highlightFields = new HashMap<>();
        highlightFields.put("segment", new HighlightParamField(120, 0));
        highlight.setFields(highlightFields);

        DecayFunction pageDecay = new DecayFunction();
        pageDecay.setName("page_decay");
        pageDecay.setType("LINEAR");
        pageDecay.setFieldName("page");
        pageDecay.setOrigin(21);
        pageDecay.setScale(100);
        pageDecay.setWeight(0.3);

        BM25SearchRequest searchRequest = BM25SearchRequest.builder("book_segment_inverted_idx", "segment:Xuande")
                .limit(10)
                .projections(Arrays.asList("id", "segment"))
                .synonyms(Arrays.asList(Arrays.asList("Xuande", "Liubei")))
                .highlight(highlight)
                .decay(Arrays.asList(pageDecay))
                .build();

        SearchRowResponse searchResponse = mochowClient.bm25Search(DATABASE, TABLE, searchRequest);
        System.out.printf("BM25 advanced search result: %s\n", JsonUtils.toJsonString(searchResponse.getRows()));
    }

    // 异步创建/删除全文索引
    public void createAndDropInvertedIndexExample() throws MochowClientException, InterruptedException {
        String indexName = "book_segment_inverted_idx_v2";
        CreateIndexRequest createIndexRequest = CreateIndexRequest.builder()
                .database(DATABASE)
                .table(TABLE)
                .addIndex(new InvertedIndex(
                        indexName,
                        new String[]{"segment"},
                        new InvertedIndexParams(
                                InvertedIndexAnalyzer.ENGLISH_ANALYZER,
                                InvertedIndexParseMode.FINE_MODE,
                                true,
                                new StopWords("CUSTOM", Arrays.asList("the", "a", "of")))))
                .build();
        mochowClient.createIndex(createIndexRequest);

        // 全文索引异步构建，构建完成后 state 才会变为 NORMAL
        while (true) {
            DescribeIndexResponse describeIndexResponse = mochowClient.describeIndex(DATABASE, TABLE, indexName);
            if (describeIndexResponse.getIndex().getState() == IndexState.NORMAL) {
                break;
            }
            Thread.sleep(3000);
        }

        mochowClient.dropIndex(DATABASE, TABLE, indexName);
    }

    // 指定向量维度子集构建索引
    public void truncationDimensionExample() throws MochowClientException, InterruptedException {
        String database = "truncation_dimension_db";
        String tableName = "truncation_dimension_table";
        if (mochowClient.hasDatabase(database)) {
            if (mochowClient.hasTable(database, tableName)) {
                mochowClient.dropTable(database, tableName);
                Thread.sleep(10000);
            }
            mochowClient.dropDatabase(database);
        }
        mochowClient.createDatabase(database);

        Schema.Builder schemaBuilder = Schema.builder()
                .addField(Field.builder()
                        .fieldName("id")
                        .fieldType(FieldType.STRING)
                        .primaryKey(true)
                        .partitionKey(true)
                        .autoIncrement(false)
                        .notNull(true).build())
                .addField(Field.builder()
                        .fieldName("vector")
                        .fieldType(FieldType.FLOAT_VECTOR)
                        .dimension(8)
                        .notNull(true).build())
                // truncationDimension 为 Index 顶层参数，格式为 [begin, end)
                .addIndex(new VectorIndex(
                        "vector_idx", "vector", IndexType.HNSW, MetricType.L2,
                        new HNSWParams(32, 200), Arrays.asList(0, 4)));

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .database(database)
                .table(tableName)
                .replication(1)
                .partition(new PartitionParams(PartitionType.HASH, 1))
                .description("test truncation dimension")
                .schema(schemaBuilder.build()).build();
        mochowClient.createTable(createTableRequest);

        while (true) {
            Thread.sleep(3000);
            if (mochowClient.describeTable(database, tableName).getTable().getState() == TableState.NORMAL) {
                break;
            }
        }

        DescribeIndexResponse describeIndexResponse = mochowClient.describeIndex(database, tableName, "vector_idx");
        System.out.printf("Truncation dimension of vector_idx: %s\n",
                JsonUtils.toJsonString(describeIndexResponse.getIndex().getTruncationDimension()));

        mochowClient.dropTable(database, tableName);
        Thread.sleep(10000);
        mochowClient.dropDatabase(database);
    }

    // 持久化 BITMAP / 持久化聚合 BITMAP 索引
    public void persistentBitmapIndexExample() throws MochowClientException, InterruptedException {
        String database = "persistent_bitmap_db";
        String tableName = "persistent_bitmap_table";
        if (mochowClient.hasDatabase(database)) {
            if (mochowClient.hasTable(database, tableName)) {
                mochowClient.dropTable(database, tableName);
                Thread.sleep(10000);
            }
            mochowClient.dropDatabase(database);
        }
        mochowClient.createDatabase(database);

        Schema.Builder schemaBuilder = Schema.builder()
                .addField(Field.builder()
                        .fieldName("id")
                        .fieldType(FieldType.STRING)
                        .primaryKey(true)
                        .partitionKey(true)
                        .autoIncrement(false)
                        .notNull(true).build())
                .addField(Field.builder()
                        .fieldName("status")
                        .fieldType(FieldType.UINT32).build())
                .addField(Field.builder()
                        .fieldName("publish_time")
                        .fieldType(FieldType.UINT64).build())
                // PERSISTENT_BITMAP 适用于等值/IN 过滤
                .addIndex(new PersistentBitmapIndex("status_bitmap_idx", "status"))
                // PERSISTENT_AGGREGATED_BITMAP 同时支持等值与范围过滤，需要 fanoutBits 与 maxDepth
                .addIndex(new PersistentAggregatedBitmapIndex(
                        "publish_time_bitmap_idx", "publish_time", 4, 10));

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .database(database)
                .table(tableName)
                .replication(1)
                .partition(new PartitionParams(PartitionType.HASH, 1))
                .description("test persistent bitmap index")
                .schema(schemaBuilder.build()).build();
        mochowClient.createTable(createTableRequest);

        while (true) {
            Thread.sleep(3000);
            if (mochowClient.describeTable(database, tableName).getTable().getState() == TableState.NORMAL) {
                break;
            }
        }

        List<Row> rows = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            rows.add(Row.builder()
                    .addField(new RowField("id", String.valueOf(i)))
                    .addField(new RowField("status", i % 3))
                    .addField(new RowField("publish_time", 1717200000L + i * 86400L)).build());
        }
        mochowClient.upsert(UpsertRequest.builder().database(database).table(tableName).rows(rows).build());

        SelectResponse selectResponse = mochowClient.select(SelectRequest.builder()
                .database(database)
                .table(tableName)
                .filter("status = 1 AND publish_time > 1717286400")
                .projections(Arrays.asList("id", "status", "publish_time"))
                .limit(10)
                .build());
        System.out.printf("Persistent bitmap filtered rows: %s\n", JsonUtils.toJsonString(selectResponse.getRows()));

        mochowClient.dropTable(database, tableName);
        Thread.sleep(10000);
        mochowClient.dropDatabase(database);
    }

    // 查询指定主键在向量索引中的覆盖状态
    public void vectorIndexMembershipExample() throws MochowClientException {
        QueryRequest queryRequest = QueryRequest.builder()
                .database(DATABASE)
                .table(TABLE)
                .addPrimaryKey("id", "0001")
                .readConsistency(ReadConsistency.STRONG)
                .projections(Arrays.asList("id"))
                .vectorIndexMembership("vector_idx")
                .build();
        QueryResponse queryResponse = mochowClient.query(queryRequest);
        System.out.printf("Vector index membership state: %s\n",
                JsonUtils.toJsonString(queryResponse.getVectorIndexMembership()));
    }

    // BM25 与 Hybrid 检索的 search iterator
    public void bm25AndHybridSearchIteratorExample() throws MochowClientException {
        BM25SearchRequest bm25Request = BM25SearchRequest.builder("book_segment_inverted_idx", "segment:Xuande")
                .limit(10)
                .projections(Arrays.asList("id"))
                .build();
        SearchIteratorArgs bm25Args = new SearchIteratorArgs();
        bm25Args.database = DATABASE;
        bm25Args.table = TABLE;
        bm25Args.request = bm25Request;
        bm25Args.batchSize = 10;
        bm25Args.totalSize = 20;
        SearchIterator bm25Iterator = mochowClient.searchIterator(bm25Args);
        while (true) {
            List<SearchResultRow> rows = bm25Iterator.next();
            if (rows == null || rows.isEmpty()) {
                break;
            }
        }
        bm25Iterator.close();

        VectorTopkSearchRequest vectorRequest = VectorTopkSearchRequest.builder(
                        "vector", new FloatVector(Arrays.asList(1F, 0.21F, 0.213F, 0F)), 10)
                .config(VectorSearchConfig.builder().ef(200).build())
                .build();
        HybridSearchRequest hybridRequest = HybridSearchRequest.builder(
                        vectorRequest,
                        BM25SearchRequest.builder("book_segment_inverted_idx", "segment:Xuande").build(),
                        0.4F, 0.6F)
                .limit(10)
                .projections(Arrays.asList("id"))
                .build();
        SearchIteratorArgs hybridArgs = new SearchIteratorArgs();
        hybridArgs.database = DATABASE;
        hybridArgs.table = TABLE;
        hybridArgs.request = hybridRequest;
        hybridArgs.batchSize = 10;
        hybridArgs.totalSize = 20;
        SearchIterator hybridIterator = mochowClient.searchIterator(hybridArgs);
        while (true) {
            List<SearchResultRow> rows = hybridIterator.next();
            if (rows == null || rows.isEmpty()) {
                break;
            }
        }
        hybridIterator.close();
    }
}
