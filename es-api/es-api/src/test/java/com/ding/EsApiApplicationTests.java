package com.ding;

import com.alibaba.fastjson.JSON;
import com.ding.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class EsApiApplicationTests {


	@Autowired
	@Qualifier("restHighLevelClient")
	private RestHighLevelClient client;

	// 测试索引的创建 Request
	@Test
	void testCreateIndex() throws IOException {
		// 1. 创建索引请求
		CreateIndexRequest request = new CreateIndexRequest("kuang_index");
		// 2. 执行请求
		CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

		System.out.println(createIndexResponse);
	}

	// 测试获取索引
	@Test
	void testExistIndex() throws IOException {
		GetIndexRequest request = new GetIndexRequest("kuang_index2");
		boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
		System.out.println(exists);
	}

	// 测试删除索引
	@Test
	void testDeleteIndex() throws IOException {
		DeleteIndexRequest request = new DeleteIndexRequest("kuang_index");
		AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
		System.out.println(delete);
	}

	// 测试添加文档
	@Test
	void testAddDocument() throws IOException {
		// 创建对象
		User user = new User("狂神说", 3);
		// 创建请求
		IndexRequest request = new IndexRequest("kuang_index");
		// 规则 put kuang_index/_doc/1
		request.id("1");
		request.timeout(TimeValue.timeValueSeconds(1));
		// request.timeout("1s");

		// 将我们的数据放入请求 json
		request.source(JSON.toJSONString(user), XContentType.JSON);

		// 客服端发送请求, 获取相应的结果
		IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

		System.out.println(indexResponse.toString());
		System.out.println(indexResponse.status());

	}

	// 获取文档，判断是否存在 get /index/doc/1
	@Test
	void testIsExists() throws IOException {
		GetRequest request = new GetRequest("kuang_index", "1");
		// 不获取返回的_source的上下文
		request.fetchSourceContext(new FetchSourceContext(false));
		request.storedFields("_none_");

		boolean exists = client.exists(request, RequestOptions.DEFAULT);
		System.out.println(exists);

	}

	// 获取文档的信息
	@Test
	void testGetDocument() throws IOException {
		GetRequest request = new GetRequest("kuang_index", "1");
		// 不获取返回的_source的上下文

		GetResponse getResponse = client.get(request, RequestOptions.DEFAULT);
		System.out.println(getResponse.getSourceAsString()); // 打印文档的内容
		System.out.println(getResponse);
	}

	// 更新文档的信息
	@Test
	void testUpdateDocument() throws IOException {
		UpdateRequest updateRequest = new UpdateRequest("kuang_index", "1");
		updateRequest.timeout("1s");
		User user = new User("狂神说java", 18);
		updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
		UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
		System.out.println(updateResponse.status());
	}

	// 删除文档记录
	@Test
	void testDeleteRequest() throws IOException {
		DeleteRequest request = new DeleteRequest("kuang_index", "1");
		request.timeout("1s");
		DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
		System.out.println(deleteResponse.status());

	}

	@Test
	void testBulkRequest() throws IOException {
		BulkRequest bulkRequest = new BulkRequest();
		bulkRequest.timeout("10s");

		ArrayList<User> userList = new ArrayList<>();
		userList.add(new User("kuangshen1", 3));
		userList.add(new User("kuangshen2", 3));
		userList.add(new User("kuangshen3", 3));
		userList.add(new User("kuangshen4", 3));
		userList.add(new User("kuangshen5", 3));

		for (int i = 0; i <userList.size(); i++) {
			bulkRequest.add(
					new IndexRequest("kuang_index")
					.id(""+(i+1))
					.source(JSON.toJSONString(userList.get(i)), XContentType.JSON)
			);

		}

		BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
		System.out.println(bulkResponse.hasFailures());
	}


	// 查询
	@Test
	void testSearch() throws IOException {
		SearchRequest searchRequest = new SearchRequest("kuang_index");
		// 构建搜索条件
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		// sourceBuilder.highlighter()
		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "kuangshen1");
		sourceBuilder.query(termQueryBuilder);
		// MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		//
		// sourceBuilder.from();
		// sourceBuilder.size();
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

		System.out.println(JSON.toJSONString(searchResponse.getHits()));
		System.out.println("========================================");
		for (SearchHit documentFields : searchResponse.getHits().getHits()) {
			System.out.println(documentFields.getSourceAsMap());
		}
	}
}
