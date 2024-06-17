package com.EduXcellence.EduXcellenceBackEnd.Service;

import com.EduXcellence.EduXcellenceBackEnd.Models.Formateur;
import com.EduXcellence.EduXcellenceBackEnd.Models.Formation;
import com.EduXcellence.EduXcellenceBackEnd.Repository.FormateurRepo;
import com.EduXcellence.EduXcellenceBackEnd.Security.AuthenticationFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class ServiceFormateur {

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private FormateurRepo formateurRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    AuthenticationFilter authenticationFilter;

    private final String KEY = "T25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNlT25lX1BpZWNl";

    private String token;

    Map map = new HashMap();

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> loginFormateur(Formateur formateur) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").regex(formateur.getEmail(), "i"));
        Formateur formateur1 = mongoTemplate.findOne(query, Formateur.class);
        System.out.println(formateur.getMotDePasse());
        System.out.println(authenticationFilter.decrypt(formateur1.getMotDePasse()));
        if (formateur1 == null || !(formateur.getMotDePasse().equals(authenticationFilter.decrypt(formateur1.getMotDePasse())))) {
            map.put("Message", "Invalid email or password");
        } else {
            token = Jwts.builder()
                    .setSubject(formateur.getId())
                    .claim("id", formateur1.getId())
                    .claim("Role", formateur1.getRole())
                    .claim("NomPrenom", formateur1.getNomPrenom())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 864_000_000))
                    .signWith(SignatureAlgorithm.HS256, KEY)
                    .compact();
            map.put("Message", "Bonjour " + formateur1.getNomPrenom());
            map.put("Token", token);
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    /*-------------------------------Gestion des formateurs---------------------------------*/

    public ResponseEntity<Map> AjouterFormateur(Formateur F, String token) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            Query query = new Query();
            Query existEmail = query(where("email").is(F.getEmail()));
            Long part = mongoTemplate.count(existEmail, Formateur.class);
            if (part == 0) {
                String motdepasseCrypter = authenticationFilter.encrypt(F.getMotDePasse());
                F.setMotDePasse(motdepasseCrypter);
                formateurRepo.save(F);
                map.put("Message", "Nouveau Formateur Ajouté Avec Succès");
            } else {
                map.put("Message", "Cet Email Exite Déjà");
            }
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> listerFormateurs(String token) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            map.put("TableFormateur", this.formateurRepo.findAll());
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public void Réassignation(String token, String id) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            Formateur formateur = mongoTemplate.findOne(query(where("id").is(id)), Formateur.class);
            if (formateur.isDisponiblite()) {
                formateur.setDisponiblite(false);
                formateurRepo.save(formateur);
            } else {
                formateur.setDisponiblite(true);
                formateurRepo.save(formateur);
            }
        }
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> NbrFormateursActivesOuDesactives(String token) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            Query query = new Query();
            Long NbrFormateurActive = mongoTemplate.count(query.addCriteria(where("active").is("true")), Formateur.class);
            Long NbrFormateurDesactive = mongoTemplate.count(query.addCriteria(where("active").is("false")), Formateur.class);
            map.put("FormateurActive", NbrFormateurActive);
            map.put("FormateurDesactive", NbrFormateurDesactive);
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> NombreDeFormateurs(String token) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            map.put("NombreDeFormateurs", this.formateurRepo.count());
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> listerUnSeulFormateur(String id, String token) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            Formateur formateur = mongoTemplate.findOne(query(where("id").is(id)), Formateur.class);
            if (formateur != null) {
                String pass = formateur.getMotDePasse();
                formateur.setMotDePasse(authenticationFilter.decrypt(pass));
                map.put("Formateur", formateur);
            } else {
                map.put("Message", "Formateur non trouvé");
            }
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> modifierFormateur(String id, String email, String nomPrenom, String motDePasse, int numTelephone, String specialite, String token) {
        if (authenticationFilter.VerifierTOKEN(token) && "ADMIN".equals(authenticationFilter.RecupererRole(token))) {
            Query query = new Query(Criteria.where("id").is(id));
            Formateur formateur = mongoTemplate.findOne(query, Formateur.class);
            if (formateur != null) {
                String passEncode = authenticationFilter.encrypt(motDePasse);
                Update update = new Update()
                        .set("email", email)
                        .set("nomPrenom", nomPrenom)
                        .set("motDePasse", passEncode)
                        .set("numTelephone", numTelephone)
                        .set("specialite", specialite);
                mongoTemplate.updateFirst(query, update, Formateur.class);
                map.put("Message", "Compte mis à jour avec succès");
            } else {
                map.put("Message", "Formateur non trouvé");
            }
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> ActiverCompteFormateur(String id, String token) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            Query query = new Query(where("_id").is(id));
            Update update = new Update().set("active", true);
            mongoTemplate.updateFirst(query, update, Formateur.class);
            map.put("Message", "Le compte a été activé");
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> DesactiverCompteFormateur(String id, String token) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            Query query = new Query(where("_id").is(id));
            Update update = new Update().set("active", false);
            mongoTemplate.updateFirst(query, update, Formateur.class);
            map.put("Message", "Le compte a été désactivé");
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> formateursDisponibles(String token) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            Query query = new Query();
            query.addCriteria(Criteria.where("Disponiblite").is(true));
            List<Formateur> list = mongoTemplate.find(query, Formateur.class);
            map.put("TableFormateurAF", list);
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> listerMesFormation(String token, String idFormateur) {
        if (authenticationFilter.VerifierTOKEN(token) && (authenticationFilter.RecupererRole(token).equals("ADMIN") || authenticationFilter.RecupererRole(token).equals("USER"))) {
            List<Map> list1 = new ArrayList<>();
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(idFormateur));
            Formateur list = mongoTemplate.findOne(query, Formateur.class);
            List<String> list2 = list.getFormationID();
            for (String f : list2) {
                Map map1 = new HashMap();
                Formation formation = mongoTemplate.findOne(query(where("_id").is(f)), Formation.class);
                map1.put("themeformation", formation.getThemeFormation());
                map1.put("idFormation", formation.getIdformation());
                list1.add(map1);
            }
            map.put("MesFormation", list1);
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public ResponseEntity<Map> detailsFormationFormateur(String token, String id) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("USER")) {
            Query query = new Query(Criteria.where("_id").is(id));
            Formation formation = mongoTemplate.findOne(query, Formation.class);
            map.put("Formation", formation);
        } else {
            map.put("Message", "Accès réfusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    public ResponseEntity<Map> afficherunseulcours(String formationid, String formateurID) {
        Query query = new Query(Criteria.where("id").is(formateurID));
        Formateur formation = mongoTemplate.findOne(query, Formateur.class);
        if (formation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Map<String, List<String>> programme = formation.getProgramme();
        if (programme != null && programme.containsKey(formationid)) {
            map.put("cours", programme.get(formationid));
            return new ResponseEntity<>(map, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<byte[]> listerlescours(String formationid, String formateurID, String cours) throws IOException {
        String pdfFilePath = "src/main/resources/cours/" + formateurID + "/" + formationid + "/" + cours;
        File pdfFile = new File(pdfFilePath);
        byte[] pdfContent = Files.readAllBytes(pdfFile.toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "file.pdf");

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

}
