document.addEventListener('DOMContentLoaded', function() {
    M.Modal.init(document.querySelectorAll('.modal'),
        {opacity:	0.5	,
        inDuration: 200,
        outDuration: 200});

    // const regForm = document.querySelector('form');
    // regForm.addEventListener('submit', function(event) {
    //     let valid = true;
    //
    //     // Валідація логіну
    //     const login = document.getElementById('reg-login');
    //     if (!login.value.match(/^[a-zA-Z0-9]{5,20}$/)) {
    //         alert('Логін повинен містити 5-20 символів і містити тільки літери та цифри.');
    //         valid = false;
    //     }
    //
    //     // Валідація дати
    //     const birthdate = document.getElementById('reg-birthdate');
    //     if (!birthdate.value.match(/^\d{4}-\d{2}-\d{2}$/)) {
    //         alert('Будь ласка, введіть дату у форматі РРРР-ММ-ДД.');
    //         valid = false;
    //     }
    //
    //     // Валідація пошти
    //     const email = document.getElementById('reg-email');
    //     const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    //     if (!email.value.match(emailPattern)) {
    //         alert('Email wrong');
    //         valid = false;
    //     }
    //
    //     if (!valid) {
    //         event.preventDefault();
    //     }
    // });

    const createButton = document.getElementById("db-create-button")
    if (createButton) createButton.addEventListener('click', createButtonClick);

    const insertButton = document.getElementById("db-insert-button")
    if (insertButton) insertButton.addEventListener('click', insertButtonClick);

    const readButton = document.getElementById("db-read-button")
    if (readButton) readButton.addEventListener('click', readButtonClick);
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
        .then(response => response.json())
        .then(data => {
            console.table(data);
        });
}

//
// function insertButtonClick(){
//     const nameInput = document.querySelector('[name="user-name"]');
//     if (! nameInput) throw '[name="user-name"] not found';
//     const phoneInput = document.querySelector('[name="user-phone"]');
//     if (! phoneInput) throw '[name="user-phone"] not found';
//
//     fetch(window.location.href,{
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/json'
//         },
//         body: JSON.stringify({
//             name: nameInput.value,
//             phone: phoneInput.value
//         })
//     }).then(r => r.json()).then(j => {
//         console.log(j)
//     });
// }
