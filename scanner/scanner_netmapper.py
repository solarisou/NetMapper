#!/usr/bin/env python3
"""
NetMapper - Scanner Réseau
SMS Informatique - Module de découverte réseau
Auteur : GHERRAS Salman
Version : 1.0

Fonctionnalités :
  - Scan ICMP (ping) pour détecter les équipements actifs
  - Lecture ARP pour récupérer les adresses MAC
  - Scan de ports TCP/UDP ouverts
  - Résolution DNS (hostname → IP)
  - Envoi automatique des données vers l'API Spring Boot
"""

import subprocess
import socket
import struct
import re
import json
import time
import logging
import ipaddress
import platform
import concurrent.futures
from datetime import datetime

import requests

# ─── Configuration ────────────────────────────────────────────────────────────

API_BASE_URL = "http://localhost:8081/api"
SCAN_TIMEOUT  = 1        # secondes par ping/port
MAX_WORKERS   = 50       # threads parallèles
COMMON_PORTS  = [22, 23, 80, 443, 8080, 8443, 21, 25, 53, 110, 143, 3306, 5432, 27017]

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    datefmt="%H:%M:%S"
)
log = logging.getLogger("NetMapper-Scanner")

# ─── Utilitaires réseau ────────────────────────────────────────────────────────

def ping(ip: str) -> bool:
    """Envoie un ping ICMP et retourne True si l'hôte répond."""
    system = platform.system().lower()
    if system == "windows":
        cmd = ["ping", "-n", "1", "-w", str(int(SCAN_TIMEOUT * 1000)), ip]
    else:
        cmd = ["ping", "-c", "1", "-W", str(int(SCAN_TIMEOUT)), ip]

    try:
        result = subprocess.run(
            cmd,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
            timeout=SCAN_TIMEOUT + 1
        )
        return result.returncode == 0
    except Exception:
        return False


def get_arp_table() -> dict:
    """
    Lit la table ARP locale et retourne un dict {ip: mac}.
    Fonctionne sous Linux/macOS/Windows.
    """
    arp_map = {}
    try:
        output = subprocess.check_output(["arp", "-a"], stderr=subprocess.DEVNULL).decode("cp850", errors="ignore")
        # Regex générique : capture IP et MAC
        pattern = re.compile(
            r'(\d{1,3}(?:\.\d{1,3}){3})\s+.*?'
            r'([0-9a-fA-F]{2}[:\-][0-9a-fA-F]{2}[:\-]'
            r'[0-9a-fA-F]{2}[:\-][0-9a-fA-F]{2}[:\-]'
            r'[0-9a-fA-F]{2}[:\-][0-9a-fA-F]{2})'
        )
        for m in pattern.finditer(output):
            ip  = m.group(1)
            mac = m.group(2).replace("-", ":").upper()
            arp_map[ip] = mac
    except Exception as e:
        log.warning(f"Lecture ARP échouée : {e}")
    return arp_map


def resolve_hostname(ip: str) -> str:
    """Résolution DNS inverse : IP → nom d'hôte."""
    try:
        return socket.gethostbyaddr(ip)[0]
    except Exception:
        return ip  # Fallback sur l'IP si pas de nom


def scan_ports(ip: str, ports: list = COMMON_PORTS) -> list:
    """
    Scan TCP sur les ports courants.
    Retourne la liste des ports ouverts.
    """
    open_ports = []
    for port in ports:
        try:
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
                s.settimeout(SCAN_TIMEOUT)
                if s.connect_ex((ip, port)) == 0:
                    open_ports.append(port)
        except Exception:
            pass
    return open_ports


def detect_device_type(open_ports: list, hostname: str) -> str:
    """
    Heuristique simple pour deviner le type d'équipement
    à partir des ports ouverts et du nom d'hôte.
    """
    h = hostname.lower()
    if any(k in h for k in ["router", "routeur", "gw", "gateway"]):
        return "Routeur"
    if any(k in h for k in ["switch", "sw-"]):
        return "Switch"
    if any(k in h for k in ["printer", "print", "hp"]):
        return "Imprimante"
    if 80 in open_ports or 443 in open_ports:
        if 22 in open_ports:
            return "Serveur"
        return "Serveur Web"
    if 22 in open_ports:
        return "Serveur"
    if 3306 in open_ports or 5432 in open_ports or 27017 in open_ports:
        return "Serveur Base de Données"
    return "Poste de travail"


# ─── Scan du réseau ───────────────────────────────────────────────────────────

def scan_ip(ip: str, arp_table: dict) -> dict | None:
    """
    Scanne une IP unique.
    Retourne un dict avec toutes les infos si l'hôte répond, sinon None.
    """
    ip_str = str(ip)
    if not ping(ip_str):
        return None

    mac      = arp_table.get(ip_str, "")
    hostname = resolve_hostname(ip_str)
    ports    = scan_ports(ip_str)
    dev_type = detect_device_type(ports, hostname)

    log.info(f"✓ Actif : {ip_str} | {hostname} | MAC:{mac or 'N/A'} | Ports:{ports}")

    return {
        "ip"       : ip_str,
        "hostname" : hostname,
        "mac"      : mac,
        "ports"    : ports,
        "type"     : dev_type,
        "scannedAt": datetime.now().isoformat(),
    }


def scan_network(cidr: str) -> list:
    """
    Scanne tous les hôtes d'un réseau CIDR.
    Ex : scan_network("192.168.1.0/24")
    """
    network  = ipaddress.ip_network(cidr, strict=False)
    hosts    = list(network.hosts())
    arp_table = get_arp_table()

    log.info(f"Début du scan → {cidr} ({len(hosts)} hôtes potentiels)")
    log.info(f"Table ARP locale : {len(arp_table)} entrées")

    results = []
    with concurrent.futures.ThreadPoolExecutor(max_workers=MAX_WORKERS) as executor:
        futures = {executor.submit(scan_ip, str(h), arp_table): h for h in hosts}
        for future in concurrent.futures.as_completed(futures):
            result = future.result()
            if result:
                results.append(result)

    log.info(f"Scan terminé : {len(results)} équipements actifs trouvés")
    return results


# ─── Intégration API Backend ───────────────────────────────────────────────────

def get_existing_ips() -> set:
    """Récupère les IP déjà enregistrées dans la base via l'API."""
    try:
        r = requests.get(f"{API_BASE_URL}/equipements", timeout=5)
        if r.status_code == 200:
            data = r.json().get("data", [])
            return {eq["adresseIp"] for eq in data}
    except Exception as e:
        log.warning(f"Impossible de récupérer les équipements existants : {e}")
    return set()


def push_equipement(device: dict) -> bool:
    """
    Envoie un équipement détecté vers POST /api/equipements.
    Retourne True si succès, False sinon.
    """
    hostname = device["hostname"]
    # Nom propre : utilise le hostname court (sans domaine)
    nom = hostname.split(".")[0] if "." in hostname else hostname

    payload = {
        "nom"       : nom,
        "type"      : device["type"],
        "adresseIp" : device["ip"],
        "adresseMac": device["mac"] if device["mac"] else None,
        "fabricant" : "Détecté automatiquement",
        "modele"    : None,
        "etat"      : "Actif",
    }

    try:
        r = requests.post(
            f"{API_BASE_URL}/equipements",
            json=payload,
            headers={"Content-Type": "application/json"},
            timeout=5
        )
        if r.status_code in (200, 201):
            log.info(f"  → Envoyé : {device['ip']} ({device['type']})")
            return True
        else:
            log.warning(f"  ✗ Rejeté ({r.status_code}) : {device['ip']} — {r.text[:120]}")
            return False
    except Exception as e:
        log.error(f"  ✗ Erreur API pour {device['ip']} : {e}")
        return False


def push_to_api(devices: list) -> tuple:
    """
    Pousse tous les nouveaux équipements vers l'API.
    Ignore ceux déjà présents en base (comparaison par IP).
    Retourne (nb_ajoutés, nb_ignorés, nb_erreurs).
    """
    existing_ips = get_existing_ips()
    log.info(f"{len(existing_ips)} équipements déjà en base")

    added = ignored = errors = 0
    for device in devices:
        if device["ip"] in existing_ips:
            log.debug(f"  → Ignoré (déjà en base) : {device['ip']}")
            ignored += 1
            continue
        if push_equipement(device):
            added += 1
        else:
            errors += 1
        time.sleep(0.05)  # légère pause pour ne pas saturer l'API

    return added, ignored, errors


# ─── Rapport de scan ──────────────────────────────────────────────────────────

def print_report(devices: list, added: int, ignored: int, errors: int):
    """Affiche un résumé du scan dans la console."""
    print("\n" + "=" * 60)
    print("  NetMapper — Rapport de Scan Réseau")
    print("=" * 60)
    print(f"  Équipements actifs détectés : {len(devices)}")
    print(f"  Nouveaux ajoutés en base    : {added}")
    print(f"  Déjà présents (ignorés)     : {ignored}")
    print(f"  Erreurs d'envoi API         : {errors}")
    print("-" * 60)

    if devices:
        print(f"  {'IP':<16} {'Hostname':<25} {'Type':<22} {'MAC':<18} Ports")
        print(f"  {'-'*16} {'-'*25} {'-'*22} {'-'*18} -----")
        for d in sorted(devices, key=lambda x: x["ip"]):
            ports_str = ",".join(str(p) for p in d["ports"]) if d["ports"] else "—"
            hostname  = (d["hostname"][:23] + "..") if len(d["hostname"]) > 25 else d["hostname"]
            print(f"  {d['ip']:<16} {hostname:<25} {d['type']:<22} {d['mac'] or '—':<18} {ports_str}")

    print("=" * 60 + "\n")


def save_json_report(devices: list, output_file: str = "scan_result.json"):
    """Sauvegarde le résultat du scan en JSON."""
    report = {
        "scanner"   : "NetMapper v1.0",
        "timestamp" : datetime.now().isoformat(),
        "total"     : len(devices),
        "devices"   : devices,
    }
    with open(output_file, "w", encoding="utf-8") as f:
        json.dump(report, f, ensure_ascii=False, indent=2)
    log.info(f"Rapport JSON sauvegardé → {output_file}")


# ─── Point d'entrée ───────────────────────────────────────────────────────────

def main():
    import argparse

    parser = argparse.ArgumentParser(
        description="NetMapper Scanner — Découverte réseau automatique"
    )
    parser.add_argument(
        "network",
        nargs="?",
        default="192.168.1.0/24",
        help="Réseau CIDR à scanner (défaut: 192.168.1.0/24)"
    )
    parser.add_argument(
        "--no-push",
        action="store_true",
        help="Scanner sans envoyer vers l'API backend"
    )
    parser.add_argument(
        "--save-json",
        metavar="FILE",
        help="Sauvegarder le résultat en JSON (ex: scan.json)"
    )
    parser.add_argument(
        "--ports",
        nargs="+",
        type=int,
        default=None,
        help="Ports à scanner (ex: --ports 22 80 443)"
    )
    args = parser.parse_args()

    # Override des ports si précisés
    global COMMON_PORTS
    if args.ports:
        COMMON_PORTS = args.ports

    print(f"\n{'='*60}")
    print(f"  NetMapper Scanner — SMS Informatique")
    print(f"  Réseau cible : {args.network}")
    print(f"  Ports scannés : {COMMON_PORTS}")
    print(f"  API Backend  : {API_BASE_URL}")
    print(f"{'='*60}\n")

    start = time.time()

    # 1. Scan du réseau
    devices = scan_network(args.network)

    # 2. Sauvegarde JSON optionnelle
    if args.save_json:
        save_json_report(devices, args.save_json)

    # 3. Push vers l'API backend
    added = ignored = errors = 0
    if not args.no_push and devices:
        log.info("Envoi des équipements vers l'API backend...")
        added, ignored, errors = push_to_api(devices)
    elif args.no_push:
        log.info("Mode --no-push : aucun envoi vers l'API")

    # 4. Rapport final
    elapsed = time.time() - start
    print_report(devices, added, ignored, errors)
    log.info(f"Durée totale du scan : {elapsed:.1f}s")


if __name__ == "__main__":
    main()