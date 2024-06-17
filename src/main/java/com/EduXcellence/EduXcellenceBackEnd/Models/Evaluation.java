package com.EduXcellence.EduXcellenceBackEnd.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
@Getter
public class Evaluation {

    @Id
    private String idEval;
    private List<String> ParticipantID = new ArrayList<>();
    @Field("formationId")
    private String formationID;
    private String dateEval;
    private String lien;


}
