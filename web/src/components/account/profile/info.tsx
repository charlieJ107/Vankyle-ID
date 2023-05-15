import {Alert, Button, Form, Modal} from "react-bootstrap";
import React from "react";
import {useTranslation} from "react-i18next";
import {User} from "oidc-client-ts";
import config from "../../../config/config";

export function Info(props: {
    show: boolean,
    user: User | null,
    handleHide: () => void
}) {
    const [status, setStatus] =
        React.useState<"idle" | "submitted" | "error" | "success">("idle");
    const [name, setName] = React.useState("");

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (props.user) {
            setStatus("submitted");
            putNameUpdate(name, props.user).then(
                (response) => {
                    if (response.status === 200) {
                        setStatus("success");
                    } else {
                        setStatus("error");
                    }
                }
            ).catch(() => {
                setStatus("error");
            });
        }
    }
    const handleClose = () => {
        setStatus("idle");
        props.handleHide();
    }
    const {t} = useTranslation();
    return (
        <Modal show={props.show} onHide={handleClose} backdrop={"static"}>
            <Modal.Header closeButton>
                <Modal.Title>{t("profile.info.name.title")}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {status === "success" && <Alert variant={"success"}>{t("profile.info.name.success")}</Alert>}
                {status === "error" && <Alert variant={"danger"}>{t("profile.info.name.error")}</Alert>}
                <Form id={"name-form"} onSubmit={handleSubmit}>
                    <Form.Group controlId="formBasicEmail">
                        <Form.Label>{t("profile.info.name.title")}</Form.Label>
                        <Form.Control value={name} type="text"
                                      placeholder={t("profile.info.name.name_please")!}
                                      onChange={(e) => {
                                          setName(e.target.value);
                                      }}/>
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="primary" form={"name-form"} type="submit">{t("profile.info.name.save")}</Button>
                <Button variant="secondary" onClick={props.handleHide}>{t("profile.info.name.cancel")}</Button>
            </Modal.Footer>
        </Modal>
    );
}

function putNameUpdate(name: string, user: User) {
    return fetch(`${config.api_url}/account/info`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `${user.token_type} ${user.access_token}`
        },
        body: JSON.stringify({name: name})
    }).then(res => {
        if (res.ok) {
            return res.json();
        } else {
            throw new Error("Failed to update name");
        }
    });
}