package net.ravendb.client.documents.session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnumerableUtils {

    /**
     * Extracts list from iterable
     */
    public static <T> List<T> toList(Iterator<T> iterator) {
        List<T> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    public static <T> T single(Iterator<T> iterator) {
        if (!iterator.hasNext()) {
            throw new IllegalStateException("Expected single result, but got empty");
        }
        T first = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalStateException("Expected single result, but got more than one");
        }
        return first;
    }

    public static <T> T singleOrDefault(Iterator<T> iterator) {
        if (!iterator.hasNext()) {
            return null;
        }
        T first = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalStateException("Expected single result, but got more than one");
        }
        return first;
    }

    public static <T> T first(Iterator<T> iterator) {
        if (!iterator.hasNext()) {
            throw new IllegalStateException("Expected single result, but got empty");
        }
        return iterator.next();
    }

    public static <T> T firstOrDefault(Iterator<T> iterator) {
        if (!iterator.hasNext()) {
            return null;
        }
        return iterator.next();
    }

    public static <T> boolean any(Iterator<T> iterator) {
        return iterator.hasNext();
    }

}
