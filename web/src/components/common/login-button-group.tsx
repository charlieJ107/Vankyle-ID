import {Button} from "@/components/ui/button.tsx";
import {useTranslation} from "react-i18next";

function LoginButtonGroup() {
    const {t} = useTranslation();
    return (
        <div className={"flex gap-3"}>
            <Button variant={"default"}>{t("login")}</Button>
            <Button variant={"secondary"}>{t("register")}</Button>
        </div>
    );
}

export default LoginButtonGroup;