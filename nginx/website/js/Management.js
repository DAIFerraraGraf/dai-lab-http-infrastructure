// Fonction pour récupérer les données depuis l'API
function fetchDataEmployee() {
    fetch('https://localhost/api/employes')
        .then(response => response.json())
        .then(data => {
            // Appelé lorsque les données sont récupérées avec succès
            populateTable(data, "employes");
        })
        .catch(error => {
            console.error('Erreur lors de la récupération des données:', error);
        });
}
function fetchDataRamassage() {
    fetch('https://localhost/api/ramassages')
        .then(response => response.json())
        .then(data => {
            // Appelé lorsque les données sont récupérées avec succès
            populateTable(data, "ramassages");
        })
        .catch(error => {
            console.error('Erreur lors de la récupération des données:', error);
        });
}

// Fonction pour remplir le tableau avec les données JSON
function populateTable(data, type) {
    const tableHeaders = document.getElementById('tableHeaders');
    const tableBody = document.getElementById('tableBody');

    // Efface le contenu actuel du tableau
    tableHeaders.innerHTML = '';
    tableBody.innerHTML = '';

    if (data.length > 0) {
        const headers = Object.keys(data[0]).filter(header => !header.startsWith('id'));
        headers.forEach(header => {
            const th = document.createElement('th');
            th.textContent = header
                .replace(/_/g, ' ') // remplacer les underscores par des espaces
                .split(' ') // split the string into an array of words
                .map(word => word.charAt(0).toUpperCase() + word.slice(1)) // mettre en majuscule la première lettre de chaque mot
                .join(' '); // remettre le tableau en string
            tableHeaders.appendChild(th);
        });

        // Ajoute les en-têtes pour les boutons
        const thEdit = document.createElement('th');
        thEdit.textContent = 'Modifier';
        tableHeaders.appendChild(thEdit);
        const thDelete = document.createElement('th');
        thDelete.textContent = 'Supprimer';
        tableHeaders.appendChild(thDelete);

        // Parcourt les données et ajoute chaque entrée dans le tableau
        data.forEach(item => {
            const row = tableBody.insertRow();
            headers.forEach(header => {
                const cell = row.insertCell();
                if (header === 'date_naissance' || header === 'date_debut_contrat' || header === 'date_ramassage') { // Remplacez 'date_colonne' par le nom de votre colonne date
                    const dateValue = new Date(item[header]).toLocaleDateString('fr-FR'); // Formatage de la date
                    cell.textContent = dateValue;
                } else {
                    cell.textContent = item[header] !== null && item[header] !== undefined ? item[header] : '-';
                }
                cell.setAttribute('data-title', header);
            });

            // Ajoute le bouton Modifier
            const editCell = row.insertCell();
            const editButton = document.createElement('button');
            editButton.textContent = 'Modifier';
            editButton.onclick = function() {
                let idToEdit;
                switch (type) {
                    case "employes":
                        idToEdit = item.id_employe;
                        break;
                    case "ramassages":
                        idToEdit = item.id_ramassage;
                        break;
                }

                // Redirection vers la page employesForm.html avec l'ID dans l'URL
                window.location.href = `${type}Update.html?id=${idToEdit}`;
            };
            editCell.appendChild(editButton);

            // Ajoute le bouton Supprimer
            const deleteCell = row.insertCell();
            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Supprimer';
            let idToDelete;
            switch(type) {  // Récupère l'ID
                case "employes":
                    idToDelete = item.id_employe;
                    break;
                case "ramassages":
                    idToDelete = item.id_ramassage;
                    break;
            }
            deleteButton.onclick = function() {
                deleteElementById(idToDelete, type);
            };
            deleteCell.appendChild(deleteButton);
        });
    }
}

function deleteElementById(id, type) {
    fetch(`https://localhost/api/${type}/${id}`, {
        method: 'DELETE',
    })
    .then(response => {
        if (response.ok) {
            console.log(`${id} supprimé avec succès.`);
            window.location.reload();
        } else {
            console.error('La suppression a échoué');
        }
    })
    .catch(error => {
        console.error('Erreur lors de la suppression:', error);
    });
}

function fetchFonctions() {
    fetch('https://localhost/api/fonctions')
        .then(response => response.json())
        .then(data => {
            populateDropdown('fonction', data);
        })
        .catch(error => {
            console.error('Erreur lors de la récupération des données:', error);
        });
}

// Fonction pour récupérer les adresses depuis l'API
function fetchAdresses() {
    fetch('https://localhost/api/adresses')
        .then(response => response.json())
        .then(data => {
            populateDropdown('adresse', data);
        })
        .catch(error => {
            console.error('Erreur lors de la récupération des données:', error);
        });
}

// Fonction pour récupérer les déchèteries depuis l'API
function fetchDecheteries() {
    fetch('https://localhost/api/decheteries')
        .then(response => response.json())
        .then(data => {
            populateDropdown('decheterie', data);
        })
        .catch(error => {
            console.error('Erreur lors de la récupération des données:', error);
        });
}


function fetchEmployesList() {
    fetch('https://localhost/api/employesList')
        .then(response => response.json())
        .then(data => {
            populateDropdown('employee', data);
        })
        .catch(error => {
            console.error('Erreur lors de la récupération des données:', error);
        });

}
function fetchVehicle() {
    fetch('https://localhost/api/vehicules')
        .then(response => response.json())
        .then(data => {
            populateDropdown('vehicule', data);
        })
        .catch(error => {
            console.error('Erreur lors de la récupération des données:', error);
        });

}
function fetchStatus(){
    fetch('https://localhost/api/status')
        .then(response => response.json())
        .then(data => {
            populateDropdown('status', data);
        })
        .catch(error => {
            console.error('Erreur lors de la récupération des données:', error);
        });
}
// Fonction pour peupler une liste déroulante avec des données
function populateDropdown(dropdownId, data) {
    const dropdown = document.getElementById(dropdownId);

    dropdown.innerHTML = '';

    data.forEach(item => {
        const option = document.createElement('option');
        option.value = item.id;
        option.text = item.nom;
        dropdown.appendChild(option);
    });
}

window.onload = function() {
    // Vérifiez si l'URL de la page actuelle est celle de la page spécifique
    if (window.location.pathname === '/employe.html') {
        // Exécutez les fonctions de récupération de données
        fetchDataEmployee();
        setInterval(fetchDataEmployee, 5000);
    }
    if (window.location.pathname === '/ramassage.html') {
        // Exécutez les fonctions de récupération de données
        fetchDataRamassage();
        setInterval(fetchDataRamassage, 5000);
    }
};

window.onscroll = function() {
    var navbar = document.querySelector('.navbar');
    if (window.pageYOffset > 50) {
        navbar.classList.add('scrolled');
    } else {
        navbar.classList.remove('scrolled');
    }
};
function logout() {
    // Supprimer tous les cookies
    var cookies = document.cookie.split(";");

    for (var i = 0; i < cookies.length; i++) {
        var cookie = cookies[i];
        var eqPos = cookie.indexOf("=");
        var name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
        document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
    }

    // Rediriger vers la page de connexion
    window.location.href = 'login.html';
}
function submitUpdateEmployeeForm() {
    // Récupérer les valeurs du formulaire
    const loginId = document.getElementById('loginId').value;
    const password = document.getElementById('password').value;
    const nom = document.getElementById('nom').value;
    const prenom = document.getElementById('prenom').value;
    const dob = document.getElementById('dob').value;
    const contractStartDate = document.getElementById('contractStartDate').value;
    const fonction = document.getElementById('fonction').value;
    const phone = document.getElementById('phone').value;
    let licenseType = document.getElementById('licenseType').value;
    const adresse = document.getElementById('adresse').value;
    const decheterie = document.getElementById('decheterie').value;

    // Créer un objet avec les valeurs
    const data = {
        idlogin: loginId,
        mdplogin: password,
        nom: nom,
        prenom: prenom,
        datenaissance: dob,
        datedebutcontrat: contractStartDate,
        fk_fonction: fonction,
        numtelephone: phone,
        typepermis: licenseType,
        fk_adresse: adresse,
        fk_decheterie: decheterie
    };

    // Convertir l'objet en JSON
    const json = JSON.stringify(data);

    // Envoyer une requête PUT à l'API
    fetch('https://localhost/api/employes/' + loginId, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: json
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text);
                });
            }
            return response.text().then(text => text ? JSON.parse(text) : {});
        })
        .then(data => {
            if (data) {
                console.log('Success:', data);
                window.location.href = 'employe.html';
                alert('Employé mis à jour avec succès');
            }
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('Erreur lors de la mise à jour de l\'employé' + error.message);
        });
}
function submitRamassageUpdateForm() {
    // Récupérer les valeurs du formulaire
    const dateRamassage = document.getElementById('date').value;
    const status = document.getElementById('status').value;
    const poids = document.getElementById('poids').value;
    const employee = document.getElementById('employee').value;
    const vehicule = document.getElementById('vehicule').value;
    const conteneur = document.getElementById('contenant').value;
    const decheterie = document.getElementById('decheterie').value;

    // Créer un objet avec les valeurs
    const data = {
        date: dateRamassage,
        fk_status: status,
        poids: poids,
        fk_contenant: conteneur,
        fk_employee: employee,
        fk_vehicule: vehicule,
        fk_decheterie: decheterie
    };

    // Convertir l'objet en JSON
    const json = JSON.stringify(data);
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id');
    // Envoyer une requête PUT à l'API
    fetch('https://localhost/api/ramassages/' + id, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: json
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text);
                });
            }
            return response.text().then(text => text ? JSON.parse(text) : {});
        })
        .then(data => {
            if (data) {
                console.log('Success:', data);
                window.location.href = 'ramassage.html';
                alert('Ramassage mis à jour avec succès');
            }
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('Erreur lors de la mise à jour du ramassage' + error.message);
        });

}
function submitCreateEmployeeForm() {
    // Récupérer les valeurs du formulaire
    const loginId = document.getElementById('loginId').value;
    const password = document.getElementById('password').value;
    const nom = document.getElementById('nom').value;
    const prenom = document.getElementById('prenom').value;
    const dob = document.getElementById('dob').value;
    const contractStartDate = document.getElementById('contractStartDate').value;
    const fonction = document.getElementById('fonction').value;
    const phone = document.getElementById('phone').value;
    let licenseType = document.getElementById('licenseType').value;
    const adresse = document.getElementById('adresse').value;
    const decheterie = document.getElementById('decheterie').value;

    // Créer un objet avec les valeurs
    const data = {
        idlogin: loginId,
        mdplogin: password,
        nom: nom,
        prenom: prenom,
        datenaissance: dob,
        datedebutcontrat: contractStartDate,
        fk_fonction: fonction,
        numtelephone: phone,
        typepermis: licenseType,
        fk_adresse: adresse,
        fk_decheterie: decheterie
    };

    // Convertir l'objet en JSON
    const json = JSON.stringify(data);

    // Envoyer une requête POST à l'API
    fetch('https://localhost/api/employes', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: json
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text);
                });
            }
            return response.text().then(text => text ? JSON.parse(text) : {});
        })
        .then(data => {
            if (data) {
                console.log('Success:', data);
                window.location.href = 'employe.html';
                alert('Employé créé avec succès');
            }
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('Erreur lors de la création de l\'employé \n' + error.message);
        });

}
function submitCreateRamassageForm() {
    // Récupérer les valeurs du formulaire
    const dateRamassage = document.getElementById('date').value;
    const status = document.getElementById('status').value;
    const poids = document.getElementById('poids').value;
    const employee = document.getElementById('employee').value;
    const vehicule = document.getElementById('vehicule').value;
    const conteneur = document.getElementById('contenant').value;
    const decheterie = document.getElementById('decheterie').value;

    // Créer un objet avec les valeurs
    const data = {
        date: dateRamassage,
        fk_status: status,
        poids: poids,
        fk_contenant: conteneur,
        fk_employee: employee,
        fk_vehicule: vehicule,
        fk_decheterie: decheterie
    };

    // Convertir l'objet en JSON
    const json = JSON.stringify(data);

    // Envoyer une requête POST à l'API
    fetch('https://localhost/api/ramassages', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: json
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text);
                });
            }
            return response.text().then(text => text ? JSON.parse(text) : {});
        })
        .then(data => {
            if (data) {
                console.log('Success:', data);
                window.location.href = 'ramassage.html';
                alert('Ramassage créé avec succès');
            }
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('Erreur lors de la création du ramassage \n' + error.message);
        });

}
function onClickAnnulerEmployee() {
    window.location.href = 'employe.html';
}
function onClickAnnulerRamassage() {
    window.location.href = 'ramassage.html';
}
function onClickAddEmployee() {
    window.location.href = 'employesCreate.html';
}
function onClickAddRamassage() {
    window.location.href = 'ramassagesCreate.html';
}