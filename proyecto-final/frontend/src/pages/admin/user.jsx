import { useState } from 'react';
import { Edit, Trash2, Plus, X, Users, Shield, User } from 'lucide-react';

// Datos falsos (Mocks) iniciales
const mockUsers = [
    { id: 1, name: 'Jose Dabas', email: 'admin@hotel.com', role: 'ADMIN', status: 'Activo' },
    { id: 2, name: 'Juan Pérez', email: 'juan@gmail.com', role: 'CLIENT', status: 'Activo' },
    { id: 3, name: 'María Gómez', email: 'maria@hotmail.com', role: 'CLIENT', status: 'Inactivo' },
];

export default function UserCrud() {
    const [users, setUsers] = useState(mockUsers);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [userToDelete, setUserToDelete] = useState(null);
    const [modalMode, setModalMode] = useState('create');

    // Estado para el formulario del Modal
    const [formData, setFormData] = useState({ id: null, name: '', email: '', role: 'CLIENT', status: 'Activo' });

    // Función para abrir el modal para Crear
    const handleOpenCreate = () => {
        setFormData({ id: null, name: '', email: '', role: 'CLIENT', status: 'Activo' });
        setModalMode('create');
        setIsModalOpen(true);
    };

    // Función para abrir el modal para Editar
    const handleOpenEdit = (user) => {
        setFormData(user);
        setModalMode('edit');
        setIsModalOpen(true);
    };

    // Función para abrir el modal de confirmación de eliminación
    const handleDeleteClick = (id) => {
        setUserToDelete(id);
        setIsDeleteModalOpen(true);
    };

    // Función para confirmar la eliminación
    const confirmDelete = () => {
        setUsers(users.filter(user => user.id !== userToDelete));
        setIsDeleteModalOpen(false);
        setUserToDelete(null);
    };

    // Función para Guardar (Crear o Editar)
    const handleSave = (e) => {
        e.preventDefault();
        if (modalMode === 'create') {
            const newUser = { ...formData, id: Date.now() }; // ID temporal
            setUsers([...users, newUser]);
        } else {
            setUsers(users.map(user => (user.id === formData.id ? formData : user)));
        }
        setIsModalOpen(false);
    };

    return (
        <div className="max-w-7xl mx-auto space-y-6">

            {/* Encabezado */}
            <div className="flex justify-between items-center bg-white p-6 rounded-lg shadow-sm border border-gray-100">
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 flex items-center gap-2">
                        <Users className="text-secondary" /> Gestión de Usuarios
                    </h2>
                    <p className="text-gray-500 text-sm mt-1">Administra los accesos y roles del sistema.</p>
                </div>
                <button
                    onClick={handleOpenCreate}
                    className="bg-secondary hover:opacity-90 text-white font-semibold py-2 px-4 rounded-lg flex items-center gap-2 transition-all shadow-md shadow-secondary/20"
                >
                    <Plus size={20} /> Nuevo Usuario
                </button>
            </div>

            {/* Tabla de Usuarios */}
            <div className="bg-white rounded-lg shadow-md overflow-hidden border border-gray-100">
                <div className="overflow-x-auto">
                    <table className="w-full text-left border-collapse">
                        <thead>
                            <tr className="bg-gray-50 text-gray-600 border-b border-gray-200 text-sm uppercase tracking-wider">
                                <th className="p-4 font-semibold">Nombre y Correo</th>
                                <th className="p-4 font-semibold">Rol</th>
                                <th className="p-4 font-semibold">Estado</th>
                                <th className="p-4 font-semibold">Acciones</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                            {users.map((user) => (
                                <tr key={user.id} className="hover:bg-gray-50 transition-colors">
                                    <td className="p-4">
                                        <div className="font-bold text-gray-800">{user.name}</div>
                                        <div className="text-sm text-gray-500">{user.email}</div>
                                    </td>
                                    <td className="p-4">
                                        <span className={`inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-bold ${user.role === 'ADMIN' ? 'bg-purple-100 text-purple-700' : 'bg-blue-100 text-blue-700'}`}>
                                            {user.role === 'ADMIN' ? <Shield size={14} /> : <User size={14} />}
                                            {user.role}
                                        </span>
                                    </td>
                                    <td className="p-4 align-top pt-5">
                                        <span className={`px-3 py-1 rounded-full text-xs font-bold ${user.status === 'Activo' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                                            {user.status}
                                        </span>
                                    </td>
                                    <td className="p-4 flex justify-start gap-3 mt-1 pt-6 text-gray-400">
                                        <button onClick={() => handleOpenEdit(user)} className="text-gray-400 hover:text-blue-600 transition-colors" title="Editar">
                                            <Edit size={20} />
                                        </button>
                                        <button onClick={() => handleDeleteClick(user.id)} className="text-gray-400 hover:text-red-600 transition-colors" title="Eliminar">
                                            <Trash2 size={20} />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            {users.length === 0 && (
                                <tr>
                                    <td colSpan="4" className="p-8 text-center text-gray-500">No hay usuarios registrados.</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* MODAL PARA CREAR / EDITAR */}
            {isModalOpen && (
                <div className="fixed inset-0 bg-black/40 backdrop-blur-md flex items-center justify-center z-50 p-4 transition-all duration-300 overflow-y-auto">
                    <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md overflow-hidden animate-in fade-in zoom-in duration-300">

                        <div className="flex justify-between items-center p-5 border-b bg-gray-50">
                            <h3 className="text-xl font-bold text-gray-800">
                                {modalMode === 'create' ? 'Crear Nuevo Usuario' : 'Editar Usuario'}
                            </h3>
                            <button onClick={() => setIsModalOpen(false)} className="text-gray-400 hover:text-gray-700">
                                <X size={24} />
                            </button>
                        </div>

                        <form onSubmit={handleSave} className="p-5 space-y-4">
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-1">Nombre Completo</label>
                                <input
                                    type="text" required
                                    value={formData.name} onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-primary outline-none"
                                />
                            </div>

                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-1">Correo Electrónico</label>
                                <input
                                    type="email" required
                                    value={formData.email} onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-primary outline-none"
                                />
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 mb-1">Rol</label>
                                    <select
                                        value={formData.role} onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                                        className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-primary outline-none bg-white"
                                    >
                                        <option value="CLIENT">CLIENT</option>
                                        <option value="ADMIN">ADMIN</option>
                                    </select>
                                </div>
                                <div>
                                    <label className="block text-sm font-semibold text-gray-700 mb-1">Estado</label>
                                    <select
                                        value={formData.status} onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                                        className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-primary outline-none bg-white"
                                    >
                                        <option value="Activo">Activo</option>
                                        <option value="Inactivo">Inactivo</option>
                                    </select>
                                </div>
                            </div>

                            <div className="flex justify-end gap-3 pt-4 border-t mt-6">
                                <button type="button" onClick={() => setIsModalOpen(false)} className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded-lg font-semibold transition-colors">
                                    Cancelar
                                </button>
                                <button type="submit" className="px-4 py-2 bg-secondary hover:opacity-90 text-white rounded-lg font-semibold transition-colors shadow-lg shadow-secondary/20">
                                    {modalMode === 'create' ? 'Crear Usuario' : 'Guardar Cambios'}
                                </button>
                            </div>
                        </form>

                    </div>
                </div>
            )}
            {/* MODAL DE ELIMINACIÓN */}
            {isDeleteModalOpen && (
                <div className="fixed inset-0 bg-black/40 backdrop-blur-md flex items-center justify-center z-[60] p-4 transition-all duration-300">
                    <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm overflow-hidden animate-in fade-in zoom-in duration-300 p-6 text-center">
                        <div className="flex justify-center mb-4">
                            <div className="bg-red-100 p-4 rounded-full">
                                <Trash2 size={40} className="text-red-600" />
                            </div>
                        </div>
                        <h3 className="text-xl font-bold text-gray-800 mb-2">¿Estás seguro?</h3>
                        <p className="text-gray-500 mb-6">
                            Esta acción no se puede deshacer. El usuario será eliminado permanentemente del sistema.
                        </p>
                        <div className="flex justify-center gap-4">
                            <button
                                onClick={() => setIsDeleteModalOpen(false)}
                                className="px-6 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg font-semibold transition-colors"
                            >
                                Cancelar
                            </button>
                            <button
                                onClick={confirmDelete}
                                className="px-6 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg font-semibold transition-colors shadow-lg shadow-red-200"
                            >
                                Eliminar
                            </button>
                        </div>
                    </div>
                </div>
            )}

        </div>
    );
}