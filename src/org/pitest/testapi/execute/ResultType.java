// 
// Decompiled by Procyon v0.5.36
// 

package org.pitest.testapi.execute;

import org.pitest.testapi.TestListener;
import org.pitest.functional.SideEffect1;
import org.pitest.testapi.TestResult;
import org.pitest.functional.F;

public enum ResultType
{
    PASS((ResultToListenerSideEffect)new ResultToListenerSideEffect() {
        @Override
        public SideEffect1<TestListener> apply(final TestResult a) {
            return ResultType.success(a);
        }
    }), 
    FAIL((ResultToListenerSideEffect)new ResultToListenerSideEffect() {
        @Override
        public SideEffect1<TestListener> apply(final TestResult a) {
            return ResultType.failure(a);
        }
    }), 
    SKIPPED((ResultToListenerSideEffect)new ResultToListenerSideEffect() {
        @Override
        public SideEffect1<TestListener> apply(final TestResult a) {
            return ResultType.skipped(a);
        }
    }), 
    STARTED((ResultToListenerSideEffect)new ResultToListenerSideEffect() {
        @Override
        public SideEffect1<TestListener> apply(final TestResult a) {
            return ResultType.started(a);
        }
    });
    
    private final F<TestResult, SideEffect1<TestListener>> function;
    
    private ResultType(final ResultToListenerSideEffect f) {
        this.function = f;
    }
    
    public SideEffect1<TestListener> getListenerFunction(final TestResult result) {
        return this.function.apply(result);
    }
    
    public static SideEffect1<TestListener> success(final TestResult result) {
        return new SideEffect1<TestListener>() {
            @Override
            public void apply(final TestListener a) {
                a.onTestSuccess(result);
            }
        };
    }
    
    public static SideEffect1<TestListener> failure(final TestResult result) {
        return new SideEffect1<TestListener>() {
            @Override
            public void apply(final TestListener a) {
                a.onTestFailure(result);
            }
        };
    }
    
    public static SideEffect1<TestListener> skipped(final TestResult result) {
        return new SideEffect1<TestListener>() {
            @Override
            public void apply(final TestListener a) {
                a.onTestSkipped(result);
            }
        };
    }
    
    public static SideEffect1<TestListener> started(final TestResult result) {
        return new SideEffect1<TestListener>() {
            @Override
            public void apply(final TestListener a) {
                a.onTestStart(result.getDescription());
            }
        };
    }
    
    private interface ResultToListenerSideEffect extends F<TestResult, SideEffect1<TestListener>>
    {
    }
}
