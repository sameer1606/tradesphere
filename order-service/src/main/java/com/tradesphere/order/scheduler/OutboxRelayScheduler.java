package com.tradesphere.order.scheduler;

import com.tradesphere.order.outbox.EventStatus;
import com.tradesphere.order.outbox.OutboxEvent;
import com.tradesphere.order.outbox.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class OutboxRelayScheduler {
    final OutboxEventRepository outboxEventRepository;
    final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${outbox.relay-interval-ms}")
    public void relay() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByEventStatus(EventStatus.PENDING);
        for(OutboxEvent outboxEvent : pendingEvents) {
            kafkaTemplate.send(outboxEvent.getTopic(), outboxEvent.getPayload());
            outboxEvent.setEventStatus(EventStatus.PUBLISHED);
            outboxEventRepository.save(outboxEvent);
        }
    }

}
