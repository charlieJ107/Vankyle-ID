import {useEffect, useState} from "react";
import {userManager} from "../../../auth/userManager";
import config from "../../../config/config"
import {UserForm} from "./userForm";
import {Loading} from "../../shared/loading";
import {useTranslation} from "react-i18next";
import {UserInterface} from "./userInterface";
import {Alert, Button, Container} from "react-bootstrap";
import {useNavigate} from "react-router-dom";

export function EditUser() {
    const [status, setStatus] = useState("loading");
    const [error, setError] = useState<null | string>(null);
    const [user, setUser] = useState<UserInterface | null>(null);
    const userId = window.location.pathname.split("/").pop();
    useEffect(() => {
        if (!userId) {
            setStatus("error");
            setError("Error getting user");
            return;
        }
        getUser(userId).then((response) => {

            switch (response.status) {
                case 2000:
                    setUser(response.user);
                    setStatus("idle");
                    setError(null);
                    break;
                case 403:
                    navigate("/");
                    break;
                case 404:
                    navigate("/admin/users?error=notfound&id=" + userId);
                    break;
                default:
                    setStatus("error");
                    setError(response.message);
            }
        }).catch((error) => {
            setStatus("error");
            setError(error.message);
        });
    }, [setStatus, setError, setUser, userId]);

    const onSubmit = (user: UserInterface) => {
        putUser(userId || user.id, user).then((response) => {
            // Convert the if else below to a switch statement
            switch (response.status) {
                case 2000:
                    setUser(response.user);
                    setStatus("success");
                    setError(null);
                    break;
                case 403:
                    navigate("/");
                    break;
                case 404:
                    navigate("/admin/users?error=notfound&id=" + user.id);
                    break;
                default:
                    setStatus("error");
                    setError(response.message);
            }
        }).catch((error) => {
            setStatus("error");
            setError(error.message);
        });
    }

    const {t} = useTranslation();
    const navigate = useNavigate();
    if (user && (status === "idle" || status === "success")) {
        return (
            <Container>
                <h1>{t("admin.user.detail.title")}</h1>
                {status === "success" && <Alert variant="success">{t("admin.user.detail.success")}</Alert>}
                <UserForm user={user} setUser={setUser} onSubmit={onSubmit}
                          onReset={() => navigate("/admin/users")}/>
            </Container>
        );
    } else {
        return (
            <Container>
                <h1>{t("admin.user.detail.title")}</h1>
                {error && <Alert variant="danger">{error}</Alert>}
                <div className={"mt-3 d-flex justify-content-center"}>
                    <Button onClick={window.history.back}>{t("utils.back")}</Button>
                </div>
                <Loading/>
            </Container>
        );
    }
}

async function getUser(userId: string) {
    const user = await userManager.getUser()
    if (user === null) {
        userManager.signinRedirect().then();
        throw new Error("User not logged in");
    }
    return fetch(`${config.api_url}/admin/user/` + userId, {
        method: "GET",
        headers: {
            "Accept": "application/json",
            'Authorization': `${user.token_type} ${user.access_token}`
        }
    }).then((response) => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 401) {
            userManager.removeUser().then();
            userManager.revokeTokens().then();
            userManager.signinRedirect().then();
            throw new Error("User not logged in");
        } else if (response.status === 403 || response.status === 404) {
            return {
                status: response.status
            };
        } else {
            throw new Error("Error getting user");
        }
    });
}

async function putUser(userId: string, user: UserInterface) {
    const currentUser = await userManager.getUser()
    if (currentUser === null) {
        userManager.signinRedirect().then();
        throw new Error("User not logged in");
    }
    return fetch(`${config.api_url}/admin/user/` + userId, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
            'Authorization': `${currentUser.token_type} ${currentUser.access_token}`
        },
        body: JSON.stringify(user)
    }).then((response) => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 401) {
            userManager.removeUser().then();
            userManager.revokeTokens().then();
            userManager.signinRedirect().then();
            throw new Error("User not logged in");
        } else if (response.status === 403 || response.status === 404) {
            return {
                status: response.status
            };
        } else {
            throw new Error("Error updating user");
        }
    });
}