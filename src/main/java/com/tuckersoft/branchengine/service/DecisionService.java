package com.tuckersoft.branchengine.service;

import com.tuckersoft.branchengine.domain.*;
import com.tuckersoft.branchengine.web.BadRequestException;
import com.tuckersoft.branchengine.web.ConflictException;
import com.tuckersoft.branchengine.web.ResourceNotFoundException;
import com.tuckersoft.branchengine.web.dto.DecisionRequest;
import com.tuckersoft.branchengine.web.dto.DecisionResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.Instant;
import java.util.Map;

@Service
public class DecisionService {

    private final DecisionRepository decisionRepository;
    private final PlaythroughRepository playthroughRepository;
    private final StoryNodeRepository nodeRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DecisionService(DecisionRepository decisionRepository, PlaythroughRepository playthroughRepository, StoryNodeRepository nodeRepository, UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        this.decisionRepository = decisionRepository;
        this.playthroughRepository = playthroughRepository;
        this.nodeRepository = nodeRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    private static final Map<String, String> UNIDAD = Map.of(
            "OBEDIENCIA", "Mesa de Guion",
            "REBELDIA", "Control de Continuidad",
            "SOSPECHA", "Oficina de Seguridad",
            "RUPTURA_CUARTA_PARED", "Departamento Netflix",
            "ENTRADA_CORRUPTA", "Archivo de Errores");

    private static final Map<String, String> CONSECUENCIA = Map.of(
            "OBEDIENCIA", "ADVANCE_MAIN_PATH",
            "REBELDIA", "FORK_TIMELINE",
            "SOSPECHA", "INJECT_WHITE_BEAR_SYMBOL",
            "RUPTURA_CUARTA_PARED", "BREAK_FOURTH_WALL",
            "ENTRADA_CORRUPTA", "DISCARD_INPUT");

    @Transactional
    public DecisionResponse processDecision(DecisionRequest request, String email, String role, String simulate) {
        Playthrough p = playthroughRepository.findById(request.getPlaythroughId())
                .orElseThrow(() -> new ResourceNotFoundException("Partida no encontrada"));
                
        if (!p.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("No es tu partida");
        }

        if ("FINALIZADA".equals(p.getStatus())) {
            throw new ConflictException("Partida finalizada");
        }

        StoryNode currentNode = p.getCurrentNode();
        String inputNorm = Normalizer.normalize(request.getRawInput(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
        
        String branchType = "OBEDIENCIA";

        if (inputNorm.contains("pax") || inputNorm.contains("f.a.c.s.") || !inputNorm.matches(".*[a-z].*")) {
            branchType = "ENTRADA_CORRUPTA";
        } else if (inputNorm.contains("netflix") || inputNorm.contains("television") || inputNorm.contains("siglo xxi") || inputNorm.contains("camara")) {
            branchType = "RUPTURA_CUARTA_PARED";
        } else if (inputNorm.contains("gobierno") || inputNorm.contains("conspiracion") || inputNorm.contains("pac") || inputNorm.contains("control") || inputNorm.contains("vigilan")) {
            branchType = "SOSPECHA";
        } else if (inputNorm.contains("no") || inputNorm.contains("nunca") || inputNorm.contains("jamas") || inputNorm.contains("sal de mi cabeza") || inputNorm.contains("rechaza") || inputNorm.contains("destruye")) {
            branchType = "REBELDIA";
        }

        String impactLevel = request.getImpactLevel();
        if (impactLevel == null) impactLevel = "LEVE"; 

        Decision d = new Decision();
        d.setPlaythrough(p);
        d.setNode(currentNode);
        d.setRawInput(request.getRawInput());
        d.setBranchType(branchType);
        
        d.setHandlerUnit(UNIDAD.get(branchType));
        
        Instant now = Instant.now();
        d.setCreatedAt(now);
        d.setUpdatedAt(now);

        if ("ENTRADA_CORRUPTA".equals(branchType)) {
            d.setImpactLevel("NINGUNO");
            d.setStatus("ERROR");
            d.setOutcomeCode(CONSECUENCIA.get(branchType));
            d.setResolvedNodeCode(null);
            decisionRepository.save(d);
            return mapToResponse(d, p);
        }

        d.setImpactLevel(impactLevel);
        d.setStatus("REGISTRADA");
        
        int lucidityDelta = 0;
        int controlDelta = 0;
        switch (impactLevel) {
            case "LEVE": lucidityDelta = -5; controlDelta = 5; break;
            case "MODERADO": lucidityDelta = -15; controlDelta = 10; break;
            case "GRAVE": lucidityDelta = -30; controlDelta = 20; break;
            case "CRITICO": lucidityDelta = -40; controlDelta = 45; break;
        }

        p.setLucidity(Math.max(0, p.getLucidity() + lucidityDelta));
        p.setControlLevel(Math.min(100, p.getControlLevel() + controlDelta));
        p.setUpdatedAt(now);

        String nextNodeCode;
        if ("RUPTURA_CUARTA_PARED".equals(branchType) || "CRITICO".equals(impactLevel)) {
            nextNodeCode = currentNode.getGlitchBranchCode();
        } else {
            nextNodeCode = currentNode.getPrimaryBranchCode();
        }

        d.setResolvedNodeCode(nextNodeCode);
        d.setOutcomeCode(CONSECUENCIA.get(branchType));

        if (p.getControlLevel() >= 100) {
            String ending = "ENDING_PAC_SYMBOL";
            d.setOutcomeCode(ending);
            p.setStatus("FINALIZADA");
            p.setEndingCode(ending);
        } else if (p.getLucidity() <= 0) {
            String ending = "ENDING_WHITE_BEAR";
            d.setOutcomeCode(ending);
            p.setStatus("FINALIZADA");
            p.setEndingCode(ending);
        } else if (nextNodeCode == null || nextNodeCode.isEmpty()) {
            String ending = "ENDING_NETFLIX_CUT";
            d.setOutcomeCode(ending);
            p.setStatus("FINALIZADA");
            p.setEndingCode(ending);
        } else {
            StoryNode nextNode = nodeRepository.findByNodeCode(nextNodeCode).orElse(null);
            if (nextNode == null) {
                String ending = "ENDING_NETFLIX_CUT";
                d.setOutcomeCode(ending);
                p.setStatus("FINALIZADA");
                p.setEndingCode(ending);
            } else {
                p.setCurrentNode(nextNode);
            }
        }

        playthroughRepository.save(p);
        decisionRepository.save(d);

        eventPublisher.publishEvent(new DecisionEvent(d, simulate));

        return mapToResponse(d, p);
    }

    public Page<DecisionResponse> getDecisions(String branchType, String impactLevel, String status, Long playthroughId, String email, String role, Pageable pageable) {
        String ownerEmail = "ROLE_ADMIN".equals(role) ? null : email;
        Page<Decision> page = decisionRepository.findWithFilters(branchType, impactLevel, status, playthroughId, ownerEmail, pageable);
        return page.map(d -> mapToResponse(d, d.getPlaythrough()));
    }

    private DecisionResponse mapToResponse(Decision d, Playthrough p) {
        return new DecisionResponse(
                d.getId(), d.getPlaythrough().getId(), d.getRawInput(), d.getBranchType(),
                d.getImpactLevel(), d.getHandlerUnit(), d.getOutcomeCode(),
                d.getResolvedNodeCode(), d.getStatus(), d.getCreatedAt(),
                p.getLucidity(), p.getControlLevel(), p.getStatus(), p.getEndingCode()
        );
    }

    public DecisionResponse getDecisionById(Long id, String email, String role) {
        Decision d = decisionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Decision no encontrada"));
        if (!"ROLE_ADMIN".equals(role) && !d.getPlaythrough().getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("No es tu decision");
        }
        return mapToResponse(d, d.getPlaythrough());
    }
}
