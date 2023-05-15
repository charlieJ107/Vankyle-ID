import config from "../../../config/config";
import {Alert, Button, Container, Table} from "react-bootstrap";
import {useTranslation} from "react-i18next";
import {useEffect, useState} from "react";
import {userManager} from "../../../auth/userManager";
import {useNavigate} from "react-router-dom";

export function Users() {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState<null | string>(null);
    const navigate = useNavigate();
    useEffect(() => {
        getUsers().then((response) => {
            if (response.status === 403) {
                navigate("/");
                return;
            }
            setUsers(response.users);
            setError(null);
        }).catch((error) => {
            setError(error.message);
        });
    }, [setUsers, setError, navigate]);
    const {t} = useTranslation();
    return (
        <Container className={"pt-3 container-lg"}>
            <h1>{t("admin.user.title")}</h1>
            {error && <Alert variant={"danger"}>{t("admin.user.error")} {error}</Alert>}
            <div>
                <Button className={"my-3"} variant={"primary"} href={"/admin/user/create"}>
                    {t("admin.user.create")}
                </Button>
            </div>
            <Table striped bordered hover>
                <thead>
                <tr>
                    <th>{t("admin.user.id")}</th>
                    <th>{t("admin.user.username")}</th>
                    <th>{t("admin.user.name")}</th>
                    <th>{t("admin.user.actions")}</th>
                </tr>
                </thead>
                <tbody>
                {users.map((user: any, index) => {
                    return (
                        <tr key={index}>
                            <td>{user.id.length > 8 ? user.id.substring(0, 8) + "..." : user.id}</td>
                            <td>{user.username}</td>
                            <td>{user.name}</td>
                            <td className={"d-flex gap-3"}>
                                <Button variant={"primary"} href={"/admin/users/" + user.id}>
                                    {t("admin.user.edit")}
                                </Button>
                                {/*TODO: Delete user*/}
                                <Button variant={"danger"} disabled>{t("admin.user.delete")}</Button>
                            </td>
                        </tr>
                    )
                })}
                </tbody>
            </Table>
        </Container>
    )
}

async function getUsers() {
    const user = await userManager.getUser();
    if (user === null) {
        userManager.signinRedirect().then();
        throw new Error("User not logged in");
    }
    return fetch(`${config.api_url}/admin/user/`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Authorization': `${user.token_type} ${user.access_token}`
        }
    })
        .then(res => {
            if (res.ok) {
                return res.json()
            } else if (res.status === 401) {
                userManager.removeUser().then();
                userManager.revokeTokens().then();
                userManager.signinRedirect().then();
            } else if (res.status === 403) {
                return {
                    status: res.status
                }
            } else {
                throw new Error("Failed to get users");
            }
        });
}