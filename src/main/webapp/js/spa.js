document.addEventListener('DOMContentLoaded', () => {
    M.Modal.init( document.querySelectorAll('.modal'), {
        opacity: 0.6,
        inDuration: 200,
        outDuration: 200,
        onOpenStart: onModalOpens,
    });
    const authSignInButton = document.getElementById("auth-sign-in") ;
    if( authSignInButton ) {
        authSignInButton.addEventListener('click', authSignInButtonClick);
    }
    else {
        console.error("#auth-sign-in not found");
    }
    // Token verification
    const spaTokenStatus = document.getElementById("spa-token-status");
    if( spaTokenStatus ) {
        const token = window.localStorage.getItem('token');
        if( token ) {
            const tokenObject = JSON.parse( atob( token ) ) ;
            // TODO: перевірити на правильність декодування та дійсність токена
            spaTokenStatus.innerText = "Дійсний до " + tokenObject.exp ;
            const appContext = getAppContext() ;
            fetch(`${appContext}/tpl/spa-auth.html`)
                .then(r=>r.text())
                .then( t =>
                    document.querySelector('auth-part').innerHTML = t ) ;

            document.getElementById("spa-log-out")
                .addEventListener('click', logoutClick ) ;
        }
        else {
            spaTokenStatus.innerText = 'Не встановлено';
        }
    }
    const spaGetDataButton = document.getElementById("spa-get-data");
    if( spaGetDataButton ) spaGetDataButton.addEventListener('click', spaGetDataClick );
});

function spaGetDataClick() {

}


function logoutClick() {
    window.localStorage.removeItem('token');
    window.location.reload();
}

function getAppContext() {
    return '/' + window.location.pathname.split('/')[1];
}

function onModalOpens() {
    [authLogin, authPassword, authMessage] = getAuthElements();
    authLogin.value = "";
    authPassword.value = "";
    authMessage.innerHTML = "";
}

function authSignInButtonClick() {
    [authLogin, authPassword, authMessage] = getAuthElements();

    if ( authLogin.value.length === 0 ) {
        authMessage.innerHTML = "Login not be found";
    }

    const appContext = getAppContext();
    fetch(`${appContext}/auth?login=${authLogin.value}&password=${authPassword.value}`, {
        method: 'GET'
    }).then( r => {
        if (r.status !== 200) {
            authMessage.innerHTML = "Авторизация отменена";
        }
        else {
            r.text().then( base64encodedText => {
                const token = JSON.parse( atob( base64encodedText ) ) ;
                if (typeof token.jti === 'undefined' ) {
                    authMessage.innerHTML = "Ошибка получения токена";
                    return;
                }
                window.localStorage.setItem( 'token', base64encodedText);
                window.location.reload();
            } );
        }
    });
}

function getAuthElements() {
    const authLogin = document.getElementById("auth-login");
    if ( ! authLogin ) {
        throw "#autn-login not found";
    }
    const authPassword = document.getElementById("auth-password");
    if ( ! authPassword ) {
        throw "#autn-password not found";
    }
    const authMessage = document.getElementById("auth-message");
    if ( ! authMessage ) {
        throw "#autn-message not found";
    }
    return [authLogin, authPassword, authMessage];
}