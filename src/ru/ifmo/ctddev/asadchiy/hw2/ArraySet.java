package ru.ifmo.ctddev.asadchiy.hw2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Unmodified NavigableSet interface implementation
 *
 * Created by Pavel Asadchiy
 * on 11.09.16 14:43.
 */
public class ArraySet<E> extends AbstractSet<E> implements NavigableSet<E> {

    private final List<E> data;
    private final Comparator<? super E> comparator;

    public ArraySet(Collection<? extends E> collection, Comparator<? super E> comparator) {
        this.comparator = comparator;
        this.data = sortedUnmodifiableList(collection, comparator);
    }

    private List<E> sortedUnmodifiableList(Collection<? extends E> collection, Comparator<? super E> comparator) {
        if (collection.isEmpty()) {
            return Collections.unmodifiableList(Collections.emptyList());
        }
        List<E> temporaryList = new ArrayList<>(collection);
        Collections.sort(temporaryList, comparator);
        List<E> data = new ArrayList<>();
        data.add(temporaryList.get(0));
        for (int i = 1; i < temporaryList.size(); i++) {
            if (comparator == null
                    ? temporaryList.get(i) != temporaryList.get(i - 1)
                    : comparator.compare(temporaryList.get(i), temporaryList.get(i - 1)) != 0) {
                data.add(temporaryList.get(i));
            }
        }
        return Collections.unmodifiableList(data);
    }

    public ArraySet() {
        this(Collections.emptyList(), null);
    }

    public ArraySet(SortedSet<E> sortedSet) {
        this(sortedSet, sortedSet.comparator());
    }

    public ArraySet(Collection<E> collection) {
        this(collection, null);
    }

    private ArraySet(List<E> collection, Comparator<? super E> comparator) {
        this.data = collection;
        this.comparator = comparator;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArraySetIterator();
    }

    @Override
    public int size() {
        return data.size();
    }

    private class ArraySetIterator implements Iterator<E> {

        private int index;

        @Override
        public boolean hasNext() {
            return index < data.size();
        }

        @Override
        public E next() {
            if (hasNext()) {
                return data.get(index++);
            }
            throw new UnsupportedOperationException("no more elements");
        }
    }

    /**
     * Start implemented methods from NavigableSet
     * @see NavigableSet
     */

    @Override
    public E lower(E e) {
        int ind = Collections.binarySearch(data, e, comparator);
        return ind == 0 ? null : (ind > 0 ? data.get(ind - 1) : data.get(-(ind + 1) - 1));
    }

    @Override
    public E floor(E e) {
        int ind = Collections.binarySearch(data, e, comparator);
        return ind == -1 ? null : (ind >= 0 ? data.get(ind) : data.get(-(ind + 1) - 1));
    }

    @Override
    public E ceiling(E e) {
        int ind = Collections.binarySearch(data, e, comparator);
        return ind >= 0 ? data.get(ind) : data.get(-(ind + 1) - 1);
    }

    @Override
    public E higher(E e) {
        int ind = Collections.binarySearch(data, e, comparator);
        return ind == data.size() - 1 ? null : (ind >= 0 ? data.get(ind) : data.get(-(ind + 1) - 1));
    }

    @Override
    public E pollFirst() {
       throw new UnsupportedOperationException("collection is not modified");
    }

    @Override
    public E pollLast() {
        throw new UnsupportedOperationException("collection is not modified");
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return new ArraySet<>(data, Collections.reverseOrder(comparator));
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    @Override
    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
        return tailSet(fromElement, fromInclusive).headSet(toElement, toInclusive);
    }

    @Override
    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
        int ind = Collections.binarySearch(data, toElement, comparator);
        return new ArraySet<>(data.subList(0, ind < 0 ? -(ind) - 1 : (inclusive ? ++ind : ind)), comparator);
    }

    @Override
    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        int ind = Collections.binarySearch(data, fromElement, comparator);
        int position = ind < 0 ? -(ind) - 1 : (!inclusive ? ++ind : ind);
        return new ArraySet<>(data.subList(position, data.size()), comparator);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return tailSet(fromElement, true);
    }

    @Override
    public E first() {
        if (data.isEmpty()) {
            throw new NoSuchElementException("no elements in set");
        }
        return data.get(0);
    }

    @Override
    public E last() {
        if (data.isEmpty()) {
            throw new NoSuchElementException("no elements in set");
        }
        return data.get(data.size() - 1);
    }

    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(data, (E) o, comparator) >= 0;
    }
}
