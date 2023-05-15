import {RequireAuth} from "../auth/requireAuth";
import {Layout} from "../layout";
import {Navigate, Route, Routes} from "react-router-dom";
import {Profile} from "./profile";
import {Security} from "./security";
import React from "react";

export function Account() {
    return (
        <RequireAuth>
            <Layout>
                <Routes>
                    <Route index element={<Navigate to={"/account/profile"}/>}/>
                    <Route path="profile" element={<Profile/>}/>
                    <Route path="security/*" element={<Security/>}/>
                    <Route path="*" element={<Navigate to={"/404"}/>}/>
                </Routes>
            </Layout>
        </RequireAuth>
    )
}