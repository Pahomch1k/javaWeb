document.addEventListener('DOMContentLoaded', function() {



    const createButton = document.getElementById("db-create-button")
    if (createButton) createButton.addEventListener('click', createButtonClick);

    const insertButton = document.getElementById("db-insert-button")
    if (insertButton) insertButton.addEventListener('click', insertButtonClick);

    const readButton = document.getElementById("db-read-button")
    if (readButton) readButton.addEventListener('click', readButtonClick);

    document.getElementById('show-all-button').addEventListener('click', function() {
        fetch('/db?all=true', {
            method: 'COPY',
        })
            .then(response => response.json())
            .then(data => showCalls(data))
            .catch(error => console.error('Error:', error));
    });
});

function createButtonClick(){
    fetch(window.location.href,{
        method: 'PUT'
    }).then(r => r.json()).then(j => {
        console.log(j)
    });
}

function insertButtonClick(){
    const nameInput = document.querySelector('[name="user-name"]');
    const phoneInput = document.querySelector('[name="user-phone"]');
    const outputElem = document.getElementById("out");

    if (!nameInput) throw '[name="user-name"] not found';
    if (!phoneInput) throw '[name="user-phone"] not found';

    // Front-end validation
    if (!nameInput.value.trim()) {
        outputElem.textContent = "The name cannot be empty!\n";
        return;
    }

    // Simple phone validation (just checks if it's numeric and 10-15 characters long)
    const phonePattern = /^\+\d{10,15}$/;
    if (!phoneInput.value.match(phonePattern)) {
        outputElem.textContent = "Please enter a correct phone number!";
        return;
    }

    fetch(window.location.href,{
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            name: nameInput.value,
            phone: phoneInput.value
        })
    }).then(r => r.json()).then(j => {
        // Update the front-end based on the server's response
        if (j.status && j.status === "validation error") {
            outputElem.textContent = j.message;
        } else {
            console.log(j);
        }
    });
}

function readButtonClick() {
    fetch(window.location.href, {
        method: "COPY",
    })
        .then(response => response.json()).then(showCalls) ;
}


function showCalls(j) {
    var table = "<table><tr><th>id</th><th>name</th><th>phone</th><th>callMoment</th><th>Delete</th></tr>";
    for (let call of j){
        let m =  ( typeof call.callMoment != null ) ?
            `<button data-id="${call.id}" onclick="callClick(event)">call</button>` : call.callMoment;
        let d = `<button data-id="${call.id}" onclick="deleteClick(event)"><i class="material-icons red">clear</i></button>`
        table += `<tr><td>${call.id}</td><td>${call.name}</td><td>${call.phone}</td><td>${m}</td><td>${d}</td></tr>`;
    }
    table += "</table>";
    document.getElementById("calls-conteiner").innerHTML = table;
}


function callClick(e) {
    const callId = e.target.getAttribute( "data-id" );
    if (confirm( `MAKE CALL FOR ORDER No ${callId}? ` ) ){
        fetch(window.location.href + "?call-id=" + callId, {
            method: "PATCH",
        }).then(r => r.json()).then( j => {
            if (typeof j.callMoment == 'undefined') {
                alert( j );
            }
            else {
                e.target.parentNode.innerHTML = j.callMoment ;
            }
        }) ;
    }
}

function deleteClick(e) {
    const btn = e.target.closest('button');
    const callId = e.target.getAttribute("data-id");
    if (confirm(`DELETE ORDER No ${callId} ? `)) {
        fetch(window.location.href + "?call-id=" + callId, {
            method: "DELETE",
        }).then(r => {
            if (r.status === 202) {
                let tr = e.btn.parentNode.parentNode;
                tr.parentNode.removeChild(tr);
            } else {
                r.json().then(alert);
            }
        });
    }
}