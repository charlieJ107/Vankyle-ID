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
import LoginButtonGroup from "@/components/common/login-button-group.tsx";
import DuoColLayout from "@/components/layout/duo-col-layout.tsx";

const FormSchema = z.object({
    username: z.string().min(2, {
        message: "Username must be at least 2 characters.",
    }),
    password: z.string().min(8, {
        message: "Password must be at least 8 characters.",
    }),
});




function Login() {
    const {t} = useTranslation();
    const form = useForm<z.infer<typeof FormSchema>>({
        resolver: zodResolver(FormSchema),
        defaultValues: {
            username: "",
            password: ""
        },
    });

    const onSubmit = async (data: z.infer<typeof FormSchema>) => {
        const reqParams = new URLSearchParams();
        reqParams.append("username", data.username);
        reqParams.append("password", data.password);
        // Merge http://localhost:8080/api/login with reqParams
        const reqUrl = new URL("http://localhost:8080/api/login");
        reqUrl.search = reqParams.toString();

        const response = await fetch(reqUrl, {
            method: "POST",
            headers: {
                "Content-Type": "x-www-form-urlencoded",
                "Accept": "application/json",
            },
            redirect:"manual",
        });
        console.log(response);
        if (response.ok) {
            const location = (await response.json()).redirectUrl;
            if (location) {
                window.location.replace(location);
            } else {
                window.location.replace("/");
            }
        } else {
            console.log("Login failed");
            form.setError("root", {
                message: t("loginError")
            });
        }
    };
    return (
        <DuoColLayout>
            <div className={"w-full flex flex-col p-12 md:p-48  justify-center"}>
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
                        <FormMessage>{form.formState.errors.root?.message}</FormMessage>
                        <div className={"flex justify-between"}>
                            <LoginButtonGroup/>
                            < Button variant={"link"}>{t("forgotPassword")}</Button>
                        </div>
                    </form>
                </Form>
            </div>
        </DuoColLayout>
    )
}

export default Login;