package io.quarkiverse.langchain4j.chat.demo;


import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;


import java.util.function.Supplier;

@ApplicationScoped
public class DocumentRetriever implements Supplier<RetrievalAugmentor> {

    private final RetrievalAugmentor augmentor;

    DocumentRetriever(QdrantEmbeddingStore store, EmbeddingModel model) {
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
            .embeddingModel(model)
            .embeddingStore(store)
            .maxResults(3)
            .build();

        augmentor = DefaultRetrievalAugmentor
            .builder()
            .contentRetriever(contentRetriever)
            .build();
    }

    @Override
    public RetrievalAugmentor get() {
        return augmentor;
    }

}
