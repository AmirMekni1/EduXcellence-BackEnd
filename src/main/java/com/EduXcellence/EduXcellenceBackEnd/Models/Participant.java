package com.EduXcellence.EduXcellenceBackEnd.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
@Getter
public class Participant {

    @Id
    private String id;
    private String email;
    private String nomPrenom;
    private String motDePasse;
    private List<String> FormationID = new ArrayList<>();
    private List<String> AttestaionID = new ArrayList<>();
    private String niveauDEtude;
    private boolean verification = false;
    private String Role = "USER";

}
