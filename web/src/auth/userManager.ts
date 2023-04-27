import {UserManager, UserManagerSettings} from "oidc-client-ts";
import config from "../config.json";


const userManagerSettings: UserManagerSettings = {
    authority: config.oidc.authority? config.oidc.authority : `${config.server_url}`,
    redirect_uri: config.oidc.redirect_uri ? config.oidc.redirect_uri : `${config.frontend_url}/oidc`,
    client_id: "account",
    client_secret: "client_secret",
    scope: config.oidc.scopes.join(" "),
    loadUserInfo: true
};

export const userManager = new UserManager(userManagerSettings);
