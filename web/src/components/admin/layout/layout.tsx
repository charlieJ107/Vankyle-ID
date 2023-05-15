import {Col, Image, Nav, Navbar, Row} from "react-bootstrap";
import React from "react";
import logo from "../../../img/logo.svg";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";

export function Layout(props: {
    children: React.ReactNode,
    currentTab: "users" | "clients",
    setCurrentTab: (tab: "users" | "clients") => void
}) {
    const handleSelect = (eventKey: string | null) => {
        if (eventKey === "users" || eventKey === "clients") {
            props.setCurrentTab(eventKey);
        }
    }
    const navigate = useNavigate();
    const {t} = useTranslation();
    return (
        <Row className={"vw-100"}>
            <Col className={"col-3"}>
                <Navbar className={"d-flex flex-column flex-shrink p-3 bg-light border-end h-100"}>
                    <Navbar.Brand className={"d-flex align-items-center justify-content-center w-100"} href="/">
                        <Image src={logo} alt="logo" width="64" height="48"
                               className="d-inline-block align-top"/>
                        <span>{t("app.name")}</span>
                    </Navbar.Brand>
                    <Navbar.Toggle aria-controls={"nav-bar-collapse"}/>
                    <Navbar.Collapse id={"nav-bar-collapse"} className={"align-items-start w-100"}>
                        <Nav className={"flex-column mt-md-3 w-100 text-center gap-3"} variant={"pills"}
                             activeKey={props.currentTab} onSelect={handleSelect}>
                            <Nav.Item>
                                <Nav.Link eventKey={"users"} onClick={() => navigate("/admin/users")}
                                          className={props.currentTab === "users" ? "text-white" : "text-black"}>
                                    {t("admin.user.title")}
                                </Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey={"clients"} onClick={() => navigate("/admin/clients")}
                                          className={props.currentTab === "clients" ? "text-white" : "text-black"}>
                                    {t("admin.client.title")}
                                </Nav.Link>
                            </Nav.Item>
                        </Nav>
                    </Navbar.Collapse>
                </Navbar>
            </Col>
            <Col>
                {props.children}
            </Col>
        </Row>
    )
}