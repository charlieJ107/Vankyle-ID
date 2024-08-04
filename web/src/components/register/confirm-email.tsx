import {useLoaderData} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {Button} from "@/components/ui/button.tsx";
import DuoColLayout from "@/components/layout/duo-col-layout.tsx";


export async function confirmEmailLoader({params}: any) {
    return await fetch(`/api/confirm-email/${params.token}`)
        .then((response) => {
            if (response.status === 200) {
                return response.json();
            } else {
                return {success: false};
            }
        })
        .then((data) => {
            return {success: data.success};
        }).catch(
            () => {
                return {success: false};
            }
        );
}

function ConfirmEmail() {
    const {success} = useLoaderData() as { success: boolean };
    const {t} = useTranslation();
    let title = t("emailConfirmError");
    if (success) {
        title = t("emailConfirmSuccess");
    }
    return (
        <DuoColLayout>
            <div className={"w-full flex flex-col p-12 md:p-48  justify-center"}>
                <h2 className={"text-3xl my-5 font-extrabold tracking-tight text-primary"}>
                    {title}
                </h2>
                <div className={"flex gap-5"}>
                    <Button variant={"default"}>{t("login")}</Button>
                </div>
            </div>
        </DuoColLayout>
    );
}

export default ConfirmEmail;