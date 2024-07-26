package com.nosbor.reviewer.api.repos;

import com.nosbor.reviewer.api.repos.entities.ProcessStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProcessStatusRepository extends JpaRepository<ProcessStatusEntity, String> {
}
