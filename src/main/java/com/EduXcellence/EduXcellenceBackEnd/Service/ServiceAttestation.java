package com.EduXcellence.EduXcellenceBackEnd.Service;

import com.EduXcellence.EduXcellenceBackEnd.Models.Attestation;
import com.EduXcellence.EduXcellenceBackEnd.Models.Formation;
import com.EduXcellence.EduXcellenceBackEnd.Models.Participant;
import com.EduXcellence.EduXcellenceBackEnd.Repository.AttestationRepo;
import com.EduXcellence.EduXcellenceBackEnd.Repository.FormationRepo;
import com.EduXcellence.EduXcellenceBackEnd.Repository.ParticipantRepo;
import com.EduXcellence.EduXcellenceBackEnd.Security.AuthenticationFilter;
import com.lowagie.text.DocumentException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static org.springframework.data.mongodb.core.query.Query.query;


@Service
public class ServiceAttestation {

    @Autowired
    private AttestationRepo attestationRepo;
    @Autowired
    AuthenticationFilter authenticationFilter;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    private Configuration freemarkerConfig;

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


    public byte[] generateAttestation(String idformation, String idparticipant) throws IOException, DocumentException, TemplateException {
        // Get the participant and formation data from your database
        Participant participant = mongoTemplate.findOne(query(Criteria.where("id").is(idparticipant)), Participant.class);
        Formation formation = mongoTemplate.findOne(query(Criteria.where("_id").is(idformation)), Formation.class);

        Map<String, Object> model = new HashMap<>();
        model.put("username", participant.getNomPrenom());
        model.put("course_name", formation.getThemeFormation());
        model.put("completion_date", new Date().toString());

        // Render the HTML template
        Template template = freemarkerConfig.getTemplate("certificate.html");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

        // Convert the HTML to a PDF document using Flying Saucer
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        renderer.createPDF(outputStream);

        // Get the generated PDF as a byte array
        byte[] pdfBytes = outputStream.toByteArray();

        return pdfBytes;
    }
}
