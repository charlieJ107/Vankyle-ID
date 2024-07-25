import Header from "@/components/layout/header.tsx";
import Footer from "@/components/layout/footer.tsx";

function HomeLayout({children}: { children: React.ReactNode }) {
    return (
        <div className={"h-screen"}>
            <div className={"flex flex-col h-full"}>
                <Header className={"mx-16 mt-10"}/>
                <main className={"flex flex-grow flex-col justify-center text-center"}>
                    {children}
                </main>
                <Footer className={"mb-10 mx-20"}/>
            </div>
        </div>
    );
}

export default HomeLayout;