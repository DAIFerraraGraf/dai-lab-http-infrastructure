--
-- Database: gestion_decheterie
-- Ferrara Justin, Graf Andrea, Hussain Lucas
-- GestionDecheterie.sql
-- Script complet de création de la base de données
--

------------------------------------------------------------------------------------------------------------------------
-- Crétion des tables
------------------------------------------------------------------------------------------------------------------------
CREATE SCHEMA gestion_decheterie;
SET search_path TO gestion_decheterie;

CREATE TABLE employe (
    idLogin VARCHAR(30),
    mdpLogin VARCHAR(30) NOT NULL,
    nom VARCHAR(30) NOT NULL,
    prenom VARCHAR(30) NOT NULL,
    dateNaissance DATE NOT NULL,
    dateDebutContrat DATE NOT NULL,
    numTelephone VARCHAR(30),
    typePermis VARCHAR(30),
    FK_adresse INTEGER,
    FK_decheterie INTEGER NOT NULL,
    FK_fonction VARCHAR(30) NOT NULL,
    PRIMARY KEY (idLogin)
);

CREATE TABLE adresse (
    id INTEGER,
    rue VARCHAR(30) NOT NULL,
    numero VARCHAR(10) NOT NULL,
    NPA VARCHAR(10) NOT NULL,
    nomVille VARCHAR(30) NOT NULL,
    pays VARCHAR(30) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE decheterie (
    id INTEGER,
    nom VARCHAR(30) NOT NULL,
    FK_adresse INTEGER NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE vehicule (
    immatriculation VARCHAR(30),
    type VARCHAR(30) NOT NULL,
    remorque BOOLEAN NOT NULL,
    anneeFabrication VARCHAR(10),
    dateExpertise DATE,
    consoCarburant DOUBLE PRECISION,
    FK_decheterie INTEGER NOT NULL,
    PRIMARY KEY (immatriculation)
);

CREATE TABLE contenant (
    id INTEGER,
    nom VARCHAR(30) NOT NULL,
    capaciteMax INTEGER,
    nbCadre INTEGER,
    taille VARCHAR(10),
    couleur VARCHAR(30),
    FK_decheterie INTEGER NOT NULL,
    FK_dechet VARCHAR(30),
    PRIMARY KEY (id)
);

CREATE TABLE dechet (
    type VARCHAR(30),
    PRIMARY KEY (type)
);

CREATE TABLE fonction (
    nom VARCHAR(30),
    PRIMARY KEY (nom)
);

CREATE TABLE status (
    nom VARCHAR(10),
    PRIMARY KEY (nom)
);

CREATE TABLE ramassage (
    id INTEGER,
    date DATE NOT NULL,
    poids DOUBLE PRECISION,
    FK_contenant INTEGER NOT NULL,
    FK_employee VARCHAR(30),
    FK_decheterie INTEGER NOT NULL,
    FK_vehicule VARCHAR(30),
    FK_status varchar(10) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE principale (
    FK_principale INTEGER,
    FK_decheterie INTEGER,
    PRIMARY KEY (FK_principale, FK_decheterie)
);

CREATE TABLE superviseur (
    FK_employee VARCHAR(30),
    FK_superviseur VARCHAR(30),
    PRIMARY KEY (FK_employee, FK_superviseur)
);

ALTER TABLE employe
ADD CONSTRAINT fk_employe_adresse
    FOREIGN KEY (FK_adresse) REFERENCES adresse(id),
ADD CONSTRAINT fk_employe_decheterie
    FOREIGN KEY (FK_decheterie) REFERENCES decheterie(id),
ADD CONSTRAINT fk_employe_fonction
    FOREIGN KEY (FK_fonction) REFERENCES fonction(nom);

ALTER TABLE decheterie
ADD CONSTRAINT fk_decheterie_adresse
    FOREIGN KEY (FK_adresse) REFERENCES adresse(id);

ALTER TABLE vehicule
ADD CONSTRAINT fk_vehicule_decheterie
    FOREIGN KEY (FK_decheterie) REFERENCES decheterie(id);

ALTER TABLE contenant
ADD CONSTRAINT fk_contenant_decheterie
    FOREIGN KEY (FK_decheterie) REFERENCES decheterie(id),
ADD CONSTRAINT fk_contenant_dechet
    FOREIGN KEY (FK_dechet) REFERENCES dechet(type);

ALTER TABLE ramassage
ADD CONSTRAINT fk_ramassage_contenant
    FOREIGN KEY (FK_contenant) REFERENCES contenant(id),
ADD CONSTRAINT fk_ramassage_employee
    FOREIGN KEY (FK_employee) REFERENCES employe(idLogin) ON DELETE SET NULL,
ADD CONSTRAINT fk_ramassage_decheterie
    FOREIGN KEY (FK_decheterie) REFERENCES decheterie(id),
ADD CONSTRAINT fk_ramassage_vehicule
    FOREIGN KEY (FK_vehicule) REFERENCES vehicule(immatriculation);


ALTER TABLE principale
ADD CONSTRAINT fk_principale_decheterie
    FOREIGN KEY (FK_decheterie) REFERENCES decheterie(id),
ADD CONSTRAINT fk_principale_principale
    FOREIGN KEY (FK_principale) REFERENCES decheterie(id);

ALTER TABLE superviseur
ADD CONSTRAINT fk_superviseur_employee
    FOREIGN KEY (FK_employee) REFERENCES employe(idLogin) ON DELETE CASCADE,
ADD CONSTRAINT fk_superviseur_superviseur
    FOREIGN KEY (FK_superviseur) REFERENCES employe(idLogin) ON DELETE CASCADE;



------------------------------------------------------------------------------------------------------------------------
-- TRIGGERS
------------------------------------------------------------------------------------------------------------------------

-----------------------------------------------------------
-- Vehicule
-- Seule une déchèterie principale peut posséder des véhicules
CREATE OR REPLACE FUNCTION check_vehicule_decheterie_principale()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.FK_decheterie NOT IN (SELECT FK_principale FROM principale) THEN
        RAISE EXCEPTION 'A vehicle can only be assigned to a principal waste disposal site';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER vehicule_decheterie_principale_trigger
BEFORE INSERT OR UPDATE ON vehicule
FOR EACH ROW EXECUTE PROCEDURE check_vehicule_decheterie_principale();

-- Seul une camionnette peut avoir ou non remorque
CREATE OR REPLACE FUNCTION check_remorque_camionnette()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.type NOT IN ('camionnette') AND NEW.remorque IS NOT FALSE THEN
        RAISE EXCEPTION 'Seul une camionnette peut avoir une remorque.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER remorque_camionnette_trigger
BEFORE INSERT OR UPDATE ON vehicule
FOR EACH ROW EXECUTE PROCEDURE check_remorque_camionnette();

-----------------------------------------------------------
-- Ramassage
-- le status ne peut être que accepté, refusé ou en attente
CREATE OR REPLACE FUNCTION check_status()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.fk_status NOT IN ('accepté', 'refusé', 'en attente') THEN
        RAISE EXCEPTION 'Le status ne peut être que accepté, refusé ou en attente';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER status_trigger
BEFORE INSERT OR UPDATE ON ramassage
FOR EACH ROW EXECUTE PROCEDURE check_status();

-- Un camion ne peut transporter que des bennes et une camionnette peut transporter autre chose que des bennes
CREATE OR REPLACE FUNCTION check_type_transport()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.FK_contenant IN (SELECT id FROM contenant WHERE nom = 'benne') AND
       EXISTS (SELECT 1 FROM vehicule WHERE type = 'camionnette' AND immatriculation = NEW.FK_vehicule) THEN
        RAISE EXCEPTION 'Camionettes cannot transport bennes';
    ELSIF NEW.FK_contenant IN (SELECT id FROM contenant WHERE nom <> 'benne') AND
          EXISTS (SELECT 1 FROM vehicule WHERE type = 'camion' AND immatriculation = NEW.FK_vehicule) THEN
        RAISE EXCEPTION 'Camions can only transport bennes';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER type_transport_trigger
BEFORE INSERT OR UPDATE ON ramassage
FOR EACH ROW EXECUTE PROCEDURE check_type_transport();

-- Seul un chauffeur peut être affecté à un ramassage
-- Un chauffeur de camionnette doit avoir un permis B ou C
-- Un chauffeur de camion doit avoir un permis C
CREATE OR REPLACE FUNCTION check_chauffeur_ramassage()
RETURNS TRIGGER AS $$
DECLARE
    _fonction employe.fk_fonction%TYPE;
    _typepermis employe.typepermis%TYPE;
    _typevehicule vehicule.type%TYPE;
BEGIN
    IF NEW.FK_employee IS NULL THEN
        RETURN NEW; -- Si la nouvelle valeur est NULL, ne pas appliquer les vérifications de permis
    END IF;

    SELECT fk_fonction, typepermis, type
    FROM employe
        INNER JOIN vehicule ON vehicule.immatriculation = NEW.FK_vehicule
    WHERE employe.idlogin = NEW.FK_employee
    INTO _fonction, _typepermis, _typevehicule;

    IF _fonction IS NULL THEN
        RAISE EXCEPTION 'Le chauffeur n''existe pas. FK_employee = %', NEW.FK_employee;
    ELSEIF _typepermis IS NULL THEN
        RAISE EXCEPTION 'Le chauffeur n''a pas de permis.';
    ELSEIF _typevehicule IS NULL THEN
        RAISE EXCEPTION 'Le chauffeur n''a pas de véhicule.';
    END IF;

    IF _fonction NOT IN ('Chauffeur') THEN
        RAISE EXCEPTION 'Seul un chauffeur peut être affecté à un ramassage.';
    END IF;

    IF _typevehicule = 'camionnette' AND _typepermis <> 'B' AND _typepermis <> 'C' THEN
        RAISE EXCEPTION 'Un chauffeur de camionnette doit avoir un permis B minimum.';
    ELSEIF _typevehicule = 'camion' AND _typepermis <> 'C' THEN
        RAISE EXCEPTION 'Un chauffeur de camion doit avoir un permis C.';
    END IF;



    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER chauffeur_ramassage_trigger
BEFORE INSERT OR UPDATE ON ramassage
FOR EACH ROW EXECUTE PROCEDURE check_chauffeur_ramassage();

-- Un ramassage ne peut être affecté qu'à un chauffeur dont la déchèterie principale est déchèterie principale du ramassage
CREATE OR REPLACE FUNCTION check_decheterie_principale_ramassage()
RETURNS TRIGGER AS $$
DECLARE
    _decheterie_principale_vehicule decheterie.id%TYPE;
    _decheterie_principale_employe decheterie.id%TYPE;
    _decheterie_principale_ramassage decheterie.id%TYPE;
BEGIN
    IF NEW.FK_employee IS NULL THEN
        RETURN NEW; -- Si la nouvelle valeur est NULL, ne pas appliquer les vérifications de permis
    END IF;

    SELECT vehicule.fk_decheterie
    FROM vehicule
    WHERE vehicule.immatriculation = NEW.FK_vehicule
    INTO _decheterie_principale_vehicule;

    SELECT employe.fk_decheterie
    FROM employe
        INNER JOIN decheterie ON decheterie.id = employe.FK_decheterie
    WHERE employe.idlogin = NEW.FK_employee
    INTO _decheterie_principale_employe;

    SELECT principale.FK_principale
    FROM principale
    WHERE principale.FK_decheterie = NEW.FK_decheterie OR principale.FK_principale = NEW.FK_decheterie
    INTO _decheterie_principale_ramassage;

    IF _decheterie_principale_vehicule IS NULL THEN
        RAISE EXCEPTION 'Le véhicule n''a pas de déchèterie principale.';
    ELSEIF _decheterie_principale_employe IS NULL THEN
        RAISE EXCEPTION 'Le chauffeur n''a pas de déchèterie principale.';
    ELSEIF _decheterie_principale_ramassage IS NULL THEN
        RAISE EXCEPTION 'Le ramassage n''a pas de déchèterie principale. Fk_decheterie = %', NEW.FK_decheterie;
    END IF;

    IF _decheterie_principale_vehicule <> _decheterie_principale_employe THEN
        RAISE EXCEPTION 'Un ramassage ne peut être affecté qu''à un chauffeur dont la déchèterie principale est la même que celle du véhicule. Fk_vehicule = %, Fk_employee = %', _decheterie_principale_vehicule, _decheterie_principale_employe;
    END IF;

    IF _decheterie_principale_ramassage <> _decheterie_principale_employe THEN
        RAISE EXCEPTION 'Un ramassage ne peut être affecté qu''à un chauffeur dont la déchèterie principale est la même que celle du ramassage. Fk_decheterie = %, Fk_employee = %', _decheterie_principale_ramassage, _decheterie_principale_employe;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER decheterie_principale_ramassage_trigger
BEFORE INSERT OR UPDATE ON ramassage
FOR EACH ROW EXECUTE PROCEDURE check_decheterie_principale_ramassage();

-----------------------------------------------------------
-- Contenant
-- le nom ne peut être que benne, grande caisse, palette ou big bag
CREATE OR REPLACE FUNCTION check_contenant()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.nom NOT IN ('benne', 'grande caisse', 'palette','big bag') THEN
        RAISE EXCEPTION 'le nom ne peut être que benne, grande caisse, palette ou big bag';

    ELSIF NEW.nom = 'benne' AND (NEW.couleur IS NULL OR NEW.capacitemax IS NULL OR NEW.taille IS NOT NULL OR NEW.nbcadre IS NOT NULL) THEN
        RAISE EXCEPTION 'Une benne doit avoir uniquement une couleur et une capacité maximale';

    ELSIF NEW.nom = 'grande caisse' AND (NEW.couleur IS NOT NULL OR NEW.capacitemax IS NULL OR NEW.taille IS NOT NULL OR NEW.nbcadre IS NOT NULL) THEN
        RAISE EXCEPTION 'Une grande caisse doit avoir uniquement une capacité maximale';

    ELSIF NEW.nom = 'palette' AND (NEW.couleur IS NOT NULL OR NEW.capacitemax IS NOT NULL OR NEW.taille IS NOT NULL OR NEW.nbcadre NOT BETWEEN 0 AND 4) THEN
        RAISE EXCEPTION 'Une palette doit avoir uniquement un nombre de cadres';

    ELSIF NEW.nom = 'big bag' AND (NEW.couleur IS NOT NULL OR NEW.capacitemax IS NOT NULL OR NEW.taille NOT IN ('petit', 'moyen', 'grand') OR NEW.nbcadre IS NOT NULL) THEN
        RAISE EXCEPTION 'Un big bag doit avoir uniquement une taille';

    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER contenant_trigger
BEFORE INSERT OR UPDATE ON contenant
FOR EACH ROW EXECUTE PROCEDURE check_contenant();

-----------------------------------------------------------
-- principale
-- Une déchèterie principale ne peut pas être gérée et une déchèterie gérée ne peut pas être principale
CREATE OR REPLACE FUNCTION check_principal_manage()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.FK_decheterie IN (SELECT FK_principale FROM principale) THEN
        RAISE EXCEPTION 'A principal decheterie cannot be managed by another';
    ELSIF NEW.FK_principale IN (SELECT FK_decheterie FROM principale) THEN
        RAISE EXCEPTION 'A managed decheterie cannot be principal';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER principal_manage_trigger
BEFORE INSERT OR UPDATE ON principale
FOR EACH ROW EXECUTE PROCEDURE check_principal_manage();

-----------------------------------------------------------
-- employé
-- La fonction d'un employé ne peut être que :Responsable, Secrétaire, Chauffeur, Employé.
-- Seul une déchèterie principale peut avoir des secrétaires
CREATE OR REPLACE FUNCTION check_fonction_employe()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.FK_fonction NOT IN ('Responsable', 'Secrétaire', 'Chauffeur', 'Employé') THEN
        RAISE EXCEPTION 'La fonction d''un employé ne peut être que :Responsable, Secrétaire, Chauffeur, Employé.';
    END IF;
    IF  NEW.FK_fonction = 'Secrétaire' AND NEW.FK_decheterie NOT IN (SELECT FK_principale FROM principale) THEN
        RAISE EXCEPTION 'Seul une déchèterie principale peut avoir des secrétaires.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER fonction_employe_trigger
BEFORE INSERT OR UPDATE ON employe
FOR EACH ROW EXECUTE PROCEDURE check_fonction_employe();

-- La date de naissance d'un employé ne peut être dans le futur
CREATE OR REPLACE FUNCTION check_date_naissance()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.datenaissance > CURRENT_DATE THEN
        RAISE EXCEPTION 'La date de naissance d''un employé ne peut être dans le futur.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER date_naisance_trigger
BEFORE INSERT OR UPDATE ON employe
FOR EACH ROW EXECUTE PROCEDURE check_date_naissance();

-- Seul un chauffeur peut avoir un type de permis
CREATE OR REPLACE FUNCTION check_permis_chauffeur()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.FK_fonction NOT IN ('Chauffeur') AND NEW.typepermis IS NOT NULL THEN
        RAISE EXCEPTION 'Seul un chauffeur peut avoir un type de permis.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER permis_chauffer_trigger
BEFORE INSERT OR UPDATE ON employe
FOR EACH ROW EXECUTE PROCEDURE check_permis_chauffeur();

-- Le permis d'un chauffeur ne peut être que B ou C
CREATE OR REPLACE FUNCTION check_type_permis_chauffeur()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.typepermis NOT IN ('B', 'C') THEN
        RAISE EXCEPTION 'Le type de permis d''un chauffeur ne peut être que B ou C.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER permis_type_chauffer_trigger
BEFORE INSERT OR UPDATE ON employe
FOR EACH ROW EXECUTE PROCEDURE check_type_permis_chauffeur();

-----------------------------------------------------------
-- superviseur
-- Seul un employé de fonction 'responsable' peut être superviseur
CREATE OR REPLACE FUNCTION check_superviseur_responsable()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT FK_fonction FROM employe WHERE idLogin = NEW.FK_superviseur) <> 'Responsable' THEN
        RAISE EXCEPTION 'Only an employee with the role ''responsable'' can be a supervisor';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER superviseur_responsable_trigger
BEFORE INSERT OR UPDATE ON superviseur
FOR EACH ROW EXECUTE PROCEDURE check_superviseur_responsable();


------------------------------------------------------------------------------------------------------------------------
-- VUES
------------------------------------------------------------------------------------------------------------------------

-----------------------------------------------------------
-- Une déchèterie principale a une vue sur ses déchèteries gérées
CREATE OR REPLACE VIEW decheterie_principale AS
SELECT p.FK_principale AS decheterie_principale_id, d1.nom AS decheterie_principale_nom,
       d2.id AS decheterie_id, d2.nom AS nom_decheterie
FROM principale p
JOIN decheterie d1 ON d1.id = p.FK_principale
JOIN decheterie d2 ON d2.id = p.FK_decheterie;

-----------------------------------------------------------
-- Une secretaire d'une déchèterie a une vue sur TOUS les ramassages passé/présent/future de sa déchèterie
-- ET de tous les employés de sa déchèterie
CREATE OR REPLACE VIEW secretaire_decheterie_employe AS
SELECT d.id AS id_decheterie, d.nom AS nom_decheterie,
       e.idlogin AS id_employe, e.nom AS nom_employe, e.prenom AS prenom_employe, e.FK_fonction AS fonction_employe,
       e.datenaissance AS date_naissance, e.datedebutcontrat AS date_debut_contrat, e.numtelephone AS numero_telephone,
       e.typepermis AS type_permis

    FROM employe e
    JOIN decheterie d ON d.id = e.fk_decheterie;

CREATE OR REPLACE VIEW secretaire_decheterie_ramassage AS
SELECT r.id AS id_ramassage, r.date AS date_ramassage,
       d.id AS id_decheterie, d.nom AS nom_decheterie, r.fk_status AS status_ramassage,ed.idlogin AS id_employe,
       ed.nom AS nom_employe, ed.prenom AS prenom_employe, c.id AS id_contenant, c.nom AS nom_contenant,
       r.poids AS poids, c.taille AS taille_contenant, c.nbCadre AS nbCadre_contenant, v.type AS type_vehicule,
       v.immatriculation AS immatriculation_vehicule
    FROM decheterie d
    JOIN ramassage r ON r.FK_decheterie = d.id
    JOIN contenant c ON c.id = r.FK_contenant
    JOIN vehicule v ON v.immatriculation = r.FK_vehicule
    JOIN employe ed ON ed.idlogin = r.FK_employee;

-----------------------------------------------------------
-- Un employé d'une déchèterie a une vue sur ses ramassages présent/futur
CREATE OR REPLACE VIEW employe_decheterie AS
SELECT *
FROM secretaire_decheterie_ramassage
    WHERE date_ramassage >= CURRENT_DATE;


------------------------------------------------------------------------------------------------------------------------
-- Insertion des données
------------------------------------------------------------------------------------------------------------------------
BEGIN;
-- Insert data into the 'adresse' table
INSERT INTO adresse (id, rue, numero, NPA, nomVille, pays) VALUES
(1, 'Chemin du petit pas', '10', '1400', 'Yverdon-les-Bains', 'Suisse'),
(2, 'Chemin du fleuve', '12', '1462', 'Yvonand', 'Suisse'),
(3, 'Chemin du petit son', '11', '1441', 'Grandson', 'Suisse'),
(4, 'Chemin du grand pas', '14', '1880', 'Bex', 'Suisse'),
(5, 'Chemin des bains', '10', '1881', 'Saillon', 'Suisse'),
(6, 'Chemin du ski', '21', '1882', 'Verbier', 'Suisse'),
(7, 'Chemin du lac', '15', '1400', 'Lausanne', 'Suisse'),
(8, 'Chemin du mont', '16', '1462', 'Geneva', 'Suisse'),
(9, 'Chemin du champ', '17', '1441', 'Zurich', 'Suisse'),
(10, 'Chemin du bois', '18', '1880', 'Bern', 'Suisse'),
(11, 'Chemin du ruisseau', '19', '1881', 'Lugano', 'Suisse'),
(12, 'Chemin du pont', '20', '1882', 'Lucerne', 'Suisse'),
(13, 'Chemin du moulin', '21', '1400', 'Basel', 'Suisse'),
(14, 'Chemin du chateau', '22', '1462', 'St. Gallen', 'Suisse'),
(15, 'Chemin du parc', '23', '1441', 'La Chaux-de-Fonds', 'Suisse'),
(16, 'Chemin du jardin', '24', '1880', 'Fribourg', 'Suisse');

-- Insert data into the 'decheterie' table
INSERT INTO decheterie (id, nom, FK_adresse) VALUES
(1, 'Decheterie Yverdon', 1),
(2, 'Decheterie Yvonand', 2),
(3, 'Decheterie Grandson', 3),
(4, 'Decheterie Bex', 4),
(5, 'Decheterie Saillon', 5),
(6, 'Decheterie Verbier', 6);

-- Insert data into the 'principale' table
INSERT INTO principale (FK_principale, FK_decheterie) VALUES
(1, 2),
(1, 3),
(1, 4),
(5, 6);

-- Insert data into the 'fonction' table
INSERT INTO fonction (nom) VALUES
('Responsable'),
('Secrétaire'),
('Chauffeur'),
('Employé');

-- Insert data into the 'employe' table Responsable, Secrétaire, Chauffeur, Employé de déchèterie
INSERT INTO employe (idLogin, mdpLogin, nom, prenom, dateNaissance, dateDebutContrat, numTelephone, typePermis, FK_adresse,fk_decheterie, fk_fonction) VALUES
('jdoe', 'password', 'Doe', 'John', '1980-01-01', '2020-01-01', NULL, NULL, 7, 1, 'Responsable'),
('agraf', 'password', 'Graf', 'Andrea', '1982-01-01', '2020-01-01', '0123456789', NULL, 9, 2, 'Responsable'),
('jdoe2', 'password', 'Doe', 'James', '1983-01-01', '2020-01-01', '0123456789', NULL, 11, 3, 'Responsable'),
('jmartin', 'password', 'Martin', 'Joseph', '1985-01-01', '2020-01-01', NULL, NULL, 15, 4, 'Responsable'),
('jdurand', 'password', 'Durand', 'Joseph', '1985-01-01', '2020-01-01', '0123456789', NULL, 15, 5, 'Responsable'),
('hlopez', 'password', 'Lopez', 'Hugo', '1985-01-01', '2020-01-01', '0123456789', NULL, 15, 6, 'Responsable'),
('jferrara', 'password', 'Ferrara', 'Justin', '1986-01-01', '2021-01-01', '0123456789', NULL, 10, 1, 'Secrétaire'),
('lhussain', 'password', 'Hussain', 'Lucas', '1986-01-01', '2021-01-01', '0123456789', NULL, 10, 1, 'Secrétaire'),
('jdoe3', 'password', 'Doe', 'Jennifer', '1984-01-01', '2020-01-01', '0123456789', NULL, 13, 5, 'Secrétaire'),
('asmith', 'password', 'Smith', 'Alice', '1985-01-01', '2021-01-01', '0123456789', NULL, 8, 1, 'Employé'),
('rsmith', 'password', 'Smith', 'Robert', '1987-01-01', '2021-01-01', '0123456789', NULL, 12, 2, 'Employé'),
('rlandry', 'password', 'Landry', 'Rachel', '1989-01-01', '2021-01-01', NULL, NULL, 16, 6, 'Employé'),
('rsmith2', 'password', 'Smith', 'Rebecca', '1988-01-01', '2021-01-01', '0123456789', 'C', 14, 1, 'Chauffeur'),
('rfournier', 'password', 'Fournier', 'Rebecca', '1988-01-01', '2021-01-01', '0123456789', 'B', 14, 1, 'Chauffeur'),
('lchevalier', 'password', 'Chevalier', 'Lance', '1988-01-01', '2021-01-01', '0123456789', 'B', 14, 5, 'Chauffeur'),
('bleusly', 'password', 'Leusly', 'Barbara', '1988-01-01', '2021-01-01', '0123456789', 'C', 14, 5, 'Chauffeur');

-- Insert data into the 'vehicule' table
INSERT INTO vehicule (immatriculation, type, remorque, anneeFabrication, dateExpertise, consoCarburant, FK_decheterie) VALUES
('VD 850 154', 'camion', FALSE, '2010', '2022-01-01', 10.5, 1),
('VD 145 154', 'camionnette', FALSE, '2015', '2022-01-01', 7.5, 1),
('VD 756 254', 'camion', FALSE, '2011', '2022-01-01', 11.5, 1),
('VD 896 654', 'camionnette', TRUE, '2016', '2022-01-01', 8.5, 1),
('VD 857 154', 'camion', FALSE, '2011', '2022-01-01', 11.5, 1),
('VD 568 154', 'camionnette', TRUE, '2018', '2022-07-30', 8.5, 1),
('VD 147 154', 'camion', FALSE, '2011', '2022-01-01', 11.5, 1),
('VS 568 745', 'camionnette', TRUE, '2018', '2022-07-30', 8.5, 5),
('VS 352 125', 'camion', FALSE, '2011', '2022-01-01', 11.5, 5),
('VS 255 145', 'camionnette', FALSE, '2018', '2022-07-30', 8.5, 5),
('VS 458 569', 'camion', FALSE, '2011', '2022-01-01', 11.5, 5),
('VS 987 865', 'camionnette', TRUE, '2018', '2022-07-30', 8.5, 5);


-- Insert data into the 'dechet' table
INSERT INTO dechet (type) VALUES
('papier'), ('carton'), ('flaconnage'), ('encombrants'), ('déchets verts'), ('inertes'), ('ferraille'), ('pet'), ('alu'), ('fer blanc'),
('verre'), ('bois'), ('appareils électroniques'), ('appareils électriques'), ('déchets spéciaux');

-- Insert data into the 'status' table
INSERT INTO status (nom) VALUES
('accepté'), ('refusé'), ('en attente');

-- Insert data into the 'contenant' table
INSERT INTO contenant (id, nom, capaciteMax, nbCadre, taille, couleur, FK_decheterie, FK_dechet) VALUES

--Décheterie 1
(1, 'benne', 40, NULL, NULL, 'blue', 1, 'papier'),
(2, 'benne', 40, NULL, NULL, 'green', 1, 'carton'),
(3, 'benne', 40, NULL, NULL, 'red', 1, 'flaconnage'),
(4, 'benne', 40, NULL, NULL, 'red', 1, 'encombrants'),
(5, 'benne', 30, NULL, NULL, 'red', 1, 'inertes'),
(6, 'benne', 40, NULL, NULL, 'red', 1, 'ferraille'),
(7, 'benne', 40, NULL, NULL, 'red', 1, 'verre'),
(8, 'benne', 40, NULL, NULL, 'red', 1, 'bois'),
(9, 'grande caisse', 5, NULL, NULL, NULL, 1, 'pet'),
(10, 'grande caisse', 5, NULL, NULL, NULL, 1, 'alu'),
(11, 'grande caisse', 5, NULL, NULL, NULL, 1, 'fer blanc'),
(12, 'grande caisse', 5, NULL, NULL, NULL, 1, 'déchets spéciaux'),
(13, 'big bag', NULL, NULL, 'grand', NULL, 1, 'déchets verts'),
(14, 'palette', NULL, 3, NULL, NULL, 1, 'appareils électroniques'),
(15, 'palette', NULL, 4, NULL, NULL, 1, 'appareils électriques'),

--Décheterie 2
(16, 'benne', 40, NULL, NULL, 'blue', 2, 'papier'),
(17, 'benne', 40, NULL, NULL, 'green', 2, 'carton'),
(18, 'benne', 40, NULL, NULL, 'red', 2, 'flaconnage'),
(19, 'benne', 40, NULL, NULL, 'red', 2, 'encombrants'),
(20, 'benne', 30, NULL, NULL, 'red', 2, 'inertes'),
(21, 'benne', 40, NULL, NULL, 'red', 2, 'ferraille'),
(22, 'benne', 40, NULL, NULL, 'red', 2, 'verre'),
(23, 'benne', 40, NULL, NULL, 'red', 2, 'bois'),
(24, 'grande caisse', 5, NULL, NULL, NULL, 2, 'pet'),
(25, 'grande caisse', 5, NULL, NULL, NULL, 2, 'alu'),
(26, 'grande caisse', 5, NULL, NULL, NULL, 2, 'fer blanc'),
(27, 'grande caisse', 5, NULL, NULL, NULL, 2, 'déchets spéciaux'),
(28, 'big bag', NULL, NULL, 'grand', NULL, 2, 'déchets verts'),
(29, 'palette', NULL, 3, NULL, NULL, 2, 'appareils électroniques'),
(30, 'palette', NULL, 4, NULL, NULL, 2, 'appareils électriques'),

--Décheterie 3
(31, 'benne', 40, NULL, NULL, 'blue', 3, 'papier'),
(32, 'benne', 40, NULL, NULL, 'green', 3, 'carton'),
(33, 'benne', 40, NULL, NULL, 'red', 3, 'flaconnage'),
(34, 'benne', 40, NULL, NULL, 'red', 3, 'encombrants'),
(35, 'benne', 30, NULL, NULL, 'red', 3, 'inertes'),
(36, 'benne', 40, NULL, NULL, 'red', 3, 'ferraille'),
(37, 'benne', 40, NULL, NULL, 'red', 3, 'verre'),
(38, 'benne', 40, NULL, NULL, 'red', 3, 'bois'),
(39, 'grande caisse', 5, NULL, NULL, NULL, 3, 'pet'),
(40, 'grande caisse', 5, NULL, NULL, NULL, 3, 'alu'),
(41, 'grande caisse', 5, NULL, NULL, NULL, 3, 'fer blanc'),
(42, 'grande caisse', 5, NULL, NULL, NULL, 3, 'déchets spéciaux'),
(43, 'big bag', NULL, NULL, 'grand', NULL, 3, 'déchets verts'),
(44, 'palette', NULL, 3, NULL, NULL, 3, 'appareils électroniques'),
(45, 'palette', NULL, 4, NULL, NULL, 3, 'appareils électriques'),

--Décheterie 4
(46, 'benne', 40, NULL, NULL, 'blue', 4, 'papier'),
(47, 'benne', 40, NULL, NULL, 'green', 4, 'carton'),
(48, 'benne', 40, NULL, NULL, 'red', 4, 'flaconnage'),
(49, 'benne', 40, NULL, NULL, 'red', 4, 'encombrants'),
(50, 'benne', 30, NULL, NULL, 'red', 4, 'inertes'),
(51, 'benne', 40, NULL, NULL, 'red', 4, 'ferraille'),
(52, 'benne', 40, NULL, NULL, 'red', 4, 'verre'),
(53, 'benne', 40, NULL, NULL, 'red', 4, 'bois'),
(54, 'grande caisse', 5, NULL, NULL, NULL, 4, 'pet'),
(55, 'grande caisse', 5, NULL, NULL, NULL, 4, 'alu'),
(56, 'grande caisse', 5, NULL, NULL, NULL, 4, 'fer blanc'),
(57, 'grande caisse', 5, NULL, NULL, NULL, 4, 'déchets spéciaux'),
(58, 'big bag', NULL, NULL, 'grand', NULL, 4, 'déchets verts'),
(59, 'palette', NULL, 3, NULL, NULL, 4, 'appareils électroniques'),
(60, 'palette', NULL, 4, NULL, NULL, 4, 'appareils électriques'),

--Décheterie 5
(61, 'benne', 40, NULL, NULL, 'blue', 5, 'papier'),
(62, 'benne', 40, NULL, NULL, 'green', 5, 'carton'),
(63, 'benne', 40, NULL, NULL, 'red', 5, 'flaconnage'),
(64, 'benne', 40, NULL, NULL, 'red', 5, 'encombrants'),
(65, 'benne', 30, NULL, NULL, 'red', 5, 'inertes'),
(66, 'benne', 40, NULL, NULL, 'red', 5, 'ferraille'),
(67, 'benne', 40, NULL, NULL, 'red', 5, 'verre'),
(68, 'benne', 40, NULL, NULL, 'red', 5, 'bois'),
(69, 'grande caisse', 5, NULL, NULL, NULL, 5, 'pet'),
(70, 'grande caisse', 5, NULL, NULL, NULL, 5, 'alu'),
(71, 'grande caisse', 5, NULL, NULL, NULL, 5, 'fer blanc'),
(72, 'grande caisse', 5, NULL, NULL, NULL, 5, 'déchets spéciaux'),
(73, 'big bag', NULL, NULL, 'grand', NULL, 5, 'déchets verts'),
(74, 'palette', NULL, 3, NULL, NULL, 5, 'appareils électroniques'),
(75, 'palette', NULL, 4, NULL, NULL, 5, 'appareils électriques'),

--Décheterie 6
(76, 'benne', 40, NULL, NULL, 'blue', 6, 'papier'),
(77, 'benne', 40, NULL, NULL, 'green', 6, 'carton'),
(78, 'benne', 40, NULL, NULL, 'red', 6, 'flaconnage'),
(79, 'benne', 40, NULL, NULL, 'red', 6, 'encombrants'),
(80, 'benne', 30, NULL, NULL, 'red', 6, 'inertes'),
(81, 'benne', 40, NULL, NULL, 'red', 6, 'ferraille'),
(82, 'benne', 40, NULL, NULL, 'red', 6, 'verre'),
(83, 'benne', 40, NULL, NULL, 'red', 6, 'bois'),
(84, 'grande caisse', 5, NULL, NULL, NULL, 6, 'pet'),
(85, 'grande caisse', 5, NULL, NULL, NULL, 6, 'alu'),
(86, 'grande caisse', 5, NULL, NULL, NULL, 6, 'fer blanc'),
(87, 'grande caisse', 5, NULL, NULL, NULL, 6, 'déchets spéciaux'),
(88, 'big bag', NULL, NULL, 'grand', NULL, 6, 'déchets verts'),
(89, 'palette', NULL, 3, NULL, NULL, 6, 'appareils électroniques'),
(90, 'palette', NULL, 4, NULL, NULL, 6, 'appareils électriques');

-- Insert data into the 'ramassage' table
INSERT INTO ramassage (id, date, fk_status, poids, FK_contenant, FK_employee, FK_decheterie, FK_vehicule) VALUES
(1, '2022-01-01', 'accepté', 100, 1, 'rsmith2', 1, 'VD 756 254'),
(2, '2022-01-02', 'refusé', 55, 28, 'rfournier', 2, 'VD 145 154'),
(3, '2024-03-06', 'accepté', 89, 59, 'rsmith2', 4, 'VD 568 154'),
(4, '2022-05-05', 'en attente', 25, 67, 'bleusly', 5, 'VS 458 569'),
(5, '2024-05-05', 'refusé', 66, 84, 'lchevalier', 6, 'VS 987 865');

-- Insert data into the 'superviseur' table
INSERT INTO superviseur (FK_employee, FK_superviseur) VALUES
('jdoe', 'jdoe'),
('rfournier', 'jdoe'),
('rsmith2', 'jdoe'),
('asmith', 'jdoe'),
('agraf', 'agraf'),
('bleusly', 'agraf'),
('lchevalier', 'agraf'),
('rsmith', 'agraf'),
('jdoe3', 'agraf'),
('jdoe2', 'jdoe2'),
('jmartin', 'jmartin'),
('jdurand', 'jdurand'),
('hlopez', 'hlopez'),
('rlandry', 'hlopez');
COMMIT ;