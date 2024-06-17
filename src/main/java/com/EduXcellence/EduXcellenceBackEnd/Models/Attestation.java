package com.EduXcellence.EduXcellenceBackEnd.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
@Getter
public class Attestation {

    @Id
    private String idAttestation;
    private Date date;
    private String ParticipantID;
    private String FormationID;


}
