package com.EduXcellence.EduXcellenceBackEnd.Repository;

import com.EduXcellence.EduXcellenceBackEnd.Models.Participant;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ParticipantRepo extends MongoRepository<Participant, String> {

}
