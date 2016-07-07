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

import rx.*;
import rx.Observable.OnSubscribe;

public final class OnSubscribeSumDouble implements OnSubscribe<Double> {

    final Observable<Double> source;

    final boolean zeroDefault;

    public OnSubscribeSumDouble(Observable<Double> source, boolean zeroDefault) {
        this.source = source;
        this.zeroDefault = zeroDefault;
    }

    @Override
    public void call(Subscriber<? super Double> t) {
        new SumDoubleSubscriber(t, zeroDefault).subscribeTo(source);
    }

    static final class SumDoubleSubscriber extends ScalarDeferredSubscriber<Double, Double> {

        double sum;
        
        public SumDoubleSubscriber(Subscriber<? super Double> actual, boolean zeroDefault) {
            super(actual);
            if (zeroDefault) {
                hasValue = true;
            }
        }

        @Override
        public void onNext(Double t) {
            if (!hasValue) {
                hasValue = true;
            }
            sum += t.doubleValue();
        }
        
        @Override
        public void onCompleted() {
            if (hasValue) {
                complete(sum);
            } else {
                actual.onError(new IllegalArgumentException());
            }
        }

    }
}
