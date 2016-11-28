package pl.pmackowski.succotash;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import pl.pmackowski.succotash.eviction.EvictionConstants;
import pl.pmackowski.succotash.eviction.impl.EvictionEntriesScheduledStrategy;
import pl.pmackowski.succotash.eviction.impl.EvictionEntriesThreadSleepStrategy;
import pl.pmackowski.succotash.eviction.impl.EvictionEntryScheduledStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pmackowski on 2016-11-25.
 */
@RunWith(Theories.class)
public class SuccotashCacheTest {

    private static final String KEY = "key";
    private static final String KEY_1 = "key1";
    private static final String KEY_2 = "key2";
    private static final String VALUE_1 = "value1";
    private static final String VALUE_2 = "value2";
    private static final long ONE_SECOND = 1000;
    private static final long TWO_SECONDS = 2 * ONE_SECOND;
    private static final long TEN_SECONDS = 10 * ONE_SECOND;

    @SuppressWarnings("unused")
    @DataPoints
    public static List<Cache<String, String>> caches = Arrays.asList(
            new SuccotashEvictEntriesCache<>(new EvictionEntriesScheduledStrategy()),
            new SuccotashEvictEntriesCache<>(new EvictionEntriesThreadSleepStrategy()),
            new SuccotashEvictEntryCache<>(new EvictionEntryScheduledStrategy<>()) // the winner
    );

    @Theory
    public void shouldReturnNullValueForNonexistentKey(Cache<String, String> cache) {
        // given
        cache.put(KEY_1, VALUE_1, ONE_SECOND);

        // when
        String actual = cache.get(KEY_2);

        // then
        assertThat(actual).isNull();
    }

    @Theory
    public void shouldReturnValueForExistingKey(Cache<String, String> cache) {
        // given
        cache.put(KEY_1, VALUE_1, ONE_SECOND);

        // when
        String actual = cache.get(KEY_1);

        // then
        assertThat(actual).isEqualTo(VALUE_1);
    }

    @Theory
    public void shouldReturnNullValueForAlreadyEvictedKey(Cache<String, String> cache) throws InterruptedException {
        // given
        cache.put(KEY_1, VALUE_1, ONE_SECOND);
        Thread.sleep(TWO_SECONDS);

        // when
        String actual = cache.get(KEY_1);

        // then
        assertThat(actual).isNull();
    }

    @Theory
    public void shouldReplaceKeyWithNewValue(Cache<String, String> cache) throws InterruptedException {
        // given
        cache.put(KEY_1, VALUE_1, TEN_SECONDS);
        cache.put(KEY_1, VALUE_2, ONE_SECOND);

        // when
        String actual = cache.get(KEY_1);

        // then
        assertThat(actual).isEqualTo(VALUE_2);
    }

    @Theory
    public void shouldReplaceKeyWithNewValueAndShorterTTL(Cache<String, String> cache) throws InterruptedException {
        // given
        cache.put(KEY_1, VALUE_1, TEN_SECONDS);
        cache.put(KEY_1, VALUE_2, ONE_SECOND);
        Thread.sleep(TWO_SECONDS);

        // when
        String actual = cache.get(KEY_1);

        // then
        assertThat(actual).isNull();
    }

    @Theory
    public void shouldReplaceKeyWithNewValueAndLongerTTL(Cache<String, String> cache) throws InterruptedException {
        // given
        cache.put(KEY_1, VALUE_1, ONE_SECOND);
        cache.put(KEY_1, VALUE_2, TEN_SECONDS);
        Thread.sleep(TWO_SECONDS);

        // when
        String actual = cache.get(KEY_1);

        // then
        assertThat(actual).isEqualTo(VALUE_2);
    }

    @Theory // TODO move to other place, its not a unit test
    public void shouldDealWithBigObjectsTogetherLargerThanMaxHeapSize(Cache<String, String> cache) throws InterruptedException {
        // EvictionConstants.EVICTION_INTERVAL is used internally by SuccotashEvictEntriesCache cache,
        // which evicts periodically all expired entries.
        // If we set timeToLive to value smaller than EvictionConstants.EVICTION_INTERVAL then for SuccotashEvictEntriesCache
        // this test could not pass. On the other hand, regardless of timeToLive SuccotashEvictEntryCache should pass this test.
        long timeToLive = EvictionConstants.EVICTION_INTERVAL;
        char[] twoHundredMegabyteObject = new char[100 * 1024 * 1024]; // char is 2 bytes large
        int numberOfObjects = 50;

        // together we put into cache ~10 GB of data
        for (int i=0; i < numberOfObjects; i++) {
            cache.put(KEY + i, new String(twoHundredMegabyteObject), timeToLive);
            Thread.sleep(timeToLive);
        }

        IntStream.rangeClosed(1, numberOfObjects).forEach( value -> {
            assertThat(cache.get(KEY + value)).isNull();
        });
    }
}