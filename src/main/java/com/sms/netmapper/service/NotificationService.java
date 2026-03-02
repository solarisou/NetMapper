package com.sms.netmapper.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service de notifications temps réel via WebSocket
 * Envoie des événements au frontend sur les topics STOMP
 *
 * @author SMS Informatique - NetMapper
 */
@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ── Topics ────────────────────────────────────────────────
    private static final String TOPIC_ALERTES    = "/topic/alertes";
    private static final String TOPIC_EQUIPEMENTS = "/topic/equipements";
    private static final String TOPIC_TICKETS    = "/topic/tickets";

    // ── Alertes ───────────────────────────────────────────────

    /**
     * Notifie le frontend d'une nouvelle alerte
     */
    public void notifierNouvelleAlerte(Integer idEquipement, String nomEquipement,
                                        String niveau, String message, String type) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event",        "NOUVELLE_ALERTE");
        payload.put("idEquipement", idEquipement);
        payload.put("equipement",   nomEquipement);
        payload.put("niveau",       niveau);
        payload.put("message",      message);
        payload.put("type",         type);
        payload.put("date",         LocalDateTime.now().toString());

messagingTemplate.convertAndSend(TOPIC_ALERTES, (Object) payload);    }

    // ── Équipements ───────────────────────────────────────────

    /**
     * Notifie le frontend d'un changement d'état d'un équipement
     */
    public void notifierChangementEtat(Integer idEquipement, String nomEquipement,
                                        String ancienEtat, String nouvelEtat) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event",        "CHANGEMENT_ETAT");
        payload.put("idEquipement", idEquipement);
        payload.put("equipement",   nomEquipement);
        payload.put("ancienEtat",   ancienEtat);
        payload.put("nouvelEtat",   nouvelEtat);
        payload.put("date",         LocalDateTime.now().toString());

messagingTemplate.convertAndSend(TOPIC_EQUIPEMENTS, (Object) payload);    }

    /**
     * Notifie le frontend d'un nouvel équipement détecté (scanner)
     */
    public void notifierNouvelEquipement(String ip, String type, String nom) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event", "NOUVEL_EQUIPEMENT");
        payload.put("ip",    ip);
        payload.put("type",  type);
        payload.put("nom",   nom);
        payload.put("date",  LocalDateTime.now().toString());

        messagingTemplate.convertAndSend(TOPIC_EQUIPEMENTS, (Object) payload);
    }

    // ── Tickets ───────────────────────────────────────────────

    /**
     * Notifie le frontend d'un changement sur un ticket
     */
    public void notifierTicket(Integer idTicket, String action, String gravite) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event",    "TICKET_" + action.toUpperCase());
        payload.put("idTicket", idTicket);
        payload.put("action",   action);
        payload.put("gravite",  gravite);
        payload.put("date",     LocalDateTime.now().toString());

        messagingTemplate.convertAndSend(TOPIC_TICKETS, (Object) payload);
    }
}