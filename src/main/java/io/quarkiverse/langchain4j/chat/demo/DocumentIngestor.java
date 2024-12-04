package io.quarkiverse.langchain4j.chat.demo;

import static io.qdrant.client.grpc.Points.PointsUpdateOperation.OperationCase.UPSERT;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.qdrant.Qdrant;
import static org.apache.camel.component.qdrant.Qdrant.Headers.ACTION;
import org.apache.camel.component.qdrant.QdrantAction;
import org.apache.camel.spi.DataType;

import static org.apache.camel.model.tokenizer.LangChain4jTokenizerDefinition.TokenizerType.OPEN_AI;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DocumentIngestor extends EndpointRouteBuilder {
   private static final DataType DATA_TYPE_QDRANT_EMBEDDINGS = new DataType("qdrant:embeddings");

    @ConfigProperty(name = "aws-s3.bucket-name")
    String bucketName;


    @Override
    public void configure() throws Exception {
        from(aws2S3(bucketName))
                .log("Incoming file")
                .convertBodyTo(String.class)  // Convert the file content to a String
                // split the documents
                .tokenize(tokenizer()
                        .byParagraph()
                        .maxTokens(1024)
                        .maxOverlap(10)
                        .using(OPEN_AI)
                        .end()
                )
                .split().body()

                .to(langchain4jEmbeddings("test"))
                .transform(DATA_TYPE_QDRANT_EMBEDDINGS)
                .setHeader(ACTION, constant(UPSERT))  // Set the header to perform an UPSERT operation in Qdrant
                .to(qdrant("my-collection"));  // Send the data to Qdrant
    }

}
