import {Image} from "react-bootstrap";
import logo from "../../img/logo.svg";
import React from "react";
import {useTranslation} from "react-i18next";

export function Brand() {
    const {t} = useTranslation();
    return (
        <div className={"d-flex align-items-center"}>
            <Image src={logo} alt="logo" width="64" height="48" className="d-inline-block align-top"/>
            <span>{t("app.name")}</span>
        </div>
    );
}