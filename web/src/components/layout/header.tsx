import {Separator} from "@/components/ui/separator.tsx";
import {useTranslation} from "react-i18next";
import LoginButtonGroup from "@/components/common/login-button-group.tsx";
import {cn} from "@/lib/utils.ts";

function Header({className}: { className?: string }) {
    const {t} = useTranslation();
    return (
        <header className={cn("flex flex-col", className)}>
            <div className={"flex justify-between"}>
                <h1 className={"scroll-m-20 text-3xl font-extrabold tracking-tight lg:text-4xl"}>{t("app")}</h1>
                <LoginButtonGroup/>
            </div>
            <Separator className={"my-5"}/>
        </header>
    );
}

export default Header;