import React from "react";
import { Navbar } from "./Navbar"

interface AppLayoutProps {
    children: React.ReactNode;
};

export const AppLayout: React.FC<AppLayoutProps> = ({ children }) => {
    return (
        <div className="min-h-screen bg-gray-900">
            <Navbar />

            {children}
        </div>
    )
};
