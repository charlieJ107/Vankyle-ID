import {Separator} from "@/components/ui/separator.tsx";
import {Languages} from "lucide-react";
import {Select, SelectContent, SelectItem, SelectTrigger, SelectValue} from "@/components/ui/select.tsx";
import {useTranslation} from "react-i18next";

function Footer(){
    const {i18n} = useTranslation();
    return (
        <footer className={"flex flex-col"}>
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

    )
}

export default Footer;