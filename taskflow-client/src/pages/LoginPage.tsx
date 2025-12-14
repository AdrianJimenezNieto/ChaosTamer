import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../../services/authService";
// HEADLESSui
import { Transition } from "@headlessui/react";
import { Fragment } from "react";

export default function LoginPage() {
  // States of the component
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, seterror] = useState<string | null>(null);

  // Navigate Hook
  const navigate = useNavigate();

  // Handle from submit
  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault(); // cancell page reload
    seterror(null); // delete previous errors

    try {
      // call the login service
      await login({ email, password });
      
      // if exit redirect to dashboard
      navigate('/dashboard');
    } catch (err) {
      // show the error if fails
      if (err instanceof Error) {
        seterror(err.message);
      } else {
        seterror("Ha ocurrido un error inesperado.")
      }
    }
  };

  return (
    <div className="flex h-screen items-center justify-center">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-sm rounded-lg bg-gray-800 p-8 shadow-lg"
      >
        <h2 className="mb-6 text-center text-3xl font-bold text-white">
          Iniciar Sesión
        </h2>

        {/* Email Input */}
        <div className="mb-4">
          <label htmlFor="email" className="mb-2 block text-sm font-medium text-gray-300">
            Email
          </label>
          <input 
            id="email"
            type="email"
            value={email}
            onChange= {(e) => setEmail(e.target.value)}
            required
            className="w-full rounded-md border border-gray-600 bg-gray-700 px-3 py-2 text-white placeholder-gray-400 transition-shadow duration-200 ease-in-out focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Password Input */}
        <div className="mb-4">
          <label htmlFor="password" className="mb-2 block text-sm font-medium text-gray-300">
            Password
          </label>
          <input 
            id="password"
            type="password"
            value={password}
            onChange= {(e) => setPassword(e.target.value)}
            required
            className="w-full rounded-md border border-gray-600 bg-gray-700 px-3 py-2 text-white placeholder-gray-400 transition-shadow duration-200 ease-in-out focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>

        {/* Error Message */}
        {/* {error && (
          <p className="text-center mb-4 text-sm text-red-400">
            {error}
          </p>
        )} */}

        <Transition
          show={error !== null}
          as={Fragment}
          enter="transition-all duration-300 ease-in-out"
          enterFrom="max-h-0 opacity-0"
          enterTo="max-h-40 opacity-100"
          leave="transition-all duration-300 ease-in-out"
          leaveFrom="max-h-40 opacity-100"
          leaveTo="max-h-0 opacity-0"
        >
          <p className="text-center text-sm text-red-400 mb-4">
            {error}
          </p>
        </Transition>

        {/* Submit Button */}
        <button
          type="submit"
          className="w-full rounded-md bg-blue-600 px-4 py-2 font-semibold text-white transition duration-200 hover:bg-blue-700 focus:outline-none focus:ring-opacity-50"
        >
          Entrar
        </button>

        {/* TODO: Add link to the register page */}
      </form>
    </div>
  );
}