package sap.kafka;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

public class SimpleProducer {
    public static void main(String[] args) throws Exception {
        final String topicName = "my-event-channel";
        // create instance for properties to access producer configs
        Properties props = new Properties();
        // Assign localhost id
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        // Set acknowledgements for producer requests.
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        // If the request fails, the producer can automatically retry
        // props.put(ProducerConfig.RETRIES_CONFIG, 0);
        // Specify buffer size in config
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16_384);
        // Reduce the no of requests less than 0
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        // Controls the total amount of memory available to the producer for buffering.
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33_554_432);
        // Enable idempotence: ensure records are not processed in duplicate or out of order
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // try-with-resources to close the producer gracefully when done or unrecoverable error occurs
        // The producer mantains TCP connections to the brokers and also operates a background I/O thread
        // to ferry the records across.
        try (var producer = new KafkaProducer<>(props)) {
            while (true) {
                final String key = "key";
                final String value = new Date().toString();
                System.out.format("Publishing value: %s%n", value);
                // Topics are by default automatically created when a producer or consumer first writes or reads from them
                final Future<RecordMetadata> result = producer.send(
                    new ProducerRecord<>(topicName, key, value),
                    (metadata, exception) -> System.out.println("Published with metadata: " + metadata + ", error: " + exception)
                );
                System.out.println(result.get());
                Thread.sleep(1000);
            }
        }
    }
}

