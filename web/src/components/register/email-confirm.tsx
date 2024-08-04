import {useTranslation} from "react-i18next";
import DuoColLayout from "@/components/layout/duo-col-layout.tsx";
import {Button} from "@/components/ui/button.tsx";

function EmailConfirm(){
    const {t} = useTranslation();
    return (
        <DuoColLayout>
            <div className={"w-full flex flex-col p-12 md:p-48  justify-center"}>
                <h2 className={"text-3xl my-5 font-extrabold tracking-tight text-primary"}>
                    {t("emailConfirm")}
                </h2>
                <p className={"text-lg my-5"}>
                    {t("emailConfirmDescription")}
                </p>
                <p className={"text-lg mb-5"}>
                    {t("emailConfirmResendDescription")}
                </p>
                <div className={"flex gap-5"}>
                    <Button variant={"default"}>{t("emailConfirmResend")}</Button>
                    <Button variant={"secondary"}>{t("login")}</Button>
                </div>

            </div>
        </DuoColLayout>
    )
}

export default EmailConfirm;