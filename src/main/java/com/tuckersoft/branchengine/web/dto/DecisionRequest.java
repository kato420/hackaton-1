package com.tuckersoft.branchengine.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class DecisionRequest {
    @NotNull
    private Long playthroughId;

    @NotBlank
    @Size(min = 10)
    private String rawInput;

    @Pattern(regexp = "^(LEVE|MODERADO|GRAVE|CRITICO)$", message = "Nivel invalido")
    private String impactLevel;

    public Long getPlaythroughId() { return playthroughId; }
    public void setPlaythroughId(Long playthroughId) { this.playthroughId = playthroughId; }
    public String getRawInput() { return rawInput; }
    public void setRawInput(String rawInput) { this.rawInput = rawInput; }
    public String getImpactLevel() { return impactLevel; }
    public void setImpactLevel(String impactLevel) { this.impactLevel = impactLevel; }
}
