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

import rx.Observable;

/**
 * A few operators for implementing the sum operation.
 * 
 * @see <a
 *      href="http://msdn.microsoft.com/en-us/library/system.reactive.linq.observable.sum%28v=vs.103%29.aspx">MSDN:
 *      Observable.Sum</a>
 */
public final class OperatorSum {
    private OperatorSum() { throw new IllegalStateException("No instances!"); }

    public static Observable<Integer> sumIntegers(Observable<Integer> source) {
        return Observable.create(new OnSubscribeSumInt(source, true));
    }

    public static Observable<Long> sumLongs(Observable<Long> source) {
        return Observable.create(new OnSubscribeSumLong(source, true));
    }

    public static Observable<Float> sumFloats(Observable<Float> source) {
        return Observable.create(new OnSubscribeSumFloat(source, true));
    }

    public static Observable<Double> sumDoubles(Observable<Double> source) {
        return Observable.create(new OnSubscribeSumDouble(source, true));
    }

    public static Observable<Integer> sumAtLeastOneIntegers(Observable<Integer> source) {
        return Observable.create(new OnSubscribeSumInt(source, false));
    }

    public static Observable<Long> sumAtLeastOneLongs(Observable<Long> source) {
        return Observable.create(new OnSubscribeSumLong(source, false));
    }

    public static Observable<Float> sumAtLeastOneFloats(Observable<Float> source) {
        return Observable.create(new OnSubscribeSumFloat(source, false));
    }

    public static Observable<Double> sumAtLeastOneDoubles(Observable<Double> source) {
        return Observable.create(new OnSubscribeSumDouble(source, false));
    }
}
