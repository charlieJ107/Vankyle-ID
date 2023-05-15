import {useTranslation} from "react-i18next";
import {Button, Container, Image} from "react-bootstrap";
import notfound from "../../img/404.svg";

export function NotFound() {
    const {t} = useTranslation();
    return (
        <Container className="justify-content-center text-center mt-5">
            <Image src={notfound} alt="dissatisfied" width={256} height={256}/>
            <h3 className={"mt-5"}>{t("utils.not_found")}</h3>
            <Button className={"mt-5"} onClick={()=>window.history.back()}>{t("utils.back")}</Button>
        </Container>
    )
}