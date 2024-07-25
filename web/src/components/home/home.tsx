import {useTranslation} from "react-i18next";
import HomeLayout from "@/components/layout/home-layout.tsx";
import LoginButtonGroup from "@/components/common/login-button-group.tsx";

function Home() {
    const {t} = useTranslation()
    return (
        <HomeLayout>
            <h2 className={"scroll-m-20 text-2xl font-bold tracking-tight lg:text-3xl"}>{t("app")}</h2>
            <p className={"scroll-m-20 text-lg text-gray-500 lg:text-xl"}>{t("description")}</p>
            <div className={"flex justify-center my-5"}><LoginButtonGroup/></div>
        </HomeLayout>
    )
}

export default Home;