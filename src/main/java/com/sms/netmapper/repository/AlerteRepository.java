package com.sms.netmapper.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.sms.netmapper.model.Alerte;
import com.sms.netmapper.model.Equipement;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Integer> {
    
    /**
     * Trouver toutes les alertes d'un équipement
     */
    List<Alerte> findByEquipement(Equipement equipement);
    
    /**
     * Trouver toutes les alertes par niveau
     */
    List<Alerte> findByNiveau(String niveau);
    
    /**
     * Trouver les alertes après une certaine date
     */
    List<Alerte> findByDateAlerteAfter(LocalDateTime date);
    
    /**
     * Trouver les alertes critiques récentes (7 derniers jours)
     */
    @Query("SELECT a FROM Alerte a WHERE a.niveau = 'Critique' AND a.dateAlerte >= :date ORDER BY a.dateAlerte DESC")
    List<Alerte> findRecentCriticalAlertes(LocalDateTime date);
}
