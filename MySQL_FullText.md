# mysql full-text index
- `전문검색(full-text search)`은 게시물의 내용이나 제목 등과 같이 문장에서 키워드를 검색하는 기능
- LIKE %%로 검색하면 인덱스를 사용하지 못한다. 
- version 5.6 이상부터 innodb도 적용 가능 
- `char`, `varchar`, `text` 타입 가능
- `set global innodb_ft_aux_table = 'springdb/article'`로 지정해두면 `select * from information_schema.innodb_ft_index_table;`로 인덱스 관련 정보를 조회할 수 있다.  


## full-text index 생성 
- DDL 생성시 CREATE TABLE statement 
- ALTER TABLE
- CREATE INDEX
  + `CREATE FULLTEXT INDEX ft_idx_notice_title_content ON article(title, contents)`

## full-text 쿼리 
- `MATCH() ... AGAINST` 구문으로 조회 가능
- `EXPLAIN SELECT * FROM article WHERE MATCH(title, content) AGAINST ('코로나');`
- MATCH() 괄호 안에는 반드시 full-text index 생성시 지정한 모든 컬럼을 명시해야한다.  
  + 그렇지 않으면 `1191. Cant't find FULLTEXT index matching the column list` 에러 발생  

## 검색 방법 
### IN NATURAL LANGUAGE MODE. default 
- `The full-text engine splits the phrase into words and performs a search in the FULLTEXT index for the words.`
- `검색 키워드를 토큰으로 쪼개서 키워드 목록 서치해봄.`
- ```
  1, '코로'
  2, '코로나 거리두기'
  3, '코로나나'
  ```
- 예를 들어 다음과 같이 저장되어 있을 때, `select * from article where match(title) against ('코로나나');` 조회시 결과는 다음과 같다. 정확도가 높은 순서대로 조회 
- ```mysql
  3, '코로나나'
  2, '코로나 거리두기'
  1, '코로'
  ```
- 참고로 `select * from article where match(title) against ('코로나나' in boolean mode);` 조회시 토큰으로 쪼개서 조회하지 않고 `코로나나`로만 조회하므로 `3, 코로나나`만 조회된다. 

### IN BOOLEAN MODE
- 키워드의 포함, 불포함을 지정할 수 있다.
  + `없으면 or 연산`:  AGAINST('hello name' IN BOOLEAN MODE);
  + `+는 AND 연산`:  AGAINST('+hello +name' IN BOOLEAN MODE);
  + `-는 NOT 연산`:  AGAINST('+hello -name' IN BOOLEAN MODE);
  
## Full-Text Stopword. 구분자. 불용어 
- `띄어쓰기`나 `문장 기호`를 기준으로 `키워드를 추출`
  + ex) `hello world` -> `hello`, `world`
- 키워드가 전부 일치하거나 prefix가 일치할 때만 조회 
- 지정된 구분자에 의해서만 인덱싱을 처리하도록 키워드 추출 알고리즘 사용.
  + select * from information_schema.innodb_ft_default_stopword;로 stopworld 키워드 조회 가능 
- `추출한 키워드 토큰이 stopword 테이블에 완전히 일치하는게 존재한다면, 키워드 목록에 추가되지 않음.` 그러므로 stopword에 정의된 키워드로는 검색이 불가능함. 
- 기본적으로 영어이고, 영문의 대소문자를 구분하지 않음. collation을 수정하면 대소문자 구분 가능 
- stopword를 사용자가 정의한 테이블로 변경할 수 있다.

### 사용자 정의 stopword 테이블 생성 및 데이터 추가 
- stopword 테이블 변경 
- ```mysql
  -- 테이블 지정
  set global innodb_ft_server_stopword_table = "testdb/custom_stopword";
  -- 조회 
  show variables like 'innodb_ft_server_stopword_table';
  ```
- 테이블 생성 
- ```mysql
  create table user_stopword (value varchar(30) not null);
  
  insert into user_stopword(value) values ('가까스로'), ('가령'), ('각각'), ('각자'), ('각종'), ('갖고말하자면'), ('같다'), ('같이'), ('개의치않고'), ('거니와');
  ```
- mysql을 재시작하면 초기화 되므로 주의! 영구적으로 사용하고 싶다면 /etc/mysql/my.cnf 파일에 등록 

## N-그램 
- 단어나 어휘를 고려하지 않고 `본문의 내용`을 `N`으로 잘라서 사용 
- 전세계 언어를 고려했을 때, 합리적인 방법
- version 5.7 부터 추가 
- `/etc/mysql/my.cnf`에서 토큰 사이즈 수정 가능 `ngram_token_size=2`. `1~10`까지 지정 가능  
- 기존 innodb_ft_min_token_size, innodb_ft_max_token_size, ft_min_word_len, and ft_max_word_len 파라미터는 무시됌 
- full-text index 생성시 `with PARSER ngram`문 추가해야함. 
  + `CREATE fULLTEXT INDEX ft_idx_notice_title_content on article(title, contents) with PARSER ngram`
- `stopword`는 `기본 full-text와 동작이 다른데, 토큰화된 단어에 stopword가 포함되어 있으면 키워드 목록에 추가되지 않음`.
  + ex) token: `hi,`, `hh,`. stopworld: `,`로 정의되어 있다면 `,`가 포함되는 `hi,`, `hh,`는 목록에서 제외. 쓸모없는 토큰을 필터링 할 수 있음  


## docker로 설정시 필요한 명령어 
- `docker exec -it mysql_boot bash` 도커 컨테이너 접속
- ```
  //vim 설치
  apt-get update
  apt-get install vim
  ```
- `vi /etc/mysql/my.cnf` mysql 설정 파일 수정
  
## 참고  
- https://dev.mysql.com/doc/refman/8.0/en/innodb-fulltext-index.html
- https://dev.mysql.com/doc/refman/8.0/en/fulltext-search.html



