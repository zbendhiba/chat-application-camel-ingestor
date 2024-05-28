# Bank Buddy Chatbot Application

Welcome to the Bank Buddy Chatbot application, an innovative example of live data ingestion into a vector database using Apache Camel, powered by the Quarkus Langchain4j framework. This application demonstrates the use of Retrieval-Augmented Generation (RAG) to enhance chatbot responses with data ingested on the fly from AWS S3.

For a detailed explanation of the application's architecture and functionality, please refer to our blog post: [Feeding LLMs Efficiently: Data Ingestion to Vector Databases with Apache Camel](#your-blog-post-link).

## Prerequisites

To run this application, you will need:
- Docker or Podman installed on your machine, as the application uses Quarkus Dev Services to automatically start a Qdrant Docker image.
- Maven installed to compile and run the Quarkus application.

## Environment Setup

Before starting the application, you need to set up your AWS environment variables. Open your terminal and export the following variables with your actual AWS credentials:

```bash
export AWS_ACCESS_KEY="your-access-key-here"
export AWS_SECRET_KEY="your-secret-key-here"
export AWS_REGION="your-region-here"  # Example: us-east-1
export AWS_BUCKET_NAME="your-bucket-name-here"
```

## Getting Started

1. **Start the Application in Developer Mode:**
    - Open your terminal.
    - Navigate to the project directory.
    - Run the following command:
      ```
      mvn compile quarkus:dev
      ```

2. **Accessing the Chatbot:**
    - Once the application is running, access the chatbot interface by visiting:
      ```
      http://localhost:8080
      ```

3. **Interacting with the Chatbot:**
    - Ask the chatbot the following question:
      ```
      What is the monthly maintenance fee for the Elite Money Market Account?
      ```
    - Initially, notice that OpenAI cannot provide an answer to this question due to the lack of data.

4. **Ingest Data:**
    - Go to the S3 bucket and upload documents from the specified folder inside the application (you will find a link to this folder in the app interface).
    - Ensure you have set the name of your S3 bucket in the application configuration.

5. **Re-ask the Question:**
    - After the data is ingested, refresh the chatbot page.
    - Ask the same question again.
    - Notice the improved response from the chatbot, now capable of providing the requested information.

## Acknowledgments

This application is based on examples from the [Quarkus Langchain4j samples](https://github.com/quarkiverse/quarkus-langchain4j/) available on GitHub. I gratefully acknowledge the Quarkus community for providing these resources, which have been instrumental in demonstrating the capabilities of modern data ingestion techniques in AI applications.

## Conclusion

This setup showcases how dynamic data ingestion can be utilized to enhance LLM responses in real-time, making this application a powerful tool for demonstrating modern AI capabilities. Enjoy interacting with your enhanced chatbot!
