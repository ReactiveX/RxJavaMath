/**
 * Copyright 2014 Netflix, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx.math.operators;

import java.util.*;

import rx.Observable;
import rx.functions.*;

/**
 * Returns the minimum element in an observable sequence.
 */
public final class OperatorMinMax {
    private OperatorMinMax() { throw new IllegalStateException("No instances!"); }

    public static <T extends Comparable<? super T>> Observable<T> min(
            Observable<T> source) {
        return minMax(source, 1);
    }

    public static <T> Observable<T> min(Observable<T> source,
            final Comparator<? super T> comparator) {
        return minMax(source, comparator, 1);
    }

    public static <T, R extends Comparable<? super R>> Observable<List<T>> minBy(
            Observable<T> source, final Func1<T, R> selector) {
        return minMaxBy(source, selector, -1);
    }

    public static <T, R> Observable<List<T>> minBy(Observable<T> source,
            final Func1<T, R> selector, final Comparator<? super R> comparator) {
        return minMaxBy(source, selector, comparator, -1);
    }

    // -------------------------------------------------------------------------------

    public static <T extends Comparable<? super T>> Observable<T> max(
            Observable<T> source) {
        return minMax(source, -1);
    }

    public static <T> Observable<T> max(Observable<T> source,
            final Comparator<? super T> comparator) {
        return minMax(source, comparator, -1);
    }

    public static <T, R extends Comparable<? super R>> Observable<List<T>> maxBy(
            Observable<T> source, final Func1<T, R> selector) {
        return minMaxBy(source, selector, 1);
    }

    public static <T, R> Observable<List<T>> maxBy(Observable<T> source,
            final Func1<T, R> selector, final Comparator<? super R> comparator) {
        return minMaxBy(source, selector, comparator, 1);
    }

    // -------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------

    
    private static <T extends Comparable<? super T>> Observable<T> minMax(
            Observable<T> source, final int flag) {
        return minMax(source, OnSubscribeMinMax.COMPARABLE_MIN, flag);
    }

    private static <T> Observable<T> minMax(Observable<T> source,
            final Comparator<? super T> comparator, final int flag) {
        return Observable.create(new OnSubscribeMinMax<T>(source, comparator, flag));
    }

    private static <T, R extends Comparable<? super R>> Observable<List<T>> minMaxBy(
            Observable<T> source, final Func1<T, R> selector, final int flag) {
        return minMaxBy(source, selector, OnSubscribeMinMax.COMPARABLE_MIN, flag);
    }

    private static <T, R> Observable<List<T>> minMaxBy(Observable<T> source,
            final Func1<T, R> selector, final Comparator<? super R> comparator,
            final int flag) {
        return source.collect(new Func0<List<T>>() {
                    @Override
                    public List<T> call() {
                        return new ArrayList<T>();
                    }
                },
                new Action2<List<T>, T>() {

                    @Override
                    public void call(List<T> acc, T value) {
                        if (acc.isEmpty()) {
                            acc.add(value);
                        } else {
                            int compareResult = comparator.compare(
                                    selector.call(acc.get(0)),
                                    selector.call(value));
                            if (compareResult == 0) {
                                acc.add(value);
                            } else if (flag * compareResult < 0) {
                                acc.clear();
                                acc.add(value);
                            }
                        }
                    }
                });
    }

}
