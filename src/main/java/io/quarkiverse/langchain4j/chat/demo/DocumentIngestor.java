package io.quarkiverse.langchain4j.chat.demo;

import jakarta.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.pinecone.PineconeVectorDb;
import org.apache.camel.component.pinecone.PineconeVectorDbAction;
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
            .transform(new DataType("pinecone:embeddings"))   // Transform the data type to a format suitable for Pinecone using a predefined transformer
            .setHeader(PineconeVectorDb.Headers.ACTION, constant(PineconeVectorDbAction.UPSERT))  // Set the header to perform an UPSERT operation in Pinecone
                .setHeader(PineconeVectorDb.Headers.INDEX_NAME, simple("{{pinecone.index-name}}"))
                .setHeader(PineconeVectorDb.Headers.COLLECTION_SIMILARITY_METRIC, constant("cosine"))
                .setHeader(PineconeVectorDb.Headers.COLLECTION_DIMENSION, constant(1536))
                .setHeader(PineconeVectorDb.Headers.COLLECTION_CLOUD, constant("aws"))
                .setHeader(PineconeVectorDb.Headers.COLLECTION_CLOUD_REGION, simple("{{pinecone.region}}"))
            .to("pinecone:my-collection");  // Send the data to Qdrant

       from("direct:answerQuestion")
                .to("langchain4j-embeddings:test")  // Use Langchain4j to generate embeddings
                .process(e -> {
                    Object body = e.getIn().getBody();
                    System.out.println("hello world");
                })
                //.transform(new DataType("qdrant:embeddings"))
               .setHeader(PineconeVectorDb.Headers.ACTION, constant(PineconeVectorDbAction.QUERY))  // Set the header to perform an UPSERT operation in Pinecone
               .setHeader(PineconeVectorDb.Headers.INDEX_NAME, simple("{{pinecone.index-name}}"))
               .setHeader(PineconeVectorDb.Headers.COLLECTION_SIMILARITY_METRIC, constant("cosine"))
               .setHeader(PineconeVectorDb.Headers.COLLECTION_DIMENSION, constant(1536))
               .setHeader(PineconeVectorDb.Headers.COLLECTION_CLOUD, constant("aws"))
               .setHeader(PineconeVectorDb.Headers.COLLECTION_CLOUD_REGION, simple("{{pinecone.region}}"))
               .setHeader(PineconeVectorDb.Headers.QUERY_TOP_K, constant(3))

               .setBody(simple("${headers.CamelLangChain4jEmbeddingsVector}"))
               .process(e -> {
                   Object body = e.getIn().getBody();
                   System.out.println("hello world");
               })
                .to("pinecone:my-collection")
                .log("body is ${body}")

                .setBody(constant("well hello"));


    }

}
