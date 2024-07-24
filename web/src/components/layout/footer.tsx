import {Separator} from "@/components/ui/separator.tsx";
import LanguagePicker from "@/components/common/language-picker.tsx";

function Footer() {
    return (
        <footer className={"flex flex-col"}>
            <Separator className={"my-5"}/>
            <div className={"flex justify-end"}>
                <LanguagePicker/>
            </div>
        </footer>

    )
}

export default Footer;