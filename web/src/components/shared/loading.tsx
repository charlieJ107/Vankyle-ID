import React from 'react';
import {Container, Spinner} from 'react-bootstrap';
import {useTranslation} from 'react-i18next';

export function Loading(props: { label?: string | null }) {
    const {t} = useTranslation();
    return (
        <Container className="justify-content-center text-center" style={{marginTop: "30vh"}}>
            <Spinner animation="border" role="status">
                <span className="visually-hidden">{t("utils.loading")}</span>
            </Spinner>
            <LoadingLabel label={props.label} />
        </Container>
    );
}

const LoadingLabel = (props: { label?: string | null }) => {
    const {t} = useTranslation();
    if (!props.label) {
        return (
            <h3 className={"mt-5"}>{t("utils.loading")}</h3>
        );
    }
    return (
        <h3 className={"mt-5"}>{props.label}</h3>
    );
}