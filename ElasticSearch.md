# ElasticSearch7.6.x

什么是ElasticSearch？

Elasticsearch是一个基于[Lucene](https://baike.baidu.com/item/Lucene/6753302)的搜索服务器。它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口。Elasticsearch是用Java语言开发的，并作为Apache许可条款下的开放源码发布，是一种流行的企业级搜索引擎。

## ES的基本概念

对比于现有的关系型数据库去学习

| Relational DB      | Elasticsearch   |
| ------------------ | --------------- |
| 数据库（database） | 索引（indices） |
| 表（tables）       | 类型（types）   |
| 行（rows）         | 文档(document)  |
| 字段（columns）    | 字段（fields）  |

## Restful风格：

| method | url地址                                          | 描述               |
| ------ | ------------------------------------------------ | ------------------ |
| PUT    | localhost:9200/indices/types/document_ID         | 创建文档（指定ID） |
| POST   | localhost:9200/indices/types                     | 创建文档（随机ID） |
| POST   | localhost:9200/indices/types/document_ID/_update | 修改文档           |
| DELETE | localhost:9200/indices/types/document_ID         | 删除文档           |
| GET    | localhost:9200/indices/types/document_ID         | 通过ID查询文档     |
| POST   | localhost:9200/indices/types/_search             | 查询所有的文档     |

indices：n. 指数；目录（index的复数）

### 基础测试：

```
PUT /test01/_doc/2
{	}
```

请求头+请求体

## 关于索引的操作

1.创建索引

```
PUT /indices/types/document_ID
{

}
```

2.数据类型：

字符串类型
	text 、 keyword
数值类型
	long, integer, short, byte, double, float, half_float, scaled_float
日期类型
	date
te布尔值类型
	boolean
二进制类型
	binary

3.指定字段的类型

```
PUT /test01
{
  "mappings": {
    "properties": {
      "name":{
        "type": "text"
      },
      "age":{
        "type": "integer"
      },
      "birthday":{
        "type": "date"
      }
    }
  }
}
```

4.可以通过 GET 请求获取具体的信息！

```
GET indices
```

5.get _cat/ 可以获得es的当前的很多信息

```
GET _cat/indices?v
```

发现一个彩蛋

```
GET _cat/
```

![Snipaste_2020-04-15_14-59-51](./img/Snipaste_2020-04-15_14-59-51.png)



输入一个cat竟然真的有个猫

6.更新数据

- put

```
put /indices/types/document_ID
{
	"name":"summer"
}
```

- POST   _update

```
POST /indices/types/document_ID/_update
{
	"doc":{
		"name":"summer"
	}
}
```

put 的方法只是简单的覆盖而已，后面加_update只会修改修改的数据。

7.删除索引

```
DELETE /indices
DELETE /indices/_doc/
DELETE /indices/_doc/document_ID
```



## 关于文档的基本操作

基本操作：

1、添加数据

```
PUT /summer/user/1
{
  "name":"summer",
  "age":"22",
  "desc":"跟着狂神学java",
  "tags":["帅哥","暖男","666"]
}
```

2.修改内容

- put

  ```
  put /indices/types/document_ID
  {
  	"name":"summer"
  }
  ```

  

- POST   _update

  ```
  POST /indices/types/document_ID/_update
  {
  	"doc":{
  		"name":"summer"
  	}
  }
  ```

3.简单地搜索！

```
GET /summer/user/_search?q=name:summer
```

条件：

```
q=name:summer   
```

4.复杂操作搜索 

> 查找

```
GET /summer/user/_search
{
  "query": {
    "match": {
      "name": "summer"
    }
  }
}
```

查出来的结果：

![Snipaste_2020-04-15_15-46-25](./img/Snipaste_2020-04-15_15-46-25.png)



我们发现我们的结果在hits中，想要的信息在hits中的hits中

n. 击打；网页点击数；采样数（hit的复数）



限制只显示哪个字段

select name,age  from user ;

```
GET /summer/user/_search
{
  "query": {
    "match": {
        "desc":"跟着狂神学java"
      }
  },
 "_source": ["name","age"]
}
```



> 排序

select * from user ORDER BY  age;

```
GET /summer/user/_search
{
  "query": {
    "match": {
        "desc":"跟着狂神学java"
      }
  },
 "sort": [
   {
     "age": {
       "order": "desc"
     }
   }
 ]
}
```



>分页  from   size

select * from user limit  from ,size  ;

```
GET /summer/user/_search
{
  "query": {
    "match": {
        "desc":"跟着狂神学java"
      }
  },
 "_source": ["name","age"],
 "from": 0,
 "size": 2
}
```



> 布尔值查询    bool 就是SQL语句中的where

1.must:

select * from user where name = 'summer' and desc = '跟着狂神学java'

```
GET /summer/user/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "name":"summer"
          }
        },
        {
          "match": {
             "desc":"跟着狂神学java"
          }
        }
      ]
    }
  }
}
```

2.should:

select * from user where name = 'summer' or desc = '跟着狂神学java'

```
GET /summer/user/_search
{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "name":"summer"
          }
        },
        {
          "match": {
             "desc":"跟着狂神学java"
          }
        }
      ]
    }
  }
}
```



3.must_not 

select * from user where not  name = 'summer' 

```
GET /summer/user/_search
{
  "query": {
    "bool": {
      "must_not": [
        {
          "match": {
            "name":"summer"
          }
        }
      ]
    }
  }
}
```



4.过滤器 filter

```
GET /summer/user/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "name":"summer"
        }
      ]
      , "filter": {
        "range": {
          "age": {
            "gte": 10,
            "lte": 20
          }
        }
      }
  }
}
```



> 匹配多个条件

```
GET /summer/user/_search
{
  "query": {
    "match": {
      "tags": "暖男 高中生"
    }
  }
}
```



> 精确查询

关于分词：
term ：直接查询精确的
match：会使用分词器解析！（先分析文档，然后在通过分析的文档进行查询！）

```
GET /summer/user/_search
{
  "query": {
    "term": {
      "name":"summer"
    }
  }
}
```



> 高亮查询

```
GET /summer/user/_search
{
  "query": {
    "match": {
      "name":"summer"
    }
  }
  , "highlight": {
    "fields": {
      "name":{}
    }
  }
}
```

![Snipaste_2020-04-15_17-01-43](./img/Snipaste_2020-04-15_17-01-43.png)



## 集成springboot 

1.原生依赖导入

```xml
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
    <version>7.6.2</version>
</dependency>
```

注：如果在创建springboot时直接选择了elasticsearch，就需要去springboot中去修改版本，因为springboot中默认的是6.X

```xml
  <properties>
        <java.version>1.8</java.version>
        <!--自定义版本-->
        <elasticsearch.version>7.13.1</elasticsearch.version>
  </properties>
```

自定义一下版本。

2.将elasticsearch核心类配置进入springboot中

```java
@Configuration
public class ElasticSearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        return  new RestHighLevelClient(RestClient.builder(new HttpHost("127.0.01",9200,"http")));
    }

}
```

3.测试

```java
package com.summer;
@SpringBootTest
class SpringbootEsApiApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Test
    void contextLoads() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("summer_002");
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    // 测试获取索引,判断其是否存在
    @Test
    void TestExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("summer_001");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    // 测试删除索引
    @Test
    void TestDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("summer_001");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete);
    }


    // 测试添加文档
    @Test
    void TestAddDocument() throws IOException {
        //创建对象
        User user = new User("summer",20);
        //创建请求
        IndexRequest request = new IndexRequest("summer_002");
        //规则  /summer_02/_doc/1
        request.id("1");
        request.timeout("1s");
        //  将数据放入请求中
        request.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端发送请求
        IndexResponse index = client.index(request, RequestOptions.DEFAULT);
        //查看结果
        System.out.println(index.toString());
        System.out.println(index.status());
    }


    // 获得文档的信息
    @Test
    void  testGetDocument() throws IOException {
        //创建get请求
        GetRequest getRequest = new GetRequest("summer_002","1");
        //执行命令 客户端发送，获得结果
        GetResponse documentFields = client.get(getRequest, RequestOptions.DEFAULT);
        //打印结果
        System.out.println(documentFields);
        System.out.println(documentFields.getSourceAsString());
    }


    // 更新文档的信息
    @Test
    void testUpdateDocument() throws IOException {
        //创建请求
        UpdateRequest updateRequest = new UpdateRequest("summer_002","1");
        User user = new User("pppnut",22);
        UpdateRequest doc = updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse update = client.update(doc, RequestOptions.DEFAULT);
        System.out.println(update);
        System.out.println(update.status());
    }

    // 删除文档记录
    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("summer_002","1");
        DeleteResponse delete = client.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete);
        System.out.println(delete.status());
    }


    // 特殊的，真的项目一般都会批量插入数据！
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        ArrayList<User> users = new ArrayList<>();
        for (int i = 10; i < 22; i++) {
            users.add(new User("summer"+i,i));
        }
        for (int i = 0; i < users.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("summer_002")
                            .source(JSON.toJSONString(users.get(i)),XContentType.JSON));
        }
        bulkRequest.timeout("10S");
        BulkResponse bulk = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk);
        System.out.println(bulk.status());
    }

    // 查询
    // SearchRequest 搜索请求
    // SearchSourceBuilder 条件构造
    // HighlightBuilder 构建高亮
    // TermQueryBuilder 精确查询
    // MatchAllQueryBuilder
    // xxx QueryBuilder 对应我们刚才看到的命令！
    @Test
    void SearchRequest() throws IOException {
        SearchRequest searchRequest = new SearchRequest("summer_002");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "summer");
       // MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        SearchSourceBuilder query = sourceBuilder.query(termQueryBuilder);
        SearchRequest source = searchRequest.source(query);
        SearchResponse searchResponse = client.search(source, RequestOptions.DEFAULT);
        System.out.println(searchResponse);
        System.out.println(JSON.toJSONString(searchResponse.getHits()));
        System.out.println(searchResponse.getHits());
    }
}
```



### 总结java对ElasticSearch进行操作

1. 请求头
2. 请求体
3. 请求体的具体操作
4. 执行请求体



报错：发现索引必须都是小写

```
[summer_001Spring] ElasticsearchStatusException[Elasticsearch exception [type=invalid_index_name_exception, reason=Invalid index name [summer_001Spring], must be lowercase]
]
```

2.坑点这里必须要new IndexRequest ,不然值会重复

```java
 users.forEach(System.out::println);
        System.out.println("====================================================================");
        for (int i = 0; i < users.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("summer_002")
                            .source(JSON.toJSONString(users.get(i)),XContentType.JSON));
            System.out.println(users.get(i));
        }
```
