import React, {useEffect} from "react";
import {useTranslation} from "react-i18next";
import {Alert, Button, Collapse, Form, InputGroup, Modal} from "react-bootstrap";
import {phonePatternTest} from "../../../utils/regex";
import {User} from "oidc-client-ts";
import config from "../../../config/config";
import i18n from "../../../i18n/i18n";

export function Phone(props: {
    show: boolean,
    user: User | null,
    handleHide: () => void
}) {
    const [status, setStatus] =
        React.useState<"idle" | "sent" | "send-again" | "error" | "success">("idle");
    const [phone, setPhone] =
        React.useState<{ prefix: string, value: string, valid?: boolean }>({prefix: "+44", value: ""});
    const [verificationCode, setVerificationCode] =
        React.useState<{ value: string, valid?: boolean }>({value: ""});
    const [counter, setCounter] = React.useState(0);
    useEffect(() => {
        if (props.user) {
            const phone_number = props.user.profile.phone_number;
            if (phone_number) {
                const phones = phone_number.split(" ");
                const prefix = phones[0];
                const value = phones[1];
                setPhone({prefix, value});
            } else {
                setPhone({prefix: "+44", value: ""});
            }
        }
    }, [props.user, setPhone]);
    useEffect(() => {
        const interval = setInterval(() => {
            counter > 0 && setCounter(counter - 1);
            if (status === "sent" && counter === 0) {
                setStatus("send-again");
            }
        }, 1000);
        return () => clearInterval(interval);
    }, [counter, status, setStatus, setCounter]);
    const handleSendVerificationCode = () => {
        if (props.user) {
            postSendVerificationCode(phone.value, props.user).then(
                (response) => {
                    if (response.status === 200) {
                        setStatus("sent");
                        setCounter(60);
                    } else {
                        setStatus("error");
                    }
                }
            ).catch((error) => {
                console.error(error);
                setStatus("error");
            });
        } else {
            setStatus("error");
        }
    }
    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (props.user) {
            putPhoneUpdate(phone.value, verificationCode.value, props.user).then(
                (response) => {
                    if (response.status === 200) {
                        setStatus("success");
                    } else {
                        setStatus("error");
                    }
                }
            ).catch((error) => {
                console.error(error);
                setStatus("error");
            });
        }
    }
    const handleClose = () => {
        setStatus("idle");
        setPhone({...phone, valid: undefined});
        setVerificationCode({value: "", valid: undefined});
        props.handleHide();
    }
    const {t} = useTranslation();
    return (
        <Modal show={props.show} onHide={handleClose} backdrop={"static"}>
            <Modal.Header closeButton>
                <Modal.Title>{t("profile.phone.title")}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {status === "error" ? <Alert variant={"danger"}>{t("profile.phone.error")}</Alert> : null}
                {status === "success" ? <Alert variant={"success"}>{t("profile.phone.success")}</Alert> : null}
                <Form id={"phone-form"} onSubmit={handleSubmit}>
                    <Form.Group className={"d-grid mt-3 ms-3 me-3 pb-3 border-bottom align-items-center"}>
                        <Form.Label>{t("profile.phone.phone_number")}:</Form.Label>
                        <InputGroup>
                            <InputGroup.Text>
                                <Form.Select value={phone.prefix}
                                             onChange={(e) => {
                                                 setPhone(
                                                     {...phone, prefix: e.target.value})
                                             }}>
                                    <option value={"+44"}>+44</option>
                                    <option value={"+86"}>+86</option>
                                </Form.Select>
                            </InputGroup.Text>
                            <Form.Control type={"text"}
                                          inputMode={"tel"}
                                          value={phone.value}
                                          onChange={(e) => {
                                              setPhone({...phone, value: e.target.value});
                                          }}
                                          onBlur={(e) => {
                                              setPhone({
                                                  ...phone,
                                                  valid: phonePatternTest(phone.prefix + e.target.value)
                                              });
                                          }}
                                          isValid={phone.valid === true}
                                          isInvalid={phone.valid === false}
                                          placeholder={t("profile.phone.enter_phone")!}/>
                            <Form.Control.Feedback type="invalid">
                                {t("profile.phone.phone_invalid")!}
                            </Form.Control.Feedback>
                        </InputGroup>
                        {status === "idle" ? <Button variant={"primary"}
                                                     className={"mt-3"}
                                                     onClick={handleSendVerificationCode}
                                                     disabled={!phone.valid}>
                                {t("profile.phone.send_verification_code")}
                            </Button> :
                            <Button className={"mt-3"}
                                    disabled={
                                        !((status === "send-again" || status === "success" || status === "error")
                                            && phone.valid && counter === 0)
                                    } onClick={handleSendVerificationCode}>
                                {t("profile.phone.send_verification_code_again") + (counter > 0 ? `(${counter})` : "")}
                            </Button>}
                    </Form.Group>
                    <Collapse in={status === "sent" || status === "send-again"}>
                        <Form.Group className={"mt-3"}>
                            <Form.Label>Verification Code</Form.Label>
                            <Form.Control type="text" placeholder={t("profile.phone.enter_verification_code")!}
                                          inputMode={"numeric"}
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
                        </Form.Group>
                    </Collapse>

                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant={status === "success" ? "outline-primary" : "primary"}
                        form={"phone-form"} type="submit"
                        disabled={!(phone.valid && verificationCode.valid)}>{t("profile.phone.save")}</Button>
                <Button variant={status === "success" ? "primary" : "secondary"}
                        onClick={handleClose}>
                    {status === "success" ? t("profile.phone.close") : t("profile.phone.cancel")}
                </Button>
            </Modal.Footer>
        </Modal>
    );
}

function postSendVerificationCode(phone: string, user: User) {
    return fetch(`${config.api_url}/account/phone`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${user.token_type} ${user.access_token}`
        },
        body: JSON.stringify({
            phone: phone,
            locale: i18n.language
        })
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error(response.statusText);
        }
    });
}

function putPhoneUpdate(phone: string, code: string, user: User) {
    return fetch(`${config.api_url}/account/phone`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${user.token_type} ${user.access_token}`
        },
        body: JSON.stringify({
            phone: phone,
            code: code
        })
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error(response.statusText);
        }
    });
}
