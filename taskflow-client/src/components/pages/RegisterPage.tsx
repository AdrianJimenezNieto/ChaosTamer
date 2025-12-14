import React, { Fragment, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { AuthLayout } from "../layouts/AuthLayout";
import { login, register } from "../../services/authService";
import { Transition } from "@headlessui/react";

export const RegisterPage: React.FC = () => {
    // Local state for the form
    const [formData, setFormData] = useState({
        fullName: '',
        email: '',
        password: '',
    })

    // Error state
    const [error, setError] = useState<string | null>(null);

    const navigate = useNavigate();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value}))
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        console.log('Datos enviados: ', formData);
        
        try {
            await register(formData);

            // AUTO-LOGIN
            await login({
                email: formData.email,
                password: formData.password,
            })

            // redirect
            navigate('/dashboard');
        } catch (error) {
            // show the error if something was wrong
            if (error instanceof Error) {
                setError(error.message);
            } else {
                setError("Ha ocurrido un error inesperado.");
            }
        }
    }

    return (
        <AuthLayout
            title="Crea tu cuenta"
            subtitle="Empieza a gestionar tus proyectos hoy mismo"
        >
            <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
                <div className="space-y-4">
                    {/* FULL NAME */}
                    <div>
                        <label htmlFor="fullName" className="block text-sm font-medium text-gray-700">
                            Nombre completo
                        </label>
                        <div className="mt-1">
                            <input
                                type="text" 
                                id="fullName"
                                name="fullName"
                                required
                                className="appearance-none block w-full px-3 py-2 border border-gray-00 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition-all"
                                placeholder="Ej. Juan Pérez"
                                value={formData.fullName}
                                onChange={handleChange}
                            />
                        </div>
                    </div>
                    {/* EMAIL */}
                    <div>
                        <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                            Correo electrónico
                        </label>
                        <div className="mt-1">
                            <input 
                                type="email"
                                id="email"
                                name="email"
                                autoComplete="email"
                                required
                                className="appearance-none block w-full px-3 py-2 border border-gray-00 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition-all"
                                placeholder="Ej. juanperez@dominio.com"
                                value={formData.email}
                                onChange={handleChange}
                            />
                        </div>
                    </div>
                    {/* PASSWORD */}
                    <div>
                        <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                            Contraseña
                        </label>
                        <div className="mt-1">
                            <input 
                                type="password"
                                id="password"
                                name="password"
                                required
                                className="appearance-none block w-full px-3 py-2 border border-gray-00 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition-all"
                                placeholder="••••••••"
                                value={formData.password}
                                onChange={handleChange}
                            />
                        </div>
                    </div>
                </div>

                {/* Error Message (Transition) */}
                <div className="h-6"> 
                    <Transition
                    show={error !== null}
                    as={Fragment}
                    enter="transition-all duration-300 ease-in-out"
                    enterFrom="opacity-0 -translate-y-2"
                    enterTo="opacity-100 translate-y-0"
                    leave="transition-all duration-300 ease-in-out"
                    leaveFrom="opacity-100 translate-y-0"
                    leaveTo="opacity-0 -translate-y-2"
                    >
                    <p className="text-center text-sm text-red-600 font-medium">
                        {error}
                    </p>
                    </Transition>
                </div>

                {/* ACTION BUTTON */}
                <div>
                    <button type="submit" className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors">
                        Registrarse
                    </button>
                </div>

                {/* LOGIN LINK */}
                <div className="text-sm text-center">
                    <span className="text-gray-500">¿Ya tienes una cuenta? </span>
                    <Link to="/login" className="font-medium text-indigo-600 hover:text-indigo-500">
                        Inicia sesión desde aquí
                    </Link>
                </div>
            </form>    
        </AuthLayout>
    )   
}