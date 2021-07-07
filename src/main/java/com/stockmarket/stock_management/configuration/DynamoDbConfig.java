package com.stockmarket.stock_management.configuration;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DynamoDbConfig {
    @Value("${amazon.dynamodb.endpoint}")
    private String awsDynamoDBEndpoint;

    @Value("${amazon.aws.accessKey}")
    private String awsAccessKey;

    @Value("${amazon.aws.secretKey}")
    private String awsSecretKey;

    @Value("${amazon.aws.signingRegion}")
    private String awsSigningRegion;

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(buildAmazonDynamoDB());
    }

//    @Bean
    public AmazonDynamoDB buildAmazonDynamoDB() {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                awsDynamoDBEndpoint,
                                awsSigningRegion
                        )
                )
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(
                                        awsAccessKey,
                                        awsSecretKey
                                )
                        )
                )
                .build();
    }

}

