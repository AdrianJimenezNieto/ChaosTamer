import React from "react";
import { Navbar } from "./Navbar"

interface AppLayoutProps {
    children: React.ReactNode;
};

export const AppLayout: React.FC<AppLayoutProps> = ({ children }) => {
    return (
        <div className="min-h-screen bg-gray-900">
            <Navbar />

            <main className="h-full w-7xl mx-auto py-6 sm:px-6 lg:px-8">
                {children}
            </main>
        </div>
    )
};
