import {Separator} from "@/components/ui/separator.tsx";
import {useTranslation} from "react-i18next";
import {cn} from "@/lib/utils.ts";
import React from "react";

function Header({className, actions}: { className?: string, actions?: React.ReactNode }) {
    const {t} = useTranslation();
    return (
        <header className={cn("flex flex-col", className)}>
            <div className={"flex justify-between"}>
                <h1 className={"scroll-m-20 text-3xl font-extrabold tracking-tight lg:text-4xl"}>{t("app")}</h1>
                {/*<LoginButtonGroup/>*/}
                {actions}
            </div>
            <Separator className={"my-5"}/>
        </header>
    );
}

export default Header;