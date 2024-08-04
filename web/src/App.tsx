import './App.css'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Home from "@/components/home/home.tsx";
import Login from "@/components/login/login.tsx";
import Register from "@/components/register/register.tsx";
import EmailConfirm from "@/components/register/email-confirm.tsx";
import ConfirmEmail, {confirmEmailLoader} from "@/components/register/confirm-email.tsx";
import Account from "@/components/account";

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
        path: "/account",
        element: <Account/>
    }

])

function App() {
    return (
        <RouterProvider router={router}/>
    )
}

export default App
