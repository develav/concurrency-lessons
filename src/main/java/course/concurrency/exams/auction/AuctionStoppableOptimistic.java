package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private Notifier notifier;
    private volatile boolean stop;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(0L, 0L, 0L);
    private AtomicReference<Bid> atomicReference = new AtomicReference<>(latestBid);

    public boolean propose(Bid bid) {
        Bid oldBid = latestBid;
        do {
            oldBid = atomicReference.get();
            if (bid.getPrice() <= oldBid.getPrice()) {
                return false;
            }
        } while (!stop && !atomicReference.compareAndSet(oldBid, bid));

        notifier.sendOutdatedMessage(oldBid);
        return true;
    }

    public Bid getLatestBid() {
        return atomicReference.get();
    }

    public Bid stopAuction() {
        stop = true;
        return atomicReference.get();
    }
}
