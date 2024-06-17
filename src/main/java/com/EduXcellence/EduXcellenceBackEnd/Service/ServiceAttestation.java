package com.EduXcellence.EduXcellenceBackEnd.Service;

import com.EduXcellence.EduXcellenceBackEnd.Models.Attestation;
import com.EduXcellence.EduXcellenceBackEnd.Models.Formation;
import com.EduXcellence.EduXcellenceBackEnd.Models.Participant;
import com.EduXcellence.EduXcellenceBackEnd.Repository.AttestationRepo;
import com.EduXcellence.EduXcellenceBackEnd.Repository.FormationRepo;
import com.EduXcellence.EduXcellenceBackEnd.Repository.ParticipantRepo;
import com.EduXcellence.EduXcellenceBackEnd.Security.AuthenticationFilter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Query.query;


@Service
public class ServiceAttestation {

    @Autowired
    private AttestationRepo attestationRepo;
    @Autowired
    private ParticipantRepo participantRepo;
    @Autowired
    private FormationRepo formationRepo;
    @Autowired
    AuthenticationFilter authenticationFilter;
    @Autowired
    MongoTemplate mongoTemplate;

    /*-------------------------------Gestion des attestations----------------------------------*/

    public ResponseEntity<Map> ajouterAttestation(Attestation attestation, String token) {
        Map map = new HashMap();
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("ADMIN")) {
            this.attestationRepo.save(attestation);
            map.put("Message", "Attestation Ajoutée Avec Succés");
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    public byte[] generateAttestation(String idformation, String idparticipant) throws IOException, DocumentException {
        Query query = new Query();
        Attestation attestation = mongoTemplate.findOne(query(Criteria.where("ParticipantID").is(idparticipant).and("FormationID").is(idformation)), Attestation.class);
        System.out.println(attestation);
        Participant participant = mongoTemplate.findOne(query(Criteria.where("id").is(attestation.getParticipantID())), Participant.class);
        Formation formation = mongoTemplate.findOne(query(Criteria.where("_id").is(attestation.getFormationID())), Formation.class);
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        document.open();
        document.add(new Paragraph("Certification"));
        document.add(new Paragraph("Username: " + participant.getNomPrenom()));
        document.add(new Paragraph("Course Name: " + formation.getThemeFormation()));
        document.add(new Paragraph("Completion Date: " + new Date()));
        document.close();

        return outputStream.toByteArray();
    }
}
