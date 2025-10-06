package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.*;

import org.junit.Test;

public class ExtractTest {

    /*
     * Testing strategy
     *
     * getTimespan(tweets):
     *  - tweets.size() = 0, 1, >1
     *  - timestamps in order, reverse order, random order
     *
     * getMentionedUsers(tweets):
     *  - no mentions
     *  - one mention at start/middle/end of text
     *  - multiple mentions
     *  - case-insensitive duplicates (e.g., @Bob, @BOB)
     *  - invalid mentions inside email (e.g., bit@mit.edu)
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "charlie", "@Alice and @BOB are attending the talk!", d3);

    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // ---- getTimespan() tests ----

    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));

        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }

    @Test
    public void testGetTimespanOneTweet() {
        Timespan timespan = Extract.getTimespan(Collections.singletonList(tweet1));
        assertEquals(d1, timespan.getStart());
        assertEquals(d1, timespan.getEnd());
    }

    @Test
    public void testGetTimespanMultipleUnorderedTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet3, tweet1, tweet2));
        assertEquals("earliest start", d1, timespan.getStart());
        assertEquals("latest end", d3, timespan.getEnd());
    }

    // ---- getMentionedUsers() tests ----

    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersSingleMention() {
        Tweet tweet = new Tweet(4, "bob", "Hello @Alice", d1);
        Set<String> mentions = Extract.getMentionedUsers(Arrays.asList(tweet));
        assertEquals(Set.of("alice"), mentions);
    }

    @Test
    public void testGetMentionedUsersCaseInsensitiveDuplicates() {
        Tweet t1 = new Tweet(5, "user", "Hey @BOB", d1);
        Tweet t2 = new Tweet(6, "user", "@bob good morning", d2);
        Set<String> mentions = Extract.getMentionedUsers(Arrays.asList(t1, t2));
        assertEquals("mentions should be case-insensitive", Set.of("bob"), mentions);
    }

    @Test
    public void testGetMentionedUsersIgnoresEmails() {
        Tweet tweet = new Tweet(7, "user", "contact me at example@mit.edu", d1);
        Set<String> mentions = Extract.getMentionedUsers(Arrays.asList(tweet));
        assertTrue("email address should not be treated as mention", mentions.isEmpty());
    }

    @Test
    public void testGetMentionedUsersMultipleMentions() {
        Set<String> mentions = Extract.getMentionedUsers(Arrays.asList(tweet3));
        assertTrue(mentions.contains("alice"));
        assertTrue(mentions.contains("bob"));
        assertEquals("expected two unique mentions", 2, mentions.size());
    }
}
