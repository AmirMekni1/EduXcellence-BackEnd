package com.EduXcellence.EduXcellenceBackEnd.Service;

import com.EduXcellence.EduXcellenceBackEnd.Models.Administrateur;
import com.EduXcellence.EduXcellenceBackEnd.Models.Formateur;
import com.EduXcellence.EduXcellenceBackEnd.Models.Participant;
import com.EduXcellence.EduXcellenceBackEnd.Repository.ParticipantRepo;
import com.EduXcellence.EduXcellenceBackEnd.Security.AuthenticationFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class ServiceAuthentification {

    @Autowired
    private final MongoTemplate mongoTemplate;

    @Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final String KEY = "T25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNl";

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ServiceAuthentification(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(KEY).parseClaimsJws(token).getBody();
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public String getKEY() {
        return KEY;
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public boolean isAuthenticated(String token) {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        return authenticationFilter.VerifierTOKEN(token);
    }
}




