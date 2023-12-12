# 커버링 인덱스

### 인덱스란?

- 데이터베이스 테이블의 특정 열(Column)에 대한 검색 및 정렬을 빠르게 수행하기 위해 생성되는 데이터 구조
- 인덱스를 사용하면 데이터베이스는 해당 열의 값을 빠르게 찾아내어 검색 및 정렬 작업을 효율적으로 수행할 수 있음

<br>

### 커버링 인덱스란?

- 쿼리를 충족시키는 데 필요한 모든 데이터를 갖고 있는 인덱스를 커버링 인덱스 (Covering Index 혹은 Covered Index),
- SELECT, WHERE, ORDER BY, GROUP BY 등에 사용되는 모든 컬럼이 인덱스의 구성요소인 경우
- 커버링 인덱스는 쿼리를 처리하는 데 필요한 모든 정보를 인덱스 자체에서 얻을 수 있도록 설계된 특별한 종류의 인덱스
- 쿼리에서 선택한 열의 데이터뿐만 아니라, 필요한 모든 열의 데이터가 이미 인덱스에 포함되어 있는 경우
- 디스크에서 별도의 테이블 데이터 페이지를 읽지 않고도 인덱스만을 통해 쿼리 결과를 가져올 수 있음
- 입출력(IO) 비용을 줄이고 쿼리의 성능을 향상시킴

<br>

### 장점

- 성능 향상: 디스크 I/O를 줄이므로 데이터베이스 쿼리의 응답 시간을 단축
- 자원 효율성: 불필요한 데이터를 읽지 않아 자원 소모를 줄임

<br>

### 단점

- 인덱스를 너무 많이 생성하면 업데이트 및 삽입 작업에 부담이 될 수 있으므로 적절한 균형을 유지해야 함


<br>



### Env

- mysql innodb





### Test DDL Scripts

```
# db
create database test;
use test;

# table
create table test_1
(
    id int auto_increment primary key,
    dummy_id_1 int not null,
    dummy_id_2 int not null,
    dummy_id_3 int not null,
    memo_1 varchar(32) not null,
    memo_2 varchar(32)
) charset = utf8mb4;

# index
create index idx_dummy_id_1 on test_1(dummy_id_1);
create index idx_dummy_id_2_dummy_id_3 on test_1(dummy_id_2, dummy_id_3);
create index idx_memo_1_memo_2 on test_1(memo_1, memo_2);
create index idx_memo_2 on test_1(memo_2);
```



<br>



### Dummy Data Insert

```
DELIMITER $$
DROP PROCEDURE IF EXISTS insertLoop$$

CREATE PROCEDURE insertLoop()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 10000000
        DO
            INSERT INTO test_1(dummy_id_1, dummy_id_2, dummy_id_3, memo_1, memo_2)
            VALUES (i + 1, i + 2, i + 3, i+4, i+5);
            SET i = i + 1;
        END WHILE;
END$$
DELIMITER $$

CALL insertLoop;
$$
```


### 비교

**커버링 인덱스를 태운 경우**

```
explain select dummy_id_1 from test_1 where dummy_id_1 = 1;
```

![Screenshot 2023-12-12 at 21 48 23](https://github.com/DongGeon0908/goofy-warehouse/assets/50691225/be0dd1d3-c77c-481d-86e2-f8e662c7788f)

**일반 조회**

```
explain select * from test_1 where dummy_id_1 = 1;
```

![Screenshot 2023-12-12 at 21 47 47](https://github.com/DongGeon0908/goofy-warehouse/assets/50691225/0dfbbc05-a6d9-4f11-b304-ad9778ffc2c9)

