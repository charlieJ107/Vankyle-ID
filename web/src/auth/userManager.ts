import {UserManager, UserManagerSettings} from "oidc-client-ts";




const userManagerSettings: UserManagerSettings = {
    authority: "/",
    redirect_uri: "/oidc",
    client_id: "account",
    client_secret: "client_secret",
    scope: "openid profile email phone address roles",
    loadUserInfo: true
};

export const userManager = new UserManager(userManagerSettings);
