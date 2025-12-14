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
                <div className="w-full max-w-md space-y-8">
                    {/* Auth header */}
                    <div className="text-center lg:text-left">
                        <h2 className="mt-6 text-3xl font-extrabold text-gray-900 tracking-tight">
                            {title}
                        </h2>
                        <p className="mt-2 text-sm text-gray-600"> 
                            {subtitle}
                        </p>
                    </div>

                    {children}

                </div>
            </div>

            {/* Decorative Image just on pc */}
            <div className="hidden lg:block lg:w-1/2 bg-gray-900 relative">

                <div className="absolute bottom-0 left-0 p-12 text-white z-10">
                    <blockquote className="text-xl font-medium">
                        "Esta aplicación ha transformacio complotamente cómo gestionamos nuestros flujos de trabajo y tareas."
                    </blockquote>
                    <p className="mt-4 font-bold">- El equipo de Desarrollo.</p>
                </div>
            </div>
        </div>
    )
};