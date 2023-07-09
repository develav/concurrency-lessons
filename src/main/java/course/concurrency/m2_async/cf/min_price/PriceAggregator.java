package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {

        ExecutorService executorService = Executors.newFixedThreadPool(shopIds.size());

        List<CompletableFuture<Double>> list = shopIds.stream()
                .map(shopId -> CompletableFuture
                        .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executorService)
                        .orTimeout(2900, TimeUnit.MILLISECONDS)
                        .exceptionally(t -> Double.NaN)
                )
                .collect(Collectors.toList());

        Stream<Double> doubleStream = list.stream().map(x -> {
            Double aDouble = Double.MAX_VALUE;
            try {
                aDouble = x.get();
            } catch (Exception e) {
            }
            return aDouble;
        });

        return doubleStream.min(Comparator.naturalOrder()).get();
    }
}
