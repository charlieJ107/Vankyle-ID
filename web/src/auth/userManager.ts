import {UserManager, UserManagerSettings} from "oidc-client-ts";
import config from "../config/config";


const userManagerSettings: UserManagerSettings = {
    authority: config.oidc.authority,
    redirect_uri: config.oidc.redirect_uri,
    client_id: "account",
    client_secret: "client_secret",
    scope: "openid profile",
    loadUserInfo: true
};

export const userManager = new UserManager(userManagerSettings);
