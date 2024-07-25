import {Separator} from "@/components/ui/separator.tsx";
import LanguagePicker from "@/components/common/language-picker.tsx";
import {cn} from "@/lib/utils.ts";

function Footer({className}: {className?: string}) {
    return (
        <footer className={cn("flex flex-col h-28", className)}>
            <Separator className={"my-5"}/>
            <div className={"flex justify-end"}>
                <LanguagePicker/>
            </div>
        </footer>

    )
}

export default Footer;