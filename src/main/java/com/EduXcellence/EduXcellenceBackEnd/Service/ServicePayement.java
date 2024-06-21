package com.EduXcellence.EduXcellenceBackEnd.Service;

import com.EduXcellence.EduXcellenceBackEnd.Models.Formation;
import com.EduXcellence.EduXcellenceBackEnd.Models.Participant;
import com.EduXcellence.EduXcellenceBackEnd.Models.Payement;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class ServicePayement {
    @Autowired
    private PayementRepo payementRepo;
    @Autowired
    private AuthenticationFilter authFiltre;
    @Autowired
    private MongoTemplate mongoTemplate;

    private Payement payement0 = new Payement();
    Map map = new HashMap();
    /*-----------------------------------Gestions des payements---------------------------*/

    public ResponseEntity<Map> listerLesPayementsdeUnSeulParticipant(String token, String idparticipant) {
        Map map1 = new HashMap();
        if (authFiltre.VerifierTOKEN(token) && authFiltre.RecupererRole(token).equals("ADMIN")) {
            Query query = new Query(Criteria.where("ParticipantID").is(idparticipant));
            List<Payement> payement = mongoTemplate.find(query, Payement.class);
            List<Map> list = new ArrayList<Map>();
            for (Payement pay : payement) {
                Query query2 = new Query(Criteria.where("idformation").is(pay.getFormationID()));
                Formation formation = mongoTemplate.findOne(query2, Formation.class);
                Map<String, Object> map2 = new HashMap<>();
                map2.put("idpayement", pay.getPayementId());
                map2.put("themedeformation", formation.getThemeFormation());
                map2.put("prix", formation.getPrix());
                map2.put("date", payement0.formatDate(pay.getDate()));
                map2.put("etatdepaiement", pay.isVerifierInscription());
                map2.put("bondecommande", pay.getBonDeCommande());
                list.add(map2);

            }
            map1.put("TablePayement", list);
            System.out.println(map1);
        } else {
            map1.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map1, HttpStatus.OK);
    }

    public byte[] getFile(String fileName) throws IOException {
        Path filePath = Paths.get("src/main/resources/BonDeCommand").resolve(fileName).normalize();
        if (Files.exists(filePath)) {
            return Files.readAllBytes(filePath);
        } else {
            throw new FileNotFoundException("File not found: " + fileName);
        }
    }

    public ResponseEntity<Map> listerLesPayement(String token) {
        if (authFiltre.VerifierTOKEN(token) && authFiltre.RecupererRole(token).equals("ADMIN")) {
            Query query = new Query();
            List<Payement> payement = payementRepo.findAll();
            List<Map> list = new ArrayList<>();
            for (Payement pay : payement) {
                Query queryP = new Query(Criteria.where("id").is(pay.getParticipantID()));
                Query queryF = new Query(Criteria.where("idformation").is(pay.getFormationID()));
                Participant participant = mongoTemplate.findOne(queryP, Participant.class);
                Formation formation = mongoTemplate.findOne(queryF, Formation.class);
                Map map1 = new HashMap();
                if (formation != null || participant != null) {
                    map1.put("nomprenom", participant.getNomPrenom());
                    map1.put("formation", formation.getThemeFormation());
                    map1.put("datedinscription", formatDate(pay.getDate()));
                    map1.put("verificationdinscrit", pay.isVerifierInscription());
                    list.add(map1);
                }
            }
            map.put("listerParticipantsInscritAuFormation", list);
        } else {
            map.put("Message", "Acceé refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

}
