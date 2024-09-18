package io.quarkiverse.langchain4j.chat.demo;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.qdrant.Qdrant;
import org.apache.camel.component.qdrant.QdrantAction;
import org.apache.camel.model.tokenizer.LangChain4jTokenizerDefinition;
import org.apache.camel.spi.DataType;

@ApplicationScoped
public class DocumentIngestor extends RouteBuilder {


    @Override
    public void configure() throws Exception {
        from("aws2-s3:{{s3.bucket.name}}")
            .log("Incoming message from S3 bucket ${body}")
            .convertBodyTo(String.class)  // Convert the file content to a String
            .tokenize(tokenizer()
                        .byParagraph()
                        .maxTokens(1024)
                        .maxOverlap(10)
                        .using(LangChain4jTokenizerDefinition.TokenizerType.OPEN_AI)
                        .end())
                .split().body()
                .to("direct:embed");

        from("direct:embed")
                .log("body :: ${body}")
            .to("langchain4j-embeddings:test")  // Use Langchain4j to generate embeddings
            .transform(new DataType("qdrant:embeddings"))   // Transform the data type to a format suitable for Qdrant using a predefined transformer
            .setHeader(Qdrant.Headers.ACTION, constant(QdrantAction.UPSERT))  // Set the header to perform an UPSERT operation in Qdrant
            .to("qdrant:my-collection");  // Send the data to Qdrant


    }

}
