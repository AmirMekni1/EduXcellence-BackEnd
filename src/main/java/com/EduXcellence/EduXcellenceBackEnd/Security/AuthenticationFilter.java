package com.EduXcellence.EduXcellenceBackEnd.Security;

import com.EduXcellence.EduXcellenceBackEnd.Models.Administrateur;
import com.EduXcellence.EduXcellenceBackEnd.Models.Formateur;
import com.EduXcellence.EduXcellenceBackEnd.Models.Participant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class AuthenticationFilter {
    @Autowired
    private MongoTemplate mongoTemplate;

    private AES256TextEncryptor textEncryptor;

    @Value("${jasypt.encryptor.password}")
    private String password;


    public boolean VerifierTOKEN(String Token) {
        try {
            Jwts.parser().setSigningKey("T25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNl").parseClaimsJws(Token).getBody();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String RecupererRole(String Token) {
        Claims claims = Jwts.parser().setSigningKey("T25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNl").parseClaimsJws(Token).getBody();
        return claims.get("Role", String.class);
    }
    
    public ResponseEntity<Map> RecupererId(String Token) {
        Claims claims = Jwts.parser().setSigningKey("T25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNl").parseClaimsJws(Token).getBody();
        Map map = new HashMap();
        String id = claims.get("id", String.class);
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        Formateur existFormateur = mongoTemplate.findOne(query, Formateur.class);
        Participant existParticipant = mongoTemplate.findOne(query, Participant.class);
        Administrateur existAdmin = mongoTemplate.findOne(query, Administrateur.class);
        if (existFormateur!= null) {
          map.put("id",id);
            map.put("email", existFormateur.getEmail());
            map.put("user","Formateur");
        } else if ( existParticipant!= null ) {
            map.put("id",id);
            map.put("email", existParticipant.getEmail());
            map.put("user","Participant");
        } else if (existAdmin != null) {
            map.put("id", id);
            map.put("user", "Administrateur");
        }


        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    public String encrypt(String text) {
        this.textEncryptor = new AES256TextEncryptor();
        this.textEncryptor.setPassword(password);
        return textEncryptor.encrypt(text);
    }


    public String decrypt(String encryptedText) {
        this.textEncryptor = new AES256TextEncryptor();
        this.textEncryptor.setPassword(password);
        return textEncryptor.decrypt(encryptedText);
    }
}