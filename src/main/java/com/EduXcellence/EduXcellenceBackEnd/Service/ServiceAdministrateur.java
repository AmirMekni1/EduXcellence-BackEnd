package com.EduXcellence.EduXcellenceBackEnd.Service;


import com.EduXcellence.EduXcellenceBackEnd.Models.*;
import com.EduXcellence.EduXcellenceBackEnd.Repository.PayementRepo;
import com.EduXcellence.EduXcellenceBackEnd.Security.AuthenticationFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.limit;


@Service
public class ServiceAdministrateur {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    AuthenticationFilter authenticationFilter;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private PayementRepo payementRepo;

    private Payement payement;
    Map map = new HashMap();

    private final String KEY = "T25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNl";
    private String token;

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map<String, String>> loginAdmin(Administrateur administrateur) {
        Map<String, String> map = new HashMap<>();
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(administrateur.getId()));
        Administrateur administrateur1 = mongoTemplate.findOne(query, Administrateur.class);
        if (administrateur1 == null || !bCryptPasswordEncoder.matches(administrateur.getMotDePasse(), administrateur1.getMotDePasse())) {
            map.put("Message", "Invalid ID or password");
        } else {
            token = Jwts.builder()
                    .setSubject(administrateur.getId())
                    .claim("id", administrateur1.getId())
                    .claim("Role", administrateur1.getRole())
                    .claim("NomPrenom", administrateur1.getNomPrenom())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 864_000_000))
                    .signWith(SignatureAlgorithm.HS256, KEY)
                    .compact();
            map.put("Message", "Bonjour " + administrateur1.getNomPrenom());
            map.put("Token", token);
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }



}


