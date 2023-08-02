package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(0L, 0L, 0L);
    private AtomicReference<Bid> atomicReference = new AtomicReference<>(latestBid);

    public boolean propose(Bid bid) {
        boolean updated = false;
        while (!updated) {
            Bid oldBid = atomicReference.get();
            if (bid.getPrice() > oldBid.getPrice()) {
                updated = atomicReference.compareAndSet(oldBid, bid);
            } else {
                break;
            }

            if (updated) {
                notifier.sendOutdatedMessage(atomicReference.get());
            }

        }
        return updated;

    }

    public Bid getLatestBid() {
        return atomicReference.get();
    }
}
