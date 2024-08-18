import './App.css'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Home from "@/components/home/home.tsx";
import Login from "@/components/login/login.tsx";
import Register from "@/components/register/register.tsx";
import EmailConfirm from "@/components/register/email-confirm.tsx";
import ConfirmEmail from "@/components/register/confirm-email.tsx";
import {confirmEmailLoader} from "@/lib/confirm-email.ts";
import Consent from "@/components/consent/Consent.tsx";
import {consentLoader} from "@/lib/consent.ts";

const router = createBrowserRouter([
    {
        path: "/",
        element: <Home/>
    },
    {
        path: "/login",
        element: <Login/>
    },
    {
        path: "/register",
        element: <Register/>
    },
    {
        path: "/email-confirm",
        element: <EmailConfirm/>
    },
    {
        path: "/confirm-email/:token",
        element: <ConfirmEmail/>,
        loader: confirmEmailLoader
    },
    {
        path:"/consent",
        element: <Consent/>,
        loader: consentLoader
    }
])

function App() {
    return (
        <RouterProvider router={router}/>
    )
}

export default App
