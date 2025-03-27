package io.hhplus.tdd.point.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;


class LockManagerTest {
    private LockManager lockManager;

    @BeforeEach
    void setUp() {
        lockManager = new LockManager();
    }

    @DisplayName("lock 생성")
    @Nested
    class getLock {

        @DisplayName("id를 받으면 해당하는 id의 ReentRantLock을 반환한다.")
        @Test
        void success() {
            // given
            Long id = 1L;

            // when
            ReentrantLock reentrantLock = lockManager.getLock(id);

            // then
            assertThat(reentrantLock).isNotNull();
        }

        @DisplayName("동일한 id로 생성한 lock이 있을 경우 동일한 lock을 반환한다.")
        @Test
        void doubleCallSameGet() {
            // given
            Long id = 1L;
            ReentrantLock reentrantLock = lockManager.getLock(id);

            // when
            ReentrantLock reentrantLock2 = lockManager.getLock(id);

            // then
            assertThat(reentrantLock).isEqualTo(reentrantLock2);
        }
    }

    @DisplayName("unLock")
    @Nested
    class unLock {

        @DisplayName("lock을 획득한 상태의 lock 객체와 id를 넘기면 락을 해제시킨다.")
        @Test
        void success() {
            // given
            Long id = 1L;
            ReentrantLock lock = lockManager.getLock(id);
            lock.lock();
            // when
            lockManager.unLock(lock, id);

            // then
            assertThat(lock.isLocked()).isFalse();
        }

        @DisplayName("lock을 획득한 상태가 아니어도 unLock 실행 시 오류가 발생하지 않는다.")
        @Test
        void success2() {
            // given
            Long id = 1L;
            ReentrantLock lock = lockManager.getLock(id);

            // when
            lockManager.unLock(lock, id);

            // then
            assertThat(lock.isLocked()).isFalse();
        }
    }

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

}