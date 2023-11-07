import React from "react";
import {useTranslation} from "react-i18next";
import {Alert, Button, Form, Image, Modal} from "react-bootstrap";
import userIcon from "../../../img/user.svg";
import {User} from "oidc-client-ts";

export function EditPicture(props: { show: boolean, user: User | null, handleHide: () => void }) {
    const [status, setStatus] =
        React.useState<"idle" | "submitted" | "error" | "success">("idle");

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setStatus("error");
    }
    const {t} = useTranslation();
    return (
        <Modal show={props.show}
               onHide={() => {
                   setStatus("idle");
                   props.handleHide();
               }}
               backdrop={"static"}>
            <Modal.Header closeButton>
                <Modal.Title>{t("profile.picture.title")}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {status === "success" && <Alert variant={"success"}>{t("profile.picture.success")}</Alert>}
                {status === "error" && <Alert variant={"danger"}>{t("profile.picture.error")}</Alert>}
                <Form id={"picture-form"} onSubmit={handleSubmit}>
                    <Image src={props.user && props.user.profile.picture ? props.user.profile.picture : userIcon}
                           width={72} height={72}
                           style={{
                               borderRadius: "50%",
                           }}/>
                    <Form.Group>
                        <Form.Label>{t("profile.picture.description")}</Form.Label>
                        <Form.Control type="file"
                                      accept={"image/*"}
                                      placeholder={t("profile.picture.select_file")!}/>
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant={status === "success" ? "outline-primary" : "primary"}
                        form={"picture-form"} type="submit">
                    {t("profile.picture.upload")}
                </Button>
                <Button variant={status === "success" ? "primary" : "secondary"}
                        onClick={() => {
                            setStatus("idle");
                            props.handleHide();
                        }}>
                    {status === "success" ? t("profile.picture.close") : t("profile.picture.cancel")}
                </Button>
            </Modal.Footer>
        </Modal>
    );
}