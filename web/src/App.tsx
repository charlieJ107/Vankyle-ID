import React from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import {Home} from './components/home/home';
import {Error} from './components/shared/error';
import {NotFound} from './components/shared/notFound';
import {Oidc} from "./components/auth/oidc";
import {Register} from "./components/register/register";
import {ForgotPassword} from "./components/forgotPassword/forgotPassword";
import {ResetPassword} from "./components/resetPassword/resetPassword";
import {ConfirmEmail} from "./components/confirmEmail/confirmEmail";
import {Login} from "./components/login/login";
import {Consent} from "./components/consent/consent";
import {Logout} from "./components/logout/logout";
import {Account} from "./components/account";
import {Admin} from "./components/admin";

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
                <Route path="/admin/*" element={<Admin/>}/>
                <Route path="/logout" element={<Logout/>}/>
                <Route path="/oidc" element={<Oidc/>}/>
                <Route path="/error" element={<Error/>}/>
                <Route path="/404" element={<NotFound/>}/>
                <Route path="*" element={<NotFound/>}/>
            </Routes>
        </BrowserRouter>
    );
}

export default App;
