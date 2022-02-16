[돌아가기](https://github.com/LEEJ0NGWAN/JPA-Basic)

# JPA

# Java Persistence API

자바 ORM 표준 인터페이스의 모음 (오픈 소스 Hibernate 기반으로 만든 표준 스펙)

### JPA 구현체

Hibernate, EclipseLink, DataNucleus ...

## ORM

Object-Relational Mapping

→ 애플리케이션의 객체와 관계형 데이터베이스의 레코드를 맵핑하는 기술

# JPA 동작

앱과 JDBC 사이에서 동작

→ JPA가 JDBC API를 사용하여 동작

### 저장

1. 앱에서 JPA가 관리할 객체(Entity) 삽입 명령 (persist & commit)
2. JPA에서 해당 Entity 분석 후 INSERT SQL 자동 생성 및 JDBC API 호출

### 조회

1. 앱에서 객체(Entity) 조회(find) 명령
2. JPA에서 SELECT SQL 자동 생성 및 JDBC API 호출
3. JPA에서 ResultSet 맵핑

# JPA 사용 이유

- ORM 기술을 통해, DB나 쿼리에 종속되는 개발에서 벗어나 애플리케이션 객체 중심의 개발을 하기 위함
- 생산성 및 유지보수 → 디비와 앱의 종속성을 최소화하는 것을 통한 생산성 향상
- 데이터 접근 추상화를 통한 독립성
- 표준 기술

### 유지보수

기존 JDBC의 경우, 모델의 필드 변경(추가 또는 삭제) 시 관련 SQL 모두 수정해야 하는 고충을 바로 해결함

→ ORM이기 때문에 SQL 의존이 없음

### 패러다임 해결

1. JPA와 상속
2. JPA와 연관관계
3. JPA와 객체 그래프 탐색
→ JPA의 지연로딩 덕분에 신뢰가능한 엔티티(값이 보장되는 엔티티) 획득 가능
4. JPA와 비교하기
→ 같은 트랜잭션 내부에서 조회한 엔티티는 자바 컬렉션에서 조회한 객체처럼 동일한 객체인 것을 보장

# JPA 성능 최적화 기능

### 1차 캐시와 동일성 보장

1. 같은 트랜잭션에서 같은 엔티티 반환 → 조회 성능 아주 조금 도움이 됨 (캐싱으로 인한 SQL 절약)
2. DB 고립 수준이 Level2(Read Comitted)여도 Repeatable Read 보장(캐싱으로 인한 SQL 절약)

```java
Long memberId = 1L;
Member m1 = jpa.find(Member.class, memberId); // JPA에 의한 쿼리 실행
Member m2 = jpa.find(Member.class, memberId); // JPA에 의한 캐시 로드

m1 == m2; // true: SQL을 한번한 실행
```

→ 실무에서 막 크게 도움이 되진 않지만 소소하게 도움이 됨

### 트랜잭션을 지원하는 쓰기 지연(버퍼링)

1. 트랜잭션 커밋까지 Persistence Context에서 SQL 누적함(일종의 버퍼링)
2. JDBC BATCH SQL 기능으로 한번에 SQL 전송(flush)

```java
tx.begin();

em.persist(memberA);
em.persist(memberB);
em.persist(memberC);
// INSERT SQL을 아직 쏘지 않는다

tx.commit(); // 커밋 시 write-behind SQL 저장소에 쌓였던 SQL을 한번에 flush
```

# 지연로딩 및 즉시로딩

### 지연 로딩

객체가 실제 사용되는 그 순간에 쿼리 조회 및 로딩

```java
Member member = memberDAO.find(memberId); // Member 객체가 필요한 순간 SELECT 쿼리 실행
Team team = member.getTeam();
String teamName = team.getName(); // 지연 로딩: Team 객체가 필요한 순간 SELECT 쿼리 실행
```

### 즉시 로딩

JOIN SQL로 한번에 연관 관계의 객체 정보도 미리 조회

```java
Member member = memberDAO.find(memberId); // 연관 Team 객체에 대한 정보까지 JOIN으로 조회
Team team = member.getTeam();
String teamName = team.getName();
```

→ 개발자 옵션에 따라 특정 객체를 즉시 로딩과 지연 로딩 선택이 가능하다

# JPA 설정

## persistence.xml

→ `resources/META-INF/persistence.xml`  로 프로젝트의 JPA 설정파일 위치는 항상 고정

## DB Dialect: hibernate.dialect

JPA는 특정 데이터베이스 종속 X → DB dialect property를 설정해주는 것을 통해 독립 보장

```java
<property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
```

# JPA 구동 방식

1. 설정 정보 조회
    
    `META-INF/persistence.xml` 설정 파일 로드
    
2. EntityManagerFactory 생성
    
    설정 파일을 바탕으로 EntityManager를 생성하는 EntityMangerFactory 생성
    
3. 팩토리에서 EntityManager 생성

```java
public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("basic");

        EntityManager em = emf.createEntityManager();

		    EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member = new Member();

            member.setId(1L);
            member.setName("a");

            em.persist(member);

            tx.commit();

        } catch (Exception e) {

            tx.rollback();
        } finally {

            em.close();
            emf.close();
        }
    }
```

# 관례

### @Entity

DTO 같은 자바 객체를 JPA에서 ORM으로 DB와 연결시켜주기 위해 사용하는 어노테이션

### @Table(”DB테이블 이름")

JPA는 디폴트로 객체 클래스 이름과 DB 테이블 이름을 동일 시 하여 맵핑하는데, 객체와 테이블의 이름이 상이할 경우 해당 어노테이션으로 맵핑 대상 테이블을 임의적으로 설정 가능

### @Column(”DB 컬럼 이름")

@Table과 동일하게, 객체 프로퍼티와 테이블 칼럼을 맵핑을 임의 설정 가능

# CRUD

- 삽입: persist(객체)
- 조회: find(객체.class, PK)
- 수정: find → 객체 수정
- 삭제: remove

⇒ transaction.commit() 실행 해야 실제 변경내용이 DB에 반영됨

# 주의사항

### EntityManagerFactory는 싱글톤패턴으로 애플리케이션 전체에서 공유해야 한다

### EntityManager는 쓰레드 간 공유하면 안된다 (사용하고 버릴 것)

→ 1회용으로 쓰고 버려야 한다

### JPA의 모든 데이터 변경은 트랜잭션 안에서 실행해야 한다

# JPQL

단순한 조회는 EntityManager.find로 객체 조회하면 되지만, 조건부 다양한 검색의 경우는 JPQL을 사용

- JPQL은 JPA에서 제공하는 SQL을 추상화(DB 종속 X)한 객체 지향 쿼리 언어
- JPQL은 엔티티 객체(즉, 애플리케이션 쪽의 객체)를 대상으로 쿼리를 사용

e.g., 회원을 모두 검색

```java
List<Member> result = em
    .createQuery("select m from Member as m", Member.class).getResultList();
```

JPA를 사용하면 엔티티 객체 중심의 개발을 하게됨

→ 문제는 검색 쿼리 (어떻게 자바 객체를 검색 할 것이냐...?)

→ 검색을 할 때도 테이블이 아니라 엔티티 객체를 대상으로 검색

→ 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능

→ 애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요

→ JPQL
