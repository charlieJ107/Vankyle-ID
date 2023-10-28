import {useTranslation} from "react-i18next";
import {useEffect, useState} from "react";
import {Container} from "react-bootstrap";
import {Loading} from "../shared/loading";
import dissatisfied from "../../img/dissatisfied.svg";
import checkmarkFilled from "../../img/checkmark_filled.svg";

export function ConfirmEmail() {
    const {t} = useTranslation();
    const [status, setStatus] =
        useState<"loading" | "error" | "success" | "invalid">("loading");
    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const code = urlParams.get("code");
        if (code) {
            postVerificationCode(code).then((response) => {
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
    }, []);
    switch (status) {
        case "loading":
            return (
                <Loading/>
            );
        case "invalid":
            return (
                <Container className="p-3 mt-5 p-md-5 form-container text-center">
                    <h1 className={"mb-3 text-center"}>{t("confirm_email.title")}</h1>
                    <img className={"my-5"} src={dissatisfied} width={72} height={72} alt={"Invalid"}/>
                    <p className={"text-center"}>{t("confirm_email.invalid")}</p>
                </Container>
            );
        case "success":
            return (
                <Container className="p-3 mt-5 p-md-5 form-container text-center">
                    <h1 className={"mb-3 text-center"}>{t("confirm_email.title")}</h1>
                    <img className={"my-5"} src={checkmarkFilled} width={72} height={72} alt={"Invalid"}/>
                    <p className={"text-center"}>{t("confirm_email.success")}</p>
                </Container>
            );
        default:
            return (
                <Container className="p-3 mt-5 p-md-5 form-container text-center">
                    <h1 className={"mb-3 text-center"}>{t("confirm_email.title")}</h1>
                    <img className={"my-5"} src={dissatisfied} width={72} height={72} alt={"Invalid"}/>
                    <p className={"text-center"}>{t("confirm_email.error")}</p>
                </Container>
            );
    }
}

function postVerificationCode(code: string) {
    return fetch(`/api/confirm-email?code=${code}`, {
        method: "GET"
    }).then((response) => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error("Network response was not ok.");
        }
    });
}