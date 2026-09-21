package com.tuckersoft.branchengine.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NodeRequest {
    @NotBlank
    @Size(min = 3, max = 40)
    private String nodeCode;

    @NotBlank
    @Size(min = 3, max = 80)
    private String title;

    @NotBlank
    @Size(min = 10)
    private String sceneText;

    @NotNull
    @Min(1)
    private Integer branchCapacity;

    private String primaryBranchCode;
    private String glitchBranchCode;

    public String getNodeCode() { return nodeCode; }
    public void setNodeCode(String nodeCode) { this.nodeCode = nodeCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSceneText() { return sceneText; }
    public void setSceneText(String sceneText) { this.sceneText = sceneText; }
    public Integer getBranchCapacity() { return branchCapacity; }
    public void setBranchCapacity(Integer branchCapacity) { this.branchCapacity = branchCapacity; }
    public String getPrimaryBranchCode() { return primaryBranchCode; }
    public void setPrimaryBranchCode(String primaryBranchCode) { this.primaryBranchCode = primaryBranchCode; }
    public String getGlitchBranchCode() { return glitchBranchCode; }
    public void setGlitchBranchCode(String glitchBranchCode) { this.glitchBranchCode = glitchBranchCode; }
}
