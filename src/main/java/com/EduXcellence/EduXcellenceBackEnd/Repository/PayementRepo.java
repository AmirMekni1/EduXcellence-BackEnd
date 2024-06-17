package com.EduXcellence.EduXcellenceBackEnd.Repository;

import com.EduXcellence.EduXcellenceBackEnd.Models.Payement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayementRepo extends MongoRepository<Payement, String> {
}
