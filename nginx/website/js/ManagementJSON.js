// Fonction pour récupérer les données depuis l'API
function fetchData() {
    fetch('http://localhost/api/cookie')
        .then(response => response.json())
        .then(data => {
            // Appelé lorsque les données sont récupérées avec succès
            populateTable(data);
        })
        .catch(error => {
            console.error('Erreur lors de la récupération des données:', error);
        });
}

// Fonction pour remplir le tableau avec les données JSON
function populateTable(data) {
    const tableHeaders = document.getElementById('tableHeaders');
    const tableBody = document.getElementById('tableBody');

    // Efface le contenu actuel du tableau
    tableHeaders.innerHTML = '';
    tableBody.innerHTML = '';

    // Vérifie si les données ne sont pas vides
    if (data.length > 0) {
        // En-têtes du tableau
        const headers = Object.keys(data[0]);
        headers.forEach(header => {
            const th = document.createElement('th');
            th.textContent = header;
            tableHeaders.appendChild(th);
        });

        // Parcourt les données et ajoute chaque entrée dans le tableau
        data.forEach(item => {
            const row = tableBody.insertRow();
            headers.forEach(header => {
                const cell = row.insertCell();
                cell.textContent = item[header] !== null && item[header] !== undefined ? item[header] : '-';
                cell.setAttribute('data-title', header);
            });
        });
    }
}

// Appel de la fonction pour récupérer les données
fetchData();