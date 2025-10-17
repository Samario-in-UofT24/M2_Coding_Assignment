package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    // TODO Task 2: Complete this class
    private final BreedFetcher delegate;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.delegate = Objects.requireNonNull(fetcher, "fetcher");
    }

    @Override
    public List<String> getSubBreeds(String breed) {
        String key = Objects.requireNonNull(breed, "breed").toLowerCase(Locale.ROOT);

        List<String> cached = cache.get(key);
        if (cached != null) return cached;

        callsMade++; // only when we hit the delegate
        try {
            List<String> result = delegate.getSubBreeds(key);
            // defensive, immutable cache entry
            List<String> copy = Collections.unmodifiableList(new ArrayList<>(result));
            cache.put(key, copy);
            return copy;
        } catch (BreedNotFoundException e) {
            // do not cache failures
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}