package com.tuckersoft.branchengine.service;

import com.tuckersoft.branchengine.domain.Decision;

public class DecisionEvent {
    private final Decision decision;
    private final String simulateFailure;

    public DecisionEvent(Decision decision, String simulateFailure) {
        this.decision = decision;
        this.simulateFailure = simulateFailure;
    }

    public Decision getDecision() {
        return decision;
    }

    public String getSimulateFailure() {
        return simulateFailure;
    }
}
