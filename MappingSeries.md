[돌아가기](https://github.com/LEEJ0NGWAN/JPA-Basic)

# 다양한 연관관계 매핑

# 매핑 시 고려사항

### 다중성

DB 관점에서의 다중성을 고려한 애노테이션

- @ManyToOne
- @OneToMany
- @OneToOne
- @ManyToMany (실무에서 쓰면 안되는 수준)

### 단방향, 양방향

- 테이블
    - 외래 키 하나를 가지고 테이블을 서로 조인 가능
    - 방향의 개념이 존재하지 않음
- 객체
    - 참조형 필드 변수를 선언한 객체 쪽에서만 참조 가능
    - 참조형 필드 변수를 한쪽 객체에서만 선언한 경우 → 단방향
    - 참조형 필드 변수를 양쪽 객체에서 선언한 경우 → 양방향(정확히 말하면, 단방향 2개)

### 연관관계 주인

객체의 양쪽 연관관계에서 주인이 되는 객체를 선정

→ 외래키를 관리하는 참조 변수를 가지는 객체 쪽이 연관관계 주인이 되도록 선정하는 것을 권장
(반대쪽 객체는 외래키 영향 없으며, 조회 기능만 수행)

# N:1

다대일 → N 쪽의 객체에서 외래키를 관리한다

```java
@Entity
public class Member {

	@Id @GeneratedValue
	private Long id;

	private String name;

	@ManyToOne
	@JoinColumn(name = "TEAM_ID")
	private Team team;

	// getter & setter
}
```

# 1:N

일대다 → 1 쪽의 객체에서 외래키를 관리하도록 연관관계 주인으로 설정 가능 but 권장하지 않음

```java
@Entity
public class Team {

	@Id @GeneratedValue
	private Long id;

	private String name;

	@OneToMany
	@JoinColumn(name = "TEAM_ID")
	List<Member> members = new ArrayList<>();

	// getter & setter
}
```

TEAM_ID 라는 연관관계 외래키를 저장하기 위한 업데이트 쿼리가 한번 더 나가기 때문에 1:N 매핑은 비추

→ 다대일에서 N 객체에 외래키와 매핑을 설정하고, 필요하다면 1 쪽의 객체에 조회기능만 추가하는 양방향 매핑을 추가하는 것을 권장

## 1:N에서 양방향 매핑

N 쪽 객체에 참조 변수를 추가하고 싶은 경우 사용

→ 공식적으로 존재하는 매핑은 아니지만, @JoinColumn 애노테이션에 약간의 트릭을 사용

insertable, updatable 옵션을 false로 설정하여, 하이버네이트가 필드의 자동 수정 및 추가를 금지시킴

→ 읽기전용 필드로 만드는 트릭

(누누히 말하지만 추가 쿼리로 인한 성능 문제로 인해 1:N 시리즈는 양방향 매핑을 포함하여 권장되지 않는다...)

(필요한 경우가 실무에서 아~주 가끔 필요할 수도 있긴 하다)

```java
@ManyToOne
@JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)
private Team team;
```

# 1:1

1:1 관계의 경우, 어떤 테이블(객체)이던지 상관없이 연관관계 주인 선정 가능

외래키에 DB 유니크 제약조건이 추가되면 1:1 관계가 된다 (1:1 보장을 위한 규칙 추가)

두 객체 중 연관관계 주인이 되는 쪽에 @JoinColumn을, 비주인 객체에는 mappedBy 속성을 추가해준다

### 연관관계 주인 객체 예시

```java
@Entity
public class Member {

	@Id @GeneratedValue
	private Long id;

	private String name;

	@OneToOne
	@JoinColumn(name = "LOCKER_ID")
	private Locker locker;

	// getter & setter
}
```

### 비 주인 객체 예시

```java
@Entity
public class Locker {

	@Id @GeneratedValue
	private Long id;

	private String name;

	@OneToOne(mappedBy = "LOCKER_ID")
	private Member member;

	// getter & setter
}
```

### 1:1 → 연관관계 주인 객체와 외래키 존재 테이블이 다른 경우

외래키가 존재하는 테이블을 기준으로 연관관계 주인을 다시 설정한다

### 정리

**주 테이블에 외래키 설정**

- 객체지향 개발자 선호
- JPA 매핑 용이
- 주 테이블 조회로 대상 테이블 데이터 존재 유무 확인 가능
- 대상 테이블 데이터 없으면 외래키 null 들어감

**대상 테이블에 외래키 설정**

- 전통적인 DB 개발자 선호
- 주 테이블과 대상 테이블의 연관관계가 일대다로 변경될 경우, 테이블 구조 유지에 용이
- 프록시 기능 한계로, 지연 로딩이 먹히지 않고 즉시 로딩됨

# N:M

RDB에서는 정규화 된 테이블 2개를 직접 다대다 관계로 표현할 수 없다

→ 중간테이블(연결 테이블)을 추가하여 일대다 및 다대일 관계로 한번 풀어낼 필요가 있다

⇒ 그러나... 객체끼리는 컬렉션 프레임워크를 사용하여 객체 2개로 다대다 관계를 표현할 수 있다

@ManyToMany 애노테이션을 이용하여 중간테이블로 표현된 다대다 관계를 객체에 매핑할 수 있다

주인 객체 쪽 참조 변수에 @JoinTable을 붙여 연결 테이블을 생성하는 것에 주의한다

### @JoinTable

연결 테이블을 자동 생성해주는 애노테이션

3가지 속성이 있다

- name: String → 연결 테이블의 이름을 설정
- joinColumns: @JoinColumn → 연결 테이블의 주인 객체의 키를 설정
- inverseJoinColumns: @JoinColumn → 연결 테이블의 비주인 객체의 키를 설정

joinColumns, inverseJoinColumns 속성을 설정하지 않을 경우,

각 객체에 포함된 `참조 변수의 이름 + _id` 의 형태로 외래키 칼럼명이 설정된다

### 주인객체

```java
@Entity
public class Member {

	@Id @GeneratedValue
	private Long id;

	private String name;

	@ManyToMany
	@JoinTable(
		name = "member_product", 
		joinColumns = @JoinColumn(name = "member_id"),
		inverseJoinColumns = @JoinColumn(name = "product_id"))
	private List<Product> products = new ArrayList<>();

	// getter & setter
}
```

### 비주인 객체

```java
@Entity
public class Product {

	@Id @GeneratedValue
	private Long id;

	private String name;

	@ManyToMany(mappedBy = "products")
	private List<Member> members;
}
```

## 다대다 관계 매핑의 한계

### 실무에서 사용 X

단순히 연결만 하고 끝나는게 아님 → 주문 시간, 수량 같은 데이터가 들어올 수 있음

### 개선 방안

다대다 관계의 경우 연결 테이블을 엔티티로 승격시켜 하나의 객체로써 다룬다

즉, @ManyToMany → @OneToMany + @ManyToOne 로 쪼갠다
