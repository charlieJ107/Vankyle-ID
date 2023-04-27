import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {useSearchParams} from "react-router-dom";
import {userManager} from "../../auth/userManager";
import {Loading} from "../shared/loading";
import {Error} from "../shared/error";

export function Oidc() {
    const [searchParams] = useSearchParams();
    const [state, setState] = useState<{
        status: string,
        errorType?: string,
        errorDescription?: string,
        errorUrl?: string,
        state?: string
    }>({status: "loading"});
    useEffect(() => {
        if (searchParams.has("error")) {
            // OAuth2 Error
            const errorType = searchParams.get("error");
            const description = searchParams.get("error_description");
            const errorUrl = searchParams.get("error_uri");
            const state = searchParams.get("state");
            setState({
                status: "error",
                errorType: errorType === null ? undefined : errorType,
                errorDescription: description === null ? undefined : description,
                errorUrl: errorUrl === null ? undefined : errorUrl,
                state: state === null ? undefined : state
            });
        } else {
            userManager.signinRedirectCallback().then((user) => {
                if (user.state) {
                    setState({status: "success"});
                    window.location.replace(user.state as string);
                } else {
                    setState({status: "success"});
                    window.location.assign("/");
                }
            }).catch((error) => {
                setState({status: "success"});
                console.error(error);
                window.location.pathname = "/";
            });
        }
    }, [searchParams, setState]);
    const {t} = useTranslation();
    if (state.status === "success") {
        return (
            <Loading label={t("utils.loading")}/>
        );
    } else if (state.status === "error") {
        return (
            <Error type={"unauthorized"}/>
        );
    } else {
        return <Loading label={t("utils.loading")}/>;
    }
}