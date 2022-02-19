[돌아가기](https://github.com/LEEJ0NGWAN/JPA-Basic)

# 영속성 관리

# Persistence Context

엔티티를 영구저장하기 위한 환경

**EntityManager.persist(entity);**

**→ 영속성 컨택스트에 객체를 저장하는 명령으로 정확히 따지면 DB에 저장하는 명령이 아니다**

영속성 컨택스트는 일종의 논리적인 개념으로, 눈에 보이는 요소는 아니다

→ 엔티티 매니저를 통해서 영속성 컨택스트에 접근 가능

# Entity lifecycle

### new/transient (비영속)

영속성 컨택스트와 전혀 관계 없는 새로운 상태

```java
// 객체 생성 (비영속)
Member member = new Member();
member.setId(1L);
member.setName("member1");
```

### managed (영속)

영속성 컨택스트에 의해 관리되는 상태

```java
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

// 객체 영속화 (managed) -> 영속성 컨택스트에 저장 명령
em.persist(member);
```

**DB에 저장되는 순간은?**

→ EntityManager의 트랜잭션이 commit 명령을 날리는 순간 DB 저장 SQL 실행

### detached (준영속)

영속성 컨택스트에 저장되어 있다가 분리된 상태

### removed (삭제)

삭제된 상태

# Persistence Context 장점

### 1차 캐시

영속성 컨택스트는 내부적으로 엔티티를 관리하고 있기 때문에 조회를 하거나 삽입을 하기 전에 1차적으로 내부에 캐싱을 하는 특징이 있다 (em.find, em.persist)

e.g., 데이터 조회 시

EntityManager.find

→ 영속성 컨택스트의 캐싱에 데이터 여부 확인 

→ 없으면 DB에서 가져옴 

→ **가져온 데이터 1차 캐싱** 

→ 대상 데이터 반환

⇒ 하나의 트랜잭션 안에서만 1차 캐싱이 유효(즉, 트랜잭션 종료 시 캐싱 초기화)하기 때문에 성능 상 크게 유리하진 않다 (성능보다는 영속성 컨택스트에 개념에서 얻을 수 있는 이점이 더 크다고 한다)

### Identity 보장 (영속되는 엔티티의 동일성 보장)

1차 캐싱 기능을 통해 Repeatable Read 기능을 애플리케이션 차원에 제공해줌 (단, 같은 트랜잭션 내부 한정)

### Transactional write-behind (트랜잭션 지원 쓰기 지연)

transaction.commit() 전까지 SQL을 보내지 않고 지연하는 기능 (일종의 버퍼링)

→ 즉, SQL을 영속성 컨택스트 내부의 쓰기 지연 SQL 저장소에 보관하고 있다가 commit 시 일괄 실행

(flush → commit)

hibernate.jdbc.batch_size

### Dirty checking (변경 감지)

트랜잭션 커밋 시점에서 영속성 컨택스트가 관리하고 있던 엔티티의 변경 사항이 있는지 체크하는 기능

→ JPA는 데이터를 마치 Java 컬렉션 프레임워크 다루듯이 사용하기 위한 프레임워크로, 별도의 update 명령어 없이 트랜잭션 커밋 시점에서 데이터 변경사항을 알아서 체크하고 flush를 진행한다

**Entity 업데이트 과정**

1. transaction.commit() 실행 시 JPA 내부에서 flush()가 실행 된다
2. flush 실행 명령 시, 영속성 컨택스트가 1차 캐싱에 관리되던 엔티티들의 기존 스냅샷과 현황을 비교한다
3. 변경 사항 확인 후 update sql을 자동 생성 한다
4. DB flush 및 commit 진행

### Lazy loading

# Flush

영속성 컨택스트 내부 변경 내용을 실제 DB에 반영하는 동작

## Flush 발생 시

- Dirty Checking
- 수정 엔티티 Lazy Loading SQL  저장소에 등록
- Lazy Loading SQL 저장소의 쿼리를 DB에 일괄 전송(CRUD)

## Flush 방법

- em.flush(): 직접 호출
- transaction.commit(): 자동 호출
- JPQL 쿼리 실행: 자동 호출

### JPQL 쿼리 실행 시 flush 자동 호출 이유

→ 영속성 컨택스트에만 저장이 되고 DB에 보관 되지 않은 객체를 DB 조회하는 것 같은 오류를 사전 예방 위함

### Flush Mode Option

- FlushModeType.AUTO(Default): commit, JPQL 실행 시
- FlushModeType.COMMIT: commit 할 때만 실행

### 주의 사항

- Persistence Context를 비우지 않음
- Persistence Context 변경 사항을 DB 동기화
- 트랜잭션 작업 단위가 중요함 → 커밋 직전에만 동기화 하면 됨

# 준영속 상태

영속 상태에 있던 Entity가 영속성 컨택스트에서 분리(Detached) 되는 상태 (em.detach)

→ 영속성 컨택스트가 제공하는 기능을 사용하지 못하는 상태(관리를 안하는 상태)
