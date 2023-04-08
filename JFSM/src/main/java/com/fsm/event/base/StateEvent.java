package com.fsm.event.base;

import java.util.Arrays;
import java.util.Set;

/**
 * @class public class StateEvent
 * @brief StateEvent class
 * FSM 시나리오의 가장 기본 단위 클래스
 */
public class StateEvent {

    // Event name
    private String name;
    // From state
    private Set<String> fromStateSet;
    // To state
    private String toState;
    // Success CallBack
    private CallBack successCallBack;
    // Fail CallBack
    private CallBack failCallBack;

    // Next event
    private String nextEvent;
    // Interval time for triggering the next event
    private int nextEventInterval;
    // Next Event Retry Count
    private int nextEventRetryCount;
    // Parameters for the callback
    private Object[] nextEventCallBackParams;

    ////////////////////////////////////////////////////////////////////////////////

    public StateEvent() {}

    ////////////////////////////////////////////////////////////////////////////////

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getFromStateSet() {
        return fromStateSet;
    }

    public void setFromStateSet(Set<String> fromStateSet) {
        this.fromStateSet = fromStateSet;
    }

    public String getToState() {
        return toState;
    }

    public void setToState(String toState) {
        this.toState = toState;
    }

    public CallBack getSuccessCallBack() {
        return successCallBack;
    }

    public void setSuccessCallBack(CallBack successCallBack) {
        this.successCallBack = successCallBack;
    }

    public CallBack getFailCallBack() {
        return failCallBack;
    }

    public void setFailCallBack(CallBack failCallBack) {
        this.failCallBack = failCallBack;
    }

    public String getNextEvent() {
        return nextEvent;
    }

    public void setNextEvent(String nextEvent) {
        this.nextEvent = nextEvent;
    }

    public int getNextEventInterval() {
        return nextEventInterval;
    }

    public void setNextEventInterval(int nextEventInterval) {
        this.nextEventInterval = nextEventInterval;
    }

    public int getNextEventRetryCount() {
        return nextEventRetryCount;
    }

    public void setNextEventRetryCount(int nextEventRetryCount) {
        this.nextEventRetryCount = nextEventRetryCount;
    }

    public Object[] getNextEventCallBackParams() {
        return nextEventCallBackParams;
    }

    public void setNextEventCallBackParams(Object[] nextEventCallBackParams) {
        this.nextEventCallBackParams = nextEventCallBackParams;
    }

    @Override
    public String toString() {
        return "StateEvent{" +
                "name='" + name + '\'' +
                ", fromStateSet='" + fromStateSet + '\'' +
                ", toState='" + toState + '\'' +
                ", nextEvent='" + nextEvent + '\'' +
                ", nextEventInterval=" + nextEventInterval +
                ", successCallBack=" + successCallBack +
                ", failCallBack=" + failCallBack +
                ", nextEventCallBackParams=" + Arrays.toString(nextEventCallBackParams) +
                '}';
    }
}
