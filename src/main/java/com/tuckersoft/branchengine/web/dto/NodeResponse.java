package com.tuckersoft.branchengine.web.dto;

import java.time.Instant;

public class NodeResponse {
    private Long id;
    private String nodeCode;
    private String title;
    private String sceneText;
    private Integer branchCapacity;
    private Integer currentBranches;
    private String primaryBranchCode;
    private String glitchBranchCode;
    private Instant createdAt;

    public NodeResponse(Long id, String nodeCode, String title, String sceneText, Integer branchCapacity, Integer currentBranches, String primaryBranchCode, String glitchBranchCode, Instant createdAt) {
        this.id = id;
        this.nodeCode = nodeCode;
        this.title = title;
        this.sceneText = sceneText;
        this.branchCapacity = branchCapacity;
        this.currentBranches = currentBranches;
        this.primaryBranchCode = primaryBranchCode;
        this.glitchBranchCode = glitchBranchCode;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getNodeCode() { return nodeCode; }
    public String getTitle() { return title; }
    public String getSceneText() { return sceneText; }
    public Integer getBranchCapacity() { return branchCapacity; }
    public Integer getCurrentBranches() { return currentBranches; }
    public String getPrimaryBranchCode() { return primaryBranchCode; }
    public String getGlitchBranchCode() { return glitchBranchCode; }
    public Instant getCreatedAt() { return createdAt; }
}
