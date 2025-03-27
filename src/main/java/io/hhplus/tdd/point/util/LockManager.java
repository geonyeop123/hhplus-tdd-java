package io.hhplus.tdd.point.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

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
