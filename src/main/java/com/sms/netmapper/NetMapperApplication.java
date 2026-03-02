package com.sms.netmapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale de l'application NetMapper
 * 
 * @author SMS Informatique
 * @version 1.0
 * @since Janvier 2026
 */
@SpringBootApplication
public class NetMapperApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetMapperApplication.class, args);
        System.out.println("\n");
        System.out.println("=".repeat(60));
        System.out.println("  ███╗   ██╗███████╗████████╗███╗   ███╗ █████╗ ██████╗ ██████╗ ███████╗██████╗ ");
        System.out.println("  ████╗  ██║██╔════╝╚══██╔══╝████╗ ████║██╔══██╗██╔══██╗██╔══██╗██╔════╝██╔══██╗");
        System.out.println("  ██╔██╗ ██║█████╗     ██║   ██╔████╔██║███████║██████╔╝██████╔╝█████╗  ██████╔╝");
        System.out.println("  ██║╚██╗██║██╔══╝     ██║   ██║╚██╔╝██║██╔══██║██╔═══╝ ██╔═══╝ ██╔══╝  ██╔══██╗");
        System.out.println("  ██║ ╚████║███████╗   ██║   ██║ ╚═╝ ██║██║  ██║██║     ██║     ███████╗██║  ██║");
        System.out.println("  ╚═╝  ╚═══╝╚══════╝   ╚═╝   ╚═╝     ╚═╝╚═╝  ╚═╝╚═╝     ╚═╝     ╚══════╝╚═╝  ╚═╝");
        System.out.println("=".repeat(60));
        System.out.println("  NetMapper Backend API - SMS Informatique");
        System.out.println("  Version: 1.0");
        System.out.println("  Port: 5000");
        System.out.println("  API Base URL: http://localhost:5000/api");
        System.out.println("=".repeat(60));
        System.out.println("\n");
    }
}
