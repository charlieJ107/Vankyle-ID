import {Alert, Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import {useTranslation} from "react-i18next";
import React, {useState} from "react";
import "../../form-container.css"
import {useNavigate} from "react-router-dom";

export function Login() {
    const [error, setError] = useState<string | null>(null);
    const [username, setUsername] =
        useState<string>("");
    const [password, setPassword] =
        useState<string>("");
    const [rememberMe, setRememberMe] = useState<boolean>(false);
    const [agreedTerms, setAgreedTerms] = useState<boolean>(true);
    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        postLogin({
            username: username,
            password: password,
            rememberMe
        }).then((response) => {
            // Since we are using the api, the response will be a json object with status code, the response will always
            // be 200. If the login is successful, the status code will be 200, otherwise it will be 401 or 403
            switch (response.status) {
                case 302:
                    window.location.href = response.redirectUrl;
                    break;
                case 401:
                    setError(t("login.error_401"));
                    break;
                case 403:
                    setError(t("login.error_403"));
                    break;
                default:
                    setError(t("login.error"));
                    break;
            }
        }).catch(() => setError(t("login.error")));
    }
    const {t} = useTranslation();
    const navigate = useNavigate();
    return (
        <Container className={"mt-5 p-3 p-md-5 d-md-grid justify-content-center"}>
            <Card className={"shadow form-container p-5"}>
                <h1 className={"text-center"}>{t("login.title")}</h1>
                {error && <Alert variant={"danger"}>{error}</Alert>}
                <Form className={"m-4"} onSubmit={handleSubmit}>
                    <Form.Group className={"mt-3"}>
                        <Form.Label>{t("login.username")}</Form.Label>
                        <Form.Control type="text" name={"username"} value={username}
                                      onChange={(e) =>
                                          setUsername(e.target.value)}/>
                        <Form.Control.Feedback type="invalid">{t("login.username_required")}</Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group className={"mt-3"}>
                        <Form.Label>{t("login.password")}</Form.Label>
                        <Form.Control type="password" name={"password"} value={password}
                                      onChange={(e) =>
                                          setPassword(e.target.value)}/>
                        <Form.Control.Feedback type="invalid">{t("login.password_required")}</Form.Control.Feedback>
                    </Form.Group>
                    <Row className={"my-3 justify-content-around"}>
                        <Col className={"text-start"}>
                            <Form.Group className={"m-1"}>
                                <Form.Check type="checkbox" label={t("login.remember_me")} checked={rememberMe}
                                            onChange={event => setRememberMe(event.target.checked)}/>
                            </Form.Group>
                        </Col>
                        <Col className={"text-end"}>
                            <Button variant="link"
                                    onClick={() => navigate("/forgot-password")}>{t("login.forgot_password")}</Button>
                        </Col>
                    </Row>
                    <Row className={"mt-3 d-flex justify-content-around gap-3"}>
                        <Col>
                            <Button className={"w-100"} variant="primary" type="submit"
                                    disabled={!(username.length > 0 && password.length > 0) && agreedTerms}>
                                {t("login.sign_in")}
                            </Button>
                        </Col>
                        <Col>
                            <Button className={"w-100"} variant="outline-primary"
                                    onChange={() => navigate("/register")}>
                                {t("login.sign_up")}
                            </Button>
                        </Col>
                    </Row>
                    <Form.Group className={"mt-3"}>
                        {!agreedTerms && <Alert variant={"warning"}
                                                className={"mt-3 py-1"}>{t("login.agree_terms_required")}</Alert>}
                        <Form.Check className={"text-body-tertiary"} type="checkbox"
                                    label={t("login.agree_terms")} checked={agreedTerms}
                                    onChange={(e) => setAgreedTerms(e.target.checked)}/>
                    </Form.Group>
                </Form>
            </Card>
        </Container>
    );
}

function postLogin({username, password, rememberMe}: { username: string, password: string, rememberMe: boolean }) {
    const urlSearchParams = new URLSearchParams();
    urlSearchParams.append("username", username);
    urlSearchParams.append("password", password);
    if (rememberMe) {
        urlSearchParams.append("rememberMe", "on");
    }
    return fetch(`/api/login?${urlSearchParams.toString()}`, {
        method: "POST",
        headers: {
            "Accept": "application/json",
            "Content-Type": "x-www-form-urlencoded"
        },
        credentials: "include"
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error("Login failed");
        }
    });
}