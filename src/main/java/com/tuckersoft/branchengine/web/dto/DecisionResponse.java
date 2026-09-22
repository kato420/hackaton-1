package com.tuckersoft.branchengine.web.dto;

import java.time.Instant;

public class DecisionResponse {
    private Long id;
    private Long playthroughId;
    private String rawInput;
    private String branchType;
    private String impactLevel;
    private String handlerUnit;
    private String outcomeCode;
    private String resolvedNodeCode;
    private String status;
    private Instant createdAt;
    
    // Additional fields needed by tests
    private Integer lucidity;
    private Integer controlLevel;
    private String playthroughStatus;
    private String endingCode;

    public DecisionResponse(Long id, Long playthroughId, String rawInput, String branchType, String impactLevel, String handlerUnit, String outcomeCode, String resolvedNodeCode, String status, Instant createdAt, Integer lucidity, Integer controlLevel, String playthroughStatus, String endingCode) {
        this.id = id;
        this.playthroughId = playthroughId;
        this.rawInput = rawInput;
        this.branchType = branchType;
        this.impactLevel = impactLevel;
        this.handlerUnit = handlerUnit;
        this.outcomeCode = outcomeCode;
        this.resolvedNodeCode = resolvedNodeCode;
        this.status = status;
        this.createdAt = createdAt;
        this.lucidity = lucidity;
        this.controlLevel = controlLevel;
        this.playthroughStatus = playthroughStatus;
        this.endingCode = endingCode;
    }

    public Long getId() { return id; }
    public Long getPlaythroughId() { return playthroughId; }
    public String getRawInput() { return rawInput; }
    public String getBranchType() { return branchType; }
    public String getImpactLevel() { return impactLevel; }
    public String getHandlerUnit() { return handlerUnit; }
    public String getOutcomeCode() { return outcomeCode; }
    public String getResolvedNodeCode() { return resolvedNodeCode; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Integer getLucidity() { return lucidity; }
    public Integer getControlLevel() { return controlLevel; }
    public String getPlaythroughStatus() { return playthroughStatus; }
    public String getEndingCode() { return endingCode; }
}
