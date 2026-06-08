package com.example.CafeAutoSystem.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class KafkaStockConsumerService {
    @KafkaListener(topics = "stock-consume-events", groupId = "cafe-admin-group")
    public void consumeStockEvent(String message){
        System.out.println("==================================================");
        System.out.println("📥 [Kafka Listener] 고객 서버로부터 실시간 자재 소모 이벤트 수신!");
        System.out.println("📦 수신된 데이터 내용: " + message);
        System.out.println("==================================================");
    }
}
