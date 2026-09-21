package com.tuckersoft.branchengine.service;

import com.tuckersoft.branchengine.domain.*;
import com.tuckersoft.branchengine.web.BadRequestException;
import com.tuckersoft.branchengine.web.ConflictException;
import com.tuckersoft.branchengine.web.ResourceNotFoundException;
import com.tuckersoft.branchengine.web.dto.PlaythroughRequest;
import com.tuckersoft.branchengine.web.dto.PlaythroughResponse;
import com.tuckersoft.branchengine.web.dto.PathResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaythroughService {

    private final PlaythroughRepository playthroughRepository;
    private final StoryNodeRepository nodeRepository;
    private final UserRepository userRepository;

    public PlaythroughService(PlaythroughRepository playthroughRepository, StoryNodeRepository nodeRepository, UserRepository userRepository) {
        this.playthroughRepository = playthroughRepository;
        this.nodeRepository = nodeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PlaythroughResponse createPlaythrough(PlaythroughRequest request, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        StoryNode node = nodeRepository.findByNodeCode(request.getStartNodeCode())
                .orElseThrow(() -> new ResourceNotFoundException("Node not found"));
        
        if (playthroughRepository.existsByPlayerTag(request.getPlayerTag())) {
            throw new ConflictException("PlayerTag ya existe");
        }
        
        if (node.getBranchCapacity() > 0 && node.getCurrentBranches() >= node.getBranchCapacity()) {
            throw new BadRequestException("Nodo lleno");
        }
        
        Playthrough p = new Playthrough();
        p.setPlayerTag(request.getPlayerTag());
        p.setUser(user);
        p.setStartNodeCode(node.getNodeCode());
        p.setCurrentNode(node);
        p.setLucidity(100);
        p.setControlLevel(0);
        p.setStatus("ACTIVA");
        p.setEndingCode(null);
        Instant now = Instant.now();
        p.setCreatedAt(now);
        p.setUpdatedAt(now);
        
        node.setCurrentBranches(node.getCurrentBranches() + 1);
        nodeRepository.save(node);
        
        playthroughRepository.save(p);
        return mapToResponse(p);
    }

    public List<PlaythroughResponse> getPlaythroughs(String email, String role) {
        if ("ROLE_ADMIN".equals(role)) {
            return playthroughRepository.findAllByOrderByCreatedAtDesc().stream().map(this::mapToResponse).collect(Collectors.toList());
        } else {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return playthroughRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()).stream().map(this::mapToResponse).collect(Collectors.toList());
        }
    }

    public PlaythroughResponse getPlaythroughById(Long id, String email, String role) {
        Playthrough p = playthroughRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No encontrado"));
        checkAccess(p, email, role);
        return mapToResponse(p);
    }

    public PathResponse getPlaythroughPath(Long id, String email, String role) {
        Playthrough p = playthroughRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No encontrado"));
        checkAccess(p, email, role);
        
        // El array de pasos estara vacio por ahora hasta Estrella 4
        return new PathResponse(
                p.getId(),
                p.getPlayerTag(),
                p.getStatus(),
                p.getEndingCode(),
                p.getStartNodeCode(),
                p.getCurrentNode().getNodeCode(),
                new ArrayList<>()
        );
    }

    private void checkAccess(Playthrough p, String email, String role) {
        if (!"ROLE_ADMIN".equals(role) && !p.getUser().getEmail().equals(email)) {
            throw new org.springframework.security.access.AccessDeniedException("No es tu partida");
        }
    }

    private PlaythroughResponse mapToResponse(Playthrough p) {
        return new PlaythroughResponse(
                p.getId(), p.getPlayerTag(), p.getUser().getEmail(),
                p.getStartNodeCode(), p.getCurrentNode().getNodeCode(),
                p.getLucidity(), p.getControlLevel(), p.getStatus(),
                p.getEndingCode(), p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}
