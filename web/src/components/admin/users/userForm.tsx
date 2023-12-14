import {Button, Form, Table} from "react-bootstrap";
import {UserInterface} from "./userInterface";
import {useTranslation} from "react-i18next";
import React from "react";
import {Dash, Plus} from "react-bootstrap-icons";

export function UserForm({user, setUser, onSubmit, onReset}: {
    user: UserInterface,
    setUser: (user: UserInterface) => void,
    onSubmit: (user: UserInterface) => void,
    onReset?: (user: UserInterface) => void
}) {
    const {t} = useTranslation();
    const [newAuthority, setNewAuthority] = React.useState<string>("");
    const [newRole, setNewRole] = React.useState<string>("");
    const onSubmitForm = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        onSubmit(user);
    }
    const onResetForm = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        onReset ? onReset(user) : setUser(user);
    }
    return (
        <Form onSubmit={onSubmitForm} onReset={onResetForm}>
            <Form.Group>
                <Form.Label>{t("admin.user.form.id")}</Form.Label>
                <Form.Control type="text" value={user.id || ""} readOnly disabled/>
            </Form.Group>
            <Form.Group>
                <Form.Label>{t("admin.user.form.username")}</Form.Label>
                <Form.Control type="text" value={user.username || ""} onChange={(e) => {
                    setUser({...user, username: e.target.value});
                }}/>
            </Form.Group>
            <Form.Group>
                <Form.Label>{t("admin.user.form.name")}</Form.Label>
                <Form.Control type="text" value={user.name || ""} onChange={(e) => {
                    setUser({...user, name: e.target.value});
                }}/>
            </Form.Group>
            <Form.Group>
                <Form.Label>{t("admin.user.form.password")}</Form.Label>
                <Form.Control type="text" value={user.password || ""}
                              placeholder={t("admin.user.form.password_not_displayed")!}
                              onChange={(e) => {
                                  setUser({...user, password: e.target.value});
                              }}/>
            </Form.Group>
            <Form.Group>
                <Form.Label>{t("admin.user.form.authorities")}</Form.Label>
                <Table striped bordered hover size="sm">
                    <thead>
                    <tr>
                        <th>{t("admin.user.form.authority")}</th>
                        <th>{t("admin.user.form.action")}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {user.authorities && user.authorities.map((authority, index) => {
                        return (
                            <tr key={index}>
                                <td>{authority}</td>
                                <td>
                                    <Button variant="danger" size="sm" onClick={() => {
                                        setUser({
                                            ...user,
                                            authorities: user.authorities!.filter((item) => item !== authority)
                                        });
                                    }}>
                                        <Dash />
                                    </Button>
                                </td>
                            </tr>
                        );
                    })}
                    <tr>
                        <td>
                            <Form.Control type="text" value={newAuthority}
                                          onChange={(e) => {
                                              setNewAuthority(e.target.value);
                                          }}/>
                        </td>
                        <td><Button variant="primary" size="sm" onClick={() => {
                            setUser({...user, authorities: [...user.authorities!, newAuthority]});
                            setNewAuthority("");
                        }}><Plus /></Button></td>
                    </tr>
                    </tbody>
                </Table>
            </Form.Group>
            <Form.Group>
                <Form.Label>{t("admin.user.form.roles")}</Form.Label>
                <Table striped bordered hover size="sm">
                    <thead>
                    <tr>
                        <th>{t("admin.user.form.role")}</th>
                        <th>{t("admin.user.form.action")}</th>
                    </tr>
                    </thead>
                    <tbody>
                    {user.roles && user.roles.map((role, index) => {
                        return (
                            <tr key={index}>
                                <td>{role}</td>
                                <td>
                                    <Button variant="danger" size="sm" onClick={() => {
                                        setUser({...user, roles: user.roles!.filter((item) => item !== role)});
                                    }}>
                                        <Dash />
                                    </Button>
                                </td>
                            </tr>
                        );
                    })}
                    <tr>
                        <td>
                            <Form.Control type="text" value={newRole} onChange={(e) => {
                                if (e.target.value.startsWith("ROLE")) {
                                    setNewRole(e.target.value.substring(5));
                                } else {
                                    setNewRole(e.target.value);
                                }
                            }}/>
                        </td>
                        <td>
                            <Button variant="primary" size="sm" onClick={() => {
                                if (newRole.length < 1) return;
                                setUser({...user, roles: [...user.roles!, newRole]});
                                setNewRole("");
                            }}>
                                <Plus />
                            </Button>
                        </td>
                    </tr>
                    </tbody>
                </Table>
            </Form.Group>
            <Form.Group>
                <Form.Switch label={t("admin.user.form.enabled")} checked={user.enabled}
                             onChange={(e) => {
                                 setUser({...user, enabled: e.target.checked});
                             }}/>
                <Form.Switch label={t("admin.user.form.accountExpired")} checked={user.accountExpired}
                             onChange={(e) => {
                                 setUser({...user, accountExpired: e.target.checked});
                             }}/>
                <Form.Switch label={t("admin.user.form.accountLocked")} checked={user.accountLocked}
                             onChange={(e) => {
                                 setUser({...user, accountLocked: e.target.checked});
                             }}/>
                <Form.Switch label={t("admin.user.form.credentialsExpired")} checked={user.credentialsExpired}
                             onChange={(e) => {
                                 setUser({...user, credentialsExpired: e.target.checked});
                             }}/>
                <Form.Switch label={t("admin.user.form.mfaEnabled")} checked={user.mfaEnabled}
                             onChange={(e) => {
                                 setUser({...user, mfaEnabled: e.target.checked});
                             }} disabled={true}/>
            </Form.Group>
            <Form.Group>
                <Form.Label>Email</Form.Label>
                <Form.Control type="text" value={user.email || ""} onChange={(e) => {
                    setUser({...user, email: e.target.value});
                }}/>
                <Form.Switch label={t("admin.user.form.email_verified")} checked={user.emailVerified}
                             onChange={(e) => {
                                 setUser({...user, emailVerified: e.target.checked});
                             }}/>
            </Form.Group>
            <Form.Group>
                <Form.Label>Phone</Form.Label>
                <Form.Control type="text" value={user.phone || ""} readOnly/>
                <Form.Switch label={t("admin.user.form.phone_verified")} checked={user.phoneVerified}
                             onChange={(e) => {
                                 setUser({...user, phoneVerified: e.target.checked});
                             }}/>
            </Form.Group>
            <Form.Group className={"w-25 my-4 d-flex justify-content-around"}>
                <Button variant="primary" type="submit">{t("admin.user.form.submit")}</Button>
                <Button variant="secondary" type="reset">{t("admin.user.form.cancel")}</Button>
            </Form.Group>
        </Form>
    );
}