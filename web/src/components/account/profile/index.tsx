import {Button, Card, Container, Image} from "react-bootstrap";
import {useTranslation} from "react-i18next";
import React, {useEffect} from "react";
import userIcon from "../../../img/user.svg";
import checkedIcon from "../../../img/checkmark_filled.svg";
import closedIcon from "../../../img/closed_filled.svg";
import {EditEmail} from "./editEmail";
import {EditPicture} from "./editPicture";
import {EditInfo} from "./editInfo";
import {EditPhone} from "./editPhone";
import {Loading} from "../../shared/loading";
import {getUserInfo, UserInfo} from "./profileUtils";
import {userManager} from "../../../auth/userManager";

export function Profile() {
    const [userInfo, setUserInfo] = React.useState<UserInfo | null>(null);
    const [status, setStatus] =
        React.useState<"loading" | "idle" | "success" | "error">("loading");
    const [pictureModifying, setPictureModifying] = React.useState(false);
    const [infoModifying, setInfoModifying] = React.useState(false);
    const [emailModifying, setEmailModifying] = React.useState(false);
    const [phoneModifying, setPhoneModifying] = React.useState(false);
    useEffect(() => {
        getUserInfo().then((updatedProfile) => {
            setUserInfo(updatedProfile);
            setStatus("idle");
        }).catch((e) => {
            if (e.message === "Failed to get user info") {
                userManager.signoutRedirect().then();
            }
            setStatus("error");
        });
    }, [setUserInfo, setStatus]);
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
                        <Image src={userInfo && userInfo.picture ? userInfo.picture : userIcon}
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
                        <EditPicture show={pictureModifying}
                                     picture_src={userInfo && userInfo.picture ? userInfo.picture : null}
                                     handleHide={() => {
                                         setPictureModifying(false);
                                         getUserInfo().then((updatedProfile) => {
                                             setUserInfo(updatedProfile);
                                         }).catch(() => {
                                             setStatus("error");
                                         });
                                     }}/>
                    </div>
                    <div className={"d-flex mt-3 ms-3 me-3 pb-3 border-bottom align-items-center"}>
                        <h3>{t("profile.info.name.title")}</h3>
                        <div className={"d-flex flex-column flex-md-row ms-5"}>
                            <p className={"mb-0"}>{userInfo && userInfo.name ? userInfo.name : t("profile.info.name.not_set")}</p>
                            <Button variant={"link"}
                                    className={"ms-0 ms-md-3 p-0"}
                                    onClick={() => {
                                        setInfoModifying(true)
                                    }}>
                                {t("profile.info.name.change_name")}
                            </Button>
                        </div>
                        <EditInfo info={userInfo} show={infoModifying} handleHide={() => {
                            setInfoModifying(false);
                            getUserInfo().then((updatedProfile) => {
                                setUserInfo(updatedProfile);
                            }).catch(() => {
                                setStatus("error");
                            });
                        }}/>
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
                                        {userInfo && userInfo.email ?
                                            userInfo.email : t("profile.email.not_set")}
                                    </p>
                                    <Button variant={"link"}
                                            className={"ms-0 ms-md-3 p-0"}
                                            onClick={() => {
                                                setEmailModifying(true)
                                            }}>
                                        {userInfo && userInfo.email ?
                                            t("profile.email.change_email") : t("profile.email.set_email")}
                                    </Button>
                                </div>
                            </div>
                            {userInfo?.email && (userInfo?.email_verified ?
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
                                </div>)}
                        </div>
                    </div>
                    <EditEmail email={userInfo && userInfo.email ? userInfo.email : null}
                               show={emailModifying}
                               handleHide={() => {
                                   setEmailModifying(false);
                                   getUserInfo().then((updatedProfile) => {
                                       setUserInfo(updatedProfile);
                                   }).catch(() => {
                                       setStatus("error");
                                   });
                               }}/>
                    <div className={"d-flex mt-3 ms-3 me-3 pb-3 border-bottom align-items-center"}>
                        <h3>{t("profile.phone.title")}</h3>
                        <div className={"d-flex flex-column ms-5"}>
                            <div>
                                <div className={"d-flex flex-column flex-md-row"}>
                                    <p className={"mb-0"}>
                                        {userInfo && userInfo.phone_number ?
                                            userInfo.phone_number : t("profile.phone.not_set")}
                                    </p>
                                    <Button variant={"link"}
                                            className={"ms-0 ms-md-3 p-0"}
                                            onClick={() => {
                                                setPhoneModifying(true)
                                            }}>
                                        {userInfo && userInfo.phone_number ?
                                            t("profile.phone.change_phone") : t("profile.phone.set_phone")}
                                    </Button>
                                </div>
                            </div>
                            {userInfo?.phone_number && (userInfo?.phone_number_verified ?
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
                                </div>)}
                        </div>
                    </div>
                    <EditPhone phoneNumber={userInfo && userInfo.phone_number ? userInfo.phone_number : null}
                               show={phoneModifying}
                               handleHide={() => {
                                   setPhoneModifying(false);
                                   getUserInfo().then((updatedProfile) => {
                                       setUserInfo(updatedProfile);
                                   }).catch(() => {
                                       setStatus("error");
                                   });
                               }}/>
                </Card.Body>
            </Card>
        </Container>
    );
}

