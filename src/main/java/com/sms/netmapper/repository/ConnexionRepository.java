package com.sms.netmapper.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sms.netmapper.model.Connexion;
import com.sms.netmapper.model.Equipement;

@Repository
public interface ConnexionRepository extends JpaRepository<Connexion, Integer> {
    
    /**
     * Trouver toutes les connexions où l'équipement est source
     */
    List<Connexion> findBySource(Equipement source);
    
    /**
     * Trouver toutes les connexions où l'équipement est destination
     */
    List<Connexion> findByDestination(Equipement destination);
}
