package io.quarkiverse.langchain4j.chat.demo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.qdrant.Qdrant;
import org.apache.camel.component.qdrant.QdrantAction;
import org.apache.camel.spi.DataType;

@ApplicationScoped
public class DocumentIngestor extends RouteBuilder {


    @Override
    public void configure() throws Exception {
        from("aws2-s3://zbendhib-bucket")
            .log("Incoming message from S3 bucket ${body}")
            .convertBodyTo(String.class)  // Convert the file content to a String
            .to("langchain4j-embeddings:test")  // Use Langchain4j to generate embeddings
            .transform(new DataType("qdrant:embeddings"))   // Transform the data type to a format suitable for Qdrant using a predefined transformer
            .setHeader(Qdrant.Headers.ACTION, constant(QdrantAction.UPSERT))  // Set the header to perform an UPSERT operation in Qdrant
            .to("qdrant:my-collection");  // Send the data to Qdrant


    }

}
