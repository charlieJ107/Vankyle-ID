import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {Alert, Button, Container, Form} from "react-bootstrap";
import {emailPattern} from "../../utils/regex";

export function ForgotPassword() {
    const [status, setStatus] =
        useState<"idle" | "error" | "sent" | "send-again" | "success" | "not_exist">("idle");
    const [email, setEmail] =
        useState<{ value: string, valid?: boolean }>({value: ""});

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setStatus("sent");
        setCounter(60);
        postForgotPassword(email.value).then((response) => {
            if (response.status === 200) {
                setStatus("success");
            } else if (response.status === 400) {
                setStatus("error");
            } else {
                setStatus("not_exist");
            }
        }).catch(() => {
            setStatus("error");
        });
    }
    const [counter, setCounter] = React.useState(0);
    useEffect(() => {
        const interval = setInterval(() => {
            counter > 0 && setCounter(counter - 1);
            if (status === "sent" && counter === 0) {
                setStatus("send-again");
            }
        }, 1000);
        return () => clearInterval(interval);
    }, [counter, status]);
    const {t} = useTranslation();
    return (
        <Container className="p-3 mt-5 p-md-5 form-container">
            <h1 className={"mb-3"}>{t("forgot_password.title")}</h1>
            {status === "error" && <Alert variant="danger">{t("forgot_password.error")}</Alert>}
            {status === "not_exist" && <Alert variant="warning">{t("forgot_password.not_exist_account")}</Alert>}
            <Form className="mt-3 justify-content-center" onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                    <Form.Label>{t("forgot_password.email")}</Form.Label>
                    <Form.Control
                        type="email"
                        value={email.value}
                        onChange={(e) =>
                            setEmail({...email, value: e.target.value})}
                        onBlur={() => {
                            const value = email.value;
                            const isValid = value.length > 0 && emailPattern.test(value);
                            setEmail({...email, valid: isValid});
                        }}
                        isValid={email.valid === true}
                        isInvalid={email.valid === false}/>
                    <Form.Control.Feedback type="invalid">{t("forgot_password.email_invalid")}</Form.Control.Feedback>
                </Form.Group>
                {status === "sent" && <Alert variant="success">{t("forgot_password.email_sent_description")}</Alert>}
                <Form.Group className="mb-3">
                    {status === "idle" ?
                        <Button variant="primary" type="submit"
                        disabled={email.valid === false} >
                            {t("forgot_password.send")}
                        </Button> :
                        <Button type={"submit"} variant="primary"
                            disabled={!((status === "send-again" || status === "success" || status === "error") && email.valid && counter === 0)}>
                            {t("forgot_password.send_again")+(counter > 0 ? `(${counter})` : "")}
                        </Button>
                    }

                    <Button variant="secondary" className="ms-2"
                            onClick={() => window.location.href=`/login`}>
                        {t("forgot_password.back")}
                    </Button>
                </Form.Group>
            </Form>
        </Container>
    );
}

function postForgotPassword(email: string) {
    return fetch(`/api/forgot-password`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({email}),
    }).then((res) => {
        if (res.ok) {
            return res.json();
        } else {
            throw new Error("Network response was not ok.");
        }
    });
}
