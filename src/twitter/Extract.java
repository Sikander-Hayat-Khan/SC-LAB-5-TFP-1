package twitter;

import java.time.Instant;
import java.util.*;
import java.util.regex.*;

// Extract consists of methods that extract information from a list of tweets.
public class Extract {

    /**
     * Get the time period spanned by tweets.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        if (tweets.isEmpty()) {
            // The spec doesn't define behavior for empty input; choose a safe default
            Instant now = Instant.now();
            return new Timespan(now, now);
        }

        Instant start = tweets.get(0).getTimestamp();
        Instant end = start;

        for (Tweet t : tweets) {
            Instant time = t.getTimestamp();
            if (time.isBefore(start)) start = time;
            if (time.isAfter(end)) end = time;
        }

        return new Timespan(start, end);
    }

    /**
     * Get usernames mentioned in a list of tweets.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> users = new HashSet<>();

        // Regex explanation:
        // (?<=^|[^A-Za-z0-9_]) ensures '@' is not part of another word (not preceded by a valid username char)
        // @([A-Za-z0-9_]+) captures the username part
        // \b ensures it ends cleanly (not followed by another username-valid char)
        Pattern mentionPattern = Pattern.compile("(?<=^|[^A-Za-z0-9_])@([A-Za-z0-9_]+)\\b");

        for (Tweet t : tweets) {
            Matcher matcher = mentionPattern.matcher(t.getText());
            while (matcher.find()) {
                String username = matcher.group(1).toLowerCase();
                users.add(username);
            }
        }

        return users;
    }

}
