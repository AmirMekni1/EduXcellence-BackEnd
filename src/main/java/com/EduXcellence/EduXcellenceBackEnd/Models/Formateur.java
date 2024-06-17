package com.EduXcellence.EduXcellenceBackEnd.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
@Getter
public class Formateur {

    @Id
    private String id;
    private String email;
    private String motDePasse;
    private String nomPrenom;
    private String specialite;
    private int numTelephone;
    private boolean active = true;
    private String Role = "USER";
    private List<String> FormationID = new ArrayList<>();
    private Map programme = new HashMap();
    private boolean Disponiblite = true;


    private String saveFile(MultipartFile file) throws IOException {
        String fileName = String.valueOf(new Random().nextInt(1000000)) + "_" + file.getOriginalFilename();
        Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        if (!Files.exists(fileStorageLocation)) {
            Files.createDirectories(fileStorageLocation);
        }
        Files.copy(file.getInputStream(), fileStorageLocation.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

}
