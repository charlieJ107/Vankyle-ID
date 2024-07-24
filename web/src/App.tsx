import './App.css'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Home from "@/components/home/home.tsx";
import Login from "@/components/login/login.tsx";

const router = createBrowserRouter([
    {
        path: "/",
        element: <Home/>
    },
    {
        path: "/login",
        element: <Login/>
    }

])

function App() {
    return (
        <RouterProvider router={router}/>
    )
}

export default App
