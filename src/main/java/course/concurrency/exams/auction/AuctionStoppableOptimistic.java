package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(0L, 0L, 0L);
    private AtomicMarkableReference<Bid> ref = new AtomicMarkableReference<>(latestBid, false);

    public boolean propose(Bid bid) {
        Bid oldBid = latestBid;
        do {
            oldBid = ref.getReference();
            if ((bid.getPrice() <= oldBid.getPrice()) && ref.isMarked()) {
                return false;
            }
        } while (!ref.compareAndSet(oldBid, bid, false, false));

        notifier.sendOutdatedMessage(oldBid);
        return true;
    }

    public Bid getLatestBid() {
        return ref.getReference();
    }

    public Bid stopAuction() {
        ref.set(ref.getReference(), true);
        return getLatestBid();
    }
}
