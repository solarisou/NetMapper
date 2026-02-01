package com.sms.netmapper.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sms.netmapper.model.Equipement;

@Repository
public interface EquipementRepository extends JpaRepository<Equipement, Integer> {
    
    /**
     * Trouver un équipement par son adresse IP
     */
    Optional<Equipement> findByAdresseIp(String adresseIp);
    
    /**
     * Trouver tous les équipements par type
     */
    List<Equipement> findByType(String type);
    
    /**
     * Trouver tous les équipements par état
     */
    List<Equipement> findByEtat(String etat);
    
    /**
     * Vérifier si une IP existe déjà
     */
    boolean existsByAdresseIp(String adresseIp);
}
