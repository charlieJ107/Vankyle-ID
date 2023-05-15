import {Navigate, Route, Routes} from "react-router-dom";
import {Users} from "./users/users";
import {Layout as AdminLayout} from "./layout/layout";
import {Clients} from "./clients/clients";
import {NotFound} from "../shared/notFound";
import {useState} from "react";
import {CreateUser} from "./users/createUser";
import {EditUser} from "./users/editUser";
import {EditClient} from "./clients/editClient";
import {CreateClient} from "./clients/createClient";

export function Admin() {
    const [currentTab, setCurrentTab] =
        useState<"users" | "clients">("users");
    return (
        <AdminLayout currentTab={currentTab} setCurrentTab={setCurrentTab}>
            <Routes>
                <Route index element={<Navigate to={"users"}/>}/>
                <Route path={"users"} element={<Users/>}/>
                <Route path={"users/:id"} element={<EditUser/>}/>
                <Route path={"user/create"} element={<CreateUser/>}/>
                <Route path={"clients"} element={<Clients/>}/>
                <Route path={"clients/:id"} element={<EditClient/>}/>
                <Route path={"client/create"} element={<CreateClient/>}/>
                <Route path={"*"} element={<NotFound/>}/>
            </Routes>
        </AdminLayout>
    );
}