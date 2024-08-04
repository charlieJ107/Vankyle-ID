import HomeLayout from "@/components/layout/home-layout.tsx";

function Account() {
  return (
    <HomeLayout>
        <div className="flex flex-col items-center justify-center h-full">
            <h1 className="text-3xl font-bold">Account</h1>
        </div>
    </HomeLayout>
  );
}

export default Account;