[돌아가기](https://github.com/LEEJ0NGWAN/JPA-Basic)

객체 내부 다른 객체 참조와 테이블 내부 외래키 조인에 대한 매핑 및 이질감을 어떻게 극복할 것인가?

# 단방향 연관관계

### @ManyToOne

다대일 연관관계 매핑을 위한 애노테이션

→ @JoinColumn(name = “테이블에 들어갈 외래키 칼럼 이름”)도 같이 설정해줘야 한다

```java
@ManyToOne
@JoinColumn(name = "TEAM_ID") // 외래키 이름을 TEAM_ID로 설정
private Team team;
```

# 양방향 연관관계

### @OneToMany

일대다 연관관계 매핑을 위한 애노테이션

→ 양방향 연관관계 매핑을 위한 연관관계 주인 객체 쪽의 참조 변수 이름을 mappedBy로 설정해줘야 한다

```java
@OneToMany(mappedBy = "team") // 연관관계 주인 쪽 참조 변수 이름
private List<Member> members = new ArrayList<>();
```

다만, 객체는 가급적이면 단방향의 연관관계가 좋다 (양방향 연관관계는 신경써야 하는 것이 매우 많아진다)

# 연관관계 주인

양방향 관계에 놓인 두 객체 중 한 객체에서만 외래키 관리가 이루어 져야 한다(연관관계 주인)

### 객체의 양방향 연관관계

각 객체의 내부에 참조하는 상호 객체를 프로퍼티로 가짐 (즉, 단방향 연관관계 2개가 존재)

`Member.team` < - > `Team.members`

### 테이블의 양방향 연관관계

외래키 1개를 이용하여 두 테이블의 연관관계를 표현 가능

`select * from member m join team t on m.team_id = t.id;`

`select * from team t join member m on t.id = m.team_id;`

### 양방향 매핑 규칙

- 양방향 연관관계의 두 객체 중 한 객체를 주인으로 선정
- 주인이 외래키 관리(등록, 수정)
- 주인이 아닌 객체는 읽기만 가능
- 주인이 아닌 객체에서는 mappedBy 속성으로 주인객체를 특정해야 한다

### 연관관계 주인 선정 기준

실제 외래키와 매핑되는 객체를 주인으로 선정

```java
class Member {

	@Id @GenereatedValue
	private Long id;

	private String name;

	@ManyToOne
	@JoinColumn(name = "team_id") // 테이블에서 외래키와 매핑된다 -> 연관관계 주인으로 선정
	private Team team;

	...
}
```

**이유**

외래키가 있는 객체 쪽으로 선정해야 엔티티와 테이블이 직접적으로 매핑이 되며, 오해의 요소를 최소화 시키고 성능 향상과 직관적 사용이 가능

# 양방향 연관관계 매핑 주의사항

### 주인 객체와 비주인 객체 둘 다 연관관계를 주입해야 한다

주인이 아닌 객체에 연관관계를 주입해도 읽기전용이기 때문에 매핑이 적용되지 않는다

```java
team.getMembers().add(member);

tx.commit(); // 매핑 적용되지 않는다!
```

```java
member.setTeam(team);

tx.commit(); // 연관관계 주인 객체에 연관관계 주입하면 매핑이 되긴 함
```

→  그러나 주인과 비주인 객체 둘다 주입을 해줘야 한다

em.persist()만 해놓고 트랜잭션 커밋이나 플러시가 일어나지 않는 이상 Persistence Context 내부에서 1차 캐싱이 되어 있는데, 비주인 객체는 트랜잭션 커밋 전까지 연관관계 매핑이 되지 않기 때문이다

```java
member.setTeam(team);

em.persist(); // 1차 캐싱 but no transaction commit

team.getMembers() // member 들어있지 않음!
```

### 무한 루프를 주의하자

LOMBOK이나 JSON 라이브러리 사용 시 양방향 연관관계 걸려있을 경우 무한 반복 루프에 걸릴 위험이 있다

- 스프링 컨트롤러에서 엔티티 자체 반환은 피하라 → 확장성과 무한루프 방지를 위해 DTO 반환을 권장

# 양방향 매핑 결론

가급적이면 쓰지마라

→ 단방향 매핑으로 설계를 끝내고, 역방향으로 조회 기능이 꼭 필요한 경우에만 양방향 연관관계를 추가한다
