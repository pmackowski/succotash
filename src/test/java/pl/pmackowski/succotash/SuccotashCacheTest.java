package pl.pmackowski.succotash;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import pl.pmackowski.succotash.eviction.impl.EvictionEntriesScheduledStrategy;
import pl.pmackowski.succotash.eviction.impl.EvictionEntryScheduledStrategy;
import pl.pmackowski.succotash.eviction.impl.EvictionEntriesThreadSleepStrategy;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pmackowski on 2016-11-25.
 */
@RunWith(Theories.class)
public class SuccotashCacheTest {

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
            new SuccotashEvictEntryCache<>(new EvictionEntryScheduledStrategy<>())
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

}