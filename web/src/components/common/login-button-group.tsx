import {Button} from "@/components/ui/button.tsx";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";

function LoginButtonGroup() {
    const {t} = useTranslation();
    const navigate = useNavigate();
    return (
        <div className={"flex gap-3"}>
            <Button variant={"default"} onClick={()=>navigate("/login")}>{t("login")}</Button>
            <Button variant={"secondary"} onClick={()=>navigate("/register")}>{t("register")}</Button>
        </div>
    );
}

export default LoginButtonGroup;