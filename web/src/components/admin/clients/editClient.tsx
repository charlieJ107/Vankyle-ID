import {useEffect, useState} from "react";
import {ClientInterface} from "./clientInterface";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";
import {Alert, Button, Container} from "react-bootstrap";
import {ClientForm} from "./clientForm";
import {Loading} from "../../shared/loading";
import {userManager} from "../../../auth/userManager";

export function EditClient() {
    const [status, setStatus] =
        useState("loading");
    const [error, setError] =
        useState<null | string>(null);
    const [client, setClient] =
        useState<ClientInterface | null>(null);
    const id = window.location.pathname.split("/").pop();
    const navigate = useNavigate();
    useEffect(() => {
        if (!id) {
            setStatus("error");
            setError("Error getting client");
            return;
        }
        getClient(id).then((response) => {
            switch (response.status) {
                case 2000:
                    setClient(response.client);
                    setStatus("idle");
                    setError(null);
                    break;
                case 403:
                    navigate("/");
                    break;
                case 404:
                    navigate("/admin/clients?error=notfound&id=" + id);
                    break;
                default:
                    setStatus("error");
                    setError(response.message);
            }
        }).catch((error) => {
            setStatus("error");
            setError(error.message);
        });
    }, [setStatus, setError, setClient, navigate, id]);
    const onSubmit = (client: ClientInterface) => {
        putClient(id || client.id, client).then((response) => {
            if (response.status === 2000) {
                setClient(response.client);
                setStatus("success");
                setError(null);
            } else if (response.status === 404) {
                navigate("/admin/clients?error=notfound&id=" + client.id);
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
    if (client && (status === "idle" || status === "success")) {
        return (
            <Container className={"pt-3 container-lg"}>
                <h1 className={"mb-3"}>{t("admin.client.detail.title")}</h1>
                {status === "success" && <Alert variant="success">{t("admin.user.detail.success")}</Alert>}
                {error && <Alert className={"mb-3"} variant="danger">{error}</Alert>}
                <ClientForm client={client} setClient={setClient} onSubmit={onSubmit}
                            onReset={() => navigate("/admin/client")}/>
            </Container>
        );
    } else {
        return (
            <Container>
                <h1>{t("admin.client.detail.title")}</h1>
                {error && <Alert variant="danger">{error}</Alert>}
                <div className={"mt-3 d-flex justify-content-center"}>
                    <Button onClick={window.history.back}>{t("utils.back")}</Button>
                </div>
                <Loading/>
            </Container>
        );
    }
}

async function getClient(id: string) {
    const user = await userManager.getUser();
    if (!user) {
        userManager.removeUser().then();
        userManager.revokeTokens().then();
        userManager.signinRedirect().then();
        throw new Error("User not logged in");
    }
    return fetch(`/api/admin/client/${id}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            'Authorization': `${user.token_type} ${user.access_token}`

        },
    }).then((response) => {
        if (response.ok) {
            return response.json();
        }
        if (response.status === 401) {
            userManager.removeUser().then();
            userManager.revokeTokens().then();
            userManager.signinRedirect().then();
            throw new Error("User not logged in");
        } else if (response.status === 403) {
            useNavigate()("/");
        } else if (response.status === 404) {
            return {
                status: 404,
                client: null,
                message: "Client not found"
            }
        } else {
            throw new Error("Error getting client");
        }
    });
}

async function putClient(id: string, data: ClientInterface) {
    const user = await userManager.getUser();
    if (!user) {
        userManager.removeUser().then();
        userManager.revokeTokens().then();
        userManager.signinRedirect().then();
        throw new Error("User not logged in");
    }
    return fetch(`/api/admin/client/${data.id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            'Authorization': `${user.token_type} ${user.access_token}`
        },
        body: JSON.stringify(data),
    }).then((response) => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 401) {
            userManager.removeUser().then();
            userManager.revokeTokens().then();
            userManager.signinRedirect().then();
            throw new Error("User not logged in");
        } else if (response.status === 403) {
            useNavigate()("/");
        } else if (response.status === 404) {
            return {
                status: 404,
                client: null,
                message: "Client not found"
            }
        } else {
            throw new Error("Error creating client");
        }
    });
}