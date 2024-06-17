package com.EduXcellence.EduXcellenceBackEnd.Service;


import com.EduXcellence.EduXcellenceBackEnd.Models.Formateur;
import com.EduXcellence.EduXcellenceBackEnd.Models.Formation;
import com.EduXcellence.EduXcellenceBackEnd.Models.Participant;
import com.EduXcellence.EduXcellenceBackEnd.Models.Payement;
import com.EduXcellence.EduXcellenceBackEnd.Repository.FormateurRepo;
import com.EduXcellence.EduXcellenceBackEnd.Repository.FormationRepo;
import com.EduXcellence.EduXcellenceBackEnd.Repository.ParticipantRepo;
import com.EduXcellence.EduXcellenceBackEnd.Repository.PayementRepo;
import com.EduXcellence.EduXcellenceBackEnd.Security.AuthenticationFilter;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ServiceFormation {

    @Autowired
    private FormationRepo formationRepo;

    @Autowired
    AuthenticationFilter authFiltre;

    @Autowired
    MongoTemplate mongoTemplate;
    Payement payement = new Payement();
    @Autowired
    private PayementRepo payementRepo;

    @Autowired
    private ServiceEmail serviceEmail;
    Map map = new HashMap();

    @Autowired
    private ParticipantRepo participantRepo;
    @Autowired
    private FormateurRepo formateurRepo;

    /*------------------------------Gestion des formations------------------------------------*/

    public ResponseEntity<Map> ajouterFormation(Formation formation, String token) {
        if (authFiltre.VerifierTOKEN(token) && authFiltre.RecupererRole(token).equals("ADMIN")) {
            Query query = new Query(Criteria.where("datedebut").is(formation.getDatedebut()));
            Long exist = mongoTemplate.count(query, Formation.class);
            if (exist > 0) {
                map.put("Message", "Il y a déjà une formation prévue à cette date");
            } else if (formation.getDatefin().before(formation.getDatedebut())) {
                map.put("Message","La date de fin de la formation doit être après la date de début");
            } else if (formation.getDatedebut().before((new Date()))) {
                map.put("Message", "La date de début de la formation doit être aujourd'hui ou après");
            } else {
                formationRepo.save(formation);
                map.put("Message","La formation a été ajoutée avec succès");
            }
        } else {

        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> ActiverFormation(String id, String token) {
        if (authFiltre.VerifierTOKEN(token) && authFiltre.RecupererRole(token).equals("ADMIN")) {
            Query query = new Query(Criteria.where("_id").is(id));
            Update update = new Update().set("affiche", true);
            mongoTemplate.updateFirst(query, update, Formation.class);
            map.put("Message", "Formation Activée");
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> DesactiverFormation(String id, String token) {
        if (authFiltre.VerifierTOKEN(token) && authFiltre.RecupererRole(token).equals("ADMIN")) {
            Query query = new Query(Criteria.where("_id").is(id));
            Update update = new Update().set("affiche", false);
            mongoTemplate.updateFirst(query, update, Formation.class);
            map.put("Message", "Formation Desactivée");
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> modifierFormation(String id, Formation formation, String token) {
        Map<String, String> map = new HashMap<>();

        if (authFiltre.VerifierTOKEN(token) && "ADMIN".equals(authFiltre.RecupererRole(token))) {
            Query query = new Query(Criteria.where("idformation").is(id));
            Formation formation1 = mongoTemplate.findOne(query, Formation.class);
           if (formation.getDatefin().before(formation.getDatedebut())) {
                map.put("Message","La date de fin de la formation doit être après la date de début");
            } else if (formation.getDatedebut().before((new Date()))) {
                map.put("Message", "La date de début de la formation doit être aujourd'hui ou après");
            } else {
                Update update = new Update()
                        .set("themeFormation", formation.getThemeFormation())
                        .set("desciption", formation.getDesciption())
                        .set("datedebut", formation.getDatedebut())
                        .set("datefin", formation.getDatefin())
                        .set("prix", formation.getPrix())
                        .set("participantID", formation.getParticipantID())
                        .set("formateurID", formation.getFormateurID())
                        .set("affiche", formation.isAffiche());
                mongoTemplate.updateFirst(query, update, Formation.class);
                map.put("Message", "Mise à jour avec succès");

            }} else {
            map.put("Message", "Accès refusé");
        } return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> affichertouteFormations(String token) {
        List<Formation> list = this.formationRepo.findAll();
        map.put("TableFormation", list);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> afficherFormationsActive(String token) {

        List<Formation> list = this.formationRepo.findAll();
        List<Object> listeaffiiche = new ArrayList<>();
        for (Formation formation : list) {
            if (formation.isAffiche()) {
                listeaffiiche.add(formation);
            }
            map.put("TableFormation", listeaffiiche);
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> afficherFormationparid(String token, String id) {
        if (authFiltre.VerifierTOKEN(token) && (authFiltre.RecupererRole(token).equals("USER") || authFiltre.RecupererRole(token).equals("ADMIN"))) {
            Query query = new Query(Criteria.where("_id").is(id));
            Formation formation = mongoTemplate.findOne(query, Formation.class);
            map.put("Formation", formation);
        } else {
            map.put("Message", "Accès réfusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    public ResponseEntity<Map> lesFormateursAffectesAFormation(String token, String formation) {
        if (authFiltre.VerifierTOKEN(token) && authFiltre.RecupererRole(token).equals("ADMIN")) {
            Query query = new Query();
            query.addCriteria(Criteria.where("FormationID").is(formation));
            List<Formateur> list = mongoTemplate.find(query, Formateur.class);

            map.put("TableFormateurAF", list);
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    public ResponseEntity<Map> planifierFormation(String formationId, String formateurId, String token) throws Exception {
        if (authFiltre.VerifierTOKEN(token) && authFiltre.RecupererRole(token).equals("ADMIN")) {
            long payementss = mongoTemplate.count(Query.query(Criteria.where("FormationID").is(formationId)), Payement.class);
            if ( payementss == 0) {
                map.put("Message", "Aucun participant inscrit dans cette formation");
            }else  if (payementss < 3 ) {
                map.put("Message", "Il doit y avoir au moins 3 inscriptions");
            }else if (payementss > 3 ) {
                String nomFormation = null;
                List<Payement> payements = mongoTemplate.find(Query.query(Criteria.where("FormationID").is(formationId).and("verifierInscription").is(false)).limit(3), Payement.class);
                for (Payement payement : payements) {
                    payement.setVerifierInscription(true);
                    payement.setFormateurID(formateurId);
                    payementRepo.save(payement);
                    Query query = new Query(Criteria.where("_id").is(payement.getParticipantID()));
                    Participant participant = mongoTemplate.findOne(query, Participant.class);
                    Query query1 = new Query(Criteria.where("_id").is(payement.getFormationID()));
                    Formation formation = mongoTemplate.findOne(query1, Formation.class);
                    serviceEmail.sendEmail(participant.getEmail(), formation.getThemeFormation(), formation.getIdformation());
                    nomFormation = formation.getThemeFormation();

                }
                Query query = new Query(Criteria.where("_id").is(formateurId));
                Formateur formateur = mongoTemplate.findOne(query, Formateur.class);
                formateur.setDisponiblite(false);
                formateur.getFormationID().add(formationId);
                serviceEmail.sendEmailFormateur(formateur.getEmail(), nomFormation);
                formateurRepo.save(formateur);

                map.put("Message", "L'inscription des 10 premiers participants a été vérifiée avec succès");
            }
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
    /*-------------------------------------------------------------------------------------------------------------------------------------------------------------------*/



}
