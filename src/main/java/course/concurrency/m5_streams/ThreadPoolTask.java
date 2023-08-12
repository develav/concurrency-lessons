package course.concurrency.m5_streams;

import java.util.concurrent.*;

public class ThreadPoolTask {

    // Task #1
    public ThreadPoolExecutor getLifoExecutor() {
        return new ThreadPoolExecutor(5, 5, 0, TimeUnit.MILLISECONDS, new LifoBlockingDeque<>());
    }

    public ThreadPoolExecutor getRejectExecutor() {
         return new ThreadPoolExecutor(8, 8, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), new ThreadPoolExecutor.DiscardPolicy());
    }

    static class LifoBlockingDeque<E> extends LinkedBlockingDeque<E> {

        @Override
        public boolean offer(E e) {
            return super.offerFirst(e);
        }

    }
}

