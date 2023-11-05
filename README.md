# PCAP Parser

## Build

Pour build des fichiers class.
Commande: `make`

Pour build un fichier jar (recommandé).
Commande: `make jar`

## Run

### 1) Développement

Pour les tests: `make run` ou `make runjar`.
Il y a la possibilité de préciser le pcap à charger avec la variable PCAP

Exemple: `make run PCAP=res/http.pcap`

### 2) Production

Commande: `java -jar out/main.jar <pcap files>`

Syntaxe:

Pour afficher des informations à propos des PCAP:

`main.jar <file1.pcap> ... <filen.pcap>`

Pour afficher les flux TCP, utiliser l'option -f ou --follow-tcp-stream

`main.jar -f <file1.pcap> ... <filen.pcap>`

## Protocoles supportés

- Ethernet
- ARP
- IPv4
- IPv6
- ICMP
- TCP
- UDP
- HTTP
- DNS
- DHCP

Je n'ai pas pu mettre en place le support de QUIC cependant j'avais préparé le suivi des flux UDP afin de catégoriser les protocole selon les ports. 
Ainsi pour le protocole QUIC court, je peux savoir qu'il y a eu un QUIC long avant.

## Architecture

Le projet est construit avec une démarche d'objet parent-enfant. Les protocoles des couches du dessous contiennent ceux de la couche du dessus

```
PCAP -> PCAPRecord -> LinkLayer -> EthernetProtocol -> IpProtocol -> ApplicationProtocol
                          |               |                 |                  |
                      (Ethernet)    (IPv4,IPv6,ARP)    (ICMP,TCP,UDP)    (DNS,HTTP,DHCP)

```
Pour la catégorisation par couche il y a des interfaces:
IPv4 et IPv6 implémentent INetworkLayerProtocol
TCP et UDP implémentent ITransportLayerProtocol


## Difficultés

Le protocole DNS est compliqué à comprendre. J'ai commencé à lire les RFC de QUIC mais elles sont également assez complexes.






