package com.EduXcellence.EduXcellenceBackEnd.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
@Getter
public class Administrateur {

    @Id
    private String id;
    private String NomPrenom;
    private String MotDePasse;
    private String Role = "ADMIN";


}
