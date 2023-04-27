import {Navigate, Route, Routes} from "react-router-dom";
import React from "react";
import {Password} from "./password/password";

export function Security(){
    return(
        <Routes>
            <Route index element={<Password/>}/>
            <Route path={"*"} element={<Navigate to={"/404"}/>}/>
        </Routes>
    )
}