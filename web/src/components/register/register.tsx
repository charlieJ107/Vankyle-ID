import {useTranslation} from "react-i18next";
import DuoColLayout from "@/components/layout/duo-col-layout.tsx";
import {z} from "zod";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form.tsx";
import {Input} from "@/components/ui/input.tsx";
import {Button} from "@/components/ui/button.tsx";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {useNavigate} from "react-router-dom";


function Register() {
    const {t, i18n} = useTranslation();
    const FormSchema = z.object({
        password: z.string().min(8, {
            message: t("passwordValidation"),
        }),
        confirm_password: z.string().min(8, {
            message: t("passwordValidation"),
        }),
        email: z.string().email({
            message: t("emailValidation"),
        })
    }).refine((data) => data.password === data.confirm_password, {
        message: t("confirmPasswordValidation"),
        path: ["confirm_password"],
    });

    const form = useForm<z.infer<typeof FormSchema>>({
        resolver: zodResolver(FormSchema),
        defaultValues: {
            password: "",
            confirm_password: "",
            email: "",
        },
    });

    const navigate = useNavigate();

    const onSubmit = async (data: z.infer<typeof FormSchema>) => {
        const response = await fetch(`http://localhost:8080/api/register?locale=${i18n.language}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        });
        if (response.ok) {
            const status: number = (await response.json()).status;
            if (status === 200) {
                navigate("/email-confirm");
            } else {
                form.setError("email", {
                    message: t("emailExists"),
                });
            }
        } else {
            const error = await response.json();
            form.setError("email", {
                type: "server",
                message: error.message,
            });
        }
    };



    return (
        <DuoColLayout>
            <div className={"w-full flex flex-col p-12 md:p-40  justify-center"}>
                <h2 className={"text-3xl my-5 font-extrabold tracking-tight text-primary"}>
                    {t("register")}
                </h2>
                <Form {...form} >
                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                        <FormField
                            control={form.control}
                            name="email"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>{t("email")}</FormLabel>
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
                        <FormField
                            control={form.control}
                            name="confirm_password"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>{t("confirmPassword")}</FormLabel>
                                    <FormControl>
                                        <Input type="password" {...field} />
                                    </FormControl>
                                    <FormMessage/>
                                </FormItem>
                            )}/>
                        <div className={"flex gap-5"}>
                            <Button variant={"default"} type={"submit"}>{t("register")}</Button>
                            <Button variant={"secondary"} onClick={() => navigate("/login")}>{t("login")}</Button>
                        </div>
                    </form>
                </Form>
            </div>
        </DuoColLayout>
    );
}

export default Register;