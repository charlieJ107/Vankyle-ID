export function getConsentInfo(params: URLSearchParams){
    return fetch(`/api/consent?${params.toString()}`, {
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
    return fetch(`/oauth2/authorize`, {
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
