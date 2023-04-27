import {Container, Image} from "react-bootstrap";
import dissatisfied from '../../img/dissatisfied.svg';
import {useTranslation} from "react-i18next";

export function Error(props: {
    type?: "forbidden" | "notFound" | "internalServerError" | "badRequest" | "unauthorized" | "unknown"
}) {
    return (
        <Container className="justify-content-center text-center mt-5">
            <Image src={dissatisfied} alt="dissatisfied" width={256} height={256}/>
            <ErrorIntroduction type={props.type}/>
        </Container>
    )
}

const ErrorIntroduction = (props: {
    type?: "forbidden" | "notFound" | "internalServerError" | "badRequest" | "unauthorized" | "unknown"
}) => {
    const {t} = useTranslation();
    switch (props.type) {
        case "forbidden":
            return (<h3 className={"mt-5"}>{t("utils.forbidden")}</h3>);
        default:
            return (<h3 className={"mt-5"}>{t("utils.error")}</h3>);
    }
}