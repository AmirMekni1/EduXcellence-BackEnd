package com.EduXcellence.EduXcellenceBackEnd.Service;

import com.EduXcellence.EduXcellenceBackEnd.Models.Evaluation;
import com.EduXcellence.EduXcellenceBackEnd.Models.Formateur;
import com.EduXcellence.EduXcellenceBackEnd.Repository.FormateurRepo;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ServiceEvaluation {
    @Autowired
    private FormateurRepo formateurRepo;

    @Autowired
    private com.EduXcellence.EduXcellenceBackEnd.Repository.EvaluationRepo evaluationRepo;
    @Autowired
    private com.EduXcellence.EduXcellenceBackEnd.Security.AuthenticationFilter authenticationFilter;

    Map<String, String> map = new HashMap<>();

    @Autowired
    private MongoTemplate mongoTemplate;

    public ResponseEntity<Map> ajouterEvaluation(String token, Evaluation evaluation) {
        if (authenticationFilter.VerifierTOKEN(token) && (authenticationFilter.RecupererRole(token).equals("ADMIN") || authenticationFilter.RecupererRole(token).equals("USER"))) {
            this.evaluationRepo.save(evaluation);
            map.put("Message", "Evaluation Ajoutée Avec Succés");
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public List<Evaluation> listerEvaluations() {
        return this.evaluationRepo.findAll();
    }

    public ResponseEntity<Map> assignerParticipant(String token, String participantId, String formationId) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("USER")) {
            Query query = new Query(Criteria.where("ParticipantID").is(participantId).and("formationID").is(formationId));
            Evaluation evaluation = mongoTemplate.findOne(query, Evaluation.class);
            if (evaluation != null) {
                // Assuming you want to update the evaluation with the participantId
                evaluation.getParticipantID().add(participantId);
                this.evaluationRepo.save(evaluation);
                map.put("Message", "Participant Assigné Avec Succés");
            } else {
                map.put("Message", "Evaluation non trouvée");
            }
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public ResponseEntity<Map> AfficherUneEvaluation(String token, String formationId) {
        if (authenticationFilter.VerifierTOKEN(token) && authenticationFilter.RecupererRole(token).equals("USER")) {
            System.out.println(formationId);
            Query query = new Query(Criteria.where("formationID").is(formationId));
            Evaluation evaluation = mongoTemplate.findOne(query, Evaluation.class);
            if (evaluation!=null){
                map.put("Dateeval", evaluation.getDateEval());
                map.put("Lien", evaluation.getLien());
            }
        } else {
            map.put("Message", "Accès refusé");
        }
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    public ResponseEntity<List<String>> Ajoutercours(MultipartFile[] files, String formationid, String formateurID) throws IOException {
        Query query = new Query(Criteria.where("id").is(formateurID));
        Formateur formation = mongoTemplate.findOne(query, Formateur.class);
        List<String> fileNames = new ArrayList<>();
        List<Object> list = (List<Object>) formation.getProgramme().getOrDefault(formationid, new ArrayList<>());

        for (MultipartFile file : files) {
            list.addFirst(saveFile(file, formateurID, formationid));
        }

        if (formation.getProgramme() == null) {
            formation.setProgramme(new HashMap<>());
        }
        formation.getProgramme().put(formationid, list);
        formateurRepo.save(formation);
        return new ResponseEntity<>(fileNames, HttpStatus.OK);
    }

    public String saveFile(MultipartFile file, String formateurID, String formationID) throws IOException {
        String fileName = String.valueOf(new Random().nextInt(1000000)) + "_" + file.getOriginalFilename();
        Path fileStorageLocation = Paths.get("src/main/resources/cours/" + formateurID + "/" + formationID).toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            Files.createDirectories(fileStorageLocation);
        }
        Files.copy(file.getInputStream(), fileStorageLocation.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    public List<String> AfficherlesCours(String formationID, String formateurID) {
        Query query = new Query(Criteria.where("formationID").is(formationID).and("FormateurID").is(formateurID));
        Formateur formateur = mongoTemplate.findOne(query, Formateur.class);

        File folder = new File("src/main/resources/cours");
        if (folder.exists() && folder.isDirectory()) {
            return Arrays.stream(folder.listFiles())
                    .filter(File::isFile)
                    .map(File::getName)
                    .collect(Collectors.toList());
        }
        throw new RuntimeException("Directory not found or is not a directory");
    }



}