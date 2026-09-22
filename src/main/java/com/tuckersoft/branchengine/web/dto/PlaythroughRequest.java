package com.tuckersoft.branchengine.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PlaythroughRequest {
    @NotBlank
    @Size(min = 2, max = 40)
    private String playerTag;

    @NotBlank
    private String startNodeCode;

    public String getPlayerTag() { return playerTag; }
    public void setPlayerTag(String playerTag) { this.playerTag = playerTag; }
    public String getStartNodeCode() { return startNodeCode; }
    public void setStartNodeCode(String startNodeCode) { this.startNodeCode = startNodeCode; }
}
