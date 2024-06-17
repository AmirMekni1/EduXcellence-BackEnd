package com.EduXcellence.EduXcellenceBackEnd.Repository;

import com.EduXcellence.EduXcellenceBackEnd.Models.Attestation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttestationRepo extends MongoRepository<Attestation, String> {
}
