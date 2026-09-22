package com.tuckersoft.branchengine.service;

import com.tuckersoft.branchengine.domain.Decision;
import com.tuckersoft.branchengine.domain.DecisionRepository;
import com.tuckersoft.branchengine.domain.RealityLog;
import com.tuckersoft.branchengine.domain.RealityLogRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;

@Component
public class NotificationListener {

    private final JavaMailSender mailSender;
    private final RealityLogRepository realityLogRepository;
    private final DecisionRepository decisionRepository;

    public NotificationListener(JavaMailSender mailSender, RealityLogRepository realityLogRepository, DecisionRepository decisionRepository) {
        this.mailSender = mailSender;
        this.realityLogRepository = realityLogRepository;
        this.decisionRepository = decisionRepository;
    }

    @Async("branchExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDecisionSaved(DecisionEvent event) {
        Decision d = decisionRepository.findById(event.getDecision().getId()).orElseThrow();
        if ("ERROR".equals(d.getStatus())) return; 
        
        String to = d.getPlaythrough().getUser().getEmail();
        String subject = "[TUCKERSOFT] " + d.getBranchType() + " en " + d.getPlaythrough().getPlayerTag() + " | Impacto " + d.getImpactLevel();
        
        String body = "--- INFORME DE REALIDAD ---\n" +
                      "Decision ID: #" + d.getId() + "\n" +
                      "Jugador: " + d.getPlaythrough().getPlayerTag() + "\n" +
                      "Rama: " + d.getBranchType() + "\n" +
                      "Impacto: " + d.getImpactLevel() + "\n" +
                      "Departamento: " + d.getHandlerUnit() + "\n" +
                      "Lucidez: " + d.getPlaythrough().getLucidity() + "\n" +
                      "Nivel de control: " + d.getPlaythrough().getControlLevel() + "\n" +
                      "Texto original: " + d.getRawInput() + "\n";
        
        RealityLog log = new RealityLog();
        log.setDecision(d);
        log.setRecipientEmail(to);
        log.setSubject(subject);
        log.setCreatedAt(Instant.now());
        
        try {
            if ("MAIL_FAILURE".equals(event.getSimulateFailure())) {
                throw new RuntimeException("Simulated mail failure via X-Bandersnatch-Simulate");
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("qa@tuckersoft.test");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            
            log.setLogStatus("SENT");
            log.setSentAt(Instant.now());
            
            d.setStatus("ESTABILIZADA");
            decisionRepository.save(d);
        } catch (Exception ex) {
            log.setLogStatus("FAILED");
            log.setErrorMessage(ex.getMessage());
            d.setStatus("ERROR");
            decisionRepository.save(d);
        }
        realityLogRepository.save(log);
    }
}
