import React from "react";
import {Alert, Button, Container, Form} from "react-bootstrap";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";
import {passwordPattern} from "../../../../utils/regex";
import config from "../../../../config/config";
import {User} from "oidc-client-ts";
import {userManager} from "../../../../auth/userManager";
import "../../../../form-container.css";

export function Password() {
    const [currentPassword, setCurrentPassword] =
        React.useState<{ value: string, valid?: boolean }>({value: ""});
    const [newPassword, setNewPassword] =
        React.useState<{ value: string, valid?: boolean }>({value: ""});
    const [confirmPassword, setConfirmPassword] =
        React.useState<{ value: string, valid?: boolean }>({value: ""});
    const [success, setSuccess] = React.useState<boolean>(false);
    const [error, setError] = React.useState<boolean>(false);
    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const user = await userManager.getUser();
        if (user) {
            putPasswordUpdate(currentPassword.value, newPassword.value, user).then(
                (response) => {
                    if (response.status === 200) {
                        setSuccess(true);
                    } else if (response.status === 403) {
                        setCurrentPassword({...currentPassword, valid: false});
                    } else {
                        setError(true);
                    }
                }
            ).catch(() => {
                setError(true);
            });
        } else {
            setError(true);
        }
    }
    const {t} = useTranslation();
    const navigateTo = useNavigate();
    return (
        <Container className={"p-3 p-md-5 form-container"}>
            <h1 className={"mb-3"}>{t("security.password.title")}</h1>
            <span>{t("security.password.description")}</span>
            <Form className={"mt-3"} onSubmit={handleSubmit}>
                {success && <Alert variant={"success"}>{t("security.password.success")}</Alert>}
                {error && <Alert variant={"danger"}>{t("security.password.error")}</Alert>}
                <Form.Group className={"mb-3"}>
                    <Form.Label>{t("security.password.current_password")}</Form.Label>
                    <Form.Control type="password" placeholder={t("security.password.current_password")!}
                                  value={currentPassword.value}
                                  isValid={currentPassword.valid === true}
                                  isInvalid={currentPassword.valid === false}
                                  onChange={(e) =>
                                      setCurrentPassword({valid: undefined, value: e.target.value})}
                                  onBlur={(e) => {
                                      const value = e.target.value;
                                      const valid = value.length > 0;
                                      setCurrentPassword({...currentPassword, valid});
                                  }}/>
                    <Form.Control.Feedback type="invalid">{t("security.password.password_invalid")}</Form.Control.Feedback>
                </Form.Group>
                <Form.Group className={"mb-3"}>
                    <Form.Label>{t("security.password.new_password")}</Form.Label>
                    <Form.Control type="password" placeholder={t("security.password.new_password")!}
                                  value={newPassword.value}
                                  isValid={newPassword.valid === true}
                                  isInvalid={newPassword.valid === false}
                                  onChange={(e) =>
                                      setNewPassword({...newPassword, value: e.target.value})}
                                  onBlur={(e) => {
                                      const valid = passwordPattern.test(e.target.value);
                                      setNewPassword({...newPassword, valid});
                                  }}
                    />
                    <Form.Control.Feedback type="invalid">
                        {t("security.password.password_invalid")}
                    </Form.Control.Feedback>
                    <Form.Text>{t("security.password.password_rules")}</Form.Text>
                </Form.Group>
                <Form.Group className={"mb-4"}>
                    <Form.Label>{t("security.password.confirm_password")}</Form.Label>
                    <Form.Control type="password" placeholder={t("security.password.confirm_password")!}
                                  value={confirmPassword.value}
                                  isValid={confirmPassword.valid === true}
                                  isInvalid={confirmPassword.valid === false}
                                  onChange={(e) =>
                                      setConfirmPassword({...confirmPassword, value: e.target.value})}
                                  onBlur={(e) => {
                                      const value = e.target.value;
                                      const valid = value.length > 0 && value === newPassword.value;
                                      setConfirmPassword({...confirmPassword, valid});
                                  }}/>
                    <Form.Control.Feedback type="invalid">
                        {t("security.password.password_mismatch")}
                    </Form.Control.Feedback>
                </Form.Group>
                <Form.Group className={"d-flex gap-3"}>
                    <Button type="submit"
                            disabled={
                                currentPassword.valid === false ||
                                newPassword.valid !== true ||
                                confirmPassword.valid !== true}>
                        {t("security.password.save")!}
                    </Button>
                    <Button variant={"outline-primary"}
                            onClick={() => {
                                navigateTo("/account");
                            }}>
                        {t("security.password.cancel")!}
                    </Button>
                </Form.Group>
            </Form>
        </Container>
    );
}

function putPasswordUpdate(currentPassword: string, newPassword: string, user: User) {
    return fetch(`${config.api_url}/account/password`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${user.token_type} ${user.access_token}`
        },
        body: JSON.stringify({
            currentPassword: currentPassword,
            newPassword: newPassword,
        }),
    }).then((res) => {
        if (res.ok) {
            return res.json();
        } else {
            throw new Error(res.statusText);
        }
    });
}