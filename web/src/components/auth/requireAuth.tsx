import React, {useEffect, useState} from "react";
import {userManager} from "../../auth/userManager";
import {Loading} from "../shared/loading";
import {Error} from "../shared/error";

export function RequireAuth(props: {
    children: JSX.Element;
    roles?: string[],
    authority?: string[]
}) {
    const [status, setStatus] =
        useState<"success" | "forbidden" | "unauthorized" | "loading" | "error">("loading");
    useEffect(() => {
        userManager.getUser().then((user) => {
            if (user === null) {
                setStatus("unauthorized");
                userManager.signinRedirect({state: window.location.href})
                    .then()
                    .catch((error) => {
                        console.error(error);
                        setStatus("error");
                    });
            } else {
                if (props.roles && props.roles.length > 0) { // Check required roles given
                    if (user.profile["role"] && props.roles.length > 0) { // Roles are given in id_token
                        const tokenRoles: string[] = user.profile["role"] as string[];
                        let forbidden: boolean = false;
                        props.roles.forEach((val) => {
                            if (!tokenRoles.includes(val)) { // Check all roles required are given in id_token
                                forbidden = true;
                            }
                        });
                        // End check roles required, result in `forbidden`
                        if (forbidden) { // Set status according to `forbidden`
                            setStatus("forbidden");
                        } else {
                            setStatus("success");
                        }
                    } else { // Roles are not given in id_token, but roles checked required
                        setStatus("forbidden");
                    }
                } else { // Required Roles are not given, login needed only
                    setStatus("success");
                }
            }
        }).catch((e) => {
            userManager.signoutRedirect().then();
        });
    }, [props, setStatus]);
    switch (status) {
        case "unauthorized": // Login required
            return <Loading/>;
        case "forbidden": // Login success, but roles check did not pass
            return <Error type={"forbidden"}/>;
        case "success": // Login success and roles checked
            return props.children;
        case "error": // Error when userManager getting user
            return <Error/>
        default:
            return <Loading/>;
    }
}
