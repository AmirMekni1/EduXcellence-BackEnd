package com.EduXcellence.EduXcellenceBackEnd.Controller;

import com.EduXcellence.EduXcellenceBackEnd.Models.Evaluation;
import com.EduXcellence.EduXcellenceBackEnd.Service.ServiceEvaluation;
import com.EduXcellence.EduXcellenceBackEnd.Service.ServiceFormateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apiFormateur")
@CrossOrigin(origins = "*")
public class FormateurController {
    @Autowired
    private ServiceEvaluation serviceEvaluation;

    @Autowired
    private ServiceFormateur serviceFormateur;


    /*------------------------------Gestion des évaluations--------------------------------*/
    @PostMapping("/ajouterEvaluation")
    public ResponseEntity<Map> ajouterEvaluation(@RequestHeader String token, @ModelAttribute Evaluation evaluation) {
        return serviceEvaluation.ajouterEvaluation(token, evaluation);
    }

    @PostMapping("/listerMesFormationFormateur")
    public ResponseEntity<Map> listerMesFormation(@RequestHeader String token, @RequestParam String idFormateur) {
        return serviceFormateur.listerMesFormation(token, idFormateur);
    }

    @PostMapping("/detailsFormationFormateur")
    public ResponseEntity<Map> detailsFormationFormateur(@RequestHeader String token, @RequestParam String id) {
        return serviceFormateur.detailsFormationFormateur(token, id);
    }

    @PostMapping("/listerUneEvaluation")
    public ResponseEntity<Map> AfficherUneEvaluation(@RequestHeader String token, @RequestParam String formationId) throws ParseException {
        return serviceEvaluation.AfficherUneEvaluation(token, formationId);
    }

    @PostMapping("/insererleProgramme")
    public ResponseEntity<List<String>> Ajoutercours(@RequestParam(name = "files") MultipartFile[] files, @RequestParam String formationid, @RequestParam String formateurID) throws IOException {
        return serviceEvaluation.Ajoutercours(files, formationid, formateurID);
    }

    @GetMapping("/lister")
    public List<String> AfficherlesCours(@RequestParam String formationID, @RequestParam String formateurID) {
        return serviceEvaluation.AfficherlesCours(formationID, formateurID);
    }

    @PostMapping("/getFilesByFormationId")
    public ResponseEntity<Map> afficherunseulcours(@RequestParam String formationid, @RequestParam String formateurID) {
        return serviceFormateur.afficherunseulcours(formationid, formateurID);
    }

    @GetMapping("/openPdf/{formateurID}/{formationid}/{cours}")
    public ResponseEntity<byte[]> listerlescours(@PathVariable String formationid, @PathVariable String formateurID, @PathVariable String cours) throws IOException {
        return serviceFormateur.listerlescours(formationid, formateurID, cours);
    }
}
