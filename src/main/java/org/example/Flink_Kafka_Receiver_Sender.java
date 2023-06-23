package org.example;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.eclipse.yasson.internal.JsonBindingBuilder;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class Flink_Kafka_Receiver_Sender {

    public static void main(String[] args) throws Exception {

        String server = "localhost:9092";
        String inputTopic = "Transakcje";
        String outputTopic = "Anomalie";


        StramStringOperation(server,inputTopic,outputTopic );

    }

    public static class StringMapper implements MapFunction<String, String> {
        @Override
        public String map(String data) throws Exception {
            ObjectMapper obj=new ObjectMapper();
            JsonNode json=obj.readTree(data);
            Card c=Card.get_Card(json.get("card_id").asInt());
            double spended=c.spended;
            return "Przekroczono limit karty "+ data+ " wydane do tej pory: "+ spended;
        }
    }
    public static class StringMapper1 implements MapFunction<String, String> {
        @Override
        public String map(String data) throws Exception {
            return data;
        }
    }

    public static class StringMapper2 implements MapFunction<String, String> {
        @Override
        public String map(String data) throws Exception {
            return "Duza odleglosc od ostatniej transakcji "+ data;
        }
    }



    public static void StramStringOperation(String server,String inputTopic, String outputTopic ) throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        FlinkKafkaConsumer011<String> flinkKafkaConsumer_transakcje= createStringConsumerForTopic(inputTopic, server);
        FlinkKafkaProducer011<String> flinkKafkaProducer_transakcje=createStringProducer(inputTopic,server);
        FlinkKafkaProducer011<String> flinkKafkaProducer_anomalie = createStringProducer(outputTopic, server);

        DataStream<String> stringInputStream1 = environment.addSource(new DataGenerator());
        stringInputStream1.map(new StringMapper1()).addSink(flinkKafkaProducer_transakcje);

        DataStream<String> stringInputStream = environment.addSource(flinkKafkaConsumer_transakcje);

        DataStream<String> filteredtemp=stringInputStream.filter(new FilterFunction<String>() {
            //filtr sprawdzajaca czy wykonanie transakcji nie przekroczy limitu na karcie patrzac na limit oraz ilosc wydanych pieniedzy do tej pory na konkretnej karcie
            @Override
            public boolean filter(String s) throws Exception {
                ObjectMapper obj=new ObjectMapper();
                JsonNode json=obj.readTree(s);
                Card c=Card.get_Card(json.get("card_id").asInt());

                if(c==null){
                    return false;
                }
                if(c.spended+json.get("value").asInt() >= c.limit) {
                    return true;
                }else{
                    c.AddSpended(json.get("value").asInt());
                    return false;
                }
            }


        });

        DataStream<String> filteredtemp1=stringInputStream.filter(new FilterFunction<String>() {
            //filtr sprawdzajacy czy karta drastycznie nie zmienila  lokalizacji
            @Override
            public boolean filter(String s) throws Exception {
                ObjectMapper obj=new ObjectMapper();
                JsonNode json=obj.readTree(s);
                Card c=Card.get_Card(json.get("card_id").asInt());

                if(c==null){
                    return false;
                }

                if(c.transakcje.size()>=2) {
                    Transakcje t1 = c.transakcje.get(c.transakcje.size() - 1);
                    Transakcje t2 = c.transakcje.get(c.transakcje.size() - 2);
                    double x1=t1.cords[0];
                    double z1=t1.cords[1];
                    double x2=t2.cords[0];
                    double z2= t2.cords[1];
                    double odleglosc=Math.sqrt(Math.pow(x2-x1,2)+Math.pow(z2-z1,2));
                    if(odleglosc>10){
                        return true;
                    }
                    else{
                        return false;
                    }
                }else{
                    return false;
                }

            }


        });




        filteredtemp.map(new StringMapper()).addSink(flinkKafkaProducer_anomalie);
        filteredtemp1.map(new StringMapper2()).addSink(flinkKafkaProducer_anomalie);
        environment.execute("Transakcje");
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

    public static FlinkKafkaProducer011<String> createStringProducer(
            String topic, String kafkaAddress){

        return new FlinkKafkaProducer011<>(kafkaAddress,
                topic, new SimpleStringSchema());
    }



    public static class DataGenerator implements SourceFunction<String> {
        private volatile boolean running=true;

        @Override
        public void run(SourceContext<String> sourceContext) throws Exception {

            List<Transakcje> temp=new ArrayList<Transakcje>();

            while( running){

                Transakcje tr=new Transakcje();
                sourceContext.collect(tr.toString());
                Thread.sleep(300);

            }
        }

        @Override
        public void cancel() {
            running=false;
        }
    }



}