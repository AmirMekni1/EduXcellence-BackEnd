package com.EduXcellence.EduXcellenceBackEnd.Controller;

import com.EduXcellence.EduXcellenceBackEnd.Models.Attestation;
import com.EduXcellence.EduXcellenceBackEnd.Models.Formation;
import com.EduXcellence.EduXcellenceBackEnd.Models.Formateur;
import com.EduXcellence.EduXcellenceBackEnd.Security.AuthenticationFilter;
import com.EduXcellence.EduXcellenceBackEnd.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/apiAdmin")
@CrossOrigin(origins = "*")
public class AdministrateurController {

    @Autowired
    private ServiceAdministrateur serviceadministrateur;
    @Autowired
    private ServiceFormateur serviceFormateur;
    @Autowired
    private ServiceFormation serviceFormation;
    @Autowired
    private ServiceAttestation serviceAttestation;
    @Autowired
    private ServiceParticipant serviceParticipant;
    @Autowired
    private ServicePayement servicePayement;
    @Autowired
    private ServiceEmail serviceEmail;


    /*-------------------------------Gestion des formateurs---------------------------------*/
    @PostMapping("/AjouterFormateur")
    public ResponseEntity<Map> NouvelleFormateur(@RequestHeader String Token, @ModelAttribute Formateur F) {
        return serviceFormateur.AjouterFormateur(F, Token);
    }

    @GetMapping("/listerFormateurs")
    public ResponseEntity<Map> RecupererFormateurs(@RequestHeader("Token") String Token) {
        return serviceFormateur.listerFormateurs(Token);
    }

    @GetMapping("/listerUnSeulFormateurs/{id}")
    public ResponseEntity<Map> listerUnSeulFormateur(@PathVariable String id, @RequestHeader String token) {
        return serviceFormateur.listerUnSeulFormateur(id, token);
    }

    @PostMapping("/Réassignation")
    public void Réassignation(@RequestHeader String token, @RequestParam String id) {
        serviceFormateur.Réassignation(token, id);
    }

    /*------------------------------Gestion des formations------------------------------------*/


    @PostMapping("/AjouterFormation")
    public ResponseEntity<Map> ajouterformation(@ModelAttribute Formation formation, @RequestHeader("Token") String Token) {
        return serviceFormation.ajouterFormation(formation, Token);
    }

    @GetMapping("/listerFormations")
    public ResponseEntity<Map> affichertouteFormations(@RequestHeader("Token") String Token) {
        return serviceFormation.affichertouteFormations(Token);
    }

    @GetMapping("/filtrerFormations")
    public ResponseEntity<Map> afficherFormationsActives(@RequestHeader("Token") String Token) {
        return serviceFormation.afficherFormationsActive(Token);
    }


    @PutMapping("/ActiverFormation")
    public ResponseEntity<Map> ActiverFormation(@RequestHeader("Token") String Token, @RequestParam("id") String id) {
        return serviceFormation.ActiverFormation(id, Token);
    }

    @PutMapping("/DesactiverFormation")
    public ResponseEntity<Map> DesactiverFormation(@RequestHeader("Token") String Token, @RequestParam("id") String id) {
        return serviceFormation.DesactiverFormation(id, Token);
    }

    @GetMapping("/listerUnSeulFormation/{id}")
    public ResponseEntity<Map> afficherFormationparid(@RequestHeader("Token") String Token, @PathVariable String id) {
        return serviceFormation.afficherFormationparid(Token, id);
    }

    @PutMapping("/modifierFormation/{id}")
    public ResponseEntity<Map> modifierFormation(@PathVariable("id") String id, @RequestBody Formation formation, @RequestHeader String token) {
        return serviceFormation.modifierFormation(id, formation, token);
    }

    @PutMapping("/ActiverCompteFormateur")
    public ResponseEntity<Map> ActiverCompteFormateur(@RequestParam("id") String id, @RequestHeader("Token") String token) {
        return serviceFormateur.ActiverCompteFormateur(id, token);
    }

    @PutMapping("/DesactiverCompteFormateur")
    public ResponseEntity<Map> DesactiverCompteFormateur(@RequestParam("id") String id, @RequestHeader("Token") String token) {
        return serviceFormateur.DesactiverCompteFormateur(id, token);
    }

    @PutMapping("/modifierCompteFormateur/{id}")
    public ResponseEntity<Map> modifierformateur(@PathVariable("id") String id,
                                                 @RequestParam("email") String email,
                                                 @RequestParam("motDePasse") String motDePasse,
                                                 @RequestParam String nomPrenom,
                                                 @RequestHeader("Token") String token,
                                                 @RequestParam("numTelephone") int numTelephone,
                                                 @RequestParam String specialite) {
        return serviceFormateur.modifierFormateur(id, email, nomPrenom, motDePasse, numTelephone, specialite, token);
    }

    @GetMapping("/lesFormateursAffectesAFormation/{formation}")
    public ResponseEntity<Map> lesFormateursAffectesAFormation(@RequestHeader String token, @PathVariable String formation) {
        return serviceFormation.lesFormateursAffectesAFormation(token, formation);
    }
    /*------------------------------------------------------------------Gestion des attestations--------------------------------------------------------------*/

    @PostMapping("/ajouterAttestation")
    public void ajouterAttestation(@RequestHeader("Token") String Token, @RequestBody Attestation attestation) {
        serviceAttestation.ajouterAttestation(attestation, Token);
    }


    /*------------------------------------------------------------------Gestion des Participants--------------------------------------------------------------*/



    @PostMapping("/planifierFormation")
    public ResponseEntity<Map> planifierFormation(@RequestHeader String token, @RequestParam String formateurId, @RequestParam String formationId) throws Exception {
        return serviceFormation.planifierFormation(formationId, formateurId, token);
    }

    @GetMapping("/listerParticipants")
    public ResponseEntity<Map> listerParticipants(@RequestHeader String token) {
        return serviceParticipant.listerParticipants(token);
    }

    @GetMapping("/listerUnSeulParticipant/{id}")
    public ResponseEntity<Map> afficherParticipantparid(@PathVariable String id, @RequestHeader String token) {
        return serviceParticipant.afficherParticipantparid(id, token);
    }

    @PostMapping("/listerLesPayementsdeUnSeulParticipant")
    public ResponseEntity<Map> listerLesPayementsdeUnSeulParticipant(@RequestHeader("token") String token, @RequestParam("idparticipant") String idparticipant) {
        return servicePayement.listerLesPayementsdeUnSeulParticipant(token, idparticipant);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            byte[] data = servicePayement.getFile(fileName);
            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/listerPayement")
    public ResponseEntity<Map> listerPayement(@RequestHeader String token) {
        return servicePayement.listerLesPayement(token);
    }


    @GetMapping("/formateursDisponibles")
    public ResponseEntity<Map> formateursDisponibles(@RequestHeader String token) {
        return serviceFormateur.formateursDisponibles(token);
    }

    @GetMapping("/NombreDeParticipants")
    public ResponseEntity<Map> NombreDeParticipants(@RequestHeader String token) {
        return this.serviceParticipant.NombreDeParticipants(token);
    }

    @GetMapping("/NombreDeFormateurs")
    public ResponseEntity<Map> NombreDeFormateurs(@RequestHeader String token) {
        return serviceFormateur.NombreDeFormateurs(token);
    }

    @GetMapping("/NombreParticipantsVerifies")
    public ResponseEntity<Map> NombreParticipantsVerifies(@RequestHeader String token) {
        return serviceParticipant.NombreParticipantsVerifies(token);
    }

    @GetMapping("/NombreParticipantsNonVerifies")
    public ResponseEntity<Map> NombreParticipantsNonVerifies(@RequestHeader String token) {
        return serviceParticipant.NombreParticipantsNonVerifies(token);
    }


}
