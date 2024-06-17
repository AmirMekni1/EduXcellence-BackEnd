package com.EduXcellence.EduXcellenceBackEnd.Repository;

import com.EduXcellence.EduXcellenceBackEnd.Models.Administrateur;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministrateurRepo extends MongoRepository<Administrateur, String> {
}
