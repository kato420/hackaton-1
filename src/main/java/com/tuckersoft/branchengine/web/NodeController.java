package com.tuckersoft.branchengine.web;

import com.tuckersoft.branchengine.domain.StoryNode;
import com.tuckersoft.branchengine.domain.StoryNodeRepository;
import com.tuckersoft.branchengine.web.dto.NodeRequest;
import com.tuckersoft.branchengine.web.dto.NodeResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/nodes")
public class NodeController {

    private final StoryNodeRepository nodeRepository;

    public NodeController(StoryNodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @PostMapping
    public ResponseEntity<NodeResponse> createNode(@Valid @RequestBody NodeRequest request) {
        if (nodeRepository.existsByNodeCode(request.getNodeCode())) {
            throw new ConflictException("El nodeCode ya existe");
        }

        StoryNode node = new StoryNode();
        node.setNodeCode(request.getNodeCode());
        node.setTitle(request.getTitle());
        node.setSceneText(request.getSceneText());
        node.setBranchCapacity(request.getBranchCapacity());
        node.setCurrentBranches(0);
        node.setPrimaryBranchCode(request.getPrimaryBranchCode());
        node.setGlitchBranchCode(request.getGlitchBranchCode());
        node.setCreatedAt(Instant.now());

        nodeRepository.save(node);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(node));
    }

    @GetMapping
    public ResponseEntity<List<NodeResponse>> getAllNodes() {
        List<NodeResponse> nodes = nodeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NodeResponse> getNodeById(@PathVariable Long id) {
        StoryNode node = nodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nodo no encontrado"));
        return ResponseEntity.ok(mapToResponse(node));
    }

    private NodeResponse mapToResponse(StoryNode node) {
        return new NodeResponse(
                node.getId(),
                node.getNodeCode(),
                node.getTitle(),
                node.getSceneText(),
                node.getBranchCapacity(),
                node.getCurrentBranches(),
                node.getPrimaryBranchCode(),
                node.getGlitchBranchCode(),
                node.getCreatedAt()
        );
    }
}
