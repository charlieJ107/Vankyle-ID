export interface ClientInterface {
    id: string;
    clientId: string;
    clientIdIssuedAt: number | null;
    clientName: string;
    clientSecret: string;
    clientSecretExpiresAt: string | null;
    clientAuthenticationMethods: ClientAuthenticationMethod[];
    authorizationGrantTypes: AuthorizationGrantType[];
    redirectUris: string[];
    scopes: string[];
    clientSettings: {
        requireProofKey: boolean;
        requireAuthorizationConsent: boolean;
        jwkSetUrl: string;
        tokenEndpointAuthenticationSigningAlgorithm: JwsAlgorithm | null;
    };
    tokenSettings: {
        authorizationCodeTimeToLive: number;
        accessTokenTimeToLive: number;
        accessTokenFormat: "self-contained" | "reference";
        refreshTokenTimeToLive: number;
        idTokenSignatureAlgorithm: JwsAlgorithm;
    }
}

export enum JwsAlgorithm {
    HS256 = "HS256",
    HS384 = "HS384",
    HS512 = "HS512",
    RS256 = "RS256",
    RS384 = "RS384",
    RS512 = "RS512",
    ES256 = "ES256",
    ES384 = "ES384",
    ES512 = "ES512",
    PS256 = "PS256",
    PS384 = "PS384",
    PS512 = "PS512"
}

export enum ClientAuthenticationMethod {
    NONE = "none",
    CLIENT_SECRET_BASIC = "client_secret_basic",
    CLIENT_SECRET_POST = "client_secret_post",
    CLIENT_SECRET_JWT = "client_secret_jwt",
    PRIVATE_KEY_JWT = "private_key_jwt",
}

export enum AuthorizationGrantType {
    AUTHORIZATION_CODE = "authorization_code",
    REFRESH_TOKEN = "refresh_token",
    CLIENT_CREDENTIALS = "client_credentials",
    JWT_BEARER = "urn:ietf:params:oauth:grant-type:jwt-bearer",
}