package sap.kafka;

import java.util.Properties;
import java.time.Duration;
import java.util.Arrays;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringDeserializer;

import static java.lang.System.out;

public class SimpleConsumer {

    public static void main(String[] args) throws Exception {
        final String topicName = "my-event-channel";
        // Kafka consumer configuration settings
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        try (var consumer = new KafkaConsumer<String, String>(props)) {
            consumer.subscribe(Set.of(topicName)); // Subscribe to the topic
            while (true) {
                // final ConsumerRecords<String, String> records = ...;
                consumer.poll(Duration.ofMillis(Long.MAX_VALUE)).forEach(it ->
                    out.println("offset = " + it.offset() + " key = " + it.key() + " value = " + it.value())
                );
                consumer.commitSync(); // Commit the offset of the record
                Thread.sleep(2000);
            }
        }
    }
}