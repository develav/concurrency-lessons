package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicLong;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private Bid latestBid = new Bid(0L, 0L, 0L);
    private AtomicLong atomicLong = new AtomicLong(0L);

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            notifier.sendOutdatedMessage(latestBid);
            do {
                latestBid = bid;
            } while (!atomicLong.compareAndSet(atomicLong.get(), bid.getPrice()));
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
