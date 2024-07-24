import Header from "@/components/layout/header.tsx";
import Footer from "@/components/layout/footer.tsx";

function Layout({children}: { children: React.ReactNode }) {
    return (
        <div className={"p-10 h-screen"}>
            <div className={"flex flex-col h-full"}>
                <Header/>
                <main className={"flex flex-grow flex-col justify-center text-center"}>
                    {children}
                </main>
                <Footer/>
            </div>
        </div>
    );
}

export default Layout;