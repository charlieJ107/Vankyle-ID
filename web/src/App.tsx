import './App.css'
import {Button} from "@/components/ui/button.tsx";
import {Separator} from "@/components/ui/separator.tsx";
import {useTranslation} from "react-i18next";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";
import {Languages} from "lucide-react";

function App() {
    const {t, i18n} = useTranslation()
    return (
        <>
            <header>
                <div className={"flex justify-between"}>
                    <h1 className={"scroll-m-20 text-3xl font-extrabold tracking-tight lg:text-4xl"}>{t("app")}</h1>
                    <div className={"flex gap-3"}>
                        <Button variant={"default"}>{t("login")}</Button>
                        <Button variant={"secondary"}>{t("register")}</Button>
                    </div>
                </div>
                <Separator className={"my-5"}/>
            </header>

            <main className={"h-96 flex flex-col justify-center text-center"}>
                <h2 className={"scroll-m-20 text-2xl font-bold tracking-tight lg:text-3xl"}>{t("app")}</h2>
                <p className={"scroll-m-20 text-lg text-gray-500 lg:text-xl"}>{t("description")}</p>
                <div className={"flex gap-3 justify-center my-10"}>
                    <Button variant={"default"}>{t("login")}</Button>
                    <Button variant={"secondary"}>{t("register")}</Button>
                </div>
            </main>

            <footer className={"h-5"}>
                <Separator className={"my-5"}/>
                <div className={"flex justify-end"}>
                    <div className={"flex gap-2 items-center"}>
                        <Languages/>
                        <Select defaultValue={"en-GB"}
                                onValueChange={(value) => {
                                    i18n.changeLanguage(value)
                                }}>
                            <SelectTrigger className="w-48">
                                <SelectValue placeholder={"Language"}/>
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="en-GB">English-UK</SelectItem>
                                <SelectItem value="zh-CN">中文-中国</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>

                </div>
            </footer>
        </>
    )
}

export default App
