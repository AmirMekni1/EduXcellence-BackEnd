package com.EduXcellence.EduXcellenceBackEnd.Models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
@Getter
public class Payement {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    @Id
    private String payementId;
    private Date date = new Date();
    private String FormationID;
    private String ParticipantID;
    private String bonDeCommande = "";
    private boolean verifierInscription = false;
    private String formateurID = "";


    public String formatDate(Date date) {
        return formatter.format(date);
    }

}
