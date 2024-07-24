import {useTranslation} from "react-i18next";
import {
    Form,
    FormControl,
    FormDescription,
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

const FormSchema = z.object({
    username: z.string().min(2, {
        message: "Username must be at least 2 characters.",
    }),
});

const handleSubmit = async (data: z.infer<typeof FormSchema>) => {console.log(data)};

function Login() {
    const {t} = useTranslation();

    const form = useForm<z.infer<typeof FormSchema>>({
        resolver: zodResolver(FormSchema),
        defaultValues: {
            username: "",
        },
    })

    return (
        <div className={"h-screen flex"}>
            <div className={"h-full w-5/12 bg-primary p-10"}>
                <h1 className={"scroll-m-20 text-3xl font-extrabold tracking-tight lg:text-4xl text-primary-foreground"}>
                    {t("app")}
                </h1>
            </div>
            <div className={"h-full"}>
                <Form {...form} >
                    <form onSubmit={form.handleSubmit(handleSubmit)} className="w-2/3 space-y-6">
                        <FormField
                            control={form.control}
                            name="username"
                            render={({field}) => (
                                <FormItem>
                                    <FormLabel>Username</FormLabel>
                                    <FormControl>
                                        <Input placeholder="shadcn" {...field} />
                                    </FormControl>
                                    <FormDescription>
                                        This is your public display name.
                                    </FormDescription>
                                    <FormMessage/>
                                </FormItem>
                            )}
                        />
                        <Button type="submit">Submit</Button>
                    </form>
                </Form>
            </div>
        </div>
    )
}

export default Login;