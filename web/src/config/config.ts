import config_json from './config.json';

const public_url = process.env.PUBLIC_URL || config_json.public_url || "http://localhost:3000"
const config = {
    public_url: process.env.PUBLIC_URL || config_json.public_url,
    api_url: process.env.REACT_APP_API_URL || config_json.api_url
    || public_url + "/api",
    oidc: {
        authority: process.env.REACT_APP_OIDC_AUTHORITY || config_json.oidc.authority || public_url,
        redirect_uri: process.env.REACT_APP_OIDC_REDIRECT_URI || config_json.oidc.redirect_uri || public_url + "/oidc",
    }
}
export default config;