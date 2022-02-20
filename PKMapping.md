[돌아가기](https://github.com/LEEJ0NGWAN/JPA-Basic)

# 기본키 매핑

테이블에 맵핑될 객체에 대해 기본키에 대한 필드를 지정하기 위해서는 `@Id`만 사용하면 가능하다

→ 자동으로 기본키에 들어갈 값을 생성하고 싶을 경우 `@GeneratedValue` 를 추가로 사용해야 한다

```java
@Id @GeneratedValue(strategy = GenerationType.AUTO)
private Long id;
```

# Strategy

기본키 값을 자동으로 생성하기 위한 애노테이션 `@GeneratedValue`를 사용하기 위해서는 전략을 설정해주어야 한다

## GenerationType.IDENTITY

기본 키 생성을 DB에 위임하는 전략 (주로, MySQL, PostgreSQL, SQL Server, DB2에서 이용)

IDENTITY 전략은 예외적으로 persist() 메소드 실행 시점에서 즉시 insert sql 실행하여 DB에서 식별자를 조회

→ 자동 생성될 기본키를 DB 삽입 전까지는 알 수 없기 때문

(삽입 직후, 객체에 생성된 기본키가 저장됨: JDBC 기본 기능)

```java
@Entity
public class Employee {

    @Id @GeneratedValue(GenerationType.IDENTITY)
    private Long id;
}
```

## GenerationType.SEQUENCE

DB Sequence 객체를 이용하여 기본키 값 생성하는 전략 (주로, ORACLE, PostgreSQL, DB2, H2에서 이용)

```java
@Entity
@SequenceGenerator(
    name = "test_seq_generator", 
    sequenceName = "test_seq", 
    initialValue = 1,
    allocationSize = 1)
public class Student {

    @Id @GeneratedValue(
        strategy = GenerationType.SEQUENCE, 
        generator = "test_seq_generator")
    private Long id;
}
```

- alloctaionSize: DB로 시퀀스 다음값 호출할 때 가져올 크기 (auto_increment의 경우 1로 설정)
- allocationSize 의 기본값은 50이다
    
    → 50개를 DB에서 미리 땡겨와서 자바 애플리케이션 메모리 상에서 한개씩 꺼내어 쓴다
    
    (최초 시퀀스 호출로 다음값이 1인 경우 한번 더 호출하여 DB 측의 시퀀스 다음값을 51로 만듦) → 2번 호출
    
- persist() 시점에 DB에서 해당 시퀀스의 다음 값을 가져와 Persistence Context의 엔티티 아이디 저장

## GenerationType.TABLE

DB Sequence 객체를 모방하는 키 생성 테이블을 참조하는 전략

- 모든 DB 적용 가능
- 성능상 불리한 점이 있음 (운영 환경에서는 고민을 해봐야 할 것)

```java
create table test_sequences (

    sequence_name varchar(255) not null,
    next_val bigint,
    primary key ( sequence_name )
);
```

```java
@Entity
@TableGenerator(
    name = "mem_seq_generator",
    table = "test_sequences",
    pkColumnValue = "member_seq", // 시퀀스 테이블에 들어갈 레코드의 기본키 값
    allocationSize = 1)
public class Member {

    @Id @GeneratedValue(
        strategy = GenerationType.TABLE, 
        generator = "mem_seq_generator")
    private Long id;
}
```

## 권장 식별자 전략

기본키는 not null, unique, not change의 조건이 있는데, 비즈니스에서 자연키는 not change 조건을 지키기 어렵다(e.g., 주민등록번호) → Long, 대체키, 키 생성 전략을 이용하는 것을 권장함

- Long: 10억 넘을 경우의 수용성
- 대체키: 시퀀스 or 유효 ID
- 자동 키 생성 전략 도입

→ 자연키를 기본키로 사용하는 것은 리스크가 크다
