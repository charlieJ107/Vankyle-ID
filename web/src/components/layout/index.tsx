import {Button, Container, Nav, Navbar} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {userManager} from "../../auth/userManager";
import {User} from "oidc-client-ts";
import {Brand} from "./brand";
import {NavPersona} from "./navPersona";
import {Link, useNavigate} from "react-router-dom";
import config from "../../config.json"

export function Layout(props: { children: React.ReactNode }) {
    const {t} = useTranslation();
    const [state, setState] = useState<{
        signedIn: boolean;
        user?: User;
    }>({
        signedIn: false,
        // For testing purposes, uncomment the following line to simulate a logged in user
        // signedIn: true,
        // user: new User({
        //     access_token: "", token_type: "",
        //     profile: {
        //         sub: "kale",
        //         iss: "some_issuer",
        //         aud: "some_audience",
        //         exp: 0,
        //         iat: 0,
        //         name: "Kyle",
        //         email: "a@a.com",
        //         // picture: userIcon
        //     }
        // })
    });
    useEffect(() => {
        userManager.getUser().then((user) => {
            if (user) {
                setState({
                    signedIn: true,
                    user: user
                });
            }
        }).catch((error) => {
            console.error(error);
        });
    }, [setState]);
    return (
        <Container className={"vh-100 pt-3"}>
            <Navbar className={"border-bottom bg-light ps-md-3 pe-md-3"} expand={"md"}>
                <Navbar.Brand href="/">
                    <Brand/>
                </Navbar.Brand>
                <Navbar.Toggle aria-controls={"nav-bar-collapse"}/>
                <Navbar.Collapse id={"nav-bar-collapse"}>
                    <Nav className={"d-flex justify-content-between align-items-start align-items-md-center w-100"}>
                        <div className={"d-flex flex-column flex-md-row ms-md-3"}>
                            {state.signedIn && state.user ?
                                <>
                                    <Nav.Item className={"ms-3 mt-3 mt-md-0"}>
                                        <Link className={"text-decoration-none text-dark"} to={"/account/profile"}>
                                            {t("profile.your_info")}
                                        </Link>
                                    </Nav.Item>
                                    <Nav.Item className={"ms-3 mt-3 mt-md-0"}>
                                        <Link className={"text-decoration-none text-dark"} to={"/account/security"}>
                                            {t("security.title")}
                                        </Link>
                                    </Nav.Item>
                                </>
                                : null}
                        </div>
                        <div className={"d-flex mt-3 mt-md-0"}>
                            {state.signedIn && state.user ?
                                <Nav.Item>
                                    <NavPersona user={state.user}/>
                                </Nav.Item> :
                                <Nav.Item className={"d-flex align-items-center"}>
                                    <SignInButton signedIn={state.signedIn}/>
                                </Nav.Item>}
                        </div>

                    </Nav>
                </Navbar.Collapse>
            </Navbar>
            {props.children}
        </Container>
    );
}

const SignInButton = (props: { signedIn: boolean }) => {
    const {t} = useTranslation();
    const navigate = useNavigate();
    return (
        <>
            <Button className={"me-3"}
                    onClick={() => window.location.href = `${config.server_url}/login`}>{t("layout.sign_in")}</Button>
            <Button variant={"outline-primary"} onClick={() => navigate("/register")}>{t("layout.sign_up")}</Button>
        </>
    );
};

