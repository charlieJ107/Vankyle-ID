import {Button, Container, Form} from "react-bootstrap";
import {useTranslation} from "react-i18next";
import React, {useEffect, useState} from "react";
import dissatisfied from "../../img/dissatisfied.svg";
import checkmarkFilled from "../../img/checkmark_filled.svg";
import {Loading} from "../shared/loading";
import {passwordPattern} from "../../utils/regex";

export function ResetPassword() {
    const [status, setStatus] =
        useState<"idle" | "error" | "invalid" | "success" | "loading">("loading");
    const [password, setPassword] =
        useState<{ value: string, valid?: boolean }>({value: ""});
    const [confirmPassword, setConfirmPassword] =
        useState<{ value: string, valid?: boolean }>({value: ""});
    const [code, setCode] = useState<string>("");

    // Handler for submit button, fold this function to have a cleaner view
    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        postResetPassword(code, password.value).then((response) => {
            if (response.status === 200) {
                setStatus("success");
            } else if (response.status === 401) {
                setStatus("invalid");
            } else {
                setStatus("error");
            }
        }).catch(() => {
            setStatus("error");
        });
    }

    useEffect(() => {
        if (status !== "loading") return;
        const params = new URLSearchParams(window.location.search);
        const code = params.get("code");
        if (code) {
            fetch(`/api/reset-password?code=${code}`, {
                method: "GET",

            }).then((response) => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw Error(response.statusText);
                }
            }).then((response) => {
                if (response.status === 200) {
                    setCode(code);
                    setStatus("idle");
                } else {
                    setStatus("invalid");
                }
            }).catch((e) => {
                console.log(e);
                setStatus("error");
            });
        } else {
            setStatus("invalid");
        }
    }, [status]);
    const {t} = useTranslation();
    if (status === "idle") {
        return (
            <Container className="p-3 mt-5 p-md-5 form-container">
                <h1 className={"mb-3"}>{t("reset_password.title")}</h1>
                <Form className="mt-3 justify-content-center" onSubmit={handleSubmit}>
                    <Form.Group className="mb-3">
                        <Form.Label>{t("reset_password.password")}</Form.Label>
                        <Form.Control type="password"
                                      value={password.value}
                                      onChange={(e) =>
                                          setPassword({...password, value: e.target.value})}
                                      onBlur={(e) => {
                                          const value = e.target.value;
                                          setPassword({
                                              ...password,
                                              valid: passwordPattern.test(value)
                                          });
                                      }} isValid={password.valid === true} isInvalid={password.valid === false}/>
                        <Form.Control.Feedback type="invalid">{t("reset_password.password_invalid")}</Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group className={"mb-3"}>
                        <Form.Label>{t("reset_password.confirm_password")}</Form.Label>
                        <Form.Control type="password"
                                      value={confirmPassword.value}
                                      onChange={(e) =>
                                          setConfirmPassword({...confirmPassword, value: e.target.value})}
                                      onBlur={(e) => {
                                          const value = e.target.value;
                                          setConfirmPassword({
                                              ...confirmPassword,
                                              valid: value.length > 0 && value === password.value
                                          });
                                      }} isValid={confirmPassword.valid === true}
                                      isInvalid={confirmPassword.valid === false}/>
                        <Form.Control.Feedback type="invalid">{t("reset_password.password_mismatch")}</Form.Control.Feedback>
                    </Form.Group>
                    <Button variant="primary" type="submit">
                        {t("reset_password.submit")}
                    </Button>
                </Form>
            </Container>
        );
    } else if (status === "success") {
        return (
            <Container className="p-3 mt-5 p-md-5 form-container text-center">
                <h1 className={"mb-3 text-center"}>{t("reset_password.title")}</h1>
                <img className={"my-5"} src={checkmarkFilled} width={72} height={72} alt={"Invalid"}/>
                <p className={"text-center"}>{t("reset_password.success")}</p>
                <div><a className={"btn btn-link"} href={`/login`}>{t("reset_password.sign_in")}</a></div>
            </Container>
        );
    } else if (status === "loading") {
        return <Loading/>;
    } else if (status === "invalid") {
        return (
            <Container className="p-3 mt-5 p-md-5 form-container text-center">
                <h1 className={"mb-3 text-center"}>{t("reset_password.title")}</h1>
                <img className={"my-5"} src={dissatisfied} width={72} height={72} alt={"Invalid"}/>
                <p className={"text-center"}>{t("reset_password.invalid")}</p>
            </Container>
        );
    } else {
        return (
            <Container className="p-3 mt-5 p-md-5 form-container text-center">
                <h1 className={"mb-3 text-center"}>{t("reset_password.title")}</h1>
                <img className={"my-5"} src={dissatisfied} width={72} height={72} alt={"Invalid"}/>
                <p className={"text-center"}>{t("reset_password.error")}</p>
            </Container>
        );
    }

}

function postResetPassword(code: string, password: string) {
    return fetch(`/api/reset-password`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            code: code,
            password: password,
        }),
    });
}