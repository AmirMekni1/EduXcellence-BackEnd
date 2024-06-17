package com.EduXcellence.EduXcellenceBackEnd.Controller;

import com.EduXcellence.EduXcellenceBackEnd.Models.Administrateur;
import com.EduXcellence.EduXcellenceBackEnd.Models.Formateur;
import com.EduXcellence.EduXcellenceBackEnd.Models.Participant;
import com.EduXcellence.EduXcellenceBackEnd.Repository.AdministrateurRepo;
import com.EduXcellence.EduXcellenceBackEnd.Security.AuthenticationFilter;
import com.EduXcellence.EduXcellenceBackEnd.Service.ServiceAdministrateur;
import com.EduXcellence.EduXcellenceBackEnd.Service.ServiceAuthentification;
import com.EduXcellence.EduXcellenceBackEnd.Service.ServiceFormateur;
import com.EduXcellence.EduXcellenceBackEnd.Service.ServiceParticipant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ApiAuth")
@CrossOrigin(origins = "*")
public class AuthentificationController {

    @Autowired
    private ServiceAuthentification serviceAuthentification;
    @Autowired
    private ServiceAdministrateur serviceAdministrateur;
    @Autowired
    private ServiceParticipant serviceParticipant;
    @Autowired
    private ServiceFormateur serviceFormateur;
    @Autowired
    private AuthenticationFilter authenticationFilter;

//    @Autowired
//    BCryptPasswordEncoder bCryptPasswordEncoder;
//    @Autowired
//    AdministrateurRepo administrateurRepo;

    @PostMapping("/loginParticipant")
    public ResponseEntity<Map> login(@ModelAttribute Participant participant) {
        return serviceParticipant.loginParticipant(participant);

    }

    @PostMapping("/inscriptionParticipant")
    public ResponseEntity<Map> inscriptionParticipant(@ModelAttribute Participant participant) {
        return serviceParticipant.inscriptionParticipant(participant);
    }

//    @PostMapping("/AjouterAdmin")
//    public Administrateur inscriptionParticipant(@ModelAttribute Administrateur Admin) {
//        String pass = bCryptPasswordEncoder.encode(Admin.getMotDePasse());
//        Admin.setMotDePasse(pass);
//        return administrateurRepo.save(Admin);
//    }

    @PostMapping("/VerifierAuthentification")
    public boolean VerifierAuthentification(@RequestParam String token) {
        return this.serviceAuthentification.isAuthenticated(token);
    }

    @PostMapping("/loginAdmin")
    public ResponseEntity<Map<String, String>> loginAdmin(@ModelAttribute Administrateur administrateur) {
        return serviceAdministrateur.loginAdmin(administrateur);
    }

    @PostMapping("/loginFormateur")
    public ResponseEntity<Map> loginFormateur(@ModelAttribute Formateur formateur) {
        return serviceFormateur.loginFormateur(formateur);
    }

    @PostMapping("/IsAuthentifier")
    public boolean IsAuthentifier(@RequestParam String token) {
        return authenticationFilter.VerifierTOKEN(token);
    }
}
