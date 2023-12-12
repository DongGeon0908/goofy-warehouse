# 커버링 인덱스

### 커버링 인덱스란?

- 쿼리를 충족시키는 데 필요한 모든 데이터를 갖고 있는 인덱스를 커버링 인덱스 (Covering Index 혹은 Covered Index),
- SELECT, WHERE, ORDER BY, GROUP BY 등에 사용되는 모든 컬럼이 인덱스의 구성요소인 경우



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

