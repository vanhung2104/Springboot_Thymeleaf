package vn.iostar.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("storage")
public class StorageProperties {
    private String location;
}