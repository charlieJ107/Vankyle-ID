import {useTranslation} from "react-i18next";
import React from "react";
import Footer from "@/components/layout/footer.tsx";

function DuoColLayout({children}: { children: React.ReactNode }) {
    const {t} = useTranslation();
    return (
        <div className={"h-screen flex"}>
            <div className={"h-0 md:h-full w-0 md:w-5/12 bg-primary md:p-20"}>
                <div className={"h-full flex flex-col justify-between"}>
                    <div>
                        <h1 className={"scroll-m-20 text-3xl font-extrabold tracking-tight lg:text-4xl text-primary-foreground"}>
                            {t("app")}
                        </h1>
                    </div>
                    <div className={"flex justify-end my-6"}>
                        <p className={"text-xl text-primary-foreground"}>{t("description")}</p>
                    </div>
                </div>
            </div>
            <div className={"h-full w-full md:w-7/12"}>
                <div className={"flex flex-col h-full justify-between"}>
                    {children}
                    <Footer className={"mx-10"}/>
                </div>
            </div>
        </div>
    );
}

export default DuoColLayout;