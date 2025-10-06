package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.*;

import org.junit.Test;

public class FilterTest {

    /*
     * Testing strategy
     *
     * writtenBy(tweets, username):
     *   - tweets.size() = 0, 1, >1
     *   - username case = same, different case
     *   - author present or absent
     *
     * inTimespan(tweets, timespan):
     *   - tweet before, within, after timespan
     *   - boundary cases (exactly at start/end)
     *
     * containing(tweets, words):
     *   - no words
     *   - one word match
     *   - multiple words
     *   - case-insensitive matching
     *   - no tweet matches
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "Lunch time soon", d3);

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false;
    }

    // ----- writtenBy() tests -----

    @Test
    public void testWrittenBySingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");

        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue(writtenBy.contains(tweet1));
    }

    @Test
    public void testWrittenByCaseInsensitive() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet3), "ALYSSA");

        assertEquals("expected two tweets by alyssa", 2, writtenBy.size());
        assertTrue(writtenBy.contains(tweet1));
        assertTrue(writtenBy.contains(tweet3));
    }

    @Test
    public void testWrittenByNoMatch() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "unknown");
        assertTrue("expected empty list", writtenBy.isEmpty());
    }

    // ----- inTimespan() tests -----

    @Test
    public void testInTimespanInclusiveBoundaries() {
        Instant start = d1;
        Instant end = d2;
        Timespan span = new Timespan(start, end);

        List<Tweet> inSpan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), span);
        assertTrue("should include tweet at start", inSpan.contains(tweet1));
        assertTrue("should include tweet at end", inSpan.contains(tweet2));
        assertFalse("should exclude tweet after end", inSpan.contains(tweet3));
    }

    @Test
    public void testInTimespanNoTweetsInRange() {
        Instant start = Instant.parse("2016-02-17T13:00:00Z");
        Instant end = Instant.parse("2016-02-17T14:00:00Z");
        Timespan span = new Timespan(start, end);

        List<Tweet> inSpan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), span);
        assertTrue("expected empty list", inSpan.isEmpty());
    }

    // ----- containing() tests -----

    @Test
    public void testContainingSingleWord() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("talk"));
        assertTrue("expected tweet1 and tweet2", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertFalse("tweet3 does not contain 'talk'", containing.contains(tweet3));
    }

    @Test
    public void testContainingCaseInsensitive() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("RIVEST"));
        assertTrue("expected both tweets mentioning rivest", containing.containsAll(Arrays.asList(tweet1, tweet2)));
    }

    @Test
    public void testContainingNoMatches() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("banana"));
        assertTrue("expected empty list", containing.isEmpty());
    }

    @Test
    public void testContainingMultipleWords() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3), Arrays.asList("rivest", "lunch"));
        assertTrue("expected tweet1 and tweet2 and tweet3", containing.containsAll(Arrays.asList(tweet1, tweet2, tweet3)));
    }
}
