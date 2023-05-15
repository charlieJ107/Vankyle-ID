import {AuthorizationGrantType, ClientAuthenticationMethod, ClientInterface, JwsAlgorithm} from "./clientInterface";
import {useTranslation} from "react-i18next";
import React from "react";
import {Button, Form, Table} from "react-bootstrap";

export function ClientForm({client, setClient, onSubmit, onReset}: {
    client: ClientInterface,
    setClient: (client: ClientInterface) => void,
    onSubmit: (client: ClientInterface) => void,
    onReset?: (client: ClientInterface) => void
}) {
    const {t} = useTranslation();
    const [newAuthenticationMethod, setNewAuthenticationMethod] =
        React.useState<ClientAuthenticationMethod>(ClientAuthenticationMethod.NONE);
    const [newGrantType, setNewGrantType] =
        React.useState<AuthorizationGrantType>(AuthorizationGrantType.AUTHORIZATION_CODE);
    const [newRedirectUri, setNewRedirectUri] = React.useState<string>("");
    const [newScope, setNewScope] = React.useState<string>("");
    const onSubmitForm = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        onSubmit(client);
    }
    const onResetForm = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        onReset ? onReset(client) : setClient(client);
    }

    return (
        <Form onSubmit={onSubmitForm} onReset={onResetForm}>
            <Form.Group className={"mb-3"}>
                <Form.Label>{t("admin.client.form.id")}</Form.Label>
                <Form.Control type="text" value={client.id || ""} readOnly disabled/>
            </Form.Group>
            <Form.Group className={"mb-3"}>
                <Form.Label>{t("admin.client.form.client_id")}</Form.Label>
                <Form.Control type="text" value={client.clientId || ""} onChange={(e) => {
                    setClient({...client, clientId: e.target.value});
                }}/>
            </Form.Group>
            <Form.Group className={"mb-3"}>
                <Form.Label>{t("admin.client.form.client_secret")}</Form.Label>
                <Form.Control type="text" value={client.clientSecret ? client.clientSecret : ""}
                              placeholder={t("admin.client.form.secret_not_displayed")!}
                              onChange={(e) => {
                                  setClient({...client, clientSecret: e.target.value});
                              }}/>
            </Form.Group>
            <Form.Group className={"mb-3"}>
                <Form.Label>{t("admin.client.form.authenticationMethods.title")}</Form.Label>
                <Table striped bordered hover size="sm">
                    <thead>
                    <tr>
                        <th>{t("admin.client.form.authenticationMethods.name")}</th>
                        <th>{t("admin.client.form.authenticationMethods.action")}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {client.clientAuthenticationMethods.map((authenticationMethod, index) => (
                        <tr key={index}>
                            <td>{t("admin.client.form.authenticationMethods." + authenticationMethod)!}</td>
                            <td>
                                <Button variant="danger" size="sm" onClick={() => {
                                    setClient({
                                        ...client,
                                        clientAuthenticationMethods: client.clientAuthenticationMethods
                                            .filter((_, i) => i !== index)
                                    });
                                }}>-</Button>
                            </td>
                        </tr>))}
                    <tr>
                        <td>
                            <Form.Select
                                value={newAuthenticationMethod}
                                onChange={(e) => {
                                    setNewAuthenticationMethod(e.target.value as ClientAuthenticationMethod);
                                }}>
                                <option value={ClientAuthenticationMethod.NONE}>
                                    {t("admin.client.form.authenticationMethods.none")}
                                </option>
                                <option value={ClientAuthenticationMethod.CLIENT_SECRET_BASIC}>
                                    {t("admin.client.form.authenticationMethods.client_secret_basic")}
                                </option>
                                <option value={ClientAuthenticationMethod.CLIENT_SECRET_POST}>
                                    {t("admin.client.form.authenticationMethods.client_secret_post")}
                                </option>
                                <option value={ClientAuthenticationMethod.CLIENT_SECRET_JWT}>
                                    {t("admin.client.form.authenticationMethods.client_secret_jwt")}
                                </option>
                                <option value={ClientAuthenticationMethod.PRIVATE_KEY_JWT}>
                                    {t("admin.client.form.authenticationMethods.private_key_jwt")}
                                </option>
                            </Form.Select>
                        </td>
                        <td>
                            <Button onClick={() => {
                                setClient({
                                    ...client,
                                    clientAuthenticationMethods: [...client.clientAuthenticationMethods, newAuthenticationMethod]
                                });
                            }}>
                                +
                            </Button>
                        </td>
                    </tr>
                    </tbody>
                </Table>
            </Form.Group>
            <Form.Group className={"mb-3"}>
                <Form.Label>{t("admin.client.form.authorizationGrantTypes.title")}</Form.Label>
                <Table striped bordered hover size="sm">
                    <thead>
                    <tr>
                        <th>{t("admin.client.form.authorizationGrantTypes.name")}</th>
                        <th>{t("admin.client.form.authorizationGrantTypes.action")}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {client.authorizationGrantTypes.map((grantType, index) => (
                        <tr key={index}>
                            <td>
                                {t("admin.client.form.authorizationGrantTypes." +
                                    (grantType === "urn:ietf:params:oauth:grant-type:jwt-bearer" ?
                                        "jwt-bearer" : grantType))}
                            </td>
                            <td>
                                <Button variant="danger" size="sm" onClick={() => {
                                    setClient({
                                        ...client,
                                        authorizationGrantTypes: client.authorizationGrantTypes
                                            .filter((_, i) => i !== index)
                                    });
                                }}>-</Button>
                            </td>
                        </tr>))}
                    <tr>
                        <td>
                            <Form.Select value={newGrantType} onChange={(e) => {
                                setNewGrantType(e.target.value as AuthorizationGrantType);
                            }}>
                                <option value={AuthorizationGrantType.AUTHORIZATION_CODE}>
                                    {t("admin.client.form.authorizationGrantTypes.authorization_code")}
                                </option>
                                <option value={AuthorizationGrantType.REFRESH_TOKEN}>
                                    {t("admin.client.form.authorizationGrantTypes.refresh_token")}
                                </option>
                                <option value={AuthorizationGrantType.JWT_BEARER}>
                                    {t("admin.client.form.authorizationGrantTypes.jwt-bearer")}
                                </option>
                                <option value={AuthorizationGrantType.CLIENT_CREDENTIALS}>
                                    {t("admin.client.form.authorizationGrantTypes.client_credentials")}
                                </option>
                            </Form.Select>
                        </td>
                        <td>
                            <Button onClick={() => {
                                setClient({
                                    ...client,
                                    authorizationGrantTypes: [...client.authorizationGrantTypes, newGrantType]
                                });
                            }}>
                                +
                            </Button>
                        </td>
                    </tr>
                    </tbody>
                </Table>
            </Form.Group>
            <Form.Group className={"mb-3"}>
                <Form.Label>{t("admin.client.form.redirectUris.title")}</Form.Label>
                <Table striped bordered hover size="sm">
                    <thead>
                    <tr>
                        <th>{t("admin.client.form.redirectUris.name")}</th>
                        <th>{t("admin.client.form.redirectUris.action")}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {client.redirectUris.map((redirectUri, index) => (
                        <tr key={index}>
                            <td>{redirectUri}</td>
                            <td>
                                <Button variant="danger" size="sm" onClick={() => {
                                    setClient({
                                        ...client,
                                        redirectUris: client.redirectUris.filter((_, i) => i !== index)
                                    });
                                }}>-</Button>
                            </td>
                        </tr>))}
                    <tr>
                        <td>
                            <Form.Control type="text" value={newRedirectUri} onChange={(e) => {
                                setNewRedirectUri(e.target.value);
                            }}/>
                        </td>
                        <td>
                            <Button onClick={() => {
                                setClient({
                                    ...client,
                                    redirectUris: [...client.redirectUris, newRedirectUri]
                                });
                            }}>
                                +
                            </Button>
                        </td>
                    </tr>
                    </tbody>
                </Table>
            </Form.Group>
            <Form.Group className={"mb-3"}>
                <Form.Label>{t("admin.client.form.scopes.title")}</Form.Label>
                <Table striped bordered hover size="sm">
                    <thead>
                    <tr>
                        <th>{t("admin.client.form.scopes.name")}</th>
                        <th>{t("admin.client.form.scopes.action")}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {client.scopes.map((scope, index) => (
                        <tr key={index}>
                            <td>{scope}</td>
                            <td>
                                <Button variant="danger" size="sm" onClick={() => {
                                    setClient({
                                        ...client,
                                        scopes: client.scopes.filter((_, i) => i !== index)
                                    });
                                }}>-</Button>
                            </td>
                        </tr>))}
                    <tr>
                        <td>
                            <Form.Control type="text" value={newScope} onChange={(e) => {
                                setNewScope(e.target.value);
                            }}/>
                        </td>
                        <td>
                            <Button onClick={() => {
                                setClient({
                                    ...client,
                                    scopes: [...client.scopes, newScope]
                                });
                            }}>
                                +
                            </Button>
                        </td>
                    </tr>
                    </tbody>
                </Table>
            </Form.Group>
            <Form.Group className={"mb-3"}>
                <Form.Switch label={t("admin.client.form.clientSettings.requireProofKey")}
                             checked={client.clientSettings.requireProofKey}
                             onChange={(e) => {
                                 setClient({
                                     ...client,
                                     clientSettings: {...client.clientSettings, requireProofKey: e.target.checked}
                                 });
                             }}/>
                <Form.Switch label={t("admin.client.form.clientSettings.requireAuthorizationConsent")}
                             checked={client.clientSettings.requireAuthorizationConsent}
                             onChange={(e) => {
                                 setClient({
                                     ...client,
                                     clientSettings: {
                                         ...client.clientSettings,
                                         requireAuthorizationConsent: e.target.checked
                                     }
                                 });
                             }}/>
            </Form.Group>
            <Form.Group className={"mb-3"}>
                <Form.Label>{t("admin.client.form.clientSettings.jwkSetUrl")}</Form.Label>
                <Form.Control type="text" value={client.clientSettings.jwkSetUrl || ""}
                              onChange={(e) => {
                                  setClient({
                                      ...client,
                                      clientSettings: {...client.clientSettings, jwkSetUrl: e.target.value}
                                  });
                              }} placeholder={t("admin.client.form.clientSettings.leave_blank_to_use_default")!}/>
            </Form.Group>
            <Form.Group className={"mb-3"}>
                <Form.Label>{t("admin.client.form.clientSettings.tokenEndpointAuthenticationSigningAlgorithm")}</Form.Label>
                <Form.Select value={client.clientSettings.tokenEndpointAuthenticationSigningAlgorithm || "none"}
                             onChange={(e) => {
                                 setClient({
                                     ...client,
                                     clientSettings: {
                                         ...client.clientSettings,
                                         tokenEndpointAuthenticationSigningAlgorithm: e.target.value === "none" ?
                                             null : e.target.value as JwsAlgorithm
                                     }
                                 });
                             }}>
                    <option value="none">none</option>
                    <option value="HS256">HS256</option>
                    <option value="HS384">HS384</option>
                    <option value="HS512">HS512</option>
                    <option value="RS256">RS256</option>
                    <option value="RS384">RS384</option>
                    <option value="RS512">RS512</option>
                    <option value="ES256">ES256</option>
                    <option value="ES384">ES384</option>
                    <option value="ES512">ES512</option>
                    <option value="PS256">PS256</option>
                    <option value="PS384">PS384</option>
                    <option value="PS512">PS512</option>
                </Form.Select>
            </Form.Group>
            <Form.Group className={"mb-3"}>
                <Form.Label>{t("admin.client.form.tokenSettings.authorizationCodeTimeToLive")}</Form.Label>
                <Form.Control type="number" value={client.tokenSettings.authorizationCodeTimeToLive}
                              onChange={(e) => {
                                  setClient({
                                      ...client,
                                      tokenSettings: {
                                          ...client.tokenSettings,
                                          authorizationCodeTimeToLive: parseInt(e.target.value)
                                      }
                                  });
                              }}/>
            </Form.Group>
            <Form.Group className={"mb-3"}>
                <Form.Label>{t("admin.client.form.tokenSettings.accessTokenTimeToLive")}</Form.Label>
                <Form.Control type="number" value={client.tokenSettings.accessTokenTimeToLive}
                              onChange={(e) => {
                                  setClient({
                                      ...client,
                                      tokenSettings: {
                                          ...client.tokenSettings,
                                          accessTokenTimeToLive: parseInt(e.target.value)
                                      }
                                  });
                              }}/>
            </Form.Group>
            <Form.Group className={"mb-3"}>
                <Form.Label>{t("admin.client.form.tokenSettings.refreshTokenTimeToLive")}</Form.Label>
                <Form.Control type="number" value={client.tokenSettings.refreshTokenTimeToLive}
                              onChange={(e) => {
                                  setClient({
                                      ...client,
                                      tokenSettings: {
                                          ...client.tokenSettings,
                                          refreshTokenTimeToLive: parseInt(e.target.value)
                                      }
                                  });
                              }}/>
            </Form.Group>
            <Form.Group className={"mb-3"}>
                <Form.Label>{t("admin.client.form.tokenSettings.accessTokenFormat")}</Form.Label>
                <Form.Select onChange={(e) => {
                    setClient({
                        ...client,
                        tokenSettings: {
                            ...client.tokenSettings,
                            accessTokenFormat: e.target.value as typeof client.tokenSettings.accessTokenFormat
                        }
                    });
                }}>
                    <option value="self-contained">Self-Contained</option>
                    <option value="reference">Reference</option>
                </Form.Select>
            </Form.Group>
            <Form.Group className={"mb-3"}>
                <Form.Label>{t("admin.client.form.tokenSettings.idTokenSignatureAlgorithm")}</Form.Label>
                <Form.Select value={client.tokenSettings.idTokenSignatureAlgorithm}
                             onChange={(e) => {
                                 setClient({
                                     ...client,
                                     tokenSettings: {
                                         ...client.tokenSettings,
                                         idTokenSignatureAlgorithm: e.target.value as JwsAlgorithm
                                     }
                                 });
                             }}>
                    <option value="HS256">HS256</option>
                    <option value="HS384">HS384</option>
                    <option value="HS512">HS512</option>
                    <option value="RS256">RS256</option>
                    <option value="RS384">RS384</option>
                    <option value="RS512">RS512</option>
                    <option value="ES256">ES256</option>
                    <option value="ES384">ES384</option>
                    <option value="ES512">ES512</option>
                    <option value="PS256">PS256</option>
                    <option value="PS384">PS384</option>
                    <option value="PS512">PS512</option>
                </Form.Select>
            </Form.Group>
            <Form.Group className={"d-flex mb-3 gap-3"}>
                <Button variant="primary" type="submit">{t("admin.client.form.submit")}</Button>
                <Button variant="secondary" type="reset">{t("admin.client.form.cancel")}</Button>
            </Form.Group>
        </Form>
    );
}