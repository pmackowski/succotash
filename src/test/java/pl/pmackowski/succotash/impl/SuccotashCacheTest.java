package pl.pmackowski.succotash.impl;

import junit.framework.TestCase;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import pl.pmackowski.succotash.Cache;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pmackowski on 2016-11-25.
 */
public class SuccotashCacheTest {

    private static final String KEY_1 = "key1";
    private static final String VALUE_1 = "value1";
    private static final long ONE_SECOND = 1000;
    private static final long TWO_SECONDS = 2000;

    private Cache<String, String> cache = new SuccotashCache<>();

    @Test
    public void shouldReturnValue() {
        // given
        cache.put(KEY_1, VALUE_1, ONE_SECOND);

        // when
        String actual = cache.get(KEY_1);

        // then
        assertThat(actual).isEqualTo(VALUE_1);
    }

    @Test
    public void shouldReturnNullForAlreadyEvictedKey() throws InterruptedException {
        // given
        cache.put(KEY_1, VALUE_1, ONE_SECOND);
        Thread.sleep(TWO_SECONDS);

        // when
        String actual = cache.get(KEY_1);

        // then
        assertThat(actual).isNull();
    }
}