package com.EduXcellence.EduXcellenceBackEnd.Controller;

import com.EduXcellence.EduXcellenceBackEnd.Models.Payement;
import com.EduXcellence.EduXcellenceBackEnd.Repository.PayementRepo;
import com.EduXcellence.EduXcellenceBackEnd.Security.AuthenticationFilter;
import com.EduXcellence.EduXcellenceBackEnd.Service.ServiceAttestation;
import com.EduXcellence.EduXcellenceBackEnd.Service.ServiceEmail;
import com.EduXcellence.EduXcellenceBackEnd.Service.ServiceParticipant;
import com.EduXcellence.EduXcellenceBackEnd.Service.ServicePayement;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/apiParticipant")
@CrossOrigin(origins = "*")
public class ParticipantController {
    @Autowired
    private ServiceParticipant serviceParticipant;
    @Autowired
    private ServicePayement servicePayement;
    @Autowired
    private ServiceAttestation serviceAttestation;
    @Autowired
    private PayementRepo payementRepo;
    @Autowired
    private AuthenticationFilter authenticationFilter;
    @Autowired
    private ServiceEmail serviceEmail;

    /*-------------------------------Gestion des participants---------------------------*/

    @GetMapping("/listerParticipants")
    public ResponseEntity<Map> listerParticipants(String token) {
        return this.serviceParticipant.listerParticipants(token);
    }


    /*-----------------------------------Gestions des payements---------------------------*/
    @SneakyThrows
    @PostMapping("/InscriptionAuFormation")
    public ResponseEntity<Map> InscriptionAuFormation(@ModelAttribute Payement payement,
                                                      @RequestHeader("Token") String token) throws IOException {
        return serviceParticipant.InscriptionAuFormation(payement, token);
    }


//    @GetMapping("/generate")
//    public ResponseEntity<Attestation> generateAttestation(@RequestParam String participantId, @RequestParam String courseId) {
//        Attestation attestation = serviceAttestation.generateAttestation(participantId, courseId);
//        if (attestation != null) {
//            // Generate PDF or return attestation data as JSON
//            return ResponseEntity.ok(attestation);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @PostMapping("/insererBonDeCommande")
    public ResponseEntity<Map> insererBonDeCommande(@RequestHeader String token,
                                                    @RequestParam("bonDeCommande") MultipartFile bonDeCommande,
                                                    @RequestParam String ParticipantID,
                                                    @RequestParam String FormationID) throws IOException {
        return serviceParticipant.insererBonDeCommande(token, bonDeCommande, ParticipantID, FormationID);
    }

    @GetMapping("/recupererBonDeCommande")
    public ResponseEntity<UrlResource> recupererBonDeCommande(@RequestHeader String token, @RequestParam String ParticipantID, @RequestParam String FormationID) {
        return serviceParticipant.recupererBonDeCommande(token, ParticipantID, FormationID);
    }
    @PostMapping("/RecupererId")
    public ResponseEntity<Map> RecupererId(@RequestParam("token") String token) {
        return authenticationFilter.RecupererId(token);
    }

    @PostMapping("/listerLesFormationAffecterAParticipant")
    public ResponseEntity<Map> listerLesFormationAffecterAParticipant(@RequestHeader String token, @RequestParam("idParticipant") String idParticipant) {
        return serviceParticipant.listerLesFormationAffecterAParticipant(token, idParticipant);
    }

    @PostMapping("/listerLesPayementsdeUnSeulParticipant")
    public ResponseEntity<Map> listerLesPayementsdeUnSeulParticipant(@RequestHeader("token") String token, @RequestParam("idparticipant") String idparticipant) {
        return servicePayement.listerLesPayementsdeUnSeulParticipant(token, idparticipant);
    }

    @PostMapping("/envoyerEmailAdmin")
    public void envoyerEmailAdmin(@RequestParam String Sujet, @RequestParam String Contenu, String from) throws Exception {
        serviceEmail.envoyerEmailAdmin(Sujet, Contenu);
    }

    @GetMapping("/certification/{dformation}/{idparticipant}")
    public ResponseEntity<byte[]> generateAttestation(@PathVariable String dformation, @PathVariable String idparticipant) throws IOException, DocumentException {
        byte[] pdfBytes = serviceAttestation.generateAttestation(dformation, idparticipant);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "certification.pdf");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}