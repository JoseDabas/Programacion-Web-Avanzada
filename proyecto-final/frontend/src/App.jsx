import { useState, useEffect } from 'react';
import { Routes, Route, Link, useLocation } from 'react-router-dom';
import { CircleUser } from 'lucide-react';
import { logout } from './services/auth.services';
import Login from './pages/auth/login';
import Home from './pages/client/home';
import AdminDashboard from './pages/admin/dashboard';
import PropertyDetail from './pages/client/PropertyDetail';
import User from './pages/admin/user';
import Register from './pages/auth/register';
import PropertyCrud from './pages/admin/PropertyCrud';
import logo from './assets/images/Logo Hotel Platform Final 2.PNG';


function App() {
  const [user, setUser] = useState(null);
  const location = useLocation();

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      try {
        setUser(JSON.parse(storedUser));
      } catch (e) {
        console.error("Error parsing user from localStorage", e);
        setUser(null);
      }
    } else {
      setUser(null);
    }
  }, [location]);

  return (
    <div className="min-h-screen flex flex-col font-sans bg-gray-50 overflow-x-hidden">

      {/* Navbar */}
      <nav className="bg-white text-gray-800 p-4 shadow-sm sticky top-0 z-50 border-b border-gray-100">
        <div className="max-w-7xl mx-auto flex justify-between items-center">
          <Link to="/" className="flex items-center gap-2">
            <img src={logo} alt="Logo" className="h-15 w-auto object-contain" />
          </Link>
          <div className="flex items-center space-x-6 text-sm font-semibold uppercase tracking-wide">

            {/* Solo mostrar si es ADMIN */}
            {user?.role === 'ADMIN' && (
              <>
                <Link to="/admin/dashboard" className="hover:text-secondary transition-colors">Dashboard</Link>
                <Link to="/admin/user" className="hover:text-secondary transition-colors">Usuarios</Link>
                <Link to="/admin/properties" className="hover:text-secondary transition-colors">Inventario</Link>
              </>
            )}

            {user ? (
              <div className="flex items-center gap-4">
                <div className="flex items-center gap-2 bg-gray-50 px-3 py-1.5 rounded-full border border-gray-200">
                  <CircleUser size={20} className="text-secondary" />
                  <span className="max-w-[120px] truncate text-gray-700 font-bold">
                    {user.role === 'ADMIN' ? 'Admin' : (user.name || 'Usuario')}
                  </span>
                </div>
                <button
                  onClick={() => logout()}
                  className="text-gray-500 hover:text-secondary transition-colors"
                >
                  Cerrar Sesión
                </button>
              </div>
            ) : (
              <Link to="/login" className="bg-secondary text-white px-5 py-2 rounded-lg hover:opacity-90 transition-all shadow-md shadow-secondary/20">
                Iniciar Sesión
              </Link>
            )}
          </div>
        </div>
      </nav>

      {/* Contenido Dinámico */}
      <main className="flex-grow p-4 md:p-8">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/admin/dashboard" element={<AdminDashboard />} />
          <Route path="/property/:id" element={<PropertyDetail />} />
          <Route path="/admin/user" element={<User />} />
          <Route path="/admin/properties" element={<PropertyCrud />} />
        </Routes>
      </main>

    </div>
  );
}

export default App;