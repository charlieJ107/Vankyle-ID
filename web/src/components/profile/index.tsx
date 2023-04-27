import {Button, Card, Container, Image} from "react-bootstrap";
import {useTranslation} from "react-i18next";

import React, {useEffect} from "react";
import {User} from "oidc-client-ts";
import userIcon from "../../img/user.svg";
import {userManager} from "../../auth/userManager";
import checkedIcon from "../../img/checkmark_filled.svg";
import closedIcon from "../../img/closed_filled.svg";
import {Email} from "./email";
import {Picture} from "./picture";
import {Info} from "./info";
import {Phone} from "./phone";
import {Loading} from "../shared/loading";

export function Profile() {
    const [user, setUser] = React.useState<User | null>(
        null
        // For testing purposes, uncomment the following line to simulate a logged in user
        // new User({
        //     access_token: "", token_type: "",
        //     profile: {
        //         sub: "kale",
        //         iss: "some_issuer",
        //         aud: "some_audience",
        //         exp: 0,
        //         iat: 0,
        //         name: "Kyle",
        //         email: "a@a.com",
        //         email_verified: true,
        //         phone_number: "+8613163905241",
        //         phone_number_verified: false,
        //         picture: userIcon
        //     }
        // })
    );
    const [status, setStatus] =
        React.useState<"loading" | "idle" | "success" | "error">("loading");
    const [pictureModifying, setPictureModifying] = React.useState(false);
    const [infoModifying, setInfoModifying] = React.useState(false);
    const [emailModifying, setEmailModifying] = React.useState(false);
    const [phoneModifying, setPhoneModifying] = React.useState(false);
    useEffect(() => {
        if (user === null) {
            userManager.getUser().then((oidc) => {
                if (oidc === null) {
                    setStatus("error");
                } else {
                    setUser(oidc);
                    setStatus("success");
                }
            });
        }
    }, [setUser, setStatus, user]);
    const {t} = useTranslation();
    if (status === "loading") return (<Loading/>);
    return (
        <Container className={"p-3 p-md-5"}>
            <h1 className={"mb-3"}>{t("profile.your_info")}</h1>
            <Card className={"bg-light shadow border-1 p-3 p-md-5"}>
                <Card.Header style={{
                    borderTopLeftRadius: "8px",
                    borderTopRightRadius: "8px"
                }}>
                    <h2>{t("profile.profile_info")}</h2>
                </Card.Header>
                <Card.Body>
                    <div className={"d-flex pb-5 border-bottom align-items-center"}>
                        <Image src={user && user.profile.picture ? user.profile.picture : userIcon}
                               width={72} height={72}
                               style={{
                                   borderRadius: "50%",
                               }}/>
                        <div className={"ms-5"}>
                            <p>{t("profile.picture.description")}</p>
                            <Button className={"ms-auto"}
                                    onClick={() => {
                                        setPictureModifying(true)
                                    }}>
                                {t("profile.picture.change_picture")}
                            </Button>
                        </div>
                        <Picture show={pictureModifying} handleHide={() => setPictureModifying(false)} user={user}/>
                    </div>
                    <div className={"d-flex mt-3 ms-3 me-3 pb-3 border-bottom align-items-center"}>
                        <h3>{t("profile.info.name.title")}</h3>
                        <div className={"d-flex flex-column flex-md-row ms-5"}>
                            <p className={"mb-0"}>{user && user.profile.name ? user.profile.name : t("profile.info.name.not_set")}</p>
                            <Button variant={"link"}
                                    className={"ms-0 ms-md-3 p-0"}
                                    onClick={() => {
                                        setInfoModifying(true)
                                    }}>
                                {t("profile.info.name.change_name")}
                            </Button>
                        </div>
                        <Info user={user} show={infoModifying} handleHide={() => setInfoModifying(false)}/>
                    </div>
                </Card.Body>
            </Card>
            <Card className={"bg-light shadow border-1 p-3 p-md-5 mt-3"}>
                <Card.Header style={{
                    borderTopLeftRadius: "8px",
                    borderTopRightRadius: "8px"
                }}>
                    <h2>{t("profile.account_info")}</h2>
                </Card.Header>
                <Card.Body>
                    <div className={"d-flex mt-3 ms-3 me-3 pb-3 border-bottom align-items-center"}>
                        <h3>{t("profile.email.title")}</h3>
                        <div className={"d-flex flex-column ms-5"}>
                            <div>
                                <div className={"d-flex flex-column flex-md-row"}>
                                    <p className={"mb-0"}>
                                        {user && user.profile.email ?
                                            user.profile.email : t("profile.email.not_set")}
                                    </p>
                                    <Button variant={"link"}
                                            className={"ms-0 ms-md-3 p-0"}
                                            onClick={() => {
                                                setEmailModifying(true)
                                            }}>
                                        {user && user.profile.email ?
                                            t("profile.email.change_email") : t("profile.email.set_email")}
                                    </Button>
                                </div>
                            </div>
                            {user?.profile.email ? user?.profile.email_verified ?
                                <div>
                                    <Image className={"text-success"} src={checkedIcon} width={16} height={16}/>
                                    <span className={"ms-1 text-muted"}>
                                {t("profile.email.email_verified")}
                            </span>
                                </div> :
                                <div className={"d-flex align-items-baseline"}>
                                    <Image src={closedIcon} width={16} height={16}/>
                                    <span className={"ms-1 text-muted"}>
                                {t("profile.email.email_not_verified")}
                            </span>
                                    <Button variant={"link"}
                                            onClick={() => {
                                                setEmailModifying(true);
                                            }}
                                            className={"text-secondary"}>
                                        {t("profile.email.verify_now")}
                                    </Button>
                                </div> : null}
                        </div>
                    </div>
                    <Email user={user} show={emailModifying}
                           handleHide={() => {
                               setEmailModifying(false);
                           }}/>
                    <div className={"d-flex mt-3 ms-3 me-3 pb-3 border-bottom align-items-center"}>
                        <h3>{t("profile.phone.title")}</h3>
                        <div className={"d-flex flex-column ms-5"}>
                            <div>
                                <div className={"d-flex flex-column flex-md-row"}>
                                    <p className={"mb-0"}>
                                        {user && user.profile.phone_number ?
                                            user.profile.phone_number : t("profile.phone.not_set")}
                                    </p>
                                    <Button variant={"link"}
                                            className={"ms-0 ms-md-3 p-0"}
                                            onClick={() => {
                                                setPhoneModifying(true)
                                            }}>
                                        {user && user.profile.phone_number ?
                                            t("profile.phone.change_phone") : t("profile.phone.set_phone")}
                                    </Button>
                                </div>
                            </div>
                            {user?.profile.phone_number ? user?.profile.phone_number_verified ?
                                <div>
                                    <Image className={"text-success"} src={checkedIcon} width={16} height={16}/>
                                    <span className={"ms-1 text-muted"}>
                                {t("profile.phone.phone_verified")}
                            </span>
                                </div> :
                                <div className={"d-flex align-items-baseline"}>
                                    <Image src={closedIcon} width={16} height={16}/>
                                    <span className={"ms-1 text-muted"}>
                                {t("profile.phone.phone_not_verified")}
                            </span>
                                    <Button variant={"link"}
                                            onClick={() => {
                                                setPhoneModifying(true);
                                            }}
                                            className={"text-secondary"}>
                                        {t("profile.phone.verify_now")}
                                    </Button>
                                </div> : null}
                        </div>
                    </div>
                    <Phone user={user} show={phoneModifying} handleHide={() => setPhoneModifying(false)}/>
                </Card.Body>
            </Card>
        </Container>
    );
}