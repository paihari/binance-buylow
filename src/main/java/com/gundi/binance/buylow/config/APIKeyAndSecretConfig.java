package com.gundi.binance.buylow.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.Base64;

@Configuration
public class APIKeyAndSecretConfig {

    @Bean(name = "apiKeysAndSecret")
    @Profile(value = "dev")
    APIKeyAndSecret dev() {
        return  getSecretForLocal();
    }

    @Bean(name = "apiKeysAndSecret")
    @Profile(value = "prod")
    APIKeyAndSecret prod() {
        return getSecretFromAWSSecretManager();
    }


    private APIKeyAndSecret getSecretForLocal() {

        return new APIKeyAndSecret("",
                "");
    }

    private APIKeyAndSecret getSecretFromAWSSecretManager() {

        String secretName = "buy-low-secret";
        String region = "us-east-1";

        // Create a Secrets Manager client
        AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard()
                .withRegion(region)
                .build();

        // In this sample we only handle the specific exceptions for the 'GetSecretValue' API.
        // See https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
        // We rethrow the exception by default.

        String secret = "";
        String decodedBinarySecret = "";
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = null;

        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (DecryptionFailureException e) {
            // Secrets Manager can't decrypt the protected secret text using the provided KMS key.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InternalServiceErrorException e) {
            // An error occurred on the server side.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InvalidParameterException e) {
            // You provided an invalid value for a parameter.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InvalidRequestException e) {
            // You provided a parameter value that is not valid for the current state of the resource.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (ResourceNotFoundException e) {
            // We can't find the resource that you asked for.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        }

        // Decrypts secret using the associated KMS CMK.
        // Depending on whether the secret is a string or binary, one of these fields will be populated.
        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();
        }
        else {
            decodedBinarySecret = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
        }

        ObjectMapper mapper = new ObjectMapper();
        APIKeyAndSecret apiKeyAndSecret = null;
        try {
            apiKeyAndSecret = mapper.readValue(secret, APIKeyAndSecret.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiKeyAndSecret;

    }

}
