import {Button, Container, Nav, Navbar} from "react-bootstrap";
import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {userManager} from "../../auth/userManager";
import {User} from "oidc-client-ts";
import {Brand} from "./brand";
import {NavPersona} from "./navPersona";
import {Link, useNavigate} from "react-router-dom";

export function Layout(props: { children: React.ReactNode }) {
    const {t} = useTranslation();
    const [user, setUser] = useState<User|null>(null);
    useEffect(() => {
        userManager.getUser().then((user) => {
            setUser(user);
        });
    }, [setUser]);
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
                            {user &&
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
                            }
                        </div>
                        <div className={"d-flex mt-3 mt-md-0"}>
                            {user ?
                                <Nav.Item>
                                    <NavPersona user={user}/>
                                </Nav.Item> :
                                <Nav.Item className={"d-flex align-items-center"}>
                                    <SignInButton/>
                                </Nav.Item>}
                        </div>

                    </Nav>
                </Navbar.Collapse>
            </Navbar>
            {props.children}
        </Container>
    );
}

const SignInButton = () => {
    const {t} = useTranslation();
    const navigate = useNavigate();
    return (
        <>
            <Button className={"me-3"}
                    onClick={() => userManager.signinRedirect({state: window.location.href})}>{t("layout.sign_in")}</Button>
            <Button variant={"outline-primary"} onClick={() => navigate("/register")}>{t("layout.sign_up")}</Button>
        </>
    );
};

