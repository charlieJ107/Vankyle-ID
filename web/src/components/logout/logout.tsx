import {Loading} from "../shared/loading";
import {useEffect} from "react";
import config from "../../config/config";
import {userManager} from "../../auth/userManager";

export function Logout() {
    useEffect(() => {
        postLogout().then((res) => {
            switch (res.status) {
                case 200:
                    userManager.removeUser().then();
                    userManager.revokeTokens().then();
                    window.location.href = "/";
                    break;
                case 302:
                    userManager.removeUser().then();
                    userManager.revokeTokens().then();
                    window.location.href = res.redirectUrl;
                    break;
                default:
                    userManager.removeUser().then();
                    userManager.revokeTokens().then();
                    window.location.href = "/";
            }
        });
    });
    return (<Loading/>);
}

function postLogout() {
    return fetch(`${config.api_url}/logout`, {
        method: "POST",
        credentials: "include"
    }).then((res) => {
        if (res.redirected) {
            return {status: 302, redirectUrl: res.url};
        } else if (res.ok) {
            return res.json().then((data) => {
                return {status: data.status, redirectUrl: data.redirectUrl};
            });
        } else {
            return {status: res.status, redirectUrl: undefined};
        }
    });
}