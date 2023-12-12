import {Loading} from "../../shared/loading";
import {ClientForm} from "./clientForm";
import {Alert} from "react-bootstrap";
import {useNavigate} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {ClientInterface, JwsAlgorithm} from "./clientInterface";
import {userManager} from "../../../auth/userManager";
import {useState} from "react";

export function CreateClient() {
    const [status, setStatus] =
        useState("idle");
    const [error, setError] =
        useState<null | string>(null);
    const [client, setClient] =
        useState<ClientInterface>({
            id: "",
            clientId: randomString(32),
            clientIdIssuedAt: Date.now(),
            clientName: "",
            clientSecret: randomString(32),
            clientSecretExpiresAt: null,
            clientAuthenticationMethods: [],
            authorizationGrantTypes: [],
            redirectUris: [],
            scopes: ["openid"],
            clientSettings: {
                requireProofKey: false,
                requireAuthorizationConsent: false,
                jwkSetUrl: "",
                tokenEndpointAuthenticationSigningAlgorithm: null,
            },
            tokenSettings: {
                authorizationCodeTimeToLive: 5,
                accessTokenTimeToLive: 5,
                accessTokenFormat: "self-contained",
                refreshTokenTimeToLive: 5,
                idTokenSignatureAlgorithm: JwsAlgorithm.RS256,
            }
        });

    const onSubmit = (client: ClientInterface) => {
        createClient(client).then((response) => {
            if (response.status === 2000) {
                setClient(response.client);
                setStatus("success");
                setError(null);
            } else if (response.status === 4000) {
                setStatus("failed");
                setError(response.message);
            } else {
                setStatus("error");
                setError("Error creating client");
            }
        }).catch((error) => {
            setStatus("error");
            setError(error.message);
        });
    }

    const {t} = useTranslation();
    const navigate = useNavigate();
    if (client && (status === "idle" || status === "success" || status === "failed")) {
        return (
            <div>
                <h1>{t("admin.client.create")}</h1>
                {error && <Alert variant="danger">{error}</Alert>}
                <ClientForm client={client} setClient={setClient} onSubmit={onSubmit}
                            onReset={() => navigate("/admin/client")}/>
            </div>
        );
    } else {
        return <Loading/>;
    }
}

async function createClient(data: ClientInterface) {
    const user = await userManager.getUser();
    if (!user) {
        userManager.removeUser().then();
        userManager.revokeTokens().then();
        userManager.signinRedirect().then();
        throw new Error("User not logged in");
    }
    return fetch(`/api/admin/client/`, {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization": `${user.token_type} ${user.access_token}`
        },
        body: JSON.stringify(data)
    }).then((response) => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 401) {
            userManager.removeUser().then();
            userManager.revokeTokens().then();
            userManager.signinRedirect().then();
        } else {
            throw new Error("Error creating user");
        }
    });
}

function randomString(length: number): string {
    const charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    let result = "";
    for (let i = 0; i < length; i++) {
        result += charset.charAt(Math.floor(Math.random() * charset.length));
    }
    return result;
}