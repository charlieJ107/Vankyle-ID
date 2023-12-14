import {Alert, Button, Container, Table} from "react-bootstrap";
import {useTranslation} from "react-i18next";
import {useEffect, useState} from "react";
import {userManager} from "../../../auth/userManager";
import {useNavigate} from "react-router-dom";

export function Clients() {
    const [clients, setClients] = useState([]);
    const [error, setError] = useState<null | string>(null);
    const navigate = useNavigate();
    useEffect(() => {
        getClients().then((response) => {
            if (response.status === 403) {
                navigate("/");
            }
            setClients(response.clients);
            setError(null);
        }).catch((error) => {
            setError(error.message);
        });
    }, [setClients, setError, navigate]);
    const {t} = useTranslation();
    return (
        <Container className={"pt-3 container-lg"}>
            <h1>{t("admin.client.title")}</h1>
            {error && <Alert variant={"danger"}>{t("admin.client.error")} {error}</Alert>}
            <div>
                <Button className={"my-3"} variant={"primary"}
                        onClick={()=>navigate("/admin/clients/create")}>{t("admin.client.create")}</Button>
            </div>
            <Table striped bordered hover>
                <thead>
                <tr>
                    <th>{t("admin.client.id")}</th>
                    <th>{t("admin.client.client_id")}</th>
                    <th>{t("admin.client.name")}</th>
                    <th>{t("admin.client.actions")}</th>
                </tr>
                </thead>
                <tbody>
                {clients.map((client: any, index) => {
                    return (
                        <tr key={index}>
                            <td>{client.id.length > 8 ? client.id.substring(0, 8) + "..." : client.id}</td>
                            <td>{client.clientId}</td>
                            <td>{client.clientName}</td>
                            <td className={"d-flex gap-3"}>
                                <Button variant={"primary"}
                                        onClick={()=>navigate("/admin/clients/" + client.id)}>{t("admin.client.edit")}</Button>
                                <Button disabled variant={"danger"}>{t("admin.client.delete")}</Button>
                            </td>
                        </tr>
                    );
                })}
                </tbody>
            </Table>
        </Container>
    );
}

async function getClients() {
    const user = await userManager.getUser();
    if (user === null) {
        userManager.signinRedirect().then();
        throw new Error("User not logged in");
    }
    return fetch(`/api/admin/clients/`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `${user.token_type} ${user.access_token}`,
        },
    }).then((response) => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 403 || response.status === 404) {
            return {
                status: response.status
            }
        } else if (response.status === 401) {
            userManager.removeUser().then();
            userManager.revokeTokens().then();
            userManager.signinRedirect().then();
        } else {
            throw new Error(response.status + ": " + response.statusText);
        }
    });
}