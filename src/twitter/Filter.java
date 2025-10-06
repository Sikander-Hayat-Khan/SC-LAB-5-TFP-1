package twitter;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Filter {

    /**
     * Find tweets written by a particular user.
     */
    public static List<Tweet> writtenBy(List<Tweet> tweets, String username) {
        List<Tweet> result = new ArrayList<>();
        for (Tweet t : tweets) {
            if (t.getAuthor().equalsIgnoreCase(username)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Find tweets that were sent during a particular timespan.
     */
    public static List<Tweet> inTimespan(List<Tweet> tweets, Timespan timespan) {
        Instant start = timespan.getStart();
        Instant end = timespan.getEnd();
        List<Tweet> result = new ArrayList<>();

        for (Tweet t : tweets) {
            Instant time = t.getTimestamp();
            if ((time.equals(start) || time.isAfter(start)) &&
                    (time.equals(end) || time.isBefore(end))) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Find tweets that contain certain words.
     */
    public static List<Tweet> containing(List<Tweet> tweets, List<String> words) {
        List<Tweet> result = new ArrayList<>();
        if (words.isEmpty()) return result;

        // lowercase all search words for case-insensitive comparison
        Set<String> lowerWords = words.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        for (Tweet t : tweets) {
            String[] tweetWords = t.getText().split("\\s+");
            for (String w : tweetWords) {
                String cleaned = w.replaceAll("[^A-Za-z0-9#@]", "").toLowerCase();
                if (lowerWords.contains(cleaned)) {
                    result.add(t);
                    break; // add each tweet only once
                }
            }
        }
        return result;
    }
}
