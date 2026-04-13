package com.tradesphere.order.scheduler;

import com.tradesphere.order.outbox.EventStatus;
import com.tradesphere.order.outbox.OutboxEvent;
import com.tradesphere.order.outbox.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class OutboxRelayScheduler {
    final OutboxEventRepository outboxEventRepository;
    final KafkaTemplate<String, String> kafkaTemplate;

    private static final long MIN_DELAY = 5000;
    private static final long MAX_DELAY = 120000;
    private static final int MULTIPLIER = 2;


//    @Scheduled(fixedDelayString = "${outbox.relay-interval-ms}")
//    public void relay() {
//        List<OutboxEvent> pendingEvents = outboxEventRepository.findByEventStatus(EventStatus.PENDING);
//        for(OutboxEvent outboxEvent : pendingEvents) {
//            kafkaTemplate.send(outboxEvent.getTopic(), outboxEvent.getPayload());
//            outboxEvent.setEventStatus(EventStatus.PUBLISHED);
//            outboxEventRepository.save(outboxEvent);
//        }
//    }
//Replacing Fixed delay with backoff strategy

    @Async
    @EventListener(classes = ApplicationReadyEvent.class)
    public void relayWithBackoff() throws InterruptedException {
        var delay = MIN_DELAY;
        while (true) {
            List<OutboxEvent> pendingEvents = outboxEventRepository.findByEventStatus(EventStatus.PENDING);
            if (pendingEvents.isEmpty()) {
                delay = Math.min(delay * MULTIPLIER, MAX_DELAY);
                Thread.sleep(delay);
                continue;
            } else {
                for (OutboxEvent outboxEvent : pendingEvents) {
                    kafkaTemplate.send(outboxEvent.getTopic(), outboxEvent.getPayload());
                    outboxEvent.setEventStatus(EventStatus.PUBLISHED);
                    outboxEventRepository.save(outboxEvent);
                }
                delay = MIN_DELAY;
                Thread.sleep(delay);
            }
        }

    }
}
