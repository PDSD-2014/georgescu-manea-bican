
#########################################################################################################
##   Proiect PDSD - RendezView
##		Bican Daniel Traian - 342C1
##		Manea Valentina - 343C1
##		Georgescu Bianca Elena - 343C1
##
##
#########################################################################################################

UTILIZARE APLICATIE

La intrarea in aplicatie se afiseaza pagina de login. User-ul trebuie sa introduca email-ul si parola
pe care le-a folosit la crearea contului. Credentialele furnizate sunt trimise catre server care face 
validarea lor si trimite, dupa caz mesaj inapoi user-ului (user inexistent, parola incorecta sau se 
aproba autentificarea).

Daca user-ul nu are un cont isi poate crea unul, accesand "New to RendezView? Register here!". Va trebui
sa furnizeze in pagina de register nume, prenume, email, parola( de 2 ori ). Se verifica daca email-ul 
are structura corecta si daca cele 2 campuri ce contin parola contin acelasi sir de caractere. In caz 
contrar se atentioneaza user-ul. Daca se furnizeaza date corecte se trimite cerere server-ului care va
face inregistrarea noului user, trimitand acestuia inapoi un id unic. Daca user-ul deja exista inregistrat
se va primi atentionare.

Aplicatia este formata din 4 fragmente:

MAP

In acest fragment va fi afisata in permanenta harta si va fi locul unde se vor plasa, la cerere markere
pentru a urmari locatia prietenilor.


ADD

Este fragmentul in care se poate face adaugarea de noi prieteni. La introducerea de text in zona marcata
cu "Enter name" se va afisa o lista cu toti userii ce au un cont creat pe server si al caror nume incepe
cu caracterele tastate pana ina cel moment. Se alege o persoana din cele sugerate si prin apasarea 
butonului "Add Friend" se adauga in lista de prieteni, iar pe ecran apare confirmarea: "<NAME> has been
added as your friend!". Lista de useri este furnizata utilizatorului curent de catre server in urma unei
cereri.
In urma adaugarii unui prieteni se activeaza si celelalte butoane din fragment: "Locate friend" si 
"Friends List".
Daca se incearca apasarea butonului Add friend pentru a doua oara pentru aceeasi persoana apare un mesaj
de atentionare: " <NAME> is already your friend!"

Butonul "Locate friend" activeaza localizarea pe harta a noului prieten. Se va deschide automat fragmentul
MAP unde se va plasa un marker cu numele prietenului.

Butonul "Friend List" ne muta in fragmentul FRIENDS


FRIENDS

In acest fragment avem listati toti prietenii adaugati, impreuna cu butoanele "Delete" - pentru stergerea
din lista de prieteni si "Locate" pentu localizarea pe harta. La apasarea butonului "Locate" acesta isi 
schimba label-ul in "Unlocate" si are actiunea inversa. 
Ca sa vizualizam markerele me mutam in fragmentul MAP.

MEET

Este fragmentul in care se pot seta intalniri cu prietenii. In zona marcata "Enter location" se seteaza 
locatia.
La "Enter name" se seteaza numele unui prieten care va urma sa primeasca invitatie la intalnire. La 
adaugarea unui prieten (apasand "Add ettendee") pe ecran apare mesaj cu lista curenta de invitati sub 
forma:
"Attendees: <NUME1>, <NUME2>, ..., <NUMEn>". Daca se incearca adaugarea unui acelasi prieten de doua ori
ca invitat apare un mesaj de atentionare: "<NUME> is already marked to join the meeting!".
Celelalte butoane existente ("Send invitation", "See meeting", "Cancel meeting") sunt dezactivate.
La apasarea butonului "Add attendee" se activeaza butonul "Send invitation".
La apasarea butonului "Send invitation" se activeaza si celelalte doua butoane si toti participantii 
sunt marcati ca urmand sa fie localizati pe harta. Daca nu s-a introdus nicio locatie apare un mesaj 
de atentionare "Choose a location!", iar daca locatia nu este una valida, utilizatorul primeste din nou
atentionare. Momentan nu exista o modalitate de alegere a unei locatii valide, deci nu se pot trimite
invitatii.
La apasarea butonului SeeMeeting se muta focusul in fragmentul MAP. 

######################################################################################################

SERVER

Formatul cererilor(implementate si testate in totalitate):

* primul numar dintr-un mesaj reprezinta tipul mesajului
* fiecare mesaj trebuie sa se termine cu \n !!!!!

1. client -> server cand isi anunta pozitia

format: 1 id_client latitudine longitudine\n

2. client -> server când întreaba de un prieten

format: 2 id_prieten\n

raspuns server:
* 2 id_prieten latitudine longitudine\n

3. client -> server logout

format: 3 id_client\n

4. client -> server login

format: 4 email parola\n

raspunsul serverului:
* 4 0  id_propriu nume prenume - login reusit\n
* 4 1 - user inexistent\n
* 4 2 - parola incorecta\n
			
5. client -> server register

format: 5 nume prenume email parola\n

raspuns server register:
* 5 0 id_client - ok\n
* 5 1 - user deja existent\n

6. client -> server lista useri

format: 6 id_propriu\n

raspuns server:
* 6 nr_clien?i id1 nume1 prenume1  id2 nume2 prenume2 ..\n

7. client->server seteaza meeting

a. un client vrea sa initieze un meeting nou
format: 7 0 id_initiator nume_locatie lat_locatie long_locatie numar_invitati id_invitat1 id_invitat_2 ...id_invitat_n\n

b. fiecare meeting va avea un id; serverul îi raspunde clientului cu id-ul meeting-ului
format: 7 1 id_meeting\n

c. fiecare client va trebui sa întrebe periodic serverul daca sunt invita?ii pending pentru el
formatul mesajului de client la server, de interogare: 7 2 id_client\n

d. serverul îi raspunde clientului care întreaba daca sunt invita?ii pending pentru el
raspuns: (trebuie serverul sa trimita locatia si id-urile participantilor fiecarui invitat)
* 7 3 nume_locatie lat_locatie long_locatie nr_participanti id_initiator id_invitat1 … id_invitat_n\n
daca nu sunt invitatii pending pentru acel client:
* 7 4
nr_participanti = nr_invitati + 1 (initiatorul)

d. când meetingul ia final, initiatorul trebuie sa anunte serverul
* 7 5 id_meeting\n 

##########################################################################################################

BAZA DE DATE SERVER

Campuri:

	id
	nume
	prenume
	email
	parola
	lat
	long
	logat  (0/1, ca sa stim daca e sau nu online, se pune pe 1 la login, pe 0 la logout)


#########################################################################################################