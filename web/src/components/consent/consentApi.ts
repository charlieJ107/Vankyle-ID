import config from '../../config/config';
export function getConsentInfo(params: URLSearchParams){
    return fetch(`${config.api_url}/consent?${params.toString()}`, {
        method: 'GET',
        headers: {
            "Accept": "application/json"
        }
    })
    .then(response => {
        if (!response.ok) {
            throw Error(response.statusText);
        }
        return response.json();
    });
}

export function postConsentInfo(data: URLSearchParams){
    return fetch(`${config.oidc.authority}/oauth2/authorize`, {
        method: 'POST',
        headers: {
            "Accept": "application/json"
        },
        body: data
    })
    .then(response => {
        if (!response.ok) {
            throw Error(response.statusText);
        }
        return response.json();
    });
}
