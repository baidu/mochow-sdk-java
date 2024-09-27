package com.baidu.mochow.examples;

import com.baidu.mochow.exception.MochowServiceException;
import com.baidu.mochow.model.AddFieldRequest;
import com.baidu.mochow.model.CreateTableRequest;
import com.baidu.mochow.model.DeleteRequest;
import com.baidu.mochow.model.DescribeIndexResponse;
import com.baidu.mochow.model.DescribeTableResponse;
import com.baidu.mochow.model.QueryRequest;
import com.baidu.mochow.model.QueryResponse;
import com.baidu.mochow.model.SearchRequest;
import com.baidu.mochow.model.SearchResponse;
import com.baidu.mochow.model.SelectRequest;
import com.baidu.mochow.model.SelectResponse;
import com.baidu.mochow.model.ShowTableStatsResponse;
import com.baidu.mochow.model.UpdateRequest;
import com.baidu.mochow.model.UpsertRequest;
import com.baidu.mochow.model.UpsertResponse;
import com.baidu.mochow.model.entity.ANNSearchParams;
import com.baidu.mochow.model.entity.Field;
import com.baidu.mochow.model.entity.HNSWParams;
import com.baidu.mochow.model.entity.HNSWSearchParams;
import com.baidu.mochow.model.entity.PartitionParams;
import com.baidu.mochow.model.entity.Row;
import com.baidu.mochow.model.entity.RowField;
import com.baidu.mochow.model.entity.Schema;
import com.baidu.mochow.model.entity.SecondaryIndex;
import com.baidu.mochow.model.entity.VectorIndex;
import com.baidu.mochow.model.enums.FieldType;
import com.baidu.mochow.model.enums.IndexState;
import com.baidu.mochow.model.enums.IndexType;
import com.baidu.mochow.model.enums.MetricType;
import com.baidu.mochow.model.enums.PartitionType;
import com.baidu.mochow.model.enums.ReadConsistency;
import com.baidu.mochow.model.enums.TableState;

import com.baidu.mochow.client.MochowClient;
import com.baidu.mochow.client.ClientConfiguration;
import com.baidu.mochow.exception.MochowClientException;
import com.baidu.mochow.util.JsonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MochowExample {
    private static final String DATABASE = "book";
    private static final String TABLE = "book_segments";
    private static final String TABLE_ALIAS = "book_segments_alias";
    private MochowClient mochowClient;

    public MochowExample(ClientConfiguration clientConfiguration) {
        this.mochowClient = new MochowClient(clientConfiguration);
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

            this.updateData();
            System.out.println("update data success");

            this.deleteData();
            System.out.println("delete data success");

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
        Schema tableSchema = Schema.builder()
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
                                .fieldType(FieldType.STRING).build())
                .addField(
                        Field.builder()
                                .fieldName("vector")
                                .fieldType(FieldType.FLOAT_VECTOR)
                                .dimension(4).build())
                .addIndex(
                        VectorIndex.builder()
                                .indexName("vector_idx")
                                .indexType(IndexType.HNSW)
                                .fieldName("vector")
                                .params(new HNSWParams(32, 200))
                                .metricType(MetricType.L2)
                                .autoBuild(false).build())
                .addIndex(new SecondaryIndex("book_name_idx", "bookName"))
                .build();
        CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .database(DATABASE)
                .table(TABLE)
                .replication(3)
                .partition(new PartitionParams(PartitionType.HASH, 1))
                .description("test")
                .schema(tableSchema).build();
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
                        .addField(new RowField("segment", "富贵功名，前缘分定，为人切莫欺心。")).build()
        );
        rows.add(
                Row.builder()
                        .addField(new RowField("id", "0002"))
                        .addField(new RowField("vector", Arrays.asList(2, 0.22, 0.213, 0)))
                        .addField(new RowField("bookName", "西游记"))
                        .addField(new RowField("author", "吴承恩"))
                        .addField(new RowField("page", 22))
                        .addField(new RowField("segment", "正大光明，忠良善果弥深。些些狂妄天加谴，眼前不遇待时临。")).build()
        );
        rows.add(
                Row.builder()
                        .addField(new RowField("id", "0003"))
                        .addField(new RowField("vector", Arrays.asList(3, 0.23, 0.213, 0)))
                        .addField(new RowField("bookName", "三国演义"))
                        .addField(new RowField("author", "罗贯中"))
                        .addField(new RowField("page", 23))
                        .addField(new RowField("segment", "细作探知这个消息，飞报吕布。")).build()
        );
        rows.add(
                Row.builder()
                        .addField(new RowField("id", "0004"))
                        .addField(new RowField("vector", Arrays.asList(4, 0.23, 0.213, 0)))
                        .addField(new RowField("bookName", "三国演义"))
                        .addField(new RowField("author", "罗贯中"))
                        .addField(new RowField("page", 24))
                        .addField(new RowField("segment", "布大惊，与陈宫商议。宫曰：“闻刘玄德新领徐州，可往投之。” 布从其言，竟投徐州来。有人报知玄德。")).build()
        );
        rows.add(
                Row.builder()
                        .addField(new RowField("id", "0005"))
                        .addField(new RowField("vector", Arrays.asList(5, 0.23, 0.213, 0)))
                        .addField(new RowField("bookName", "三国演义"))
                        .addField(new RowField("author", "罗贯中"))
                        .addField(new RowField("page", 25))
                        .addField(new RowField("segment", "玄德曰：“布乃当今英勇之士，可出迎之。”糜竺曰：“吕布乃虎狼之徒，不可收留；收则伤人矣。")).build()
        );
        for (int i = 6; i <= 100; i++) {
            rows.add(
                    Row.builder()
                            .addField(new RowField("id", String.valueOf(i)))
                            .addField(new RowField("vector", Arrays.asList(0.2f + i * 0.01, 0.23, 0.213, 0)))
                            .addField(new RowField("bookName", "三国演义"))
                            .addField(new RowField("author", "罗贯中"))
                            .addField(new RowField("page", 26))
                            .addField(new RowField("segment", "玄德曰：“布乃当今英勇之士，可出迎之。”糜竺曰：“吕布乃虎狼之徒，不可收留；收则伤人矣。")).build()
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
                .projections(Arrays.asList("id", "bookName")).build();
        QueryResponse queryResponse = mochowClient.query(queryRequest);
        System.out.printf("Query result: %s\n", JsonUtils.toJsonString(queryResponse.getRow()));
    }

    public void selectData() throws MochowClientException {
        SelectRequest selectRequest = SelectRequest.builder()
                .database(DATABASE)
                .table(TABLE)
                .limit(30)
                .readConsistency(ReadConsistency.EVENTUAL)
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
        }
        SearchRequest searchRequest = SearchRequest.builder()
                .database(DATABASE)
                .table(TABLE)
                .anns(
                        ANNSearchParams.builder()
                                .vectorField("vector")
                                .vectorFloats(Arrays.asList(1F, 0.21F, 0.213F, 0F))
                                .filter("bookName='三国演义'")
                                .params(HNSWSearchParams.builder().ef(200).limit(10).build()).build()
                ).build();
        SearchResponse searchResponse = mochowClient.search(searchRequest);
        System.out.printf("Search response: %s\n", JsonUtils.toJsonString(searchResponse));
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
}