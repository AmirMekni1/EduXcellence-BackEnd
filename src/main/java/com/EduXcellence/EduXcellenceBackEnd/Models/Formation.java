package com.EduXcellence.EduXcellenceBackEnd.Models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
@Getter
public class Formation {

    @Id
    private String idformation;
    private String themeFormation;
    private String desciption;
    private Date datedebut;
    private Date datefin;
    private double prix;
    private String ParticipantID;
    private List<String> FormateurID = new ArrayList<>();
    private boolean affiche = true;

}
