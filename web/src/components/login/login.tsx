import {useTranslation} from "react-i18next";
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage
} from "@/components/ui/form.tsx";
import {zodResolver} from "@hookform/resolvers/zod";
import {useForm} from "react-hook-form";
import {z} from "zod"
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import LanguagePicker from "@/components/common/language-picker.tsx";
import {Separator} from "@/components/ui/separator.tsx";
import LoginButtonGroup from "@/components/common/login-button-group.tsx";

const FormSchema = z.object({
    username: z.string().min(2, {
        message: "Username must be at least 2 characters.",
    }),
    password: z.string().min(8, {
        message: "Password must be at least 8 characters.",
    }),
});

const onSubmit = async (data: z.infer<typeof FormSchema>) => {
    // TODO: Implement login
    console.log(data);
};

function Login() {
    const {t} = useTranslation();

    const form = useForm<z.infer<typeof FormSchema>>({
        resolver: zodResolver(FormSchema),
        defaultValues: {
            username: "",
            password: ""
        },
    })
// TODO Mobile Landscape
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
                    <div className={"w-full flex flex-col p-12 md:p-48 justify-center"}>
                        <h2 className={"text-3xl my-5 font-extrabold tracking-tight text-primary"}>
                            {t("login")}
                        </h2>
                        <Form {...form} >
                            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                                <FormField
                                    control={form.control}
                                    name="username"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>{t("username")}</FormLabel>
                                            <FormControl>
                                                <Input {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="password"
                                    render={({field}) => (
                                        <FormItem>
                                            <FormLabel>{t("password")}</FormLabel>
                                            <FormControl>
                                                <Input type="password" {...field} />
                                            </FormControl>
                                            <FormMessage/>
                                        </FormItem>
                                    )}
                                />
                                <div className={"flex justify-between"}>
                                    <LoginButtonGroup/>
                                    < Button variant={"link"}>{t("forgotPassword")}</Button>
                                </div>
                            </form>
                        </Form>
                    </div>
                    <footer className={"h-28"}>
                        <Separator/>
                        <div className={"flex justify-end me-3 my-5"}>
                            <LanguagePicker/>
                        </div>
                    </footer>
                </div>
            </div>
        </div>
    )
}

export default Login;