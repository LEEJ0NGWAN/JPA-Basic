[돌아가기](https://github.com/LEEJ0NGWAN/JPA-Basic)

# 엔티티 매핑

# 객체와 테이블 맵핑

## @Entity

JPA가 관리하는 클래스라는 것을 명시하는 애노테이션

→ JPA을 통해 테이블과 매핑하려는 클래스에 @Entity 부착 필수

- 기본 생성자 필수 요구(public 또는 protected) → 리플렉션과 같은 프록시 사용 위함
- final, enum, interface, inner 클래스 불가
- 필드에 final 사용 X

### name

@Entity(name = “특정 이름")

→ JPA에서 사용할 엔티티의 고유 이름을 설정 (기본값 = 클래스 이름)

→ 웬만하면 기본값 사용함

## @Table

엔티티에 맵핑될 테이블 관련 설정 → 이름이 다를 경우 @Table(name=”테이블이름") 설정

(디폴트: 엔티티와 테이블 이름이 같을 경우 자동 매핑)

## @Column

객체 필드와 칼럼 맵핑 관련 설정

- name
- unique
- length
- nullable

# DB 스키마 자동 생성

애플리케이션 실행 시점에 DDL 자동 생성 가능

→ 테이블 중심의 개발에서 객체 중심 개발로  집중 가능

- DB dialect에 따라 DDL 생성
- 운영 서버에서는 가급적이면 이 기능 자제할 것

### `hibernate.hbm2ddl.auto`

persistence.xml 에서 설정 시 다양한 자동 DDL 생성 가능

- create : 기존 테이블 제거 후 생성 (drop if exists → create)
- create-drop : 애플리케이션 종료 시점에 테이블 drop 후처리가 추가된 create
- update: 변경 부분 반영 (운영 DB 사용 X) → 단, “추가"에 대해서만 변경 반영(제거는 위험해서 반영 X)
- validate: 엔티티와 테이블이 정상 매핑되었는지만 검사
- none: 어떤 동작도 하지 않음 → 관례상 적는 값으로 명시적으로 자동 DDL 생성 기능을 미사용 선언

```java
<property name="hibernate.hbm2ddl.auto" value="create"/>
<property name="hibernate.hbm2ddl.auto" value="create-drop"/>
<property name="hibernate.hbm2ddl.auto" value="update"/>
<property name="hibernate.hbm2ddl.auto" value="validate"/>
<property name="hibernate.hbm2ddl.auto" value="none"/>
```

## 주의

### 운영에서 절대 create, create-drop, update 사용 금지

위험도가 너무 크다

### 개발 서버: create, update 권장

### 테스트 서버: update, validate 권장

### 스테이징 및 운영 서버: validate, none 권장

# 필드와 컬럼 맵핑

## @Column

`@Column( ... )`

- name: 매핑될 테이블 칼럼 명 지정 (디폴트: 객체 필드 이름)
- insertable: 등록 가능 여부 (디폴트: TRUE)
- updatable: 수정 가능 여부
- nullable(DDL): DDL 자동 생성 시 not null 제약조건 유무
- unique(DDL):  DDL 자동 생성 시 한 칼럼에 대한 유니크 제약조건을 간단하게 걸기 위한 속성
- columnDefinition(DDL): 칼럼 정보 직접 주입 → `varchar(100) default ‘EMPTY’`
- length(DDL): String 타입에 대해, 길이 제약 조건 정의 (디폴트: 255)
- precision: BigDecimal 또는 BigIntger 타입에 대해, 소수 포함 전체 자리수 (디폴트: 19)
- scale(DDL)

### @Temporal

날짜 타입 매핑 (java: Date, Calendar → DB: DATE, TIME, TIMESTAMP)

`@Temporal(TemporalType.STRING)`

→ java8 부터는 크게 필요 없는 기능... (LocalDate, LocalDateTime으로 대체)

최신 하이버네이트가 자동으로 LocalDate → DATE, LocalDateTime → TIMESTAMP로 변환함

### @Enumerated

enum 타입 매핑 (디폴트: EnumType.ORDINAL)

- EnumType.ORDINAL: enum 순서를 DB에 저장
- EnumType.STRING: enum 이름을 DB에 저장

`@Enumerated(EnumType.STRING)` 권장

→ EnumType.ORDINAL의 경우 ENUM 변경에 따른 파급 효과가 너무 위험하기 때문

### @Lob

VARCHAR 크기를 초월하는 컨텐츠(LOB) 타입에 대한 매핑 (BLOB, CLOB)

- 필드 타입 = 문자열 → CLOB 매핑 (String, char[], java.sql.CLOB)
- 필드 타입 ≠ 문자열 → BLOB 매핑 (byte[], java.sql.BLOB)

### @Transient

테이블 칼럼과의 맵핑에서 제외하고 싶은 객체 필드에 대한 설정
