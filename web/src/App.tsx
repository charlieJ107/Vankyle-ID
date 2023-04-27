import React from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import {BrowserRouter, Navigate, Route, Routes} from 'react-router-dom';
import {Home} from './components/home/home';
import {Profile} from './components/profile';
import {Security} from './components/security';
import {Error} from './components/shared/error';
import {Loading} from './components/shared/loading';
import {NotFound} from './components/shared/notFound';
import {RequireAuth} from "./components/auth/requireAuth";
import {Oidc} from "./components/auth/oidc";
import {Layout} from "./components/layout";
import {Register} from "./components/register/register";
import {ForgotPassword} from "./components/forgotPassword/forgotPassword";
import {ResetPassword} from "./components/resetPassword/resetPassword";
import {ConfirmEmail} from "./components/confirmEmail/confirmEmail";
import {Login} from "./components/login/login";
import {Consent} from "./components/consent/consent";
import {Logout} from "./components/logout/logout";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/consent" element={<Consent/>}/>
                <Route path={"/register"} element={<Register/>}/>
                <Route path="/forgot-password" element={<ForgotPassword/>}/>
                <Route path="/reset-password" element={<ResetPassword/>}/>
                <Route path="/confirm-email" element={<ConfirmEmail/>}/>
                <Route path="/account/*" element={<Account/>}/>
                <Route path="/logout" element={<Logout/>}/>
                <Route path="/oidc" element={<Oidc/>}/>
                <Route path="/error" element={<Error/>}/>
                <Route path="/404" element={<NotFound/>}/>
                <Route path="*" element={<NotFound/>}/>
                {/* For Debug */}
                <Route path="/loading" element={<Loading/>}/>
            </Routes>
        </BrowserRouter>
    )
}

function Account() {
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

export default App;
