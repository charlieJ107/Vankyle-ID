import {LoaderFunction} from "react-router-dom";

export const consentLoader: LoaderFunction<{
    clientId: string;
    scope: string[];
    state: string;
}> = async ({params}) => {
    if (params.clientId && params.scope && params.state) {

        const reqParams = new URLSearchParams();
        reqParams.append("client_id", params.clientId);
        reqParams.append("scope", params.scope);
        reqParams.append("state", params.state);
        const resp = await fetch(`http://localhost:8080/api/consent`, {
            method: "POST",
            headers: {
                "Content-Type": "x-www-form-urlencoded",
                "Accept": "application/json",
            },
            body: reqParams,
        });
        if (resp.status === 200) {
            return resp.json();
        } else {
            return {error: "Invalid parameters"};
        }
    } else {
        return {error: "Invalid parameters"};
    }
}