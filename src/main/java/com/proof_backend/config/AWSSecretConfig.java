package com.proof_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import java.util.Map;


@Configuration
@Slf4j
public class AWSSecretConfig implements EnvironmentPostProcessor {

    private static boolean IS_INITIALIZED = false;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            if (!IS_INITIALIZED) {
                ObjectMapper objectMapper = new ObjectMapper();
                String secretsEnv = environment.getProperty("aws.secret_name");
                if (secretsEnv == null) return;

                Region region = Region.of(environment.getProperty("aws.region"));
                if (region == null) {
                    throw new IllegalAccessException("Missing AwsSecretsManager Region");
                }

                String[] secrets = toSecretList(secretsEnv);
                SecretsManagerClient client = SecretsManagerClient.builder().region(region).build();

                if (secrets != null) {
                    for (String secret : secrets) {
                        log.debug("processing secret: " + secret);
                        GetSecretValueRequest req = GetSecretValueRequest.builder().secretId(secret).build();
                        String secretString = client.getSecretValue(req).secretString();
                        try {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> map = objectMapper.readValue(secretString, Map.class);
                            environment.getPropertySources().addFirst(new MapPropertySource("secrets-" + secret, map));
                        } catch (Exception e) {
                            log.error("Error processing secret: " + secret, e);
                        }
                    }
                }

                client.close();
                IS_INITIALIZED = false;
            }
        } catch (Exception ex) {
            log.error("Error while configure aws secret manager");
        }
    }

    private String[] toSecretList(String secrets) {
        if (secrets == null) {
            return null;
        }
        return secrets.replaceAll("\\s", "").split(",");
    }
}

