import {User} from "oidc-client-ts";
import React from "react";
import {Dropdown, Image, NavDropdown} from "react-bootstrap";
import userIcon from "../../img/user.svg";
import {useTranslation} from "react-i18next";

export function NavPersona(props: { user: User }) {
    const PersonaImage = () => (
        <div className={"d-inline-block"}
             style={{
                 borderRadius: "50%",
             }}>
            <Image src={props.user.profile.picture ? props.user.profile.picture : userIcon}
                   width={40} height={40}
                   style={{
                       borderRadius: "50%",
                   }}/>
        </div>
    );
    const {t} = useTranslation();
    return (
        <NavDropdown title={<PersonaImage/>}
                     className={"me-3"}
                     align={"end"}>
            <NavDropdown.Item disabled={true} className={"text-dark"}>
                <div className={"d-flex align-items-center"}>
                    <Image src={props.user.profile.picture ? props.user.profile.picture : userIcon}
                           width="48" height="48" className="d-inline-block align-top"/>
                    <span className={"ms-3"}>{props.user.profile.name?props.user.profile.name:props.user.profile.sub}</span>
                </div>
            </NavDropdown.Item>
            <NavDropdown.Divider/>
            <Dropdown.Item onClick={()=>window.location.replace("/logout")}>
                    {t("layout.logout")}
            </Dropdown.Item>
        </NavDropdown>
    );
}
