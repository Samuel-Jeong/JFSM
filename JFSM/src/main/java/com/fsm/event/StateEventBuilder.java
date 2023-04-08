package com.fsm.event;

import com.fsm.event.base.CallBack;
import com.fsm.event.base.StateEvent;

import java.util.Set;

public class StateEventBuilder {

    private final StateEvent stateEvent;

    public StateEventBuilder() {
        this.stateEvent = new StateEvent();
    }

    public StateEventBuilder setName(String name) {
        stateEvent.setName(name);
        return this;
    }

    public StateEventBuilder setFromStateSet(Set<String> fromStateSet) {
        stateEvent.setFromStateSet(fromStateSet);
        return this;
    }

    public StateEventBuilder setToState(String toState) {
        stateEvent.setToState(toState);
        return this;
    }

    public StateEventBuilder setSuccessCallBack(CallBack successCallBack) {
        stateEvent.setSuccessCallBack(successCallBack);
        return this;
    }

    public StateEventBuilder setFailCallBack(CallBack failCallBack) {
        stateEvent.setFailCallBack(failCallBack);
        return this;
    }

    public StateEventBuilder setNextEvent(String nextEvent) {
        stateEvent.setNextEvent(nextEvent);
        return this;
    }

    public StateEventBuilder setNextEventInterval(int nextEventInterval) {
        stateEvent.setNextEventInterval(nextEventInterval);
        return this;
    }

    public StateEventBuilder setNextEventRetryCount(int nextEventRetryCount) {
        stateEvent.setNextEventRetryCount(nextEventRetryCount);
        return this;
    }

    public StateEventBuilder setNextEventCallBackParams(Object[] nextEventCallBackParams) {
        stateEvent.setNextEventCallBackParams(nextEventCallBackParams);
        return this;
    }

    public StateEvent build() {
        return stateEvent;
    }

}
