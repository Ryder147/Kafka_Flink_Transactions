package org.example;


import java.util.Properties;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

public class Anomaly_Receiver {
        public static void main(String[] args) throws Exception {

    String inputTopic = "Anomalie";
    String server = "localhost:9092";

    StramConsumrer(inputTopic, server);
}

        public static void StramConsumrer(String inputTopic, String server) throws Exception {
            StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
            FlinkKafkaConsumer011<String> flinkKafkaConsumer = createStringConsumerForTopic(inputTopic, server);
            DataStream<String> stringInputStream = environment.addSource(flinkKafkaConsumer);


            stringInputStream.map(new MapFunction<String, String>() {


                @Override
                public String map(String value) throws Exception {
                    return "Otrzymane Anomalie : " + value;
                }
            }).print();

            environment.execute();
        }

        public static FlinkKafkaConsumer011<String> createStringConsumerForTopic(
                String topic, String kafkaAddress) {

            Properties props = new Properties();
            props.setProperty("bootstrap.servers", kafkaAddress);
            //props.setProperty("group.id",kafkaGroup);
            FlinkKafkaConsumer011<String> consumer = new FlinkKafkaConsumer011<>(
                    topic, new SimpleStringSchema(), props);

            return consumer;
        }

}