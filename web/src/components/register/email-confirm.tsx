import {useTranslation} from "react-i18next";
import DuoColLayout from "@/components/layout/duo-col-layout.tsx";

function EmailConfirm(){
    const {t} = useTranslation();
    return (
        <DuoColLayout>
            <div className={"w-full flex flex-col p-12 md:p-48  justify-center"}>
                <h2 className={"text-3xl my-5 font-extrabold tracking-tight text-primary"}>
                    {t("emailConfirm")}
                </h2>
            </div>
        </DuoColLayout>
    )
}

export default EmailConfirm;