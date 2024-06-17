package com.EduXcellence.EduXcellenceBackEnd.Service;

import com.EduXcellence.EduXcellenceBackEnd.Models.*;
import com.EduXcellence.EduXcellenceBackEnd.Repository.AttestationRepo;
import com.EduXcellence.EduXcellenceBackEnd.Repository.ParticipantRepo;
import com.EduXcellence.EduXcellenceBackEnd.Repository.PayementRepo;
import com.EduXcellence.EduXcellenceBackEnd.Security.AuthenticationFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class ServiceParticipant {
    @Autowired
    private ParticipantRepo participantRepo;
    @Autowired
    private PayementRepo payementRepo;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    AuthenticationFilter authenticationFilter;
    private boolean p;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    AttestationRepo attestationRepo;
    private final String KEY = "T25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNl";

    private String token;


    Map map = new HashMap();

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> loginParticipant(Participant participant) {
        Query query = new Query();
        query.addCriteria(where("email").regex(participant.getEmail(), "i"));
        Participant user = mongoTemplate.findOne(query, Participant.class);
        if (user == null || !(participant.getMotDePasse().equals(authenticationFilter.decrypt(user.getMotDePasse())))) {
            map.put("Message", "Invalid email or password");
            map.put("verif", "false");
        } else {
            token = Jwts.builder()
                    .setSubject(participant.getEmail())
                    .claim("id", user.getId())
                    .claim("NomPrenom", user.getNomPrenom())
                    .claim("NiveauDEtude", user.getNiveauDEtude())
                    .claim("Role", user.getRole())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 864_000_000))
                    .signWith(SignatureAlgorithm.HS256, KEY)
                    .compact();
            map.put("Message", "Bonjour " + user.getNomPrenom());
            map.put("verif", "true");
            map.put("Token", token);
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> inscriptionParticipant(Participant participant) {
        Query query = new Query();
        Query existEmail = query(where("email").regex(participant.getEmail(), "i"));
        Long part = mongoTemplate.count(existEmail, Participant.class);
        if (part == 0) {
            String encryptedPassword = authenticationFilter.encrypt(participant.getMotDePasse());
            participant.setMotDePasse(encryptedPassword);
            this.participantRepo.save(participant);
            map.put("Message", "Ajouté avec Succès");
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            map.put("Message", "Email existe Déjà");
            return new ResponseEntity<>(map, HttpStatus.OK);
        }
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> listerParticipants(String token) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            List<Participant> participant = this.participantRepo.findAll();
            List<Participant>Listeparticipants = new ArrayList<>();
            for (Participant p : participant){
                Query query = new Query(Criteria.where("ParticipantID").is(p.getId()));
                Long payement = mongoTemplate.count(query, Payement.class);
                if (payement>0){
                    Listeparticipants.add(p);
                }
            }
            map.put("TableParticipant",Listeparticipants);
        } else {
            map.put("Message", "Accès réfusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/



    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> NombreParticipantsVerifies(String token) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            Query query = new Query();
            Long NbrParticipantVerifier = mongoTemplate.count(query.addCriteria(where("verification").is(true)), Participant.class);
            map.put("ParticipantVerifier", NbrParticipantVerifier);
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> NombreParticipantsNonVerifies(String token) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            Query query = new Query();
            Long NbrParticipantNonVerifier = mongoTemplate.count(query.addCriteria(where("verification").is(false)), Participant.class);
            map.put("ParticipantNonVerifier", NbrParticipantNonVerifier);
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> NombreDeParticipants(String token) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            map.put("NombreDeParticipants", this.participantRepo.count());
        } else {
            map.put("Message", "Accés refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> insererBonDeCommande(String token, MultipartFile bonDeCommande, String ParticipantID, String FormationID) throws IOException {
        Query query = new Query();
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("USER")) {
            Attestation attestation = new Attestation();
            attestation.setParticipantID(ParticipantID);
            attestation.setFormationID(FormationID);
            attestation.setDate(new Date());
            attestationRepo.save(attestation);
            Payement payement = mongoTemplate.findOne(query(where("ParticipantID").is(ParticipantID).and("FormationID").is(FormationID)), Payement.class);
            System.out.println(payement);
            System.out.println(ParticipantID);
            System.out.println(FormationID);
            if (payement.isVerifierInscription() == true) {
                payement.setBonDeCommande(saveFile(bonDeCommande));
                payementRepo.save(payement);
                map.put("Message", "Votre bon de commande a été inséré");
            } else {
                map.put("Message", "Votre inscription n'est pas encore validé");
            }
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public String saveFile(MultipartFile file) throws IOException {
        String fileName = String.valueOf(new Random().nextInt(1000000)) + "_" + file.getOriginalFilename();
        Path fileStorageLocation = Paths.get("src/main/resources/BonDeCommand").toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            Files.createDirectories(fileStorageLocation);
        }
        Files.copy(file.getInputStream(), fileStorageLocation.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<UrlResource> recupererBonDeCommande(String token, String ParticipantID, String FormationID) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("USER")) {
            Payement payement = mongoTemplate.findOne(query(where("ParticipantID").is(ParticipantID).and("FormationID").is(FormationID)), Payement.class);

            if (payement != null && payement.getBonDeCommande() != null) {
                Path filePath = Paths.get("uploads").toAbsolutePath().resolve(payement.getBonDeCommande()).normalize();
                try {
                    UrlResource resource = new UrlResource(filePath.toUri());
                    if (resource.exists() && resource.isReadable()) {
                        HttpHeaders headers = new HttpHeaders();
                        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
                        return ResponseEntity.ok()
                                .headers(headers)
                                .body(resource);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    }
                } catch (MalformedURLException e) {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> afficherParticipantparid(String id, String token) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            Participant participant = mongoTemplate.findOne(query(where("id").is(id)), Participant.class);
            if (participant != null) {
                String pass = participant.getMotDePasse();
                participant.setMotDePasse(authenticationFilter.decrypt(pass));
                map.put("Participant", participant);
            } else {
                map.put("Message", "Participant not found");
            }
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> InscriptionAuFormation(Payement payement, String Token) throws IOException {
        if (authenticationFilter.VerifierTOKEN(Token) && authenticationFilter.RecupererRole(Token).equals("USER")) {

            Query existe = new Query(where("FormationID").is(payement.getFormationID()).and("ParticipantID").is(payement.getParticipantID()));
            Long NBRparticipant = mongoTemplate.count(existe, Payement.class);
            if (NBRparticipant < 1) {
                Query query = new Query(where("_id").is(payement.getParticipantID()));
                payementRepo.save(payement);
                Participant participant = mongoTemplate.findOne(query, Participant.class);
                participant.getFormationID().add(payement.getFormationID());
                participantRepo.save(participant);
                map.put("Message", "Votre inscription est enregistrée avec succès, Veuillez attendre la vérification de l'administrateur");
            } else {
                map.put("Message", "Vous êtes déjà inscrit à cette formation");
            }
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/


    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/



    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> listerLesFormationAffecterAParticipant(String token, String idParticipant) {
        if (authenticationFilter.VerifierTOKEN(token) && (authenticationFilter.RecupererRole(token).equals("ADMIN") || authenticationFilter.RecupererRole(token).equals("USER"))) {
            Query query = new Query();
            query.addCriteria(where("ParticipantID").is(idParticipant).and("verifierInscription").is(true).and("bonDeCommande").ne(""));
            List<Payement> list = mongoTemplate.find(query, Payement.class);
            List list1 = new ArrayList();
            for (Payement pay : list){
                Map map1 = new HashMap();
                Formation formation = mongoTemplate.findOne(query(where("_id").is(pay.getFormationID())), Formation.class);
                map1.put("idFormation", formation.getIdformation());
                map1.put("themeFormation", formation.getThemeFormation());
                map1.put("idFormateur", pay.getFormateurID());
                list1.add(map1);
            }
            map.put("TableFormationAffecterAParticipant", list1);
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    // public ResponseEntity<Map> RecupererInformationFinalisationdInscription(String token, String idParticipant) {

    // }
}

