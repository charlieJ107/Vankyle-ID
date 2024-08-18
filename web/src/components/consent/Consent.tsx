import HomeLayout from "@/components/layout/home-layout.tsx";

function Consent() {
    return (
        <HomeLayout>
            <h2 className={"scroll-m-20 text-2xl font-bold tracking-tight lg:text-3xl"}>Consent</h2>
            <p className={"scroll-m-20 text-lg text-gray-500 lg:text-xl"}>By using this app, you consent to the use of cookies.</p>
        </HomeLayout>
    );
}

export default Consent;
