import React, { useState, Fragment } from "react";
import { Link, useNavigate } from "react-router-dom";
import { login } from "../../services/authService";
// Components
import { AuthLayout } from "../layouts/AuthLayout";
// HEADLESSui
import { Transition } from "@headlessui/react";
import { LoadingSpinner } from "../ui/LoadingSpinner";

export default function LoginPage() {
  // States of the component
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  })
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  

  // Navigate Hook
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value}));
  };

  // Handle from submit
  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault(); // cancell page reload
    setError(null); // delete previous errors
    setIsLoading(true);
    try {
      // call the login service
      await login(formData);
      // if exit redirect to dashboard
      navigate('/dashboard');
    } catch (err) {
      console.log(err)
      // show the error if fails
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("Ha ocurrido un error inesperado.")
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <AuthLayout
      title="Bienvenido de nuevo"
      subtitle="Ingresa tus credenciales para acceder a tu espacio"
    >
      <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
        <div className="space-y-4">
          
          {/* Email Input */}
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700">
              Email
            </label>
            <div className="mt-1">
              <input 
                id="email"
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
                placeholder="tu@email.com"
                className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition duration-200"
              />
            </div>
          </div>

          {/* Password Input */}
          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700">
              Contraseña
            </label>
            <div className="mt-1">
              <input 
                id="password"
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                required
                placeholder="••••••••"
                className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm transition duration-200"
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

        {/* Submit Button */}
        <button
          type="submit"
          className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors"
        >
          { isLoading ? (
            <div className="flex items-center gap-2">
                <LoadingSpinner size="sm" className="border-gray-400 border-t-white" />
                <span>Entrando...</span>
            </div>
          ) : (
            <span>Entrar</span>
          )}
        </button>

        {/* Link to Register */}
        <div className="text-sm text-center mt-4">
          <span className="text-gray-500">¿No tienes una cuenta? </span>
          <Link to="/register" className="font-medium text-indigo-600 hover:text-indigo-500">
            Regístrate gratis
          </Link>
        </div>
      </form>
    </AuthLayout>
  );
}