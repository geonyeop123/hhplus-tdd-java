# Java의 동시성 제어에 대한 기술보고서

### Java에서 제공하는 동시성 제어 관련한 키워드와 방법, 장단점 서술 및 적용 사례
---

## synchronized 

우선 동시성 테스트를 위해  임계영역의 있는 메서드를 멀티쓰레드 환경에서 실행해보았다.
```java
    @DisplayName("동시에 한 유저에 대한 여러 요청이 들어왔을 때 정상적으로 동작한다.")
    @Test
    void concurrency() throws InterruptedException {
        // given
        Long id = 11L;
        int threadCount = 4;
        CountDownLatch latch = new CountDownLatch(threadCount);
        // when
        for (int i = 0; i < threadCount; i++) {
            int number = i;
            new Thread(() -> {
                if(number % 2 == 0){
                    pointService.charge(id, 10L);
                }else{
                    pointService.use(id, 10L);
                }
                latch.countDown();
            }).start();
        }

        latch.await();

        // then
        UserPoint userPoint = userPointTable.selectById(id);
        assertThat(userPoint.point()).isEqualTo(0L);
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
        assertThat(pointHistories).extracting("userId", "amount", "type")
                .containsExactly(
                        tuple(id, 10L, TransactionType.CHARGE)
                        , tuple(id, 10L, TransactionType.USE)
                        , tuple(id, 10L, TransactionType.CHARGE)
                        , tuple(id, 10L, TransactionType.USE)
                );
    }
```

동시성 문제가 발생하는 코드는 다음과 같다.
```java
    public UserPoint charge(Long id, Long amount) {
        long updateMillis = System.currentTimeMillis();

        UserPoint userPoint = userPointTable.selectById(id);
        UserPoint chargedPoint = userPoint.charge(amount);
        pointHistoryService.insert(id, amount, CHARGE, updateMillis);

        return userPointTable.insertOrUpdate(id, chargedPoint.point());
    }

    public UserPoint use(Long id, Long amount) {
        long updateMillis = System.currentTimeMillis();

        UserPoint userPoint = userPointTable.selectById(id);
        UserPoint chargedPoint = userPoint.use(amount);
        pointHistoryService.insert(id, amount, USE, updateMillis);

        return userPointTable.insertOrUpdate(id, chargedPoint.point());
    }
```

![Image](https://github.com/user-attachments/assets/be712c41-195c-4437-a6ed-5d3498017794)

공유 자원 (table)에 따른 동시성 문제가 발생하는 것으로 확인되며, 오류와 함께 테스트 조차 끝나지 않고 있다.

이를 해결하기 위해 synchronized 키워드를 사용해보았습니다. 
synchronized 키워드는 메서드 단위 혹은 synchronized block 을 만들어 사용할 수 있습니다.

```java
    public synchronized UserPoint charge(Long id, Long amount) {
        long updateMillis = System.currentTimeMillis();

        UserPoint userPoint = userPointTable.selectById(id);
        UserPoint chargedPoint = userPoint.charge(amount);
        pointHistoryService.insert(id, amount, CHARGE, updateMillis);

        return userPointTable.insertOrUpdate(id, chargedPoint.point());
    }

    public synchronized UserPoint use(Long id, Long amount) {
        long updateMillis = System.currentTimeMillis();

        UserPoint userPoint = userPointTable.selectById(id);
        UserPoint chargedPoint = userPoint.use(amount);
        pointHistoryService.insert(id, amount, USE, updateMillis);

        return userPointTable.insertOrUpdate(id, chargedPoint.point());
    }
```

![Image](https://github.com/user-attachments/assets/025a8ba1-fe02-4dac-8001-34af01295452)

테스트에 통과하였지만, synchronized 키워드는 아래와 같은 문제를 야기할 수 있습니다.

객체 수준에서 락(lock)을 걸어 한 번에 하나의 스레드만 메서드에 접근할 수 있도록 처리하는 방식

- lock을 소유하지 못한 스레드는 작업을 수행하지 못하여 성능 이슈 문제와 dead lock을 야기할 수 있다.


**Dead Lock (교착 상태)**
잘못된 자원 관리로 인하여 둘 이상의 프로세스 또는 스레드들이 아무것도 진행하지 않는 상태

---
## ConcurrentHashMap + ReentantLock

ConcurrentHashMap이란 ?

- Java의 멀티 스레드 환경에서 안전하게 사용할 수 있는 Map 구현체
- 일반적으로 HashMap은 동기화 처리가 되어있지 않아 Race Condition이 발생할 수 있음
- ConcurrentHashMap은 내부적으로 세그먼트 단위로 락을 걸어 동시 접근에 최적화

ReentantLock이란 ?

- 동기화(lock) 매커니즘으로, 멀티 스레드 환경에서 공유 자원의 동시 접근을 제어하기 위해 사용
- 명시적 락을 방식을 사용하며, 재진입, 공정성, 락 타임아웃, 조건 변수 지원 등 다양한 기능을 제공

프로젝트 적용

멀티 쓰레드 환경에서 동일한 id의 값으로 충전, 사용을 요청하는 경우 동기화하기 위해 ReenTantLock을 사용

다른 id 값의 처리는 병렬적으로 처리되어야 하기 때문에, ConcurrentHashMap을 활용하여 Lock 관리

```java
@Component
public class LockManager {

    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public ReentrantLock getLock(Long id){
        lockMap.putIfAbsent(id, new ReentrantLock());
        return lockMap.get(id);
    }

    public void unLock(ReentrantLock lock, Long id){

        if(null != lock && lock.isLocked()){
            lock.unlock();
        }
        lockMap.remove(id);
    }

}
```

해당 컴포넌트를 생성하여, lock 발급과 해제 처리에 대한 관심사 분리를 진행하였습니다.

```java
    public UserPoint use(Long id, Long amount) {
        long updateMillis = System.currentTimeMillis();

        ReentrantLock lock = lockManager.getLock(id);
        lock.lock();
        try{
            UserPoint userPoint = userPointTable.selectById(id);
            UserPoint chargedPoint = userPoint.use(amount);
            pointHistoryService.insert(id, amount, USE, updateMillis);
            return userPointTable.insertOrUpdate(id, chargedPoint.point());
        }finally {
            lockManager.unLock(lock, id);
        }
    }
```

lock을 발급받아, 동기화 로직을 처리하고 해제하는 과정입니다.

발급과 해제, 그리고 멀티쓰레드 환경에서 정말로 동일한 id에 대한 요청에 같은 lock을 보유하게 되는지 확인하기 위해 
테스트 코드를 작성하였으며 Service 통합테스트에서도 해당 기능으로 테스트를 완료하였습니다.

LockManagerTest.java
```java 
    @DisplayName("10개의 쓰레드에서 동일한 id로 manager로 lock을 받으면 동일한 lock을 부여받는다.")
    @Test
    void useLockByMultiThread() throws InterruptedException {
        // given
        Long id = 1L;
        Set<ReentrantLock> set = new HashSet<>();
        // when
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        // when

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                ReentrantLock lock = lockManager.getLock(id);
                lock.lock();
                try{
                    TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 500));
                    set.add(lock);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    lockManager.unLock(lock, id);
                }
                latch.countDown();
            }).start();
        }

        latch.await();

        // then
        assertThat(set).hasSize(1);
    }
```
