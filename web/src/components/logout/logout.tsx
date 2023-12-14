import {Loading} from "../shared/loading";
import {useEffect} from "react";
import {userManager} from "../../auth/userManager";

export function Logout() {
    useEffect(() => {
        userManager.signoutRedirect().then();
        // postLogout().then((res) => {
        //     switch (res.status) {
        //         case 200:
        //             userManager.signoutRedirect().then();
        //             window.location.href = "/";
        //             break;
        //         case 302:
        //             userManager.signoutRedirect().then();
        //             window.location.href = res.redirectUrl;
        //             break;
        //         default:
        //             userManager.signoutRedirect().then();
        //             window.location.href = "/";
        //     }
        // });
    });
    return (<Loading/>);
}

async function postLogout() {
    return fetch(`/api/logout`, {
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