import { Fragment } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Menu, MenuButton, MenuItems, MenuItem, Transition } from "@headlessui/react";
import { useAuthStore } from "../../store/authStore";

// helper to join conditional classes
function classNames(...classes: string[]) {
    return classes.filter(Boolean).join(' ');
}

export const Navbar = () => {
    const navigate = useNavigate();
    const logout = useAuthStore((state) => state.logout);

    // Placeholder fot the initials
    const userInitials = "AD";

    const handleLogOut = () => {
        logout();
        navigate('/login');
    }

    return (
        <nav className="bg-gray-800 border-b border-gray-700">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex items-center justify-between h-16">

                    {/* LEFT ZONE: LOGO */}
                    <div className="flex items-center">
                        <Link to="/dashboard" className="flex-shrink-0">
                            <span className="text-white text-xl font-bold tracking-wider">
                                TaskFlow
                            </span>
                        </Link>
                    </div>

                    {/* NAVIGATION LINKS */}
                    <div className="hidden md:block">
                        <div className="ml-10 flex items-baseline space-x-4">
                            <Link
                                to="/dashboard"
                                className="bg-gray-900 text-white px-3 py2 rounded-md text-sm font-medium"
                            >
                                Tableros
                            </Link>
                        </div>
                    </div>

                    {/* RIGHT ZONE: ACTIONS AND PROFILE */}
                    <div className="hidden md:block">
                        <div className="ml-4 flex items-center md:ml-6">

                            {/* Button for fast creating */}
                            <button
                                type="button"
                                className="bg-indigo-600 p-1 rounded-full text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-800 focus:ring-white px-3 text-sm font-medium mr-4 transition-colors"
                            >
                                + Crear
                            </button>
                        </div>
                    </div>

                    {/* USER MENU DROPDOWN */}
                    <Menu as="div" className="ml-3 relative">
                        <div>
                            <MenuButton className="max-w-xs bg-gray-800 rounded-full flex items-center text-sm focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-gray-800 focus:ring-white">
                                <span className="sr-only">Abrir menú de usuario</span>
                                {/* AVATAR WITH INITIALS */}
                                <div className="h-8 w-8 rounded-full bg-indigo-500 flex items-center justify-center text-white font-bold">
                                    {userInitials}
                                </div>
                            </MenuButton>
                        </div>

                        <Transition
                            as={Fragment}
                            enter="transition ease-out duration-100"
                            enterFrom="transform opacity-0 scale-95"
                            enterTo="transform opacity-100 scale-100"
                            leave="transition ease-in duration-75"
                            leaveFrom="transform opacity-100 scale-100"
                            leaveTo="transform opacity-0 scale-95"
                        >
                            <MenuItems className="origin-top-right absolute right-0 mt-2 w-48 rounded-md shadow-lg py-1 bg-white ring-1 ring-black ring-opacity-5 focus:outline-none z-50">
                                <div className="px-4 py-2 border-b">
                                    <p className="text-sm text-gray-500">Conectado como</p>
                                    <p className="text-sm font-medium text-gray-900 truncate">usuario@email.com</p>
                                </div>

                                <MenuItem>
                                    {({focus}) => (
                                        <button
                                            onClick={handleLogOut}
                                            className={classNames(
                                                focus ? 'bg-gray-100' : '',
                                                'block w-full text-left px-4 py-2 text-sm text-red-600'
                                            )}
                                        >
                                            Cerrar Sesión
                                        </button>
                                    )}
                                </MenuItem>
                            </MenuItems>
                        </Transition>
                    </Menu>
                </div>
            </div>
        </nav>
    )
}