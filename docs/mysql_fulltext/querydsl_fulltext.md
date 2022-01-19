
# QueryDSL에서 MySQL fulltext search 쿼리 사용하기
- ```
  1. native 쿼리 사용 
  2. dialect 등록 
  ```
- native로 조회하면 QueryDSL을 쓰는 이점이 없기 때문에.. 2번으로 결정.  
- 기본적으로 MySQL match() ... against() 함수가 들어있지 않기 때문에 등록해야함.

## MySQLDialect 
- ```
  public class MySqlDialect extends MySQL8Dialect {

    public MySqlDialect() {
        super();
        this.registerFunction("match", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "match(?1, ?2) against (?3)"));
    }
  }
  ```
- 현재 사용하고 있는 MySQL 버전에 맞는 dialect를 상속받는다  

## application.properties 경로 추가
- `spring.jpa.database-platform=project.board.configuration.MySqlDialect`

## fulltext index 생성 
- ```
  @Column(name = "url", columnDefinition = "VARCHAR(255) NOT NULL, FULLTEXT KEY urlFulltext (url)")
  private String url;
  ```
- columnDefinition에 위처럼 추가가 가능하지만, 이 프로젝트에서는 복합키이고, 추가적으로 mysql 설정이 필요하니 MySQL에서 직접 생성해둠
- `create fulltext index ft_idx_article_title_content on article(title, content) with parser ngram;`

## QueryDsl 사용 
- ```
  private BooleanExpression containsTitle(String searchText) {
    if (StringUtils.isNotBlank(searchText)) {
      NumberTemplate<Double> template = Expressions.numberTemplate(Double.class, "function('match',{0},{1},{2})", article.title, article.content, searchText);
      return template.gt(0);
    }
    return null;
  }
  ```
- expression은 expression 타입으로 변환해줘야 하기 때문에 template.get(0)로 캐스팅 작업. 
  + https://stackoverflow.com/questions/9186845/how-to-use-mysqls-full-text-search-from-jpa