package com.EduXcellence.EduXcellenceBackEnd.Security;

import com.EduXcellence.EduXcellenceBackEnd.Models.Formation;
import com.EduXcellence.EduXcellenceBackEnd.Service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Value;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.Provider;

import java.security.Provider;

@Configuration
public class AppConfig {
    private MongoTemplate mongoTemplate;

    @Bean
    public ServiceAdministrateur serviceAdministrateur() {
        return new ServiceAdministrateur();
    }

    @Bean
    public ServiceAuthentification serviceAuthentification() {
        return new ServiceAuthentification(mongoTemplate);
    }

    @Bean
    public ServiceParticipant serviceParticipant() {
        return new ServiceParticipant();
    }

    @Bean
    public ServiceFormateur serviceFormateur() {
        return new ServiceFormateur();
    }

    @Bean
    public ServiceFormation serviceFormation() {
        return new ServiceFormation();
    }

    @Bean
    public ServicePayement ServicePayement() {
        return new ServicePayement();
    }

    @Bean
    public ServiceEvaluation ServiceEvaluation() {
        return new ServiceEvaluation();
    }

    @Bean
    public ServiceAttestation ServiceAttestation() {
        return new ServiceAttestation();
    }

    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
