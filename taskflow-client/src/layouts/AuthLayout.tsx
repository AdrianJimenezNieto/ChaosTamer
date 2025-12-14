import React from "react";

interface AuthLayoutProps {
    children: React.ReactNode;
    title: string;
    subtitle: string;
};

export const AuthLayout: React.FC<AuthLayoutProps> = ({ children, title, subtitle }) => {
    return (
        <div className="min-h-screen flex w-full">
            {/* Mobile section */}
            <div className="w-full lg:w-1/2 flex flex-col justify-center items-center bg-white px-8 py-12 lg:px-24">

            </div>
        </div>
    )
};