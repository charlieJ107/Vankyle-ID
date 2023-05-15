import React, {useEffect, useState} from "react";
import {Button, Col, Container, Row} from "react-bootstrap";
import {useTranslation} from "react-i18next";
import {Layout} from "../layout";
import {userManager} from "../../auth/userManager";
import config from "../../config/config"
import {User} from "oidc-client-ts";
import {Navigate, useNavigate} from "react-router-dom";

export function Home() {
    const [user, setUser] = useState<User | null>(null);
    useEffect(() => {
        userManager.getUser().then(oidc => {
            if (oidc) {
                setUser(oidc);
            }
        });
    }, [setUser]);
    const {t} = useTranslation();
    const navigateTo = useNavigate();
    if (user === null) {
        return (
            <Layout>
                <Container className={"d-flex flex-column text-center justify-content-center h-50"}>
                    <h1 className={"mt-5"}>{t("home.description")}</h1>
                    <p className={"mt-3"}>{t("home.introduction")}</p>
                    <Row className={"mt-3"}>
                        <Col className={"text-end"}>
                            <Button onClick={() => window.location.href = `${config.public_url}/login`}>
                                {t("home.sign_in")}
                            </Button>
                        </Col>
                        <Col className={"text-start"}>
                            <Button variant={"outline-primary"}
                                    onClick={() => navigateTo("/register")}>
                                {t("home.sign_up")}
                            </Button>
                        </Col>
                    </Row>
                </Container>
            </Layout>
        );
    } else {
        return (<Navigate to={"/account"}/>);
    }

}