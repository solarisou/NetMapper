package com.sms.netmapper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sms.netmapper.model.Salle;

@Repository
public interface SalleRepository extends JpaRepository<Salle, Integer> {
}
