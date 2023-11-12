import {useState} from "react";
import {UserInterface} from "./userInterface";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";
import {Alert} from "react-bootstrap";
import {UserForm} from "./userForm";
import {userManager} from "../../../auth/userManager";

export function CreateUser() {
    const [error, setError] = useState<null | string>(null);
    const [user, setUser] = useState<UserInterface>({
        id: "",
        username: "",
        password: "",
        authorities: [],
        roles: [],
        accountExpired: false,
        accountLocked: false,
        credentialsExpired: false,
        enabled: true,
        mfaEnabled: false,
        name: "",
        email: "",
        emailVerified: false,
        phone: "",
        phoneVerified: false,
        picture: ""
    });
    const onSubmit = (user: UserInterface) => {
        // 2000 = success, 4001 = user already exists
        createUser(user).then((response) => {
            switch (response.status) {
                case 2000:
                    setUser(response.user);
                    setError(null);
                    break;
                case 4001:
                    setError("User already exists");
                    break;
                case 403:
                    navigate("/");
                    break;
                default:
                    setError(response.message);
            }
        }).catch((error) => {
            setError(error.message);
        });
    }
    const {t} = useTranslation();
    const navigate = useNavigate();
    return (
        <div>
            <h1>{t("admin.user.create")}</h1>
            {error && <Alert variant="danger">{error}</Alert>}
            <UserForm user={user} setUser={setUser} onSubmit={onSubmit}
                      onReset={() => navigate("/admin/users")}/>
        </div>
    );
}

async function createUser(data: UserInterface) {
    const user = await userManager.getUser();
    if (!user) {
        userManager.removeUser().then();
        userManager.revokeTokens().then();
        userManager.signinRedirect().then();
        throw new Error("User not logged in");
    }
    return fetch(`/api/admin/user/`, {
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
        } else if (response.status === 403) {
            return {
                status: response.status
            };
        } else {
            throw new Error("Error creating user");
        }
    });
}