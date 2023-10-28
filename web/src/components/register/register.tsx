import React, {useState} from "react";
import {Alert, Button, Container, Form} from "react-bootstrap";
import {useTranslation} from "react-i18next";
import "../../form-container.css";
import i18n from "../../i18n/i18n";
import {emailPattern, passwordPattern} from "../../utils/regex";
import checkmark_filled from "../../img/checkmark_filled.svg";

export function Register() {
    const [status, setStatus] =
        useState<"idle" | "error" | "error_send_again" | "sent" | "send-again" | "success" | "exist">("idle");
    const [email, setEmail] =
        useState<{ value: string, valid?: boolean }>({value: ""});
    const [password, setPassword] =
        useState<{ value: string, valid?: boolean }>({value: ""});
    const [confirmPassword, setConfirmPassword] =
        useState<{ value: string, valid?: boolean }>({value: ""});

    // Handler for submit button, fold this function to have a clean code view
    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        postRegister(email.value, password.value).then((response) => {
            if (response.status === 200) {
                setStatus("success");
            } else if (response.status === 401) {
                setStatus("exist");
            } else {
                setStatus("error");
            }
        }).catch(() => {
            setStatus("error");
        });
    }

    const {t} = useTranslation();
    if (status === "success" ){
        return (
            <Container className="p-3 mt-5 p-md-5 form-container text-center">
                <h1 className={"mb-3 text-center"}>{t("register.sign_up")}</h1>
                <img className={"my-5"} src={checkmark_filled} alt={"success"} width={72} height={72}/>
                <p className={"text-center"}>{t("register.email_activate_description")}</p>
                <a className={"btn btn-link"} href={`/login`}>{t("register.sign_in")}</a>
            </Container>
        );
    }
    return (
        <Container className="p-3 mt-5 p-md-5 form-container">
            <h1 className={"mb-3"}>{t("register.sign_up")}</h1>
            {status === "error" && <Alert variant="danger">{t("register.error")}</Alert>}
            {status === "exist" && <Alert variant="warning">{t("register.existing_account")}</Alert>}
            <Form className="mt-3 justify-content-center" onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                    <Form.Label>{t("register.email")}</Form.Label>
                    <Form.Control
                        type="email"
                        value={email.value}
                        isValid={email.valid === true}
                        isInvalid={email.valid === false}
                        onChange={(e) =>
                            setEmail({...email, value: e.target.value})}
                        onBlur={(e) => {
                            const value = e.target.value;
                            setEmail({...email, valid: emailPattern.test(value)});
                        }}/>
                    <Form.Control.Feedback type="invalid">{t("register.email_invalid")}</Form.Control.Feedback>
                </Form.Group>
                <Form.Group className="mb-3">
                    <Form.Label>{t("register.password")}</Form.Label>
                    <Form.Control type="password"
                                  value={password.value}
                                  isValid={password.valid === true}
                                  isInvalid={password.valid === false}
                                  onChange={(e) =>
                                      setPassword({...password, value: e.target.value})}
                                  onBlur={(e) => {
                                      const value = e.target.value;
                                      setPassword({...password, valid: passwordPattern.test(value)});
                                  }}/>
                    <Form.Control.Feedback type="invalid">{t("register.password_invalid")}</Form.Control.Feedback>
                </Form.Group>
                <Form.Group className={"mb-3"}>
                    <Form.Label>{t("register.confirm_password")}</Form.Label>
                    <Form.Control type="password"
                                  value={confirmPassword.value}
                                  isValid={confirmPassword.valid === true}
                                  isInvalid={confirmPassword.valid === false}
                                  onChange={(e) =>
                                      setConfirmPassword({...confirmPassword, value: e.target.value})}
                                  onBlur={(e) => {
                                      const value = e.target.value;
                                      setConfirmPassword({...confirmPassword, valid: value === password.value});
                                  }}/>
                    <Form.Control.Feedback type="invalid">{t("register.password_mismatch")}</Form.Control.Feedback>
                </Form.Group>
                <Form.Group className={"d-flex flex-column flex-md-row gap-3"}>
                    <Button type="submit" variant={"primary"}
                            disabled={!(email.valid && password.valid &&
                                confirmPassword.valid)}>
                        {t("register.sign_up")}
                    </Button>
                    <span className={"ms-0 ms-md-5"}>
                        {t("register.already_have_account")}
                        <a className={"ms-3 btn btn-link"} href={`/login`}>
                            {t("register.sign_in")}
                        </a>
                    </span>
                </Form.Group>
            </Form>
        </Container>
    );
}

function postRegister(email: string, password: string) {
    return fetch(`/api/register`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            email: email,
            password: password,
            locale: i18n.language
        }),
    }).then((res) => {
        if (res.ok) {
            return res.json();
        } else {
            throw new Error("Error");
        }
    });
}

