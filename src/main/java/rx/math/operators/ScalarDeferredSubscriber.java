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

import java.util.concurrent.atomic.AtomicInteger;

import rx.*;

/**
 * A subscriber that is unbounded-in, can hold onto a value and emits it when
 * requested.
 *
 * @param <T> the input value type
 * @param <R> the output value type
 * 
 * TODO move this to RxJava
 */
public abstract class ScalarDeferredSubscriber<T, R> extends Subscriber<T> {

    final Subscriber<? super R> actual;
    
    protected boolean hasValue;
    
    protected R value;
    
    final AtomicInteger state = new AtomicInteger();

    static final int NO_REQUEST_NO_VALUE = 0;
    static final int HAS_REQUEST_NO_VALUE = 1;
    static final int NO_REQUEST_HAS_VALUE = 2;
    static final int HAS_REQUEST_HAS_VALUE = 3;
    
    public ScalarDeferredSubscriber(Subscriber<? super R> actual) {
        this.actual = actual;
    }

    @Override
    public final void onError(Throwable e) {
        value = null;
        actual.onError(e);
    }
    
    @Override
    public void onCompleted() {
        if (hasValue) {
            complete(value);
        } else {
            actual.onCompleted();
        }
    }
    
    protected final void complete(R v) {
        Subscriber<? super R> a = actual;
        
        for (;;) {
            int s = state.get();
            if (s == NO_REQUEST_HAS_VALUE || s == HAS_REQUEST_HAS_VALUE || a.isUnsubscribed()) {
                return;
            }
            if (s == HAS_REQUEST_NO_VALUE) {
                a.onNext(v);
                if (!a.isUnsubscribed()) {
                    a.onCompleted();
                }
                return;
            }
            value = v;
            if (state.compareAndSet(s, NO_REQUEST_HAS_VALUE)) {
                return;
            }
        }
    }

    public final void subscribeTo(Observable<T> source) {
        Subscriber<? super R> a = actual;
        a.add(this);
        a.setProducer(new Producer() {
            @Override
            public void request(long n) {
                innerRequested(n);
            }
        });
        source.unsafeSubscribe(this);
    }
    
    @Override
    public final void setProducer(Producer p) {
        p.request(Long.MAX_VALUE);
    }
    
    final void innerRequested(long n) {
        if (n <= 0) {
            return;
        }

        Subscriber<? super R> a = actual;

        for (;;) {
            int s = state.get();
            if (s == HAS_REQUEST_NO_VALUE || s == HAS_REQUEST_HAS_VALUE || a.isUnsubscribed()) {
                return;
            }
            if (s == NO_REQUEST_HAS_VALUE) {
                if (state.compareAndSet(s, HAS_REQUEST_HAS_VALUE)) {
                    a.onNext(value);
                    if (!a.isUnsubscribed()) {
                        a.onCompleted();
                    }
                }
                return;
            }
            if (state.compareAndSet(s, HAS_REQUEST_NO_VALUE)) {
                return;
            }
        }
    }
}
