import {Button, Card, Container, Form} from "react-bootstrap";
import {useTranslation} from "react-i18next";
import React, {useEffect, useState} from "react";
import {Client, Scope} from "./consentTypes";
import {Loading} from "../shared/loading";
import {Error} from "../shared/error";
import {getConsentInfo, postConsentInfo} from "./consentApi";
import userIcon from "../../img/user.svg";

export function Consent() {
    const [status, setStatus]
        = useState<"idle" | "success" | "error" | "loading">("loading");
    const [client, setClient] = useState<Client | null>(null);
    const [user, setUser] =
        useState<{ picture?: string, principal: string }>({principal: ""});
    const [scopes, setScopes] =
        useState<{ previouslyApprovedScopes: Scope[], scopesToApprove: Scope[] }>(
            {previouslyApprovedScopes: [], scopesToApprove: []});
    const [oauthState, setOauthState] = useState<string>("");
    const {t} = useTranslation();

    useEffect(() => {
        // parse the query string into an object
        const params = new URLSearchParams(window.location.search);
        getConsentInfo(params).then((response) => {
            if (response.status === 200 && response.client && response.state && response.user) {
                setClient(response.client);
                setOauthState(response.state);
                setUser(response.user);
                setScopes({
                    previouslyApprovedScopes: response.previouslyApprovedScopes,
                    scopesToApprove: response.scopesToApprove
                });
                setStatus("idle");
            } else if (response.status === 401) {
                window.location.replace("/login");
            } else {
                setStatus("error");
            }
        }).catch(() => {
            setStatus("error");
        });
    }, [setClient, setUser, setScopes, setStatus]);

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        const param = new URLSearchParams();
        param.append("client_id", client!.client_id);
        param.append("state", oauthState);
        scopes.previouslyApprovedScopes.forEach((scope) => {
            param.append("scope", scope.scope_id);
        });
        scopes.scopesToApprove.forEach((scope) => {
            if (scope.approved) {
                param.append("scope", scope.scope_id);
            }
        });
        postConsentInfo(param).then((response) => {
            if (response.status === 302 && response.redirectUrl) {
                setStatus("success");
                window.location.replace(response.redirectUrl);
            } else {
                setStatus("error");
            }
        });
    }

    const handleReset = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        scopes.scopesToApprove.forEach((scope) => {
            scope.approved = false;
        });
        handleSubmit(event);
    }

    if (status === "loading") {
        return (<Loading/>);
    } else if (status === "error") {
        return (<Error/>);
    } else {
        return (
            <Container className={"mt-5 p-3 p-md-5 d-md-grid justify-content-center"}>
                <Card className={"shadow form-container p-5"}>
                    <h1 className={"text-center"}>{t("consent.sign_in_with")}</h1>
                    <Card.Body>
                        <h3 className={"text-center"}>
                            <span className={"fw-bold text-primary"}>{client!.client_name} </span>
                            {t("consent.want_access")}
                            <span className={"text-primary"}> {user.principal}</span>
                        </h3>
                        <div className={"text-center"}>
                            <img src={user.picture ? user.picture : userIcon} alt={user.principal} width={48}
                                 height={48}/>
                            <span>{user.principal}</span>
                        </div>
                        <p className={"mx-2 mx-md-5"}>{t("consent.select_what")}</p>
                        <Form className={"mx-2 mx-md-5"} onSubmit={handleSubmit} onReset={handleReset}>
                            {scopes.previouslyApprovedScopes.map((scope: Scope, index: number) => {
                                return (
                                    <Form.Group className={"py-3 border-solid border-bottom border-top"} key={index}>
                                        <Form.Check.Input className={"ms-3"} checked={true} disabled={true}
                                                          name={scope.scope_id}/>
                                        <Form.Check.Label className={"ms-3"}>{scope.scope_name}</Form.Check.Label>
                                    </Form.Group>
                                );
                            })}
                            {scopes.scopesToApprove.map((scope: Scope, index: number) => {
                                return (
                                    <Form.Group className={"py-3 border-solid border-bottom border-top"} key={index}>
                                        <Form.Check.Input className={"ms-3"} onChange={(e) => {
                                            setScopes({
                                                ...scopes,
                                                scopesToApprove: scopes.scopesToApprove.map((value) => {
                                                    if (value.scope_id === scope.scope_id) {
                                                        value.approved = e.target.checked;
                                                    }
                                                    return value;
                                                })
                                            })
                                        }} checked={scope.approved}/>
                                        <Form.Check.Label className={"ms-3"}>{scope.scope_name}</Form.Check.Label>
                                    </Form.Group>
                                );
                            })}
                            <div className={"py-3 d-flex justify-content-around"}>
                                <Button variant="primary" type={"submit"}>{t("consent.approve")}</Button>
                                <Button variant="secondary" type={"reset"}>{t("consent.cancel")}</Button>
                            </div>
                        </Form>
                        <p className={"mt-3 mx-3"}>{t("consent.description")}</p>
                    </Card.Body>
                </Card>
            </Container>
        );
    }
}
