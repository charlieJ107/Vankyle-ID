import {Alert, Button, Collapse, Form, Modal} from "react-bootstrap";
import React, {useEffect} from "react";
import {useTranslation} from "react-i18next";
import {emailPattern} from "../../../utils/regex";
import i18n from "../../../i18n/i18n";
import {userManager} from "../../../auth/userManager";

export function EditEmail(props: { show: boolean, email: string | null, handleHide: () => void }) {
    const [status, setStatus] =
        React.useState<"idle" | "sent" | "send-again" | "error" | "success">("idle");
    const [email, setEmail] =
        React.useState<{ value: string, valid?: boolean }>({value: props.email ? props.email : ""});
    const [verificationCode, setVerificationCode] =
        React.useState<{ value: string, valid?: boolean }>({value: ""});

    // Counter for resend verification code, fold these function to have a cleaner view
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

    // Send verification code API calls, fold these function to have a cleaner view
    const handleSendVerificationCode = () => {
        postSendVerificationCode(email.value).then(
            (response) => {
                if (response.status === 200) {
                    setStatus("sent");
                    setCounter(60);
                } else {
                    setStatus("error");
                }
            }
        ).catch(() => {
            setStatus("error");
        });
    }

    // Update email API calls, fold these function to have a cleaner view
    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        putEmailUpdate(email.value, verificationCode.value).then(
            (response) => {
                if (response.status === 200) {
                    setStatus("success");
                } else if (response.status === 400) {
                    setVerificationCode({...verificationCode, valid: false});
                } else {
                    setStatus("error");
                }
            }
        ).catch(() => {
            setStatus("error");
        });
    }

    // Handle closing modal, fold this function to have a cleaner view
    const handleClose = () => {
        setStatus("idle");
        setEmail({...email, valid: undefined});
        setVerificationCode({value: "", valid: undefined});
        props.handleHide();
    }
    const {t} = useTranslation();
    return (
        <Modal show={props.show} onHide={handleClose} backdrop={"static"}>
            <Modal.Header closeButton>
                <Modal.Title>{t("profile.email.title")}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form id={"email-form"} onSubmit={handleSubmit}>
                    {status === "success" && <Alert variant={"success"}>{t("profile.email.success")}</Alert>}
                    {status === "error" && <Alert variant={"danger"}>{t("profile.email.error")}</Alert>}
                    <Form.Group>
                        <Form.Label>{t("profile.email.email_address")}</Form.Label>
                        <Form.Control value={email.value} type="email"
                                      placeholder={t("profile.email.enter_email")!}
                                      isValid={email.valid === true}
                                      isInvalid={email.valid === false}
                                      onChange={(e) => {
                                          setEmail({...email, value: e.target.value});
                                      }}
                                      onBlur={() => {
                                          setEmail({...email, valid: emailPattern.test(email.value)});
                                      }}/>
                        <Form.Control.Feedback type="invalid">
                            {t("profile.email.email_invalid")}
                        </Form.Control.Feedback>
                        {status === "idle" ?
                            <Button className={"mt-3"}
                                    disabled={!email.valid}
                                    onClick={handleSendVerificationCode}>
                                {t("profile.email.send_verification_code")}
                            </Button> :
                            <Button className={"mt-3"}
                                    disabled={!((status === "send-again" || status === "success" || status === "error") && email.valid && counter === 0)}
                                    onClick={() => {
                                        handleSendVerificationCode();
                                    }}>
                                {t("profile.email.send_verification_code_again") + (counter > 0 ? `(${counter})` : "")}
                            </Button>
                        }

                    </Form.Group>
                    <Collapse in={status === "sent" || status === "send-again"}>
                        <Form.Group className={"mt-3"}>
                            <Form.Label>{t("profile.email.verification_code")}</Form.Label>
                            <Form.Control type="text" placeholder="Enter verification code"
                                          value={verificationCode.value}
                                          onChange={(e) =>
                                              setVerificationCode({...verificationCode, value: e.target.value})}
                                          isValid={verificationCode.valid === true}
                                          isInvalid={verificationCode.valid === false}
                                          onBlur={() => {
                                              setVerificationCode({
                                                  ...verificationCode,
                                                  valid: verificationCode.value.length === 6
                                              });
                                          }}/>
                            <Form.Control.Feedback
                                type="invalid">{t("profile.email.verification_code_invalid")}</Form.Control.Feedback>
                        </Form.Group>
                    </Collapse>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant={status === "success" ? "outline-primary" : "primary"} type="submit" form={"email-form"}
                        disabled={!(email.valid && verificationCode.valid)}>{t("profile.email.save")}</Button>
                <Button variant={status === "success" ? "primary" : "secondary"}
                        onClick={handleClose}>
                    {status === "success" ? t("profile.email.close") : t("profile.email.cancel")}
                </Button>
            </Modal.Footer>
        </Modal>
    );
}

async function postSendVerificationCode(email: string) {
    const user = await userManager.getUser();
    if (!user) throw new Error("User not logged in");
    const res = await fetch(`/api/account/email`, {
        method: "POST",
        body: JSON.stringify({email: email, locale: i18n.language}),
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${user.token_type} ${user.access_token}`
        },
    });
    if (res.ok) {
        return await res.json();
    } else {
        throw new Error("Failed to send verification code");
    }
}

async function putEmailUpdate(email: string, code: string) {
    const user = await userManager.getUser();
    if (!user) throw new Error("User not logged in");
    const res = await fetch(`/api/account/email`, {
        method: "PUT",
        body: JSON.stringify({email: email, code: code}),
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${user.token_type} ${user.access_token}`
        }
    });
    if (res.ok) {
        return await res.json();
    } else {
        throw new Error("Failed to verify email");
    }
}