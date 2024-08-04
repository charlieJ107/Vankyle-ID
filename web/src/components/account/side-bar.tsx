import {cn} from "@/lib/utils.ts";
import React from "react";


function SideBarItem({className, children}: { className?: string, children: React.ReactNode }) {
    return (
        <div className={cn(className, "flex items-center")}>
            {children}
        </div>
    );
}

function SideBar({className}: { className?: string }) {

    return (
        <div className={cn(className, "flex flex-col")}>
            <SideBarItem>
                <div>Profile</div>
            </SideBarItem>
            <SideBarItem>
                <div>Settings</div>
            </SideBarItem>
        </div>
    );
}

export default SideBar;