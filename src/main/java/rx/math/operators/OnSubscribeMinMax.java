/**
 * Copyright 2016 Netflix, Inc.
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
import rx.Observable.OnSubscribe;
import rx.Subscriber;

public final class OnSubscribeMinMax<T> implements OnSubscribe<T> {

    final Observable<T> source;
    
    final Comparator<? super T> comparator;
    
    final int compensator;
    
    @SuppressWarnings("rawtypes")
    public static final Comparator<Comparable> COMPARABLE_MIN = new Comparator<Comparable>() {
        @SuppressWarnings("unchecked")
        @Override
        public int compare(Comparable a, Comparable b) {
            return a.compareTo(b);
        }
    };

    public OnSubscribeMinMax(Observable<T> source, Comparator<? super T> comparator, int compensator) {
        this.source = source;
        this.comparator = comparator;
        this.compensator = compensator;
    }

    @Override
    public void call(Subscriber<? super T> t) {
        new MinMaxSubscriber<T>(t, comparator, compensator).subscribeTo(source);
    }

    static final class MinMaxSubscriber<T> extends ScalarDeferredSubscriber<T, T> {

        final Comparator<? super T> comparator;
        
        final int compensator;
        
        public MinMaxSubscriber(Subscriber<? super T> actual, Comparator<? super T> comparator, int compensator) {
            super(actual);
            this.comparator = comparator;
            this.compensator = compensator;
        }

        @Override
        public void onNext(T t) {
            T v = value;
            if (hasValue) {
                if (comparator.compare(v, t) * compensator > 0) {
                    value = t;
                }
            } else {
                value = t;
                hasValue = true;
            }
        }
        
        @Override
        public void onCompleted() {
            if (hasValue) {
                complete(value);
            } else {
                actual.onError(new NoSuchElementException());
            }
        }
    }
}
