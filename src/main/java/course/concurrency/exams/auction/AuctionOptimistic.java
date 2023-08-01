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
        boolean updated = false;
        while (!updated) {
            Long oldPrice = latestBid.getPrice();
            Long newPrice = bid.getPrice();

            if (newPrice > oldPrice) {
                latestBid = bid;
                updated = atomicLong.compareAndSet(oldPrice, newPrice);
            } else {
                break;
            }

            if (updated) {
                notifier.sendOutdatedMessage(latestBid);
            }

        }
        return updated;

    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
