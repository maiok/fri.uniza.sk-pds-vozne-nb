/*funkcia na vypocet aktualnej ceny vozna*/
create or replace function func_aktCena (cena number, rok number, odpis number) return number
is
aktCena number;
begin
    aktCena := cena;
    for i in 1..rok
    loop 
        aktCena := aktCena * ((100-odpis)/100);
    end loop;
    return aktCena;
end func_aktCena;
/



/*1. 
zobrazenie aktuálneho stavu vozňov podľa zadaných kritérií ( podľa typu vozňa, vlastníka, doby
nadobudnutia, aktuálnej ceny spolu s prepočtom celkovej ceny vozňa podľa odpisov, */
/*do wheru len pridat podmienku*/
select id_vozna, typ_vozna, vlastnik.nazov_spolocnosti, vyrobca.nazov_spolocnosti, nazov_stanice, vozen.datum_nadobudnutia
from vozen
join typ_vozna using(id_typu_vozna)
join spolocnost vlastnik on (vozen.id_vlastnika = vlastnik.id_spolocnosti)
join spolocnost vyrobca on (vozen.id_vyrobcu = vyrobca.id_spolocnosti)
join stanica on (vozen.id_domovskej_stanice = stanica.id_stanice)
where {podmienka};

/*podla ceny*/
create or replace procedure proc1 (cena_min number, cena_max number, proc out sys_refcursor)
is
begin
open proc for
    select id_vozna,odpis_percenta, cena,
    func_aktCena(cena, FLOOR(months_between(sysdate,datum_nadobudnutia)/12), odpis_percenta) 
    from vozen join typ_vozna using(id_typu_vozna)
    where func_aktCena(cena, FLOOR(months_between(sysdate,datum_nadobudnutia)/12), odpis_percenta) <= cena_max 
    and func_aktCena(cena, FLOOR(months_between(sysdate,datum_nadobudnutia)/12), odpis_percenta) >= cena_min;
end;
/
declare
l_cursor  SYS_REFCURSOR;
begin
    execute proc1(1000, 10000000, l_cursor);
end;
/



/*2. 
zoznam priradeného vozňa konkrétnej železničnej stanici v zadanom čase podľa zadaných
kritérií (pozor, jedna stanica môže byť domovskou stanicou viacerých vozňov rôznom v čase) –
vypíšte aj informáciu o stave majetku, */

select id_vozna from vozen_stanica
join stanica using(id_stanice)
where nazov_stanice = {nazov_stanice}
and v_stanici_od >= {dat_min} or v_stanici_do <= {dat_max};


create or replace function func_stavMajetku(nazov_stanice VARCHAR2, dat_od Date, dat_do Date)
return number
is
cursor cur1 is
    select id_vozna, cena, FLOOR(months_between(sysdate,datum_nadobudnutia)/12) as rok, odpis_percenta from vozen_stanica
    join stanica using(id_stanice)
    join vozen using(id_vozna)
    join typ_vozna using(id_typu_vozna)
    where stanica.nazov_stanice = nazov_stanice
    and v_stanici_od >= dat_od or v_stanici_do <= dat_do;
suma number;
begin
    suma := 0;
    for i in cur1
    loop
        if cur1%FOUND then
            suma := suma+func_aktCena(i.cena, i.rok, i.odpis_percenta);
        end if;
    end loop;
    return suma;
end func_stavMajetku;
/


/*3
vypíšte štatistiky o jednotlivých typoch vozňov podľa zadaných kritérií*/

select id_typu_vozna, count(*) from vozen
group by id_typu_vozna
order by id_typu_vozna;

select id_typu_vozna, count(*), vlastnik.nazov_spolocnosti from vozen
join spolocnost vlastnik on (vlastnik.id_spolocnosti = vozen.id_vlastnika)
where vlastnik.nazov_spolocnosti = {podmienka}
group by vlastnik.nazov_spolocnosti, id_typu_vozna;

/* 4.
vypíšte životný cyklus vozňa -históriu od nadobudnutia, cez informácie o opravách, údržbe až
po vyradenie, 
*/

select XMLROOT(
XMLELEMENT("VOZEN",
    XMLELEMENT("Informacie_o_vozni",
    XMLFOREST(ID_VOZNA as "id_vozna",
              vz.ID_TYPU_VOZNA as "typ_vozna",
              vz.ID_VLASTNIKA as "vlastnik_vozna",
              vz.ID_VYROBCU as "vyrobca_vozna",
              vz.ID_DOMOVSKEJ_STANICE as "domovska_stanica",
              vz.DATUM_NADOBUDNUTIA as "zakupeny")
    ),XMLELEMENT("KONTORLY",
    XMLAGG(
    XMLELEMENT("KONTROLA", XMLATTRIBUTES(id_kontroly as "id"),
    XMLFOREST(kontrola.ID_TYPU_KONTROLY as "id_typu_kontroly",
              kontrola.POPIS_KONTROLY as "popis_kontroly"
              ))
    )
    ),XMLELEMENT("OPRAVY",
              XMLAGG(
              XMLELEMENT("OPRAVA",XMLATTRIBUTES(id_opravy as "id"),
              XMLFOREST(ID_KONTROLY as "id_kontroly",
                        oprava.ID_TYPU_OPRAVY as "id_typu_opravy",
                        oprava.POPIS_OPRAVY as "popis_opravy"
              ))
              )),
              XMLELEMENT("VYRADENIE", XMLFOREST(
              datum_vyradenia as "datum_vyradenia",
              vyradeny_vozen.POPIS_VYRADENIA as "popis_vyradenia"))
              ),version '1.0') as XML
from vozen vz
left join kontrola using(id_vozna)
LEFT join oprava using(id_kontroly)
left join vyradeny_vozen using(id_vozna)
where id_vozna = {podmienka}
group by ID_VOZNA,ID_TYPU_VOZNA,ID_VLASTNIKA,ID_VYROBCU,ID_DOMOVSKEJ_STANICE,DATUM_NADOBUDNUTIA,datum_vyradenia,POPIS_VYRADENIA;

/* 5
vypíšte štatistiku nákladov spojených s konkrétnym vozňom alebo za skupiny podľa rôznych
kritérií
*/
select id_vozna, to_char(oprava_od, 'MM') as mesiac, sum(cena_suciastky) as cena 
from vozen
join kontrola using(id_vozna)
join oprava using (id_kontroly)
join oprava_suciastka using(id_opravy)
join suciastka using(id_suciastky)
where id_vozna = {podmienka}
group by id_vozna, to_char(oprava_od, 'MM')
order by id_vozna, to_char(oprava_od, 'MM');

select id_vozna,to_char(oprava_od, 'MM') as mesiac, sum(cena_opravy) as cena 
from vozen
join kontrola using(id_vozna)
join oprava using (id_kontroly)
join oprava_suciastka using(id_opravy)
join typ_opravy using(id_typu_opravy)
where id_vozna = {podmienka}
group by id_vozna, to_char(oprava_od, 'MM')
order by id_vozna, to_char(oprava_od, 'MM');

Select id_vozna,to_char(oprava_od, 'MM') as mesiac, sum(cena_kontroly) as cena
from vozen
join kontrola using(id_vozna)
join typ_kontroly using(id_typu_kontroly)
where id_vozna = {podmienka}
group by id_vozna, to_char(oprava_od, 'MM')
order by id_vozna, to_char(oprava_od, 'MM');

/* 6
vypíšte vývoj nákladov na prevádzku jednotlivých typov vozňov za zadané obdobie a zadanou
presnosťou (mesačne, polročne, ročne)
*/
select id_typu_vozna,to_char(oprava_od, 'MM') as mesiac, sum(cena_suciastky) as cena 
from vozen
join kontrola using(id_vozna)
join oprava using (id_kontroly)
join oprava_suciastka using(id_opravy)
join suciastka using(id_suciastky)
group by id_typu_vozna,to_char(oprava_od, 'MM')
order by id_typu_vozna,to_char(oprava_od, 'MM');

select id_typu_vozna,to_char(oprava_od, 'MM') as mesiac, sum(cena_opravy) as cena 
from vozen
join kontrola using(id_vozna)
join oprava using (id_kontroly)
join oprava_suciastka using(id_opravy)
join typ_opravy using(id_typu_opravy)
group by id_typu_vozna,to_char(oprava_od, 'MM')
order by id_typu_vozna,to_char(oprava_od, 'MM');

Select id_typu_vozna,to_char(oprava_od, 'MM') as mesiac, sum(cena_kontroly) as cena
from vozen
join kontrola using(id_vozna)
join oprava using (id_kontroly)
join oprava_suciastka using(id_opravy)
join typ_kontroly using(id_typu_kontroly)
group by id_typu_vozna,to_char(oprava_od, 'MM')
order by id_typu_vozna,to_char(oprava_od, 'MM');

/* 7.
výpis troch najporuchovejších vozňov v definovaných kategóriách (typ vozňa, vlastník,
výrobca, porucha, obdobie ...), */
select * from(
select rank() over(order by count(id_opravy) desc) as poradie, count(id_opravy),
id_vozna
from kontrola
join oprava using(id_kontroly)
join vozen using(id_vozna)
where id_typu_vozna = {podmienka}
group by id_vozna
) where poradie <=3;

select * from(
select rank() over(order by count(id_opravy) desc) as poradie, count(id_opravy),
id_vozna
from kontrola
join oprava using(id_kontroly)
join vozen using(id_vozna)
where id_vlastnika = {podmienka}
group by id_vozna
) where poradie <=3;

select * from(
select rank() over(order by count(id_opravy) desc) as poradie, count(id_opravy),
id_vozna
from kontrola
join oprava using(id_kontroly)
join vozen using(id_vozna)
where id_vyrobcu = {podmienka}
group by id_vozna
) where poradie <=3;

select * from(
select rank() over(order by count(id_opravy) desc) as poradie, count(id_opravy),
id_vozna
from kontrola
join oprava using(id_kontroly)
where id_typu_opravy = {podmienka}
group by id_vozna
) where poradie <=3;

select * from(
select rank() over(order by count(id_opravy) desc) as poradie, count(id_opravy),
id_vozna
from kontrola
join oprava using(id_kontroly)
using oprava_suciastka(id_opravy)
where oprava_od <= {podmienka} and oprava_do >= {podmienka}
group by id_vozna
) where poradie <=3;

/* 8
výpis vozňov pre každú organizáciu, ktoré ani po 5tich rokoch prevádzky nepotrebovali žiaden
servis (napr. náhradný diel, opravu)
*/

select v.id_vozna, v.id_vlastnika
from vozen v
join kontrola k on (v.id_vozna = k.id_vozna)
join kontrola_zamestnanec kz on (k.id_kontroly = kz.id_kontroly)
where kz.kontrola_od >= v.datum_nadobudnutia 
and kz.kontrola_od <= add_months(v.datum_nadobudnutia,60) 
and not exists (
    select 'x' from oprava o
    where o.id_kontroly = k.id_kontroly
)
group by v.id_vlastnika, v.id_vozna;


/* 9
výpis vozňov, ktoré musia prejsť v nasledujúcom období (obdobie definované parametrom,
napr. 1 mesiac, 3 mesiace, rok, ...) servisnou kontrolou,
*/

Select id_vozna
from vozen 
join kontrola using(id_vozna)
join kontrola_zamestnanec using(id_kontroly)
where kontrola_od >= sysdate and kontrola_od <= add_months(sysdate,12);

/* 10
ku každému vozňu vypísať celkovú cenu opráv, ktoré boli vykonané počas opravy. 
*/
Select id_vozna, sum(cena_suciastky) as suma_za_opravy
from kontrola
left join oprava using (id_kontroly)
left join oprava_suciastka using(id_opravy)
left join suciastka using(id_suciastky)
left join typ_suciastky using(id_typu_suciastky)
group by id_vozna
order by id_vozna;

select id_vozna, sum(cena_opravy) from kontrola 
left join oprava using (id_kontroly)
left join typ_opravy using(id_typu_opravy)
group by id_vozna
order by id_vozna;

Select id_vozna, sum(cena_kontroly) as cena
from vozen
join kontrola using(id_vozna)
join typ_kontroly using(id_typu_kontroly)
group by id_vozna
order by id_vozna;

/*11. 
vyhľadávanie predražených komponentov v definovanom období – porovnanie cien
totožných, resp. porovnateľných komponentov, ktoré kúpili jednotlivé organizácie v totožnom
období: napr. CARGO kupovalo (totožné) nápravy za polovičnú cenu ako FIRMA1 v roku 2016*/
create or replace function func11(vlastnikVozna Number, datumZac DATE, datumKon DATE) return VARCHAR2
is
cursor suciastka1 is select * from oprava_suciastka
    join suciastka using(id_suciastky)
    join oprava using(id_opravy)
    join kontrola using(id_kontroly)
    join vozen using(id_vozna)
    join spolocnost on(spolocnost.ID_SPOLOCNOSTI = suciastka.ID_DODAVATELA)
    join typ_suciastky using(id_typu_suciastky)
    where id_vlastnika = vlastnikVozna
    and oprava_od between datumZac and datumKon;
cursor suciastka2 is select * from oprava_suciastka
    join suciastka using(id_suciastky)
    join oprava using(id_opravy)
    join kontrola using(id_kontroly)
    join vozen using(id_vozna)
    join spolocnost on(spolocnost.ID_SPOLOCNOSTI = suciastka.ID_DODAVATELA)
    join typ_suciastky using(id_typu_suciastky)
    where oprava_od between datumZac and datumKon;
vystup varchar2(32767);
begin
    vystup := '';
    for i in suciastka1
    loop
        for j in suciastka2
        loop
            if (i.id_dodavatela <> j.id_dodavatela and i.cena_suciastky > j.cena_suciastky) then
                    vystup := (vystup ||'Nazov suciastky: '|| i.typ_suciastky ||'    Povodny dodavatel: '|| i.nazov_spolocnosti ||'     Povodna Cena: '|| i.cena_suciastky)||'    Lepsi Dodavatel: '|| j.nazov_spolocnosti ||'   Lepsia cena: '|| j.cena_suciastky|| chr(10);
                    exit;
            end if;
        end loop;
    end loop;
    return vystup;
end func11;
/

/*12.
výpis vozňov na vyradenie po jednotlivých organizáciách (dôvod vyradenia: vek, opotrebenie,
poškodenie, ...),*/
select * from VYRADENY_VOZEN
join vozen using(id_vozna)
join spolocnost m on(vozen.id_vlastnika = m.id_spolocnosti)
where nazov_spolocnosti = {podmienka}
and popis_vyradenia = {podmienka};

/* 13.
sledovanie počtu vozňov (podľa rôznych kritérií) v definovanej organizácii čase),
 */
select id_vlastnika, count(id_vozna)
from vozen
where datum_nadobudnutia >= to_date('1.1.2000','DD/MM/YYYY')
and not exists (
select 'x' from vyradeny_vozen
where vyradeny_vozen.id_vozna = vozen.id_vozna
)
group by id_vlastnika
order by id_vlastnika;

/* 14.
výpis domovskej stanice, na ktorú je najviac vozňov (po jednotlivých organizáciách a podľa
hodnoty),
 */
select id_vlastnika, id_domovskej_stanice, count(id_vozna)
from vozen
group by id_vlastnika, id_domovskej_stanice
having count(id_vozna)=(
    select max(count(id_vozna)) from vozen group by id_vlastnika, id_domovskej_stanice
);

select * from(
select rank() over(order by sum(func_aktCena(cena, FLOOR(months_between(sysdate,datum_nadobudnutia)/12), odpis_percenta)) desc) as poradie, id_vlastnika, id_domovskej_stanice,  sum(func_aktCena(cena, FLOOR(months_between(sysdate,datum_nadobudnutia)/12), odpis_percenta))
from vozen
join typ_vozna using(id_typu_vozna)
group by id_vlastnika, id_domovskej_stanice)
where poradie <= 1;


/* 15.
evidencia výkonov zamestnancov podľa rôznych kritérií */
select rod_cislo, meno, priezvisko, count(id_opravy) 
from zamestnanec
join osoba using(rod_cislo)
join oprava_suciastka using(id_zamestnanca)
group by rod_cislo, meno, priezvisko
order by count(id_opravy) desc;

select rod_cislo, meno, priezvisko, count(id_opravy) 
from zamestnanec
join osoba using(rod_cislo)
join oprava_suciastka using(id_zamestnanca)
where id_spolocnosti = {podmienka}
group by rod_cislo, meno, priezvisko
order by count(id_opravy) desc;

select rod_cislo, meno, priezvisko, count(id_opravy) 
from zamestnanec
join osoba using(rod_cislo)
join oprava_suciastka using(id_zamestnanca)
join oprava using(id_opravy)
join kontrola using(id_kontroly)
join vozen using(id_vozna)
where it_typu_vozna = {podmienka}
group by rod_cislo, meno, priezvisko
order by count(id_opravy) desc;